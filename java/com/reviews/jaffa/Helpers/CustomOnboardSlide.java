package com.reviews.jaffa.Helpers;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.reviews.jaffa.R;

import agency.tango.materialintroscreen.SlideFragment;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by gautham on 6/8/17.
 */

public class CustomOnboardSlide extends SlideFragment {

    RadioGroup group;
    RadioButton critic,fan;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.custom_onboard_slide, container, false);

        group = (RadioGroup) view.findViewById(R.id.RadioGroup);
        critic = (RadioButton) view.findViewById(R.id.critic);
        fan = (RadioButton) view.findViewById(R.id.fan);
        return view;
    }

    @Override
    public int backgroundColor() {
        return R.color.custom_slide_background;
    }

    @Override
    public int buttonsColor() {
        return R.color.custom_slide_buttons;
    }

    @Override
    public boolean canMoveFurther() {
        SharedPreferences.Editor editor = getActivity().getSharedPreferences(getString(R.string.user_tag), MODE_PRIVATE).edit();
        if(critic.isChecked()){
            editor.putString(getString(R.string.user_tag), "IsCritic");
            editor.commit();
            return true;
        }else if(fan.isChecked()){
            editor.putString(getString(R.string.user_tag), "IsFan");
            editor.commit();
            return true;
        }else{
            return false;
        }
    }

    @Override
    public String cantMoveFurtherErrorMessage() {
        return getString(R.string.error_message);
    }

}
