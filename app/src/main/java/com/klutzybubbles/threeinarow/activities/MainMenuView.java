package com.klutzybubbles.threeinarow.activities;

import android.animation.Animator;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityOptionsCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;

import com.getkeepsafe.taptargetview.TapTarget;
import com.getkeepsafe.taptargetview.TapTargetSequence;
import com.klutzybubbles.threeinarow.interfaces.OnNavigationClickListener;

/**
 * <h1>MainMenuView.java</h1>
 * Class used as a Fragment that contains all the content for the Main Menu Screen
 *
 * @author Lee Tzilantonis
 * @version 1.0.0
 * @since 10/6/2018
 * @see android.support.v4.app.Fragment
 */
public class MainMenuView extends android.support.v4.app.Fragment {

    /**
     * Main view that holds all the content for the Main Menu Screen
     */
    private View view;

    /**
     * Called when the View within the fragment is to be inflated
     *
     * @param i - The LayoutInflater to be used
     * @param g - The ViewGroup the view should be contained within
     * @param b - The Bundle object parsed to the View
     * @return - The created View
     */
    @Override
    public View onCreateView(@NonNull LayoutInflater i, ViewGroup g, Bundle b) {
        if (this.view == null)
            this.view = i.inflate(R.layout.activity_main_menu, g, false);
        return this.view;
    }

    /**
     * Called when the containing Activity is resumed
     */
    @Override
    public void onResume() {
        View view = this.view.findViewById(R.id.fade_foreground);
        view.clearAnimation();
        view.animate().alpha(0.0F).setDuration(1000L).setListener(null);
        super.onResume();
    }

    /**
     * Called when the containing activity has been created, letting us know is is safe to use
     * getActivity(). Due to the static nature of the Main Menu Screen all content is contained
     * within the onActivityCreated(Bundle) method
     *
     * @param savedInstanceState - The Bundle state parsed to the Activity
     */
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (this.getView() == null || this.getActivity() == null)
            return;
        OnNavigationClickListener l;
        try {
            l = (OnNavigationClickListener) this.getActivity();
        } catch (ClassCastException e) {
            return;
        }
        if (this.view != null) {
            final ImageButton game = view.findViewById(R.id.button_game);
            final Activity a = this.getActivity();
            final View fade = this.view.findViewById(R.id.fade_foreground);
            game.setOnClickListener(v -> fade.animate().alpha(1.0F).setDuration(500L).setListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {}

                @Override
                public void onAnimationEnd(Animator animation) {
                    Intent i = new Intent(view.getContext(), GameView.class);
                    ActivityOptionsCompat o = ActivityOptionsCompat.makeSceneTransitionAnimation(a, game, "play");
                    animation.cancel();
                    a.startActivity(i, o.toBundle());
                }

                @Override
                public void onAnimationCancel(Animator animation) {}

                @Override
                public void onAnimationRepeat(Animator animation) {}
            }));
            Button high = view.findViewById(R.id.button_high_scores);
            high.setOnClickListener(v -> l.requestChange(SplashScreen.SCOREBOARD));
            Button settings = view.findViewById(R.id.button_settings);
            settings.setOnClickListener(v -> l.requestChange(SplashScreen.SETTINGS));

            ImageButton help = view.findViewById(R.id.button_help);
            help.setMinimumWidth(24);
            help.setMinimumHeight(24);

            View[] targets = new View[4];

            targets[0] = game;
            targets[1] = settings;
            targets[2] = high;
            targets[3] = help;

            String[] title = this.getResources().getStringArray(R.array.main_menu_help_titles);
            String[] text = this.getResources().getStringArray(R.array.main_menu_help_text);

            help.setOnClickListener(v -> {
                TapTargetSequence seq = new TapTargetSequence(this.getActivity());
                for (int i = 0; i < targets.length; i++)
                    seq.target(TapTarget.forView(targets[i], title[i], text[i])
                            .cancelable(true)
                            .transparentTarget(true)
                            .textColor(R.color.White)
                            .outerCircleColor(R.color.RoyalBlue)
                            .outerCircleAlpha(0.95F)
                            .targetRadius(70)
                            .drawShadow(true)
                            .dimColor(R.color.Black));
                seq.continueOnCancel(true).considerOuterCircleCanceled(true);
                seq.start();
            });
        }
    }
}
