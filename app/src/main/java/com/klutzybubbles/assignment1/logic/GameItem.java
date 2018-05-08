package com.klutzybubbles.assignment1.logic;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.GridView;

/**
 * <h1>GameItem.class</h1>
 * Class used to store the state and GradientDrawable background of the item / block
 *
 * @author Lee Tzilantonis
 * @version 1.0.1
 * @since 5/5/2018
 */
public class GameItem extends View {

    /**
     * The default and global colors used to draw the GameItem background
     */
    public static final int[] COLORS = {Color.GRAY, Color.RED, Color.BLUE, Color.WHITE, Color.GRAY};

    /**
     * The background of the GameItem
     */
    private final GradientDrawable background = new GradientDrawable();

    /**
     * The current state of the GameItem
     * <p></p>
     * There are only 3 states:
     * 0 - Empty or un-clicked
     * 1 - Clicked with color set to A
     * 2 - Clicked with color set to B
     */
    private int state = 0;

    /**
     * Instantiates the GameItem with a default state (0)
     *
     * @param context - The context to be parsed to the super constructor
     */
    public GameItem(Context context) {
        super(context);
        Log.v("GameItem:CONSTRUCT", "call");
        super.setBackground(this.background);
        this.update();
    }

    /**
     * Sets the current state of the GameItem but only if the GameItem hasn't already been changed
     *
     * @param state - The new state of the GameItem, must be between 0 and 2
     */
    public void setState(int state) {
        Log.v("GameItem:setState", "call");
        if (this.state > 0)
            return;
        Log.d("GameItem:setState", "State can change");
        this.setStateOverride(state);
    }

    /**
     * Sets the current state of the GameItem regardless of whether or not the GameItem has been clicked
     *
     * @param state - The new state of the GameItem, must be between 0 and 2
     */
    public void setStateOverride(int state) {
        Log.v("GameItem:setStateO", "call");
        Log.d("GameItem:setStateO", "State parsed - " + state);
        if (state < 0 || state > 2)
            return;
        Log.d("GameItem:setStateO", "State within boundaries");
        this.state = state;
        this.update();
    }

    /**
     * Gets the current state of the GameItem
     *
     * @return - The current state of the GameItem, should only ever be between 0 and 2
     */
    public int getState() {
        Log.v("GameItem:getState  ", "call");
        Log.d("GameItem:getState", "State - " + this.state);
        return this.state;
    }

    /**
     * Checks whether or not the GameItem can be clicked (have it's state changed) without having
     * to override
     *
     * @return - Whether or not the GameItem can be clicked
     */
    public boolean canClick() {
        Log.v("GameItem:canClick", "call");
        Log.d("GameItem:canClick", "Result - " + (this.state <= 0));
        return this.state <= 0;
    }

    /**
     * Updates the GameItem to reflect it's current state using the static Color's
     */
    public void update() {
        Log.v("GameItem:update", "call");
        Log.d("GameItem:update", "Background color - " + GameItem.COLORS[this.state]);
        this.background.setColor(GameItem.COLORS[this.state]);
        Log.d("GameItem:update", "Border color - " + GameItem.COLORS[3]);
        this.background.setStroke(1, GameItem.COLORS[3]);
    }

    /**
     * Sets the GameItem's size relative to the GridView's column width
     *
     * @param parent - The GridView to get the column width from
     */
    public void setRelativeTo(GridView parent) {
        Log.v("GameItem:setRelativeTo", "call");
        int width = parent.getColumnWidth();
        Log.d("GameItem:setRelativeTo", "Column width - " + width);
        super.setLayoutParams(new GridView.LayoutParams(width, width));
        super.setPadding(0, 0, 0, 0);
    }

    /**
     * Highlights the GameItem by having it flash the color set in the static Color's
     * <p></p>
     * NOTE: The highlight function cannot be un-done without disposing of the GameItem
     */
    public void highlight() {
        Log.v("GameItem:highlight", "call");
        final AnimationDrawable ad = new AnimationDrawable();
        ad.addFrame(this.background, 400);
        GradientDrawable highlight = new GradientDrawable();
        highlight.setColor(GameItem.COLORS[4]);
        highlight.setStroke(1, GameItem.COLORS[3]);
        ad.addFrame(highlight, 400);
        ad.setOneShot(false);
        this.setBackground(ad);
        (new Handler()).postDelayed(ad::start, 100);
    }

    /**
     * Returns a String representation of the GameItem for debugging purposes
     *
     * @return - String representation of the GameItem Object
     */
    @Override
    public String toString() {
        Log.v("GameItem:toString", "call");
        return "[State: " + this.state + "]";
    }

}
