package com.klutzybubbles.threeinarow.interfaces;

/**
 * <h1>OnNavigationClickListener.java</h1>
 * Class used as a listener interface for navigating the Splash Screen
 *
 * @author Lee Tzilantonis
 * @version 1.0.0
 * @since 10/6/2018
 */
public interface OnNavigationClickListener {

    /**
     * Used to request a ViewPager page change
     *
     * @param to - The position to change to
     */
    void requestChange(int to);

}
