package com.reviews.jaffa.Adapters;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.reviews.jaffa.Fragments.MainGridFragment;
import com.reviews.jaffa.LeaderBoardCards.CardFragmentPagerAdapter;
import com.reviews.jaffa.LeaderBoardCards.ShadowTransformer;
import com.reviews.jaffa.MainActivity;
import com.reviews.jaffa.POJO.GridItem;
import com.reviews.jaffa.POJO.ImageItem;
import com.reviews.jaffa.R;
import com.reviews.jaffa.Volley.VolleySingleton;
import com.squareup.picasso.Picasso;

/**
 * Created by GauthamVejandla on 3/17/17.
 */
public class GridViewAdapter extends RecyclerView.Adapter<GridViewAdapter.MyViewHolder> {

    private Context mContext;
    private MainGridFragment.OnMainGridFragmentListener mListener;
    private ArrayList<GridItem> mGridData = new ArrayList<GridItem>();
    private List<String> title, rating,director;
    TextView movie_title, movie_director, movie_rating;
    ImageView movie_image;
    private CardFragmentPagerAdapter mFragmentCardAdapter;
    private ShadowTransformer mFragmentCardShadowTransformer;
    private ViewPager mViewPager;

    public GridViewAdapter(Context context,List<String> title,List<String> rating,List<String>director) {
        mContext = context;
        this.title = title;
        this.rating = rating;
        this.director = director;
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
            movie_director = (TextView)view.findViewById(R.id.grid_movie_director);
            movie_rating = (TextView) view.findViewById(R.id.grid_movie_rating);
        }
    }


    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.grid_item_layout, parent, false);


        mViewPager = (ViewPager) itemView.findViewById(R.id.viewPager);
        mFragmentCardAdapter = new CardFragmentPagerAdapter(((MainActivity)mContext).getSupportFragmentManager(),
                dpToPixels(2, mContext));

        mFragmentCardShadowTransformer = new ShadowTransformer(mViewPager, mFragmentCardAdapter);
        mFragmentCardShadowTransformer.enableScaling(true);
        mViewPager.setAdapter(mFragmentCardAdapter);
        mViewPager.setPageTransformer(false, mFragmentCardShadowTransformer);
        mViewPager.setOffscreenPageLimit(3);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {

        setImage(title.get(position).toString(),movie_image );
        movie_title.setText(title.get(position).toString());
        movie_director.setText(director.get(position).toString());
        movie_rating.setText(rating.get(position).toString());



        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onPropertyClick(title.get(position).toString());
            }
        });

    }

    public void setImage(String title,final  ImageView movie_img){

        String url = "http://jaffareviews.com/Images/Movies/"+title+"/Movie.jpg";
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
    public int getItemCount() {
        return title.size();
    }

    public static float dpToPixels(int dp, Context context) {
        return dp * (context.getResources().getDisplayMetrics().density);
    }
}