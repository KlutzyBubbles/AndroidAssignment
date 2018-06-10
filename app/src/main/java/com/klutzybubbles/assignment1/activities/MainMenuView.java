package com.klutzybubbles.assignment1.activities;

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
import com.klutzybubbles.assignment1.interfaces.OnNavigationClickListener;

public class MainMenuView extends android.support.v4.app.Fragment {

    private View view;

    @Override
    public View onCreateView(@NonNull LayoutInflater i, ViewGroup g, Bundle b) {
        if (this.view == null)
            this.view = i.inflate(R.layout.activity_main_menu, g, false);
        return this.view;
    }

    @Override
    public void onResume() {
        View view = this.view.findViewById(R.id.fade_foreground);
        view.clearAnimation();
        //view.setAlpha(1.0F);
        view.animate().alpha(0.0F).setDuration(1000L).setListener(null);
        super.onResume();
    }

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
            high.setOnClickListener(v -> l.onClick(SplashScreen.SCOREBOARD));
            Button settings = view.findViewById(R.id.button_settings);
            settings.setOnClickListener(v -> l.onClick(SplashScreen.SETTINGS));

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
