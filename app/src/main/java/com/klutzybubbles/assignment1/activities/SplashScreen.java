package com.klutzybubbles.assignment1.activities;

import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.Window;

import com.klutzybubbles.assignment1.interfaces.OnNavigationClickListener;

import java.util.ArrayList;
import java.util.List;

public class SplashScreen extends FragmentActivity implements OnNavigationClickListener {

    public static final int SETTINGS = 0, MAIN_MENU = 1, SCOREBOARD = 2;

    private ViewPager pager;

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        View decorView = getWindow().getDecorView();
        if (hasFocus) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN);
            } else {
                decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
            }
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                decorView.setSystemUiVisibility(
                        View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_splash_screen);
        this.pager = findViewById(R.id.main_pager);
        List<Fragment> frags = new ArrayList<>();
        frags.add(SplashScreen.SETTINGS, new GameSettingsView());
        frags.add(SplashScreen.MAIN_MENU, new MainMenuView());
        frags.add(SplashScreen.SCOREBOARD, new ScoreboardView());
        this.pager.setAdapter(new SplashPagerManager(this.getSupportFragmentManager(), frags));
        pager.setCurrentItem(SplashScreen.MAIN_MENU);
    }

    @Override
    public void onClick(int to) {
        pager.setCurrentItem(to, true);
    }

    private class SplashPagerManager extends FragmentStatePagerAdapter {

        List<Fragment> frags;

        private SplashPagerManager(FragmentManager fm, List<Fragment> frags) {
            super(fm);
            if (frags != null && frags.size() == 3)
                this.frags = frags;
            else
                throw new IllegalArgumentException("Cannot parse null list");
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case SplashScreen.SETTINGS:
                    return this.frags.get(SplashScreen.SETTINGS);
                case SplashScreen.SCOREBOARD:
                    return this.frags.get(SplashScreen.SCOREBOARD);
                default:
                    return this.frags.get(SplashScreen.MAIN_MENU);
            }
        }

        @Override
        public int getCount() {
            return 3;
        }
    }

}
