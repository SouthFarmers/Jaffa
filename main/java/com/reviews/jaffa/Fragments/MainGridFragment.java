package com.reviews.jaffa.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.reviews.jaffa.Adapters.GridViewAdapter;
import com.reviews.jaffa.R;
import com.reviews.jaffa.Volley.VolleySingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by GauthamVejandla on 4/8/17.
 */

public class MainGridFragment extends Fragment implements View.OnClickListener {


    private OnMainGridFragmentListener mListener;
    static GridViewAdapter mainadapter;
    static RecyclerView recyclerView;
    ProgressBar progressBar;
    private static List<String> listMovieTitle, listMovieRating, listMovieDirector, listMovieID, ListMovieImage, ListMovieMusicDirector, ListMovieReleaseDate, ListMovieReviews, ListMovieInfo;

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

    public interface OnMainGridFragmentInteractionListener {
        void onPropertyClick(String title);
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

        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.mainlist_fragment, container, false);
        progressBar = (ProgressBar) rootView.findViewById(R.id.main_progressBar);
        recyclerView = (RecyclerView) rootView.findViewById(R.id.main_list);
        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getActivity(), 2);
        recyclerView.setLayoutManager(mLayoutManager);
        progressBar.setVisibility(View.VISIBLE);

       allMoviesvolley();
        return rootView;
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
                            listMovieDirector = new ArrayList<String>();
                            listMovieID = new ArrayList<String>();
                            ListMovieImage = new ArrayList<String>();
                            ListMovieMusicDirector = new ArrayList<String>();
                            ListMovieReleaseDate = new ArrayList<String>();
                            ListMovieReviews = new ArrayList<String>();
                            ListMovieInfo = new ArrayList<String>();


                            if (response.has("movies")) {
                                JSONArray jsonArray = response.getJSONArray("movies");

                                for (int i = 0; i < jsonArray.length(); i++) {

                                    if (jsonArray.getJSONObject(i).has("MovieName")) {
                                        listMovieTitle.add(i, (jsonArray.getJSONObject(i).optString("MovieName")));
                                        listMovieRating.add(i, (jsonArray.getJSONObject(i).optString("AvgRating")));
                                        listMovieDirector.add(i, (jsonArray.getJSONObject(i).optString("Director")));
                                        listMovieID.add(i, (jsonArray.getJSONObject(i).optString("MovieID")));
                                        ListMovieImage.add(i, (jsonArray.getJSONObject(i).optString("MovieImage")));
                                        ListMovieMusicDirector.add(i, (jsonArray.getJSONObject(i).optString("MusicDirector")));
                                        ListMovieReleaseDate.add(i, (jsonArray.getJSONObject(i).optString("ReleaseDate")));
                                        ListMovieReviews.add(i, (jsonArray.getJSONObject(i).optString("Reviews")));
                                        ListMovieInfo.add(i, (jsonArray.getJSONObject(i).optString("MovieInfo")));
                                    }
                                }
                            }


                                mainadapter = new GridViewAdapter(getActivity(), listMovieTitle,listMovieRating,listMovieDirector, listMovieID, ListMovieImage, ListMovieMusicDirector, ListMovieReleaseDate);
                                recyclerView.setAdapter(mainadapter);
                            progressBar.setVisibility(View.GONE);

                        } catch (JSONException e) {

                            e.printStackTrace();
                            progressBar.setVisibility(View.GONE);
                        }
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                        progressBar.setVisibility(View.GONE);
                    }
                });

        VolleySingleton.getInstance().addToRequestQueue(jsonRequest);
    }
}
