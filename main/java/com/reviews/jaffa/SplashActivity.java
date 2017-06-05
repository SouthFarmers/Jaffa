package com.reviews.jaffa;

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
import android.widget.Button;
import android.widget.EditText;
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
import com.reviews.jaffa.Fragments.UserProfileFragment;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

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
    private final int SPLASH_DISPLAY_LENGTH = 3000;

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

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        if(isLoggedIn()){
                            startActivity(intent);
                        }else{
                            tryLogin();
                        }
                    }
                }, SPLASH_DISPLAY_LENGTH);

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
                    //page.setIconLayoutParams(width, height, marginTop, marginLeft, marginRight, marginBottom);
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
        loginButton.setReadPermissions("public_profile");
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
                    Log.e(TAG,object.toString());
                    Log.e(TAG,response.toString());

                    try {
                        //AccessToken token = AccessToken.getCurrentAccessToken();
                        userId = object.getString("id");
                        if(object.has("first_name"))
                            firstName = object.getString("first_name");
                        if(object.has("last_name"))
                            lastName = object.getString("last_name");
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
            parameters.putString("fields", "id, first_name, last_name, user_friends");
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
}
