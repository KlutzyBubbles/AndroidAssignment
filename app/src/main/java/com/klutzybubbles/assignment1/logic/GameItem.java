package com.klutzybubbles.assignment1.logic;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Handler;
import android.view.View;
import android.widget.GridView;

import java.io.Serializable;

/**
 * Created by KlutzyBubbles on 8/03/2018.
 */

public class GameItem extends View implements Serializable {

    public static final int[] COLORS = {Color.GRAY, Color.RED, Color.BLUE, Color.WHITE, Color.GRAY};

    private GradientDrawable background = new GradientDrawable();
    private int state = 0;

    public GameItem(Context context) {
        super(context);
        //super.setText("Test");
        super.setBackground(this.background);
        this.update();
    }

    public void setState(int state) {
        if (state < 0 || state > 2 || this.state > 0)
            return;
        this.state = state;
        this.update();
    }

    public void setStateOverride(int state) {
        if (state < 0 || state > 2)
            return;
        this.state = state;
        this.update();
    }

    public int getState() {
        return this.state;
    }

    public boolean canClick() {
        return this.state <= 0;
    }

    public void update() {
        this.background.setColor(GameItem.COLORS[this.state]);
        this.background.setStroke(1, Color.DKGRAY);
    }

    public void setRelativeTo(GridView parent) {
        int width = parent.getColumnWidth();
        super.setLayoutParams(new GridView.LayoutParams(width, width));
        super.setPadding(0, 0, 0, 0);
    }

    public void highlight() {
        final AnimationDrawable ad = new AnimationDrawable();
        Handler h = new Handler();

        ad.addFrame(new ColorDrawable(GameItem.COLORS[this.getState()]), 400);
        ad.addFrame(new ColorDrawable(GameItem.COLORS[3]), 400);
        ad.setOneShot(false);

        this.setBackground(ad);
        h.postDelayed(new Runnable() {
            @Override
            public void run() {
                ad.start();
            }
        }, 100);
    }

    @Override
    public String toString() {
        return "[State: " + this.state + "]";
    }

}
