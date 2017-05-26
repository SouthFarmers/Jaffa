package com.reviews.jaffa.Fragments;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.RotateAnimation;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.JsonObjectRequest;
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
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

/**
 * Created by GauthamVejandla on 4/9/17.
 */

public class MovieDetailFragment extends Fragment implements View.OnClickListener  {

    private static final String movie_NAME = "prop_name";
    private String movieName, movieDirector,movieRating,movieReleaseDate,movieMusicDirector,movieImage;
    private OnMovieDetailFragmentListener mListener;
    TextView movie_name,movie_director,movie_rating, movie_releasedate, movie_musicdirector, movie_title;
    ImageView movie_img;
    private static List<String> listReviews_fbId,listReviews_rating, listReviews_revtext,listReviews_friend,listReviews_critic;
    CollapsingToolbarLayout collap;

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
        if (getArguments() != null) {
            movieName = getArguments().getString(movie_NAME);
        }
        moviedetailsvolley();
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_details_view, container, false);
        movie_img = (ImageView) view.findViewById(R.id.detail_thumbnail);
        movie_title = (TextView) view.findViewById(R.id.movie_title_label);
        movie_director = (TextView) view.findViewById(R.id.movie_detail_director);
        movie_rating = (TextView) view.findViewById(R.id.movie_detail_rating);
        movie_releasedate = (TextView) view.findViewById(R.id.movie_detail_releasedate);
        movie_musicdirector = (TextView) view.findViewById(R.id.movie_detail_mdirector);

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mListener.enableCollapse();
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

        JsonObjectRequest jsonRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        System.out.print(response);
                        // the response is already constructed as a JSONObject!
                        try {
                            listReviews_fbId = new ArrayList<String>();
                            listReviews_rating = new ArrayList<String>();
                            listReviews_revtext = new ArrayList<String>();
                            listReviews_friend = new ArrayList<String>();
                            listReviews_critic = new ArrayList<String>();
                            if (response.has("Movie")) {
                                JSONObject responseObject = response.getJSONObject("Movie");

                                movieName = responseObject.optString("MovieName");
                                movieDirector = responseObject.optString("Director");
                                movieRating = responseObject.optString("AvgRating");
                                movieReleaseDate = responseObject.optString("ReleaseDate");
                                movieMusicDirector = responseObject.optString("MusicDirector");
                                movieImage = responseObject.optString("MovieImage");

                                if (responseObject.has("Reviews")) {
                                    JSONArray jsonArray = responseObject.getJSONArray("Reviews");

                                    for (int i = 0; i < jsonArray.length(); i++) {
                                        listReviews_fbId.add(i, (jsonArray.getJSONObject(i).optString("FbID")));
                                        listReviews_rating.add(i, (jsonArray.getJSONObject(i).optString("MovieRating")));
                                        listReviews_revtext.add(i, (jsonArray.getJSONObject(i).optString("MovieReview")));
                                        listReviews_critic.add(i, (jsonArray.getJSONObject(i).optString("IsCritic")));
                                        listReviews_friend.add(i, (jsonArray.getJSONObject(i).optString("IsFriend")));
                                    }
                                }


                                setMovieValues();
                                setImage(movieImage);
                            }
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
        movie_rating.setText(movieRating+"/100");
    }


    public static String setDate(String date) {
        String results = date.replaceAll("^/Date\\(", "");
        SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yyyy");
        String millisecond = results.substring(0, results.indexOf('-'));
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(Long.parseLong(millisecond));
        return sdf.format(calendar.getTime());
    }
}
