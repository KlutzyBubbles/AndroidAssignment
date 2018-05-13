package com.klutzybubbles.assignment1.activities;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Window;

import com.klutzybubbles.assignment1.interfaces.OnNavigationClickListener;

import java.util.ArrayList;
import java.util.List;

public class SplashScreen extends FragmentActivity implements OnNavigationClickListener {

    public static final int SETTINGS = 0, MAIN_MENU = 1, SCOREBOARD = 2;

    private ViewPager pager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_splash_screen);
        this.pager = findViewById(R.id.main_pager);
        List<Fragment> frags = new ArrayList<>();
        frags.add(SplashScreen.SETTINGS, new GameSettingsHolder());
        frags.add(SplashScreen.MAIN_MENU, new MainMenu());
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
