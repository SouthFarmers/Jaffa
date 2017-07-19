package com.reviews.jaffa.Fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.cleveroad.pulltorefresh.firework.FireworkyPullToRefreshLayout;
import com.mingle.widget.LoadingView;
import com.reviews.jaffa.Adapters.GridViewAdapter;
import com.reviews.jaffa.LeaderBoardCards.CardFragmentPagerAdapter;
import com.reviews.jaffa.LeaderBoardCards.ShadowTransformer;
import com.reviews.jaffa.R;
import com.reviews.jaffa.Volley.VolleySingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by GauthamVejandla on 4/8/17.
 */

public class MainGridFragment extends Fragment implements View.OnClickListener {


    private OnMainGridFragmentListener mListener;
    private static final int REFRESH_DELAY = 4500;
    private boolean mIsRefreshing;
    static GridViewAdapter mainadapter;
    static RecyclerView recyclerView;
    private ViewPager mViewPager;
    LoadingView progress;
    FireworkyPullToRefreshLayout mPullToRefresh;
    private CardFragmentPagerAdapter mFragmentCardAdapter;
    private ShadowTransformer mFragmentCardShadowTransformer;
    private int numberofleaders;
    private static List<String> listMovieTitle, listMovieRating, leaderboardname,leaderboardfollowers,leaderboardratings,leaderboardfbId;
    private static List<Boolean> leaderboardisfollowing;
    String restoreduserid;

    public static MainGridFragment newInstance() {
        Bundle args = new Bundle();
        MainGridFragment fragment = new MainGridFragment();
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    public void onResume() {
        super.onResume();
        getActivity().setTitle(getString(R.string.app_name));
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mListener.disableCollapse();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnMainGridFragmentListener) {
            mListener = (OnMainGridFragmentListener) context;
        }else {
            throw new RuntimeException(context.toString()
                    + " must implement OnCheeseCategoriesFragmentListener");
        }
    }

    public interface OnMainGridFragmentListener {
        void onPropertyClick(String title);
        void disableCollapse();
    }

    @Override
    public void onClick(View v) {
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.mainlist_fragment, container, false);

        SharedPreferences prefs = getActivity().getSharedPreferences(getString(R.string.shared_pref_FbID), MODE_PRIVATE);
        restoreduserid = prefs.getString(getString(R.string.shared_pref_FbID), null);

        progress = (LoadingView) rootView.findViewById(R.id.main_progress);
        mPullToRefresh = (FireworkyPullToRefreshLayout) rootView.findViewById(R.id.pullToRefresh);
        recyclerView = (RecyclerView) rootView.findViewById(R.id.main_list);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getActivity(), 2);
        recyclerView.setLayoutManager(mLayoutManager);
        mViewPager = (ViewPager) rootView.findViewById(R.id.viewPager);

       allMoviesvolley();
        loadLeaderboard();
        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initRefreshView();

        mPullToRefresh.post(new Runnable() {
            @Override
            public void run() {
                mPullToRefresh.setRefreshing(mIsRefreshing);
            }
        });
    }

    public void allMoviesvolley(){
        String url ="http://jaffareviews.com/api/Movie/GetNewReleases";

        JsonObjectRequest jsonRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            listMovieTitle = new ArrayList<String>();
                            listMovieRating = new ArrayList<String>();


                            if (response.has("movies")) {
                                JSONArray jsonArray = response.getJSONArray("movies");

                                for (int i = 0; i < jsonArray.length(); i++) {

                                    if (jsonArray.getJSONObject(i).has("MovieName")) {
                                        listMovieTitle.add(i, (jsonArray.getJSONObject(i).optString("MovieName")));
                                        listMovieRating.add(i, (jsonArray.getJSONObject(i).optString("AvgRating")));
                                    }
                                }
                            }
                                mainadapter = new GridViewAdapter(getActivity(), listMovieTitle,listMovieRating);
                                recyclerView.setAdapter(mainadapter);

                            recyclerView.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {

                                @Override
                                public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                                    recyclerView.removeOnLayoutChangeListener(this);
                                    final Handler handler = new Handler();
                                    progress.setVisibility(View.GONE);
                                }
                            });

                            mainadapter.notifyDataSetChanged();
                            mPullToRefresh.setRefreshing(mIsRefreshing = false);

                        } catch (JSONException e) {

                            e.printStackTrace();
                            progress.setVisibility(View.GONE);
                        }
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                        progress.setVisibility(View.GONE);
                    }
                });

        VolleySingleton.getInstance().addToRequestQueue(jsonRequest);
    }


    private void initRefreshView() {
        mPullToRefresh.setOnRefreshListener(new FireworkyPullToRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mIsRefreshing = true;
                allMoviesvolley();
            }
        });
    }

    public static float dpToPixels(int dp, Context context) {
        return dp * (context.getResources().getDisplayMetrics().density);
    }

    public void loadLeaderboard(){

        String url ="http://jaffareviews.com/api/Movie/GetTopCritics?UserFbID=100000636216504";
        //String url ="http://jaffareviews.com/api/Movie/GetTopCritics?UserFbID="+restoreduserid;

        JsonObjectRequest jsonRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            leaderboardname = new ArrayList<String>();
                            leaderboardfollowers = new ArrayList<String>();
                            leaderboardratings = new ArrayList<String>();
                            leaderboardfbId = new ArrayList<String>();
                            leaderboardisfollowing = new ArrayList<Boolean>();

                                JSONArray jsonArray = response.getJSONArray("critics");
                            numberofleaders = jsonArray.length();
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    leaderboardname.add(i, (jsonArray.getJSONObject(i).optString("FirstName"))+" "+(jsonArray.getJSONObject(i).optString("LastName")));
                                    leaderboardfollowers.add(i, (jsonArray.getJSONObject(i).optString("NumOfFollowers")));
                                    leaderboardratings.add(i, (jsonArray.getJSONObject(i).optString("NumOfRatings")));
                                    leaderboardfbId.add(i, (jsonArray.getJSONObject(i).optString("fbID")));
                                    leaderboardisfollowing.add((jsonArray.getJSONObject(i).optBoolean("IsFollowing")));
                                }
                            mFragmentCardAdapter = new CardFragmentPagerAdapter(getActivity().getSupportFragmentManager(),
                                    dpToPixels(2, getActivity()), numberofleaders, leaderboardname, leaderboardfollowers, leaderboardratings, leaderboardfbId, leaderboardisfollowing);
                            mFragmentCardShadowTransformer = new ShadowTransformer(mViewPager, mFragmentCardAdapter);
                            mFragmentCardShadowTransformer.enableScaling(true);
                            mViewPager.setAdapter(mFragmentCardAdapter);
                            mViewPager.setPageTransformer(false, mFragmentCardShadowTransformer);
                            mViewPager.setOffscreenPageLimit(3);

                        } catch (JSONException e) {

                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                    }
                });

        VolleySingleton.getInstance().addToRequestQueue(jsonRequest);
    }
}