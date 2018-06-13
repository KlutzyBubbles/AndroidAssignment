package com.klutzybubbles.threeinarow.logic;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.widget.FrameLayout;
import android.widget.GridView;

/**
 * <h1>GameItem.class</h1>
 * Class used to store the state and GradientDrawable background of the item / block
 *
 * @author Lee Tzilantonis
 * @version 1.0.2
 * @since 10/6/2018
 */
public class GameItem extends FrameLayout {

    /**
     * The default and global colors used to draw the GameItem background
     */
    public static final int[] COLORS = {Color.GRAY, Color.RED, Color.BLUE, Color.WHITE, Color.GRAY};

    /**
     * The background of the GameItem
     */
    private final GradientDrawable background = new GradientDrawable(), innerBackground = new GradientDrawable();

    /**
     * The current and new state of the GameItem
     * <p></p>
     * There are only 3 states:
     * 0 - Empty or un-clicked
     * 1 - Clicked with color set to A
     * 2 - Clicked with color set to B
     */
    private int currentState = 0, newState = 0;

    /**
     * The inner view used to animate a state change
     */
    private final View inner;

    /**
     * The width of the GameItem strokes
     */
    private static final int stroke = 10;

    /**
     * Instantiates the GameItem with a default state (0)
     *
     * @param context - The context to be parsed to the super constructor
     */
    public GameItem(Context context) {
        super(context);
        Log.v("GameItem:CONSTRUCT", "call");
        inner = new View(context);
        View outer = new View(context);
        FrameLayout.LayoutParams l = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        //l.width = RelativeLayout.LayoutParams.MATCH_PARENT;
        //l.height = RelativeLayout.LayoutParams.MATCH_PARENT;

        inner.setLayoutParams(l);
        inner.setBackground(this.innerBackground);
        outer.setLayoutParams(l);
        outer.setBackground(this.background);

        super.addView(outer);
        super.addView(inner);
        //super.setBackground(this.background);
        this.update();
    }

    /**
     * Sets the current state of the GameItem but only if the GameItem hasn't already been changed
     *
     * @param state - The new state of the GameItem, must be between 0 and 2
     */
    public void setState(int state) {
        Log.v("GameItem:setState", "call");
        if (this.currentState > 0)
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
        this.newState = state;
        this.update();
    }

    /**
     * Gets the current state of the GameItem
     *
     * @return - The current state of the GameItem, should only ever be between 0 and 2
     */
    public int getState() {
        Log.v("GameItem:getState  ", "call");
        Log.d("GameItem:getState", "State - " + this.currentState);
        return this.currentState;
    }

    /**
     * Checks whether or not the GameItem can be clicked (have it's state changed) without having
     * to override
     *
     * @return - Whether or not the GameItem can be clicked
     */
    public boolean canClick() {
        Log.v("GameItem:canClick", "call");
        Log.d("GameItem:canClick", "Result - " + (this.currentState <= 0));
        return this.currentState <= 0;
    }

    /**
     * Updates the GameItem to reflect it's current state using the static Color's
     */
    private void update() {
        Log.v("GameItem:update", "call");
        Log.v("GameItem:update", "Background color OLD - " + GameItem.COLORS[this.currentState]);
        Log.d("GameItem:update", "Background color NEW - " + GameItem.COLORS[this.newState]);
        if (this.currentState == this.newState)
            this.background.setColor(GameItem.COLORS[this.newState]);
        Log.d("GameItem:update", "Border color - " + GameItem.COLORS[3]);
        this.background.setStroke(GameItem.stroke, GameItem.COLORS[3]);
        Animation scale = new ScaleAnimation(0.0F, 1.0F, 0.0F, 1.0F, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        scale.setDuration(100L);
        Animation alpha = new AlphaAnimation(0.0F, 1.0F);
        alpha.setDuration(100L);
        AnimationSet set = new AnimationSet(false);
        set.setFillEnabled(true);
        set.setDuration(100L);
        set.addAnimation(scale);
        set.addAnimation(alpha);
        this.innerBackground.setColor(GameItem.COLORS[this.newState]);
        this.innerBackground.setStroke(GameItem.stroke, GameItem.COLORS[3]);
        this.currentState = newState;
        set.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {}

            @Override
            public void onAnimationEnd(Animation animation) {
                Log.d("GameItem:anim", "End");
                background.setColor(GameItem.COLORS[currentState]);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {}
        });
        this.inner.startAnimation(set);
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
        //inner.setAlpha(0.0F);
    }

    /**
     * Highlights the GameItem by having it flash the color set in the static Color's
     * <p></p>
     * NOTE: The highlight function cannot be un-done without disposing of the GameItem
     */
    public void highlight() {
        Log.v("GameItem:highlight", "call");
        this.background.setColor(GameItem.COLORS[4]);
        Animation alpha = new AlphaAnimation(0.0F, 1.0F);
        alpha.setDuration(400L);
        alpha.setRepeatMode(Animation.REVERSE);
        alpha.setRepeatCount(Animation.INFINITE);
        inner.startAnimation(alpha);
    }

    /**
     * Returns a String representation of the GameItem for debugging purposes
     *
     * @return - String representation of the GameItem Object
     */
    @Override
    public String toString() {
        Log.v("GameItem:toString", "call");
        return "[State: " + this.currentState + "]";
    }

}
