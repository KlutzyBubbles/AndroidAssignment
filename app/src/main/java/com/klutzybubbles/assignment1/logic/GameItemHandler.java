package com.klutzybubbles.assignment1.logic;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;

import com.klutzybubbles.assignment1.activities.GameView;
import com.klutzybubbles.assignment1.activities.R;

import java.util.Random;

/**
 * Created by ltzil on 18/03/2018.
 */

public class GameItemHandler extends BaseAdapter implements GridView.OnItemClickListener {

    public static final int MAX_WIDTH = 5;
    public static final int MAX_HEIGHT = 5;

    private final GameItem[] items;

    private final int width, height;
    private int count = 0;
    private final int[] blocks;

    private final Context gridContext;

    public GameItemHandler(Context context, int width, int height) {
        super();
        if (width > GameItemHandler.MAX_WIDTH || height > GameItemHandler.MAX_HEIGHT || context == null)
            throw new IllegalArgumentException("Grid size can not be bigger than the MAX");
        this.width = width;
        this.height = height;
        this.gridContext = context;
        int temp = this.width * this.height;
        this.items = new GameItem[temp];
        int[] normal = new int[temp];
        for (int i = 0; i < temp; i++)
            this.items[i] = new GameItem(this.gridContext);
        temp = 2;
        for (int i = 0; i < normal.length; i++) {
            normal[i] = temp;
            temp = temp == 2 ? 1 : 2;
        }
        int index;
        Random r = new Random();
        for (int i = normal.length - 1; i > 0; i--) {
            index = r.nextInt(i + 1);
            temp = normal[index];
            normal[index] = normal[i];
            normal[i] = temp;
        }
        this.blocks = normal;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        GameItem g = null;
        if (position > this.items.length) {
            for (GameItem t : this.items) {
                if (t == null)
                    continue;
                if (t.getId() == view.getId()) {
                    g = t;
                    break;
                }
            }
            if (g == null)
                return;
        } else
            g = this.items[position];
        if (g.clicked())
            return;
        try {
            g.setState(this.blocks[count]);
        } catch (ArrayIndexOutOfBoundsException e) {
            System.err.println("Ouch");
            return;
        }
        count++;
    }

    public void refreshSettings(GridView parent) {
        SharedPreferences s = this.gridContext.getSharedPreferences(this.gridContext.getString(R.string.shared_preferences), Context.MODE_PRIVATE);
        GameItem.COLORS[0] = s.getInt("blank", GameItem.COLORS[0]);
        GameItem.COLORS[1] = s.getInt("color_a", GameItem.COLORS[1]);
        GameItem.COLORS[2] = s.getInt("color_b", GameItem.COLORS[2]);
        GameItem.COLORS[3] = s.getInt("color_border", GameItem.COLORS[3]);
        for (int i = 0; i < this.items.length; i++)
            this.items[i].setRelativeTo(parent);
    }

    @Override
    public int getCount() {
        return this.items.length;
    }

    @Override
    public Object getItem(int position) {
        return this.items[position];
    }

    @Override
    public long getItemId(int position) {
        return this.items[position] == null ? null : this.items[position].getId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        System.out.println(this.items[position].toString());
        this.items[position].setRelativeTo((GridView) parent);
        return this.items[position];
    }
}
