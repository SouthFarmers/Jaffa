package com.reviews.jaffa;

import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.reviews.jaffa.Adapters.GridViewAdapter;
import com.reviews.jaffa.Fragments.MainGridFragment;
import com.reviews.jaffa.Fragments.MovieDetailFragment;
import com.reviews.jaffa.Fragments.SettingsFragment;
import com.reviews.jaffa.Fragments.SocialShareFragment;
import com.reviews.jaffa.Fragments.UserProfileFragment;
import com.reviews.jaffa.Helpers.ImageHelper;
import com.reviews.jaffa.Volley.VolleySingleton;

//import static com.reviews.jaffa.R.id.gridView;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        MainGridFragment.OnMainGridFragmentListener,
        MovieDetailFragment.OnMovieDetailFragmentListener,
        UserProfileFragment.OnUserprofileFragmentListener,
        SocialShareFragment.OnSocialShareFragmentListener,
        SettingsFragment.OnSettingsFragmentListener{

    private static final String TAG = MainActivity.class.getSimpleName();
    private Toolbar toolbar;
    private ProgressBar mProgressBar;
    private GridViewAdapter mGridAdapter;
    private ImageView userpic;
    private TextView username;
    public static ActionBarDrawerToggle toggle;

    public Toolbar getToolbar() {
        return toolbar;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = (Toolbar) findViewById(R.id.toolbar);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View header=navigationView.getHeaderView(0);
        userpic = (ImageView)  header.findViewById(R.id.nav_userpic);
        username = (TextView)  header.findViewById(R.id.nav_username);

        SharedPreferences sharedPrefs = getSharedPreferences(getString(R.string.shared_pref_FbID), MODE_PRIVATE);
        if(sharedPrefs.contains(getString(R.string.shared_pref_FbID))){

            username.setText(sharedPrefs.getString(getString(R.string.shared_pref_username), null));
            setImage(sharedPrefs.getString(getString(R.string.shared_pref_FbID), null));

        }

        launchMainGridFragment();

    }


    @Override
    public void onPropertyClick(String title) {
        launchPropertyDetailFragment(title);
    }

    @Override
    public void onSettingsSave() {
        launchMainGridFragment();
    }

    public void disableCollapse() {}

    public void enableCollapse() {}

    private void launchMainGridFragment() {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.mainlist_fragment, MainGridFragment.newInstance());
        ft.addToBackStack(null);
        ft.commit();
    }

    private void launchPropertyDetailFragment(String title) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.mainlist_fragment, MovieDetailFragment.newInstance(title));
        ft.addToBackStack(null);
        ft.commit();
    }

    private void launchUserProfileFragment() {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.mainlist_fragment, UserProfileFragment.newInstance());
        ft.addToBackStack(null);
        ft.commit();
    }

    private void launchSocailSharingFragment() {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.mainlist_fragment, SocialShareFragment.newInstance());
        ft.addToBackStack(null);
        ft.commit();
        }


    private void launchSettingsFragment() {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.mainlist_fragment, SettingsFragment.newInstance());
        ft.addToBackStack(null);
        ft.commit();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    private void syncFrags() {
        android.support.v4.app.Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.mainlist_fragment);
        if (fragment instanceof MainGridFragment) {
        } else if (fragment instanceof MovieDetailFragment) {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_profile) {
            launchUserProfileFragment();
        } else if (id == R.id.nav_manage) {
            launchSettingsFragment();
        }  else if (id == R.id.nav_share) {
            launchSocailSharingFragment();
        } else if (id == R.id.nav_send) {
            Toast.makeText(this, "This is will be redirected to android play store!",
                    Toast.LENGTH_LONG).show();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void setImage(String id){
        ImageRequest imgRequest = new ImageRequest("https://graph.facebook.com/" + id + "/picture?width=500&height=500",
                new Response.Listener<Bitmap>() {
                    @Override
                    public void onResponse(Bitmap response) {
                        userpic.setImageBitmap(ImageHelper.getRoundedCornerBitmap(MainActivity.this, response, 250, 200, 200, false, false, false, false));
                    }
                }, 0, 0, ImageView.ScaleType.FIT_XY, Bitmap.Config.ARGB_8888, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                userpic.setBackgroundColor(Color.parseColor("#ff0000"));
                error.printStackTrace();
            }
        });
        VolleySingleton.getInstance().addToRequestQueue(imgRequest);
    }

}
