package com.reviews.jaffa.Fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;

import com.reviews.jaffa.Helpers.Shareable;
import com.reviews.jaffa.MainActivity;
import com.reviews.jaffa.R;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by gautham on 6/9/17.
 */

public class SettingsFragment extends Fragment  {

    private SettingsFragment.OnSettingsFragmentListener mListener;
    RadioButton critic,fan;

    String message = "Visit Jaffa reviews!";
    String url = "http://jaffareviews.com/";
    Uri pic = Uri.parse("http://jaffareviews.com/Images/Movies/Manam/Movie.jpg");

    public static SettingsFragment newInstance() {
        Bundle args = new Bundle();
        SettingsFragment fragment = new SettingsFragment();
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().setTitle(getString(R.string.app_name));
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onCreateOptionsMenu(
            Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_save, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle item selection
        switch (item.getItemId()) {
            case R.id.settings_save:
                SharedPreferences.Editor editor = getActivity().getSharedPreferences(getString(R.string.user_tag), MODE_PRIVATE).edit();
                if(critic.isChecked()){
                    editor.putString(getString(R.string.user_tag), "IsCritic");
                    editor.commit();
                }else if(fan.isChecked()){
                    editor.putString(getString(R.string.user_tag), "IsFan");
                    editor.commit();
                }
                mListener.onSettingsSave();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof SettingsFragment.OnSettingsFragmentListener) {
            mListener = (SettingsFragment.OnSettingsFragmentListener) context;
        }else {
            throw new RuntimeException(context.toString()
                    + " must implement OnSettingsFragmentFragmentListener");
        }
    }

    public interface OnSettingsFragmentListener {
        void onSettingsSave();
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.menu_settings, container, false);
        critic = (RadioButton) rootView.findViewById(R.id.settings_critic);
        fan = (RadioButton) rootView.findViewById(R.id.settings_fan);
        SharedPreferences prefs = getActivity().getSharedPreferences(getString(R.string.user_tag), MODE_PRIVATE);
        String check = prefs.getString(getString(R.string.user_tag), null);
        if( check == "IsCritic"){
            critic.setChecked(true);
        }else{
            fan.setChecked(true);
        }

        return rootView;
    }

}


