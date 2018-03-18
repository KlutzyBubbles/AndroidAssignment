package com.klutzybubbles.assignment1.logic;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.view.View;

import com.klutzybubbles.assignment1.activities.R;

/**
 * Created by KlutzyBubbles on 8/03/2018.
 */

public class GameItem extends View {

    public static int COLOR_A = Color.RED;
    public static int COLOR_B = Color.BLUE;
    public static int BLANK = Color.GRAY;

    private int state = 0;

    public GameItem(Context context) {
        super(context);
    }

    public void setState(int state) {
        if (state < 0 || state > 2 || this.state > 0)
            return;
        this.state = state;
    }

    public void reDesign() {
        SharedPreferences s = getContext().getSharedPreferences(super.getContext().getString(R.string.shared_preferences), Context.MODE_PRIVATE);
        int a = s.getInt("color_a", -1);
        int b = s.getInt("color_b", -1);
        int blank = s.getInt("blank", -1);
        GameItem.COLOR_A = a == -1 ? GameItem.COLOR_A : a;
        GameItem.COLOR_B = b == -1 ? GameItem.COLOR_B : b;
        GameItem.BLANK = blank == -1 ? GameItem.BLANK : blank;
    }

}
