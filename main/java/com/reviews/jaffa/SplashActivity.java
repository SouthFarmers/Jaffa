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

import com.codemybrainsout.onboarder.AhoyOnboarderActivity;
import com.codemybrainsout.onboarder.AhoyOnboarderCard;
import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.LoggingBehavior;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.github.glomadrian.roadrunner.DeterminateRoadRunner;
import com.reviews.jaffa.Fragments.UserProfileFragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import static java.security.AccessController.getContext;

/**
 * Created by gautham on 6/2/17.
 */

public class SplashActivity extends AhoyOnboarderActivity {

    private String firstName,lastName;
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
                AhoyOnboarderCard ahoyOnboarderCard1 = new AhoyOnboarderCard("Movie Reviews", "View reviews for all new releases.", R.drawable.backpack);
                AhoyOnboarderCard ahoyOnboarderCard2 = new AhoyOnboarderCard("Reviews From Friends", "Check out reviews from your facebook friends and critics you follow.", R.drawable.chalk);
                AhoyOnboarderCard ahoyOnboarderCard3 = new AhoyOnboarderCard("Add Review", "Sign up as a critic or member to add your reviews!.", R.drawable.chat);

                ahoyOnboarderCard1.setBackgroundColor(R.color.black_transparent);
                ahoyOnboarderCard2.setBackgroundColor(R.color.black_transparent);
                ahoyOnboarderCard3.setBackgroundColor(R.color.black_transparent);

                final List<AhoyOnboarderCard> pages = new ArrayList<>();

                pages.add(ahoyOnboarderCard1);
                pages.add(ahoyOnboarderCard2);
                pages.add(ahoyOnboarderCard3);

                for (AhoyOnboarderCard page : pages) {
                    page.setTitleColor(R.color.white);
                    page.setDescriptionColor(R.color.grey_200);
                    page.setTitleTextSize(dpToPixels(12, this));
                    page.setDescriptionTextSize(dpToPixels(8, this));
                }

                setFinishButtonTitle("Get Started");
                showNavigationControls(true);
                setGradientBackground();

                //set the button style you created
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    setFinishButtonDrawableStyle(ContextCompat.getDrawable(this, R.drawable.rounded_button));
                }

                Typeface face = Typeface.createFromAsset(getAssets(), "fonts/Roboto-Light.ttf");
                setFont(face);
                setOnboardPages(pages);
            }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onFinishButtonPressed() {
        tryLogin();
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
            startActivity(intent);
            finish();
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
                        getFriends();
                        SharedPreferences.Editor editor = getSharedPreferences(getString(R.string.shared_pref_FbID), MODE_PRIVATE).edit();
                        editor.putString(getString(R.string.shared_pref_FbID), userId);
                        editor.putString(getString(R.string.shared_pref_username), firstName+" "+lastName);
                        editor.commit();

                        SharedPreferences.Editor editor2 = getSharedPreferences(hasSeenTutorial, MODE_PRIVATE).edit();
                        editor2.putBoolean(hasSeenTutorial, true);
                        editor2.commit();


                    } catch (JSONException e) {
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
}
