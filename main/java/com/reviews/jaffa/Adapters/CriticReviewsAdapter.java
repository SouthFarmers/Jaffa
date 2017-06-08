package com.reviews.jaffa.Adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.mingle.widget.LoadingView;
import com.reviews.jaffa.Fragments.MovieDetailFragment;
import com.reviews.jaffa.R;
import com.reviews.jaffa.Volley.VolleySingleton;

import org.json.JSONObject;

import java.util.List;

/**
 * Created by GauthamVejandla on 5/29/17.
 */

public class CriticReviewsAdapter extends BaseAdapter implements View.OnClickListener{

    Context mContext;
    private MovieDetailFragment.OnMovieDetailFragmentListener mListener;
    private List<String> listRevcritic_fbId, listRevcritic_rating,listRevcritic_revtext;
    String access_token = "EAAFkKlScYZAcBAF1TLu9iurW5FO6vJMgKAPabNP603mSdsGPRnOixeKEZB9J4w10fpNCTY%20tOg6Xj4EhEF4X7f67Wpb8wkDKM4uUZCU2oDS6cOfsbXnzNb7lcSPJrjMyk5xzKhn9DjamXpEM%20W180Ha4ZCABCd3yYZD";

    private static class ViewHolder {
        TextView revtext;
        RatingBar revrating;
    }

    public CriticReviewsAdapter(Context context, List<String> listRevcritic_fbId, List<String> listRevcritic_rating, List<String>listRevcritic_revtext) {
        this.mContext=context;
        this.mContext=context;
        this.listRevcritic_fbId = listRevcritic_fbId;
        this.listRevcritic_rating = listRevcritic_rating;
        this.listRevcritic_revtext = listRevcritic_revtext;
        mListener = (MovieDetailFragment.OnMovieDetailFragmentListener) context;


    }

    @Override
    public void onClick(View v) {


    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return listRevcritic_fbId.size();
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    public class Holder
    {
        TextView revtext;
        TextView revName;
        ImageView fb_img;
        RatingBar revrating;
    }



    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        CriticReviewsAdapter.Holder holder=new CriticReviewsAdapter.Holder();
        View rowView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.moviedetail_review_row, parent, false);

        holder.revtext = (TextView) rowView.findViewById(R.id.reviewer_text);
        holder.revName = (TextView) rowView.findViewById(R.id.reviewer_name);
        holder.fb_img = (ImageView) rowView.findViewById(R.id.fb_icon);
        holder.revrating = (RatingBar) rowView.findViewById(R.id.reviewer_rating);
        getUserprofile(listRevcritic_fbId.get(position).toString(), holder);
        setImage("https://graph.facebook.com/"+listRevcritic_fbId.get(position).toString()+"/picture?type=large&w‌​idth=100&height=150",holder);

        holder.revtext.setText(listRevcritic_revtext.get(position).toString());
        holder.revrating.setRating(Float.parseFloat(listRevcritic_rating.get(position).toString()));
        rowView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });

        return rowView;
    }



    public void getUserprofile(String fbID, final CriticReviewsAdapter.Holder holder){

        String url = "https://graph.facebook.com/"+fbID+"?access_token="+access_token;

        JsonObjectRequest jsonRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        holder.revName.setText(response.optString("first_name"));
                    }

                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                    }
                });

        VolleySingleton.getInstance().addToRequestQueue(jsonRequest);
    }


    public void setImage(String url, final CriticReviewsAdapter.Holder holder){
        Log.d("",url);
        ImageRequest imgRequest = new ImageRequest(url,
                new Response.Listener<Bitmap>() {
                    @Override
                    public void onResponse(Bitmap response) {
                        holder.fb_img.setImageBitmap(response);
                    }
                }, 0, 0, ImageView.ScaleType.FIT_XY, Bitmap.Config.ARGB_8888, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                holder.fb_img.setBackgroundColor(Color.parseColor("#ff0000"));
                error.printStackTrace();
            }
        });
        VolleySingleton.getInstance().addToRequestQueue(imgRequest);
    }
}