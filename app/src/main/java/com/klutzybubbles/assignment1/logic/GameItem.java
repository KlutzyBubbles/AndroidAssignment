package com.klutzybubbles.assignment1.logic;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.support.v7.widget.AppCompatTextView;
import android.view.View;
import android.widget.GridView;
import android.widget.TextView;

import com.klutzybubbles.assignment1.activities.GameView;
import com.klutzybubbles.assignment1.activities.R;

/**
 * Created by KlutzyBubbles on 8/03/2018.
 */

public class GameItem extends View {

    public static final int[] COLORS = {Color.GRAY, Color.RED, Color.BLUE, Color.WHITE};

    private GradientDrawable background = new GradientDrawable();
    private int state = 0;

    public GameItem(Context context) {
        super(context);
        //super.setText("Test");
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN)
            //noinspection deprecation
            super.setBackgroundDrawable(this.background);
        else
            super.setBackground(this.background);
        this.update();
    }

    public void setState(int state) {
        if (state < 0 || state > 2 || this.state > 0)
            return;
        this.state = state;
        this.update();
    }

    public int getState() {
        return this.state;
    }

    public boolean isClicked() {
        return this.state > 0;
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

    @Override
    public String toString() {
        return "[State: " + this.state + "]";
    }

}
