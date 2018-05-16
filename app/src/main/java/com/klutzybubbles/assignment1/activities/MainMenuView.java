package com.klutzybubbles.assignment1.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ActivityOptionsCompat;
import android.transition.Fade;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RelativeLayout;

import com.github.amlcurran.showcaseview.ShowcaseView;
import com.github.amlcurran.showcaseview.SimpleShowcaseEventListener;
import com.github.amlcurran.showcaseview.targets.ViewTarget;
import com.klutzybubbles.assignment1.interfaces.OnNavigationClickListener;

public class MainMenuView extends android.support.v4.app.Fragment {

    private View view;

    private int currentTut = 0;
    private boolean isRunning = false;
    private View[] targets = new View[4];
    private String[][] text = new String[4][2];

    @Override
    public View onCreateView(LayoutInflater i, ViewGroup g, Bundle b) {
        if (this.view == null)
            this.view = i.inflate(R.layout.activity_main_menu, g, false);
        return this.view;
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
        /**
        FragmentActivity activity = this.getActivity();
        Button b = activity.findViewById(R.id.button_game);
        b.setOnClickListener(v -> {
        Intent i = new Intent(activity.getApplicationContext(), GameView.class);
        startActivity(i);
        });
        b = activity.findViewById(R.id.button_high_scores);
        b.setOnClickListener(v -> l.onClick(SplashScreen.SCOREBOARD));
        b = activity.findViewById(R.id.button_settings);
        b.setOnClickListener(v -> l.onClick(SplashScreen.SETTINGS));
        b.setOnClickListener(v -> {
        Intent i = new Intent(activity.getApplicationContext(), GameSettings.class);
        startActivity(i);
        });
        b = activity.findViewById(R.id.button_help);
        b.setOnClickListener(v -> {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.help_doc)));
        startActivity(browserIntent);
        });
         */
        if (this.view != null) {
            final ImageButton game = view.findViewById(R.id.button_game);
            final Activity a = this.getActivity();
            Fade fadeOut = new Fade(Fade.MODE_OUT);
            fadeOut.setDuration(1000);
            Fade fadeIn = new Fade(Fade.MODE_IN);
            fadeIn.setDuration(1000);
            this.setEnterTransition(fadeIn);
            this.setExitTransition(fadeOut);
            a.getWindow().setEnterTransition(fadeIn);
            a.getWindow().setExitTransition(fadeOut);
            game.setOnClickListener(v -> {
                Intent i = new Intent(view.getContext(), GameView.class);
                ActivityOptionsCompat o = ActivityOptionsCompat.makeSceneTransitionAnimation(a, game, "play");
                startActivity(i, o.toBundle());
                /*
                Intent i = new Intent(view.getContext(), GameView.class);
                ActivityOptionsCompat o = ActivityOptionsCompat.makeSceneTransitionAnimation(a, b, "play");
                startActivity(i, o.toBundle());
                */
            });
            Button high = view.findViewById(R.id.button_high_scores);
            high.setOnClickListener(v -> l.onClick(SplashScreen.SCOREBOARD));
            Button settings = view.findViewById(R.id.button_settings);
            settings.setOnClickListener(v -> l.onClick(SplashScreen.SETTINGS));

            Button help = view.findViewById(R.id.button_help);

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
                if (!isRunning) {
                    isRunning = true;
                    tutorial();
                }
            });
        }
    }

    private void tutorial() {
        if (this.currentTut >= 4) {
            this.currentTut = 0;
            this.isRunning = false;
        } else if (this.isRunning) {
            ShowcaseView sv = new ShowcaseView.Builder(getActivity())
                    .withMaterialShowcase()
                    .setStyle(this.currentTut == 3 ? R.style.MaterialShowcase_LetsGo : R.style.MaterialShowcase_GotIt)
                    .setTarget(new ViewTarget(this.targets[this.currentTut]))
                    .hideOnTouchOutside()
                    .setContentTitle(this.text[this.currentTut][0])
                    .setContentText(this.text[this.currentTut][1])
                    .blockAllTouches()
                    .setShowcaseEventListener(new SimpleShowcaseEventListener() {
                        @Override
                        public void onShowcaseViewDidHide(ShowcaseView view) {
                            currentTut++;
                            tutorial();
                        }
                    })
                    .build();
            if (this.currentTut == 2) {
                RelativeLayout.LayoutParams lps = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                lps.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
                lps.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
                int margin = ((Number) (getResources().getDisplayMetrics().density * 12)).intValue();
                lps.setMargins(margin, margin, margin, margin);
                sv.setButtonPosition(lps);
            }
            sv.setStyle(this.currentTut == 3 ? R.style.MaterialShowcase_LetsGo : R.style.MaterialShowcase_GotIt);
            sv.setAlpha(0);
            sv.show();
            targets[this.currentTut].bringToFront();
        }
    }

}
