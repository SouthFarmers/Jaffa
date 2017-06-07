package com.reviews.jaffa.Fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.mingle.widget.LoadingView;
import com.reviews.jaffa.Helpers.ImageHelper;
import com.reviews.jaffa.R;
import com.reviews.jaffa.SplashActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by gautham on 5/31/17.
 */

public class UserProfileFragment extends Fragment implements View.OnClickListener {

    private OnUserprofileFragmentListener mListener;
    private ImageView profilePicImageView;
    private TextView greeting;
    private CallbackManager callbackManager;
    private LoginButton logoutButton;
    private String firstName,lastName, userId, fullName;
    private URL profilePicture;
    private String TAG = "LoginActivity";
    LoadingView userProgress;



    public static UserProfileFragment newInstance() {
        Bundle args = new Bundle();
        UserProfileFragment fragment = new UserProfileFragment();
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        FacebookSdk.sdkInitialize(getActivity());
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().setTitle(getString(R.string.app_name));
        AppEventsLogger.activateApp(getActivity());
        //updateUI();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mListener.disableCollapse();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnUserprofileFragmentListener) {
            mListener = (OnUserprofileFragmentListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement UserProfileFragmentFragmentListener");
        }
    }

    public interface OnUserprofileFragmentInteractionListener {
    }

    public interface OnUserprofileFragmentListener {
        void disableCollapse();
    }

    @Override
    public void onClick(View v) {
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_user_profile, container, false);
        logoutButton = (LoginButton) rootView.findViewById(R.id.loginButton);
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                logout();
            }
        });

        profilePicImageView = (ImageView) rootView.findViewById(R.id.profilePicture);
        greeting = (TextView) rootView.findViewById(R.id.greeting);
        userProgress = (LoadingView) rootView.findViewById(R.id.user_progress);

        SharedPreferences sharedPrefs = getActivity().getSharedPreferences(getString(R.string.shared_pref_FbID), MODE_PRIVATE);
        if(sharedPrefs.contains(getString(R.string.shared_pref_FbID))){

            userId = sharedPrefs.getString(getString(R.string.shared_pref_FbID), null);
            fullName = sharedPrefs.getString(getString(R.string.shared_pref_username), null);
            updateUI();

        }
        return rootView;
    }

    private class LoadProfileImage extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public LoadProfileImage(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... uri) {
            String url = uri[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(url).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {

            if (result != null) {
                Bitmap resized = Bitmap.createScaledBitmap(result, 200, 200, true);
                bmImage.setImageBitmap(ImageHelper.getRoundedCornerBitmap(getContext(), resized, 250, 200, 200, false, false, false, false));
                userProgress.setVisibility(View.GONE);
            }
        }
    }

    private void updateUI() {
        if (userId != null) {
            new LoadProfileImage(profilePicImageView).execute("https://graph.facebook.com/" + userId + "/picture?width=500&height=500");
            greeting.setText(fullName);

        } else {
            Bitmap icon = BitmapFactory.decodeResource(getContext().getResources(), R.mipmap.ic_launcher);
            profilePicImageView.setImageBitmap(ImageHelper.getRoundedCornerBitmap(getContext(), icon, 200, 200, 200, false, false, false, false));
            greeting.setText("Please Login!");
        }
    }

    private void logout(){
        LoginManager.getInstance().logOut();
        Intent splash = new Intent(getActivity(), SplashActivity.class);
        startActivity(splash);
    }

}


