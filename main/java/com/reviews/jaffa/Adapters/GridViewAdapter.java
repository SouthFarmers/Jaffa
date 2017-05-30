package com.reviews.jaffa.Adapters;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.reviews.jaffa.Fragments.MainGridFragment;
import com.reviews.jaffa.POJO.GridItem;
import com.reviews.jaffa.POJO.ImageItem;
import com.reviews.jaffa.R;
import com.squareup.picasso.Picasso;

/**
 * Created by GauthamVejandla on 3/17/17.
 */
public class GridViewAdapter extends RecyclerView.Adapter<GridViewAdapter.MyViewHolder> {

    private Context mContext;
    private MainGridFragment.OnMainGridFragmentListener mListener;
    private ArrayList<GridItem> mGridData = new ArrayList<GridItem>();
    private List<String> title, rating,director, movieID, mImage, mDirector, releaseDate;
    TextView movie_title,movie_id, movie_director;
    ImageView movie_image;
    RatingBar movie_rating;

    public GridViewAdapter(Context context,List<String> title,List<String> rating,List<String>director,List<String>movieID, List<String>mImage, List<String>mDirector, List<String>releaseDate) {
        mContext = context;
        this.title = title;
        this.rating = rating;
        this.director = director;
        this.movieID = movieID;
        this.mImage = mImage;
        this.mDirector = mDirector;
        this.releaseDate = releaseDate;
        mListener = (MainGridFragment.OnMainGridFragmentListener) context;

    }

    public class MyViewHolder extends RecyclerView.ViewHolder {


        public final View mView;
        public String mBoundString;

        public MyViewHolder(View view) {
            super(view);
            mView = view;
            movie_image = (ImageView) view.findViewById(R.id.grid_movie_image);
            movie_title = (TextView) view.findViewById(R.id.grid_movie_title);
            movie_id = (TextView) view.findViewById(R.id.grid_movie_id);
            movie_director = (TextView)view.findViewById(R.id.grid_movie_director);
            movie_rating = (RatingBar) view.findViewById(R.id.grid_movie_rating);
        }
    }


    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.grid_item_layout, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {

        movie_image.setImageResource(R.mipmap.ic_launcher);
        movie_title.setText(title.get(position).toString());
        movie_director.setText(director.get(position).toString());
        movie_id.setText(movieID.get(position).toString());
        movie_rating.setNumStars(Integer.parseInt(rating.get(position).toString()));



        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onPropertyClick(title.get(position).toString());
            }
        });

    }


    @Override
    public int getItemCount() {
        return title.size();
    }
}