package com.reviews.jaffa.Fragments;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookAuthorizationException;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.facebook.share.ShareApi;
import com.facebook.share.Sharer;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.model.SharePhoto;
import com.facebook.share.model.SharePhotoContent;
import com.facebook.share.widget.ShareDialog;
import com.reviews.jaffa.Helpers.ImageHelper;
import com.reviews.jaffa.Helpers.JSONParser;
import com.reviews.jaffa.R;

import org.json.JSONObject;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;

import static com.facebook.FacebookSdk.getApplicationContext;

/**
 * Created by gautham on 5/31/17.
 */

public class UserProfileFragment extends Fragment implements View.OnClickListener {

    private OnUserprofileFragmentListener mListener;
    private LoginButton loginButton;
    private static final String PERMISSION = "publish_actions";
    private final String PENDING_ACTION_BUNDLE_KEY =
            "com.example.hellofacebook:PendingAction";
    private ImageView profilePicImageView;
    private TextView greeting;
    private PendingAction pendingAction = PendingAction.NONE;
    private CallbackManager callbackManager;
    private ProfileTracker profileTracker;
    private ShareDialog shareDialog;

    private enum PendingAction {
        NONE,
        POST_PHOTO,
        POST_STATUS_UPDATE
    }


    public static UserProfileFragment newInstance() {
        Bundle args = new Bundle();
        UserProfileFragment fragment = new UserProfileFragment();
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
        updateUI();
    }

    @Override
    public void onPause() {
        super.onPause();
        AppEventsLogger.deactivateApp(getActivity());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        profileTracker.stopTracking();
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
                    + " must implement OnCheeseCategoriesFragmentListener");
        }
    }

    public interface OnUserprofileFragmentInteractionListener {
        //void onPropertyClick(String title);
    }

    public interface OnUserprofileFragmentListener {
        // void onPropertyClick(String title);
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

        callbackManager = CallbackManager.Factory.create();
        // Callback registration
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Toast toast = Toast.makeText(getActivity(), "Logged In", Toast.LENGTH_SHORT);
                toast.show();
                handlePendingAction();
                updateUI();
            }

            @Override
            public void onCancel() {
                // App code
                if (pendingAction != PendingAction.NONE) {
                    showAlert();
                    pendingAction = PendingAction.NONE;
                }
                updateUI();
            }

            @Override
            public void onError(FacebookException exception) {
                if (pendingAction != PendingAction.NONE
                        && exception instanceof FacebookAuthorizationException) {
                    showAlert();
                    pendingAction = PendingAction.NONE;
                }
                updateUI();

            }

            private void showAlert() {
                new AlertDialog.Builder(getActivity())
                        .setTitle(R.string.cancelled)
                        .setMessage(R.string.permission_not_granted)
                        .setPositiveButton(R.string.ok, null)
                        .show();
            }

        });
        shareDialog = new ShareDialog(this);
        shareDialog.registerCallback(
                callbackManager,
                shareCallback);

        if (savedInstanceState != null) {
            String name = savedInstanceState.getString(PENDING_ACTION_BUNDLE_KEY);
            pendingAction = PendingAction.valueOf(name);
        }


        profileTracker = new ProfileTracker() {
            @Override
            protected void onCurrentProfileChanged(Profile oldProfile, Profile currentProfile) {
                updateUI();
                handlePendingAction();
            }
        };


        profilePicImageView = (ImageView) rootView.findViewById(R.id.profilePicture);
        greeting = (TextView) rootView.findViewById(R.id.greeting);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoginManager.getInstance().logInWithReadPermissions(getActivity(), Arrays.asList("public_profile"));
            }
        });
        return rootView;
    }

    private FacebookCallback<Sharer.Result> shareCallback = new FacebookCallback<Sharer.Result>() {
        @Override
        public void onCancel() {
            Log.d("FacebookFragment", "Canceled");
        }

        @Override
        public void onError(FacebookException error) {
            Log.d("FacebookFragment", String.format("Error: %s", error.toString()));
            String title = getString(R.string.error);
            String alertMessage = error.getMessage();
            showResult(title, alertMessage);
        }

        @Override
        public void onSuccess(Sharer.Result result) {
            Log.d("FacebookFragment", "Success!");
            if (result.getPostId() != null) {
                String title = getString(R.string.success);
                String id = result.getPostId();
                String alertMessage = getString(R.string.successfully_posted_post, id);
                showResult(title, alertMessage);
            }
        }

        private void showResult(String title, String alertMessage) {
            new AlertDialog.Builder(getActivity())
                    .setTitle(title)
                    .setMessage(alertMessage)
                    .setPositiveButton(R.string.ok, null)
                    .show();
        }
    };


    private void updateUI() {
        boolean enableButtons = AccessToken.getCurrentAccessToken() != null;

        Profile profile = Profile.getCurrentProfile();
        if (enableButtons && profile != null) {
            new LoadProfileImage(profilePicImageView).execute(profile.getProfilePictureUri(200, 200).toString());
            greeting.setText(getString(R.string.hello_user, profile.getFirstName()));
        } else {
            Bitmap icon = BitmapFactory.decodeResource(getContext().getResources(), R.mipmap.ic_launcher);
            profilePicImageView.setImageBitmap(ImageHelper.getRoundedCornerBitmap(getContext(), icon, 200, 200, 200, false, false, false, false));
            greeting.setText(null);
        }
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putString(PENDING_ACTION_BUNDLE_KEY, pendingAction.name());
    }

    private void handlePendingAction() {
        PendingAction previouslyPendingAction = pendingAction;
        pendingAction = PendingAction.NONE;

        switch (previouslyPendingAction) {
            case NONE:
                break;
            case POST_PHOTO:
                break;
            case POST_STATUS_UPDATE:
                break;
        }
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
}


