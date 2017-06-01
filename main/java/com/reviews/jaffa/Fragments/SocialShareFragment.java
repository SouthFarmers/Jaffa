package com.reviews.jaffa.Fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;

import com.reviews.jaffa.Helpers.Shareable;
import com.reviews.jaffa.R;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by gautham on 6/1/17.
 */

public class SocialShareFragment extends Fragment implements View.OnClickListener  {

    private OnSocialShareFragmentListener mListener;
    Button fb,twitter,gplus,messages,email,defaultb;

    String message = "Visit Jaffa reviews!";
    String url = "http://jaffareviews.com/";
    Uri pic = Uri.parse("http://jaffareviews.com/Images/Movies/Manam/Movie.jpg");

    public static SocialShareFragment newInstance() {
        Bundle args = new Bundle();
        SocialShareFragment fragment = new SocialShareFragment();
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
        if (context instanceof SocialShareFragment.OnSocialShareFragmentListener) {
            mListener = (SocialShareFragment.OnSocialShareFragmentListener) context;
        }else {
            throw new RuntimeException(context.toString()
                    + " must implement OnCheeseCategoriesFragmentListener");
        }
    }

    public interface OnSocialShareFragmentInteractionListener {
        void onPropertyClick(String title);
    }

    public interface OnSocialShareFragmentListener {
        void onPropertyClick(String title);
        void disableCollapse();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_facebook:
                facebook(v);
                break;
            case R.id.button_twitter:
                twitter(v);
                break;
            case R.id.button_gplus:
                plus(v);
                break;
            case R.id.button_messages:
                messages(v);
                break;
            case R.id.button_email:
                email(v);
                break;
            case R.id.button_default:
                defaultAction(v);
                break;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_social_sharing, container, false);
        fb = (Button) rootView.findViewById(R.id.button_facebook);
        twitter= (Button) rootView.findViewById(R.id.button_twitter);
        gplus = (Button) rootView.findViewById(R.id.button_gplus);
        messages= (Button) rootView.findViewById(R.id.button_messages);
        email= (Button) rootView.findViewById(R.id.button_email);
        defaultb = (Button) rootView.findViewById(R.id.button_default);

        fb.setOnClickListener(this);
        twitter.setOnClickListener(this);
        gplus.setOnClickListener(this);
        messages.setOnClickListener(this);
        email.setOnClickListener(this);
        defaultb.setOnClickListener(this);

        return rootView;
    }

    public void facebook(View v) {
        Shareable shareInstance = new Shareable.Builder(getActivity())
                .message(message)
                .socialChannel(Shareable.Builder.FACEBOOK)
                .url(url)
                .image(pic)
                .build();
        shareInstance.share();
    }

    public void twitter(View v) {
        Shareable shareInstance = new Shareable.Builder(getActivity())
                .message(message)
                .socialChannel(Shareable.Builder.TWITTER)
                .url(url)
                .image(pic)
                .build();
        shareInstance.share();
    }
    public void plus(View v) {
        Shareable shareInstance = new Shareable.Builder(getActivity())
                .message(message)
                .socialChannel(Shareable.Builder.GOOGLE_PLUS)
                .url(url)
                .image(pic)
                .build();
        shareInstance.share();
    }

    public void messages(View v) {
        Shareable shareInstance = new Shareable.Builder(getActivity())
                .message(message)
                .socialChannel(Shareable.Builder.MESSAGES)
                .url(url)
                .image(pic)
                .build();
        shareInstance.share();
    }

    public void email(View v) {
        Shareable shareInstance = new Shareable.Builder(getActivity())
                .message(message)
                .socialChannel(Shareable.Builder.EMAIL)
                .url(url)
                .image(pic)
                .build();
        shareInstance.share();
    }

    public void defaultAction(View v) {
        Shareable shareInstance = new Shareable.Builder(getActivity())
                .message(message)
                .url(url)
                .image(pic)
                .build();
        shareInstance.share();
    }
}
