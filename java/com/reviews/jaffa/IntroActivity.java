package com.reviews.jaffa;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.FloatRange;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Toast;

import com.reviews.jaffa.Helpers.CustomOnboardSlide;

import agency.tango.materialintroscreen.MaterialIntroActivity;
import agency.tango.materialintroscreen.SlideFragmentBuilder;
import agency.tango.materialintroscreen.animations.IViewTranslation;

/**
 * Created by gautham on 6/8/17.
 */

public class IntroActivity extends MaterialIntroActivity {
    private String hasSeenTutorial = "hasseentutorial";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        enableLastSlideAlphaExitTransition(true);

        getBackButtonTranslationWrapper()
                .setEnterTranslation(new IViewTranslation() {
                    @Override
                    public void translate(View view, @FloatRange(from = 0, to = 1.0) float percentage) {
                        view.setAlpha(percentage);
                    }
                });

        addSlide(new SlideFragmentBuilder()
                .backgroundColor(R.color.first_slide_background)
                .buttonsColor(R.color.first_slide_buttons)
                .image(R.drawable.img_office)
                .title("Organize your time with us")
                .description("Would you try?")
                .build());

        addSlide(new SlideFragmentBuilder()
                .backgroundColor(R.color.second_slide_background)
                .buttonsColor(R.color.second_slide_buttons)
                .title("Want more?")
                .description("Go on")
                .build());

        addSlide(new CustomOnboardSlide());

        addSlide(new SlideFragmentBuilder()
                .backgroundColor(R.color.fourth_slide_background)
                .buttonsColor(R.color.fourth_slide_buttons)
                .title("That's it")
                .description("Login with Facebook to continue")
                .build());
    }

    @Override
    public void onFinish() {
        SharedPreferences.Editor editor2 = getSharedPreferences(hasSeenTutorial, MODE_PRIVATE).edit();
        editor2.putBoolean(hasSeenTutorial, true);
        editor2.commit();
        super.onFinish();
    }
}
