package com.klutzybubbles.threeinarow.activities;

import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.Window;

import com.klutzybubbles.threeinarow.interfaces.OnNavigationClickListener;

import java.util.ArrayList;
import java.util.List;

/**
 * <h1>SplashScreen.java</h1>
 * Class used to hold all fragments that make up the starting or splash screen for the application
 *
 * @author Lee Tzilantonis
 * @version 1.0.0
 * @since 10/6/2018
 * @see FragmentActivity
 * @see OnNavigationClickListener
 */
public class SplashScreen extends FragmentActivity implements OnNavigationClickListener {

    /**
     * Default order for each Fragment, used here if we want to rearrange Fragments fast
     */
    public static final int SETTINGS = 0, MAIN_MENU = 1, SCOREBOARD = 2;

    /**
     * The ViewPager that holds all of the Fragments on the Starting Screen
     */
    private ViewPager pager;

    /**
     * Called when the window's focus has changed. Used to make the application run in fullscreen
     * except when a dialog or important message that requires navigation buttons appears.
     *
     * @param hasFocus - Whether or not the FragmentActivity has the main focus
     */
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

    /**
     * Called when the FragmentActivity is created
     *
     * @param savedInstanceState - The Bundle state parsed to the Activity
     */
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

    /**
     * Called when a child Object or external Object requests that the ViewPager page be changed
     *
     * @param to - The page to change to (should only ever be one of the static variables)
     */
    @Override
    public void requestChange(int to) {
        pager.setCurrentItem(to, true);
    }

    /**
     * <h1>SplashScreen.SplashPagerManager</h1>
     * Class used to manage all Fragments contained within the ViewPager
     *
     * @author Lee Tzilantonis
     * @version 1.0.0
     * @since 10/6/2018
     * @see FragmentStatePagerAdapter
     */
    private class SplashPagerManager extends FragmentStatePagerAdapter {

        /**
         * The list of Fragments to display
         */
        List<Fragment> frags;

        /**
         * Instantiates the SplashPagerManager parsing the FragmentManager to the super constructor
         * and using the list of Fragments as the pages
         *
         * @param fm - FragmentManager to parse to the super constructor
         * @param frags - List of Fragment screens to use as pages
         */
        private SplashPagerManager(FragmentManager fm, List<Fragment> frags) {
            super(fm);
            if (frags != null && frags.size() == 3)
                this.frags = frags;
            else
                throw new IllegalArgumentException("Cannot parse null list");
        }

        /**
         * Gets the item in the specified position. If the position isn't apart of the static
         * variables then the Manager will redirect to the main menu (1) Fragment
         *
         * @param position - The position to switch to
         * @return - The Fragment in the specified position
         */
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

        /**
         * Gets the count of Fragments within the Manager
         *
         * @return - 3
         */
        @Override
        public int getCount() {
            return 3;
        }
    }

}
