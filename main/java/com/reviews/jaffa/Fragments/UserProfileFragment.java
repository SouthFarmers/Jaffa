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
import android.widget.TextView;

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
import com.reviews.jaffa.Helpers.ImageHelper;
import com.reviews.jaffa.R;

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
    private LoginButton loginButton;
    private String firstName,lastName;
    private URL profilePicture;
    private String userId;
    private String TAG = "LoginActivity";



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
        loginButton = (LoginButton) rootView.findViewById(R.id.loginButton);
        loginButton.setFragment(this);
        loginButton.setReadPermissions("public_profile");
        callbackManager = CallbackManager.Factory.create();
        loginButton.registerCallback(callbackManager, callback);
        profilePicImageView = (ImageView) rootView.findViewById(R.id.profilePicture);
        greeting = (TextView) rootView.findViewById(R.id.greeting);
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

            }
        }
    }



    private void updateUI() {
        if (userId != null) {
            new LoadProfileImage(profilePicImageView).execute("https://graph.facebook.com/" + userId + "/picture?width=500&height=500");
            greeting.setText(getString(R.string.hello_user, lastName+" "+firstName));
        } else {
            Bitmap icon = BitmapFactory.decodeResource(getContext().getResources(), R.mipmap.ic_launcher);
            profilePicImageView.setImageBitmap(ImageHelper.getRoundedCornerBitmap(getContext(), icon, 200, 200, 200, false, false, false, false));
            greeting.setText(null);
        }
    }


    FacebookCallback<LoginResult> callback = new FacebookCallback<LoginResult>() {
        @Override
        public void onSuccess(LoginResult loginResult) {
            GraphRequest request = GraphRequest.newMeRequest(loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
                @Override
                public void onCompleted(JSONObject object, GraphResponse response) {
                    Log.e(TAG,object.toString());
                    Log.e(TAG,response.toString());

                    try {
                        userId = object.getString("id");
                        profilePicture = new URL("https://graph.facebook.com/" + userId + "/picture?width=500&height=500");
                        if(object.has("first_name"))
                            firstName = object.getString("first_name");
                        if(object.has("last_name"))
                            lastName = object.getString("last_name");
                        updateUI();
                        SharedPreferences.Editor editor = getActivity().getSharedPreferences(getString(R.string.shared_pref_FbID), MODE_PRIVATE).edit();
                        editor.putString(getString(R.string.shared_pref_FbID), userId);
                        editor.commit();

                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                    }
                }
            });
            Bundle parameters = new Bundle();
            parameters.putString("fields", "id, first_name, last_name");
            request.setParameters(parameters);
            request.executeAsync();
        }

        @Override
        public void onCancel() {
        }

        @Override
        public void onError(FacebookException e) {
            e.printStackTrace();
        }
    };

    private void logout(){
        LoginManager.getInstance().logOut();
    }
}


