package com.reviews.jaffa.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RatingBar;
import android.widget.TextView;

import com.reviews.jaffa.Fragments.MovieDetailFragment;
import com.reviews.jaffa.R;

import java.util.List;

/**
 * Created by GauthamVejandla on 5/29/17.
 */

public class CriticReviewsAdapter extends BaseAdapter implements View.OnClickListener{

    Context mContext;
    private MovieDetailFragment.OnMovieDetailFragmentListener mListener;
    private List<String> listRevfrnd_fbId, listRevfrnd_rating,listRevfrnd_revtext;
    //private static LayoutInflater inflater=null;

    // View lookup cache
    private static class ViewHolder {
        TextView revtext;
        RatingBar revrating;
    }

    public CriticReviewsAdapter(Context context, List<String> listRevfrnd_fbId, List<String> listRevfrnd_rating, List<String>listRevfrnd_revtext) {
        this.mContext=context;
        this.mContext=context;
        this.listRevfrnd_fbId = listRevfrnd_fbId;
        this.listRevfrnd_rating = listRevfrnd_rating;
        this.listRevfrnd_revtext = listRevfrnd_revtext;
        mListener = (MovieDetailFragment.OnMovieDetailFragmentListener) context;

    }

    @Override
    public void onClick(View v) {


    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return listRevfrnd_fbId.size();
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
        RatingBar revrating;
    }



    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        CriticReviewsAdapter.Holder holder=new CriticReviewsAdapter.Holder();
        View rowView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.moviedetail_review_row, parent, false);

        holder.revtext = (TextView) rowView.findViewById(R.id.reviewer_text);
        holder.revrating = (RatingBar) rowView.findViewById(R.id.reviewer_rating);

        holder.revtext.setText(listRevfrnd_revtext.get(position).toString());
        holder.revrating.setNumStars(3);
        rowView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });
        return rowView;
    }
}