package com.reviews.jaffa;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.TaskStackBuilder;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.LoggingBehavior;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.github.glomadrian.roadrunner.DeterminateRoadRunner;
import com.reviews.jaffa.Fragments.UserProfileFragment;
import com.reviews.jaffa.Helpers.CustomOnboardSlide;
import com.reviews.jaffa.Volley.VolleySingleton;
import com.twitter.sdk.android.core.DefaultLogger;
import com.twitter.sdk.android.core.Twitter;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterConfig;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import agency.tango.materialintroscreen.MaterialIntroActivity;
import agency.tango.materialintroscreen.SlideFragment;
import agency.tango.materialintroscreen.SlideFragmentBuilder;

import static java.security.AccessController.getContext;

/**
 * Created by gautham on 6/2/17.
 */

public class SplashActivity extends MaterialIntroActivity {

    private String firstName,lastName, email, coverpic;
    private String userId;
    private String TAG = "LoginActivity";
    private CallbackManager callbackManager;
    private LoginButton loginButton;
    Intent intent;
    boolean loggedin;
    private String hasSeenTutorial = "hasseentutorial";
    private String friendsIDs = "friendsIDs";
    private final int SPLASH_DISPLAY_LENGTH = 3000;
    String frindsIDs="";
    DeterminateRoadRunner determinateLoadingPath;
    ImageView textImage;
    private ValueAnimator progressAnimator;
    private FinishLoadingListener finishLoadingListener;
    private Animation textAnimation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        FacebookSdk.setIsDebugEnabled(true);
        FacebookSdk.addLoggingBehavior(LoggingBehavior.INCLUDE_ACCESS_TOKENS);

        TwitterConfig config = new TwitterConfig.Builder(this)
                .twitterAuthConfig(new TwitterAuthConfig(Constants.TWITTER_CONSUMER_KEY, Constants.TWITTER_CONSUMER_SECRET))
                .build();
        Twitter.initialize(config);

        intent = new Intent(this, MainActivity.class);

        SharedPreferences sharedPrefs = getSharedPreferences(hasSeenTutorial, MODE_PRIVATE);
            if(sharedPrefs.getBoolean(hasSeenTutorial, false)){

                setContentView(R.layout.activity_splash);
                determinateLoadingPath = (DeterminateRoadRunner) findViewById(R.id.determinate);
                textImage = (ImageView) findViewById(R.id.text_image);

                textAnimation = AnimationUtils.loadAnimation(this, R.anim.text);
                textAnimation.setAnimationListener(new Animation.AnimationListener() {
                    @Override public void onAnimationStart(Animation animation) {
                        //Empty
                    }

                    @Override public void onAnimationEnd(Animation animation) {
                        if (finishLoadingListener != null) {
                            finishLoadingListener.onLoadingFinish();
                        }
                    }

                    @Override public void onAnimationRepeat(Animation animation) {
                        //Empty
                    }
                });

                progressAnimator = ValueAnimator.ofInt(0, 1000).setDuration(4000);
                progressAnimator.setStartDelay(2000);
                progressAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override public void onAnimationUpdate(ValueAnimator animation) {
                        int value = (Integer) animation.getAnimatedValue();
                        determinateLoadingPath.setValue(value);
                    }
                });

                progressAnimator.addListener(new Animator.AnimatorListener() {
                    @Override public void onAnimationStart(Animator animation) {
                    }

                    @Override public void onAnimationEnd(Animator animation) {
                        determinateLoadingPath.stop();
                        textImage.startAnimation(textAnimation);
                        textImage.setVisibility(View.VISIBLE);
                        if(isLoggedIn()){
                            getFriends();
                            startActivity(intent);
                        }else{
                            tryLogin();
                        }
                    }

                    @Override public void onAnimationCancel(Animator animation) {
                        //Empty
                    }

                    @Override public void onAnimationRepeat(Animator animation) {
                        //Empty
                    }
                });
                progressAnimator.start();

            }else{
                TaskStackBuilder.create(this)
                        .addNextIntentWithParentStack(new Intent(this, SplashActivity.class))
                        .addNextIntent(new Intent(this, IntroActivity.class))
                        .startActivities();
            }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    public void tryLogin(){
        LayoutInflater layoutInflaterAndroid = LayoutInflater.from(this);
        View mView = layoutInflaterAndroid.inflate(R.layout.splash_login_now, null);
        AlertDialog.Builder alertDialogBuilderUserInput = new AlertDialog.Builder(this);
        alertDialogBuilderUserInput.setTitle("Please Login...").setCancelable(true).setNegativeButton(
                "Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                }).setView(mView);
        loginButton = (LoginButton) mView.findViewById(R.id.loginButton);
        loginButton.setReadPermissions("public_profile", "user_friends");
        callbackManager = CallbackManager.Factory.create();
        loginButton.registerCallback(callbackManager, callback);
        intent = new Intent(this, MainActivity.class);
        AlertDialog alertDialogAndroid = alertDialogBuilderUserInput.create();
        alertDialogAndroid.show();
    }
    FacebookCallback<LoginResult> callback = new FacebookCallback<LoginResult>() {
        @Override
        public void onSuccess(LoginResult loginResult) {

            GraphRequest request = GraphRequest.newMeRequest(loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
                @Override
                public void onCompleted(JSONObject object, GraphResponse response) {

                    try {
                        //AccessToken token = AccessToken.getCurrentAccessToken();
                        userId = object.getString("id");
                        if(object.has("first_name"))
                            firstName = object.getString("first_name");
                        if(object.has("last_name"))
                            lastName = object.getString("last_name");
                        if(object.has("email"))
                            email = object.getString("email");
                        if(object.has("cover"))
                            coverpic = object.getJSONObject("cover").getString("source");
                        getFriends();
                        SharedPreferences.Editor editor = getSharedPreferences(getString(R.string.shared_pref_FbID), MODE_PRIVATE).edit();
                        editor.putString(getString(R.string.shared_pref_FbID), userId);
                        editor.putString(getString(R.string.shared_pref_username), firstName+" "+lastName);
                        editor.putString(getString(R.string.shared_pref_email), email);
                        editor.putString(getString(R.string.shared_pref_coverpic), coverpic);
                        editor.commit();
                        registerUserWithDB();

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


                }
            });
            Bundle parameters = new Bundle();
            parameters.putString("fields", "id, first_name, last_name, email, cover");
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

    public boolean isLoggedIn() {
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        return accessToken != null;
    }


    public void setFinishLoadingListener(
            FinishLoadingListener finishLoadingListener) {
        this.finishLoadingListener = finishLoadingListener;
    }

    public interface FinishLoadingListener {
        void onLoadingFinish();
    }

    private void getFriends() { GraphRequest request= GraphRequest.newMyFriendsRequest(AccessToken.getCurrentAccessToken(), new GraphRequest.GraphJSONArrayCallback() {

        @Override
        public void onCompleted(JSONArray objects, GraphResponse response) {
            // TODO Auto-generated method stub
            try
            {
                JSONArray raw = response.getJSONObject().getJSONArray("data");
                for(int x=0;x<objects.length();x++){
                    frindsIDs = raw.getJSONObject(x).getString("id");
                    frindsIDs = frindsIDs+",";
                }
                SharedPreferences.Editor editor3 = getSharedPreferences(friendsIDs, MODE_PRIVATE).edit();
                editor3.putString(friendsIDs, frindsIDs);
                editor3.commit();

            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    });
        Bundle parameters = new Bundle();
        parameters.putString("fields", "id,name,installed");
        request.setParameters(parameters);
        request.executeAsync();

    }

    private void registerUserWithDB(){
        Boolean isCritic = false;
        startActivity(intent);
        finish();

        SharedPreferences prefs = getSharedPreferences(getString(R.string.user_tag), MODE_PRIVATE);
        if(prefs.getString(getString(R.string.shared_pref_FbID), null) == "IsCritic"){
            isCritic = true;
        }

        JSONArray jsonArray = new JSONArray();
        JSONObject jsonObject = new JSONObject();

        try{
            jsonObject.put("FirstName",firstName);
            jsonObject.put("LastName",lastName);
            jsonObject.put("EmailID", email);
            jsonObject.put("IsCritic", isCritic);
            jsonObject.put("FbID",userId);
            jsonArray.put(jsonObject);

        }catch(Exception e){

        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, "http://jaffareviews.com/api/movie/AddUser", jsonObject,
                new Response.Listener<JSONObject>(){
                    @Override
                    public void onResponse(JSONObject response) {
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

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(jsonObjectRequest);
    }
}
