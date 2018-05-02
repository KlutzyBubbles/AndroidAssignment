package com.klutzybubbles.assignment1.logic;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.constraint.ConstraintLayout;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import com.klutzybubbles.assignment1.activities.GameView;
import com.klutzybubbles.assignment1.activities.R;

import java.util.Random;

/**
 * Created by ltzil on 18/03/2018.
 */

public class GameItemHandler extends BaseAdapter implements GridView.OnItemClickListener {

    public static final int MAX_WIDTH = 5, MAX_HEIGHT = 5;
    public static final int MIN_HEIGHT = 3, MIN_WIDTH = 3;

    private final GameItem[] items;

    private final int width, height;
    private int count = 0, start;
    private int[] blocks;

    private boolean gameState = false;
    private boolean paused = false;

    private final Context gridContext;
    private final GameView parent;

    private final GameTimer timer;

    public GameItemHandler(GameView parent, Context context, int width, int height, int start) {
        super();
        Log.d("GIH:CONSTRUCT", "call");
        if (width > GameItemHandler.MAX_WIDTH || height > GameItemHandler.MAX_HEIGHT || context == null)
            throw new IllegalArgumentException("Grid size can not be bigger than the MAX");
        if (width < GameItemHandler.MIN_WIDTH || height < GameItemHandler.MIN_HEIGHT || parent == null)
            throw new IllegalArgumentException("Grid size can not be smaller than the MIN");
        this.width = width;
        this.height = height;
        this.gridContext = context;
        this.parent = parent;
        this.start = start;
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
        this.blocks = normal;
        this.placeStarters();
        int index;
        Random r = new Random();
        for (int i = normal.length - 1; i > count; i--) {
            index = r.nextInt((i - count) + 1) + count;
            temp = normal[index];
            normal[index] = normal[i];
            normal[i] = temp;
        }
        this.blocks = normal;
        this.timer = new GameTimer();
    }

    private void placeStarters() {
        Log.d("GIH:placeStarters", "call");
        if (this.start == 0)
            return;
        int temp = this.start;
        Random r = new Random();
        for (int i = 0; i < temp; i++) {
            this.start--;
            int slot = r.nextInt(this.blocks.length - count);
            int c = 0;
            for (int j = 0; j < slot; j++) {
                boolean cont = false;
                while (!cont) {
                    if (!this.items[c].isClicked()) {
                        if (j == slot - 1)
                            this.items[c].setState(this.blocks[i]);
                        cont = true;
                    }
                    c++;
                }
            }
            this.count++;

        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Log.d("GIH:onItemClick", "call");
        if (!this.gameState)
            return;
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
        } else
            g = this.items[position];
        if (g != null && !g.isClicked()) {
            try {
                int state = this.blocks[count];
                g.setState(state);
                count++;
                this.parent.setNext(this.getNext());
                // top
                if (position >= this.width * 2) {
                    if (this.items[position - this.width].getState() == state && this.items[position - (this.width * 2)].getState() == state) {
                        this.stop(2);
                        this.items[position - this.width].highlight();
                        this.items[position - (this.width * 2)].highlight();
                        this.items[position].highlight();
                        return;
                    }
                }

                // bottom
                if (position <= (this.width * this.height) - (this.width * 2)) {
                    if (this.items[position + this.width].getState() == state && this.items[position + (this.width * 2)].getState() == state) {
                        this.stop(2);
                        this.items[position + this.width].highlight();
                        this.items[position + (this.width * 2)].highlight();
                        this.items[position].highlight();
                        return;
                    }
                }

                // left
                if (position % this.width >= 2) {
                    if (this.items[position - 1].getState() == state && this.items[position - 2].getState() == state) {
                        this.stop(2);
                        this.items[position - 2].highlight();
                        this.items[position - 1].highlight();
                        this.items[position].highlight();
                        return;
                    }
                }

                // right
                if (position % this.width <= this.width - 3) {
                    if (this.items[position + 1].getState() == state && this.items[position + 2].getState() == state) {
                        this.stop(2);
                        this.items[position + 1].highlight();
                        this.items[position + 2].highlight();
                        this.items[position].highlight();
                        return;
                    }
                }

                // middle vert
                if (position >= this.width && position <= (this.width * this.height) - this.width) {
                    if (this.items[position + this.width].getState() == state && this.items[position - this.width].getState() == state) {
                        this.stop(2);
                        this.items[position + this.width].highlight();
                        this.items[position - this.width].highlight();
                        this.items[position].highlight();
                        return;
                    }
                }

                // middle hori
                if (position % this.width >= 1 && position % this.width <= this.width - 2) {
                    if (this.items[position + 1].getState() == state && this.items[position - 1].getState() == state) {
                        this.stop(2);
                        this.items[position + 1].highlight();
                        this.items[position - 1].highlight();
                        this.items[position].highlight();
                        return;
                    }
                }
                if (this.getNext() == 0)
                    this.stop(1);
            } catch (ArrayIndexOutOfBoundsException e) {
                System.err.println("Ouch");
            }
        }
    }

    public void refreshSettings(GridView parent) {
        Log.d("GIH:refreshSettings", "call");
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
        Log.v("GIH:getCount", "call");
        return this.items.length;
    }

    @Override
    public Object getItem(int position) {
        Log.d("GIH:getItem", "call");
        return this.items[position];
    }

    @Override
    public long getItemId(int position) {
        Log.d("GIH:getItemId", "call");
        return this.items[position] == null ? null : this.items[position].getId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Log.v("GIH:getView", "call");
        this.items[position].setRelativeTo((GridView) parent);
        return this.items[position];
    }

    public void start() {
        Log.d("GIH:start", "call");
        this.gameState = true;
        this.paused = false;
        this.parent.setNext(this.blocks[count]);
        System.err.println("Next: " + this.blocks[count]);
        this.timer.start();
    }

    public void pause() {
        Log.d("GIH:pause", "call");
        if (this.gameState && !this.paused) {
            this.paused = true;
            this.timer.pause();
        }
    }

    public void stop(int cause) {
        Log.d("GIH:stop", "call");
        this.gameState = false;
        this.paused = false;
        this.timer.stop();
        switch (cause) {
            case 1:
                this.parent.onSuccess();
                break;
            case 2:
                this.parent.onFail();
                break;
            case -1:
                break;
            default:
                this.parent.onEnd();
        }
    }

    public void updateTime(TextView t) {
        Log.v("GIH:updateTime", "call");
        t.setText(this.timer.getFormatted());
    }

    public int getNext() {
        Log.d("GIH:getNext", "call");
        if (this.blocks.length <= this.count) {
            Log.i("GIH:getNext", "RETURN: 0 (Default)");
            return 0;
        }
        Log.i("GIH:getNext", "RETURN: " + this.blocks[count]);
        return this.blocks[count];
    }

}
