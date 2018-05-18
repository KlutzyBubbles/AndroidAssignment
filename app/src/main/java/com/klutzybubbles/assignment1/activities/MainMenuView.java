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

    private View[] targets = new View[4];
    private String[][] text = new String[4][2];

    @Override
    public View onCreateView(@NonNull LayoutInflater i, ViewGroup g, Bundle b) {
        if (this.view == null)
            this.view = i.inflate(R.layout.activity_main_menu, g, false);
        return this.view;
    }

    /*
    @Override
    public void onResume() {
        View view = this.view.findViewById(R.id.fade_foreground);
        view.clearAnimation();
        //view.setAlpha(1.0F);
        view.animate().alpha(0.0F).setDuration(1000L).setListener(null);
        super.onResume();
    }
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
            //Fade fadeOut = new Fade(Fade.MODE_OUT);
            //fadeOut.setDuration(1000);
            //Fade fadeIn = new Fade(Fade.MODE_IN);
            //fadeIn.setDuration(1000);
            //this.setEnterTransition(fadeIn);
            //this.setExitTransition(fadeOut);
            //a.getWindow().setEnterTransition(fadeIn);
            //a.getWindow().setExitTransition(fadeOut);
            final View fade = this.view.findViewById(R.id.fade_foreground);
            //fade.clearAnimation();
            //view.setAlpha(1.0F);
            //fade.animate().alpha(0.0F).setDuration(1000L).setListener(null);
            game.setOnClickListener(v -> {
                fade.animate().alpha(1.0F).setDuration(500L).setListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        Intent i = new Intent(view.getContext(), GameView.class);
                        ActivityOptionsCompat o = ActivityOptionsCompat.makeSceneTransitionAnimation(a, game, "play");
                        animation.cancel();
                        a.startActivity(i, o.toBundle());
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {

                    }

                    @Override
                    public void onAnimationRepeat(Animator animation) {

                    }
                });
                /*
                AlphaAnimation alpha = new AlphaAnimation(1.0F, 0.0F);
                alpha.setDuration(1000L);
                setExitTransition(alpha);
                alpha.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        Intent i = new Intent(view.getContext(), GameView.class);
                        ActivityOptionsCompat o = ActivityOptionsCompat.makeSceneTransitionAnimation(a, game, "play");
                        a.startActivity(i, o.toBundle());
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });
                view.findViewById(R.id.fade_foreground).startAnimation(alpha);
                //Intent i = new Intent(view.getContext(), GameView.class);
                //ActivityOptionsCompat o = ActivityOptionsCompat.makeSceneTransitionAnimation(a, game, "play");
                //startActivity(i, o.toBundle());
                */
            });
            Button high = view.findViewById(R.id.button_high_scores);
            high.setOnClickListener(v -> l.onClick(SplashScreen.SCOREBOARD));
            Button settings = view.findViewById(R.id.button_settings);
            settings.setOnClickListener(v -> l.onClick(SplashScreen.SETTINGS));

            ImageButton help = view.findViewById(R.id.button_help);

            targets[0] = game;
            targets[1] = settings;
            targets[2] = high;
            targets[3] = help;

            text[0][0] = "Play";
            text[0][1] = "This is the play button, it will redirect to the game screen";
            text[1][0] = "Settings";
            text[1][1] = "Click or swipe to the right to access game settings such as colors or grid size.";
            text[2][0] = "Scoreboard";
            text[2][1] = "Click or swipe to the left to show your personal high scores";
            text[3][0] = "Help";
            text[3][1] = "Now that you are familiar with the main menu, feel free to play or look out for this icon for help";

            help.setOnClickListener(v -> {
                TapTargetSequence seq = new TapTargetSequence(this.getActivity());
                for (int i = 0; i < targets.length; i++)
                    seq.target(TapTarget.forView(targets[i], text[i][0], text[i][1])
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
