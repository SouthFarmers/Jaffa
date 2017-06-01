package com.reviews.jaffa.Fragments;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.RotateAnimation;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.reviews.jaffa.Adapters.CriticReviewsAdapter;
import com.reviews.jaffa.Adapters.FriendReviewsAdapter;
import com.reviews.jaffa.MainActivity;
import com.reviews.jaffa.R;
import com.reviews.jaffa.Volley.VolleySingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by GauthamVejandla on 4/9/17.
 */

public class MovieDetailFragment extends Fragment implements View.OnClickListener  {

    private static final String movie_NAME = "prop_name";
    private String movieName, movieDirector,movieRating,movieReleaseDate,movieMusicDirector,movieImage, movieID;
    private OnMovieDetailFragmentListener mListener;
    TextView movie_name,movie_director,movie_rating, movie_releasedate, movie_musicdirector, movie_title;
    ImageView movie_img;
    private static List<String> listRevfrnd_fbId,listRevfrnd_rating, listRevfrnd_revtext,listRevcritic_fbId,listRevcritic_rating,listRevcritic_revtext;
    ListView frndrevlistView, criticrevlistview;
    static FriendReviewsAdapter frndsrevadapter;
    static CriticReviewsAdapter criticrevadapter;
    ProgressBar progressBar;
    View view;

    public MovieDetailFragment() {
    }

    public static MovieDetailFragment newInstance(String title) {
        Bundle args = new Bundle();
        args.putString(movie_NAME, title);
        MovieDetailFragment fragment = new MovieDetailFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        if (getArguments() != null) {
            movieName = getArguments().getString(movie_NAME);
        }
        moviedetailsvolley();
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.activity_details_view, container, false);
        progressBar = (ProgressBar) view.findViewById(R.id.detail_progressBar);
        movie_img = (ImageView) view.findViewById(R.id.detail_thumbnail);
        movie_title = (TextView) view.findViewById(R.id.movie_title_label);
        movie_director = (TextView) view.findViewById(R.id.movie_detail_director);
        movie_rating = (TextView) view.findViewById(R.id.movie_detail_rating);
        movie_releasedate = (TextView) view.findViewById(R.id.movie_detail_releasedate);
        movie_musicdirector = (TextView) view.findViewById(R.id.movie_detail_mdirector);
        frndrevlistView=(ListView)view.findViewById(R.id.friends_review_list);
        criticrevlistview=(ListView)view.findViewById(R.id.critic_review_list);
        progressBar.setVisibility(View.VISIBLE);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mListener.enableCollapse();
    }

    @Override
    public void onCreateOptionsMenu(
            Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_add_review, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle item selection
        switch (item.getItemId()) {
            case R.id.add_review:

                SharedPreferences prefs = getActivity().getSharedPreferences(getString(R.string.shared_pref_FbID), MODE_PRIVATE);
                final String restoreduserid = prefs.getString(getString(R.string.shared_pref_FbID), null);
                if (restoreduserid != null) {
                    LayoutInflater layoutInflaterAndroid = LayoutInflater.from(getActivity());
                    View mView = layoutInflaterAndroid.inflate(R.layout.add_review, null);
                    AlertDialog.Builder alertDialogBuilderUserInput = new AlertDialog.Builder(getActivity());
                    alertDialogBuilderUserInput.setView(mView);

                    final EditText addreviewText = (EditText) mView.findViewById(R.id.add_review_text);
                    final RatingBar addreviewRating = (RatingBar) mView.findViewById(R.id.add_review_rating);
                    alertDialogBuilderUserInput
                            .setCancelable(false)
                            .setPositiveButton("Add", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialogBox, int id) {
                                    // ToDo get user input here
                                    AddReview(addreviewText.getText().toString(), Integer.parseInt(movieID), Integer.parseInt(restoreduserid), addreviewRating.getRating());

                                }
                            })

                            .setNegativeButton("Cancel",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialogBox, int id) {
                                            dialogBox.cancel();
                                        }
                                    });

                    AlertDialog alertDialogAndroid = alertDialogBuilderUserInput.create();
                    alertDialogAndroid.show();
                }else{
                    AlertDialog.Builder builder1 = new AlertDialog.Builder(getActivity());
                    builder1.setMessage("Login to add review.");
                    builder1.setCancelable(true);
                    builder1.setPositiveButton(
                            "Ok",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                                    ft.replace(R.id.mainlist_fragment, UserProfileFragment.newInstance());
                                    ft.addToBackStack(null);
                                    ft.commit();
                                }
                            });
                    builder1.setNegativeButton(
                            "Cancel",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                }
                            });
                    AlertDialog alert11 = builder1.create();
                    alert11.show();
                }


                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnMovieDetailFragmentListener) {
            mListener = (OnMovieDetailFragmentListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnCheeseDetailFragmentListener");
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        // ((MainActivity) getActivity()).getToolbar().setTitle(movieName);
    }

    public interface OnMovieDetailFragmentListener {
        void enableCollapse();
    }

    public void moviedetailsvolley(){
        String url = "http://jaffareviews.com/api/Movie/GetMovie?movieName="+movieName+"&fbIds=1468306842,715741731";
 //String url = "http://jaffareviews.com/api/Movie/GetMovie?movieName=manam&fbIds=1468306842,715741731";
        JsonObjectRequest jsonRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        System.out.print(response);
                        // the response is already constructed as a JSONObject!
                        try {
                            listRevfrnd_fbId = new ArrayList<String>();
                            listRevfrnd_rating = new ArrayList<String>();
                            listRevfrnd_revtext = new ArrayList<String>();

                            listRevcritic_fbId = new ArrayList<String>();
                            listRevcritic_rating = new ArrayList<String>();
                            listRevcritic_revtext = new ArrayList<String>();

                            if (response.has("Movie")) {
                                JSONObject responseObject = response.getJSONObject("Movie");

                                movieID = responseObject.optString("MovieID");
                                movieName = responseObject.optString("MovieName");
                                movieDirector = responseObject.optString("Director");
                                movieRating = responseObject.optString("AvgRating");
                                movieReleaseDate = responseObject.optString("ReleaseDate");
                                movieMusicDirector = responseObject.optString("MusicDirector");
                                movieImage = responseObject.optString("MovieImage");

                                if (responseObject.has("Reviews")) {
                                    JSONArray jsonArray = responseObject.getJSONArray("Reviews");

                                    for (int i = 0; i < jsonArray.length(); i++) {
                                        if(jsonArray.getJSONObject(i).optString("IsFriend").equalsIgnoreCase("true")){
                                            listRevfrnd_fbId.add(i, (jsonArray.getJSONObject(i).optString("FbID")));
                                            listRevfrnd_rating.add(i, (jsonArray.getJSONObject(i).optString("MovieRating")));
                                            listRevfrnd_revtext.add(i, (jsonArray.getJSONObject(i).optString("MovieReview")));
                                        }else{
                                            listRevcritic_fbId.add(i, (jsonArray.getJSONObject(i).optString("FbID")));
                                            listRevcritic_rating.add(i, (jsonArray.getJSONObject(i).optString("MovieRating")));
                                            listRevcritic_revtext.add(i, (jsonArray.getJSONObject(i).optString("MovieReview")));
                                        }
                                    }
                                }

                                frndsrevadapter = new FriendReviewsAdapter(getActivity(), listRevfrnd_fbId,listRevfrnd_rating,listRevfrnd_revtext, progressBar);
                                frndrevlistView.setAdapter(frndsrevadapter);

                                criticrevadapter = new CriticReviewsAdapter(getActivity(), listRevcritic_fbId,listRevcritic_rating,listRevcritic_revtext);
                                criticrevlistview.setAdapter(criticrevadapter);


                                setMovieValues();
                                setImage(movieImage);
                            }
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



    public void setImage(String url){
        Log.d("",url);
        ImageRequest imgRequest = new ImageRequest(url,
                new Response.Listener<Bitmap>() {
                    @Override
                    public void onResponse(Bitmap response) {
                        movie_img.setImageBitmap(response);
                    }
                }, 0, 0, ImageView.ScaleType.FIT_XY, Bitmap.Config.ARGB_8888, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                movie_img.setBackgroundColor(Color.parseColor("#ff0000"));
                error.printStackTrace();
            }
        });
        VolleySingleton.getInstance().addToRequestQueue(imgRequest);
    }



    @Override
    public void onClick(View v) {
        switch (v.getId()) {

        }
    }

    public void setMovieValues(){
        movie_title.setText(movieName);
        movie_releasedate.setText(setDate(movieReleaseDate));
        movie_director.setText(movieDirector);
        movie_musicdirector.setText(movieMusicDirector);
        movie_rating.setText(movieRating+" %");
    }


    public static String setDate(String date) {
        String results = date.replaceAll("^/Date\\(", "");
        SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yyyy");
        String millisecond = results.substring(0, results.indexOf('-'));
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(Long.parseLong(millisecond));
        return sdf.format(calendar.getTime());
    }

    public void AddReview(final String text, final int movieID, final int FbID, final float rating){

        JSONArray jsonArray = new JSONArray();
        JSONObject jsonObject = new JSONObject();

        try{
            jsonObject.put("MovieID",movieID);
            jsonObject.put("Rating",rating);
            jsonObject.put("Review", text);
            jsonObject.put("FbID",FbID);
            jsonArray.put(jsonObject);

        }catch(Exception e){

        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, "http://jaffareviews.com/api/movie/AddRating", jsonObject,
                new Response.Listener<JSONObject>(){
                    @Override
                    public void onResponse(JSONObject response) {
                        moviedetailsvolley();
                        try {
                            JSONArray arrData = response.getJSONArray("data");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener(){
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("Error.Response", error.toString());
                    }
                });

        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        requestQueue.add(jsonObjectRequest);
    }
}