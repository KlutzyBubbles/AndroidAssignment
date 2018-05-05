package com.klutzybubbles.assignment1.logic;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.TextView;

import com.klutzybubbles.assignment1.activities.GameView;

import java.util.Random;

/**
 * Created by  on 18/03/2018.
 */

public class GameItemHandler extends BaseAdapter implements GridView.OnItemClickListener {

    public static final int MAX_SIZE = 6;
    public static final int MIN_SIZE = 1;

    private final GameItem[] items;
    private final GameItem next;

    private final int size;
    private int count = 0, start;
    private int[] blocks;

    private boolean gameState = false;
    private boolean paused = false;

    private final GameView parent;

    private final GameTimer timer;

    public GameItemHandler(GameView parent, Context context, int size, int start) {
        super();
        Log.d("GIH:CONSTRUCT", "call");
        if (size > GameItemHandler.MAX_SIZE || context == null)
            throw new IllegalArgumentException("Grid size can not be bigger than the MAX");
        if (size < GameItemHandler.MIN_SIZE || parent == null)
            throw new IllegalArgumentException("Grid size can not be smaller than the MIN");
        this.size = size;
        this.start = start;
        this.parent = parent;
        int temp = this.size * this.size;
        Log.d("GIH:CONSTRUCT", "size - " + this.size);
        Log.d("GIH:CONSTRUCT", "count - " + temp);
        this.items = new GameItem[temp];
        int[] normal = new int[temp];
        for (int i = 0; i < temp; i++)
            this.items[i] = new GameItem(context);
        temp = 2;
        for (int i = 0; i < normal.length; i++) {
            normal[i] = temp;
            temp = temp == 2 ? 1 : 2;
        }
        this.blocks = normal;
        this.next = new GameItem(context);
        int index;
        Random r = new Random();
        for (int i = normal.length - 1; i > this.start; i--) {
            index = r.nextInt((i - this.start) + 1) + this.start;
            temp = normal[index];
            normal[index] = normal[i];
            normal[i] = temp;
        }
        this.blocks = normal;
        this.timer = new GameTimer();
    }

    private void placeStarters() {
        Log.v("GIH:placeStarters", "call");
        if (this.start == 0)
            return;
        int temp = this.start;
        Random r = new Random();
        for (int i = 0; i < temp; i++) {
            this.start--;
            Log.v("GIH:placeStarters", "length - " + this.blocks.length);
            Log.v("GIH:placeStarters", "count - " + this.count);
            Log.v("GIH:placeStarters", "start - " + temp);
            int slot = r.nextInt(this.blocks.length - this.count) + 1;
            Log.v("GIH:placeStarters", "slot - " + slot);
            int c = 0;
            jj:for (int j = 0; j < slot; j++) {
                Log.v("GIH:placeStarters", "j - " + j);
                boolean cont = false;
                while (!cont) {
                    Log.v("GIH:placeStarters", "c - " + c);
                    if (this.items[c].canClick()) {
                        Log.v("GIH:placeStarters", "Can click");
                        Log.v("GIH:placeStarters", "j == slot - 1 - " + (j == slot -1));
                        if (j == slot - 1) {
                            this.onItemClick(null, this.items[c], c, 1L);
                            //this.items[c].setState(this.blocks[i]);
                            break jj;
                        }
                        cont = true;
                    }
                    c++;
                }
            }

        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Log.v("GIH:onItemClick", "call");
        if (!this.gameState)
            return;
        Log.e("GIH:onItemClick", "after call");
        GameItem g = null;
        if (position > this.items.length) {
            Log.e("GIH:onItemClick", "position greater than");
            for (GameItem t : this.items) {
                if (t == null)
                    continue;
                if (t.getId() == view.getId()) {
                    Log.e("GIH:onItemClick", "ID's match");
                    g = t;
                    break;
                }
            }
        } else
            g = this.items[position];
        Log.e("GIH:onItemClick", "position" + position);
        if (g != null && g.canClick()) {
            Log.e("GIH:onItemClick", "Not null and can click");
            try {
                int state = this.blocks[count];
                g.setState(state);
                count++;
                this.next.setStateOverride(this.getNext());
                // top
                if (position >= this.size * 2) {
                    Log.e("GIH:onItemClick", "top");
                    if (this.items[position - this.size].getState() == state && this.items[position - (this.size * 2)].getState() == state) {
                        this.stop(2);
                        this.items[position - this.size].highlight();
                        this.items[position - (this.size * 2)].highlight();
                        this.items[position].highlight();
                        return;
                    }
                }

                // bottom
                if (position < (this.size * this.size) - (this.size * 2)) {
                    Log.e("GIH:onItemClick", "bottom");
                    if (this.items[position + this.size].getState() == state && this.items[position + (this.size * 2)].getState() == state) {
                        this.stop(2);
                        this.items[position + this.size].highlight();
                        this.items[position + (this.size * 2)].highlight();
                        this.items[position].highlight();
                        return;
                    }
                }

                // left
                if (position % this.size >= 2) {
                    Log.e("GIH:onItemClick", "left");
                    if (this.items[position - 1].getState() == state && this.items[position - 2].getState() == state) {
                        this.stop(2);
                        this.items[position - 2].highlight();
                        this.items[position - 1].highlight();
                        this.items[position].highlight();
                        return;
                    }
                }

                // right
                if (position % this.size <= this.size - 3) {
                    Log.e("GIH:onItemClick", "right");
                    if (this.items[position + 1].getState() == state && this.items[position + 2].getState() == state) {
                        this.stop(2);
                        this.items[position + 1].highlight();
                        this.items[position + 2].highlight();
                        this.items[position].highlight();
                        return;
                    }
                }

                // middle vertical
                if (position >= this.size && position < (this.size * this.size) - this.size) {
                    Log.e("GIH:onItemClick", "middle vertical");
                    if (this.items[position + this.size].getState() == state && this.items[position - this.size].getState() == state) {
                        this.stop(2);
                        this.items[position + this.size].highlight();
                        this.items[position - this.size].highlight();
                        this.items[position].highlight();
                        return;
                    }
                }

                // middle horizontal
                if (position % this.size >= 1 && position % this.size <= this.size - 2) {
                    Log.e("GIH:onItemClick", "middle horizontal");
                    if (this.items[position + 1].getState() == state && this.items[position - 1].getState() == state) {
                        this.stop(2);
                        this.items[position + 1].highlight();
                        this.items[position - 1].highlight();
                        this.items[position].highlight();
                        return;
                    }
                }
            } catch (ArrayIndexOutOfBoundsException e) {
                System.err.println("Ouch");
                Log.e("GIH:onItemClick", e.getMessage());
                e.printStackTrace();
            }
            if (this.getNext() == 0)
                this.stop(1);
        }
    }

    public static void refreshSettings(GridView parent) {
        SharedPreferences s = PreferenceManager.getDefaultSharedPreferences(parent.getContext());
        GameItem.COLORS[0] = Color.parseColor(s.getString("blank", "#696969"));
        GameItem.COLORS[1] = Color.parseColor(s.getString("color_a", "#0000FF"));
        GameItem.COLORS[2] = Color.parseColor(s.getString("color_b", "#FF0000"));
        GameItem.COLORS[3] = Color.parseColor(s.getString("border", "#000000"));
        GameItem.COLORS[4] = Color.parseColor(s.getString("highlight", "#FFFFFF"));
    }

    public void refreshBlocks(GridView parent) {
        Log.d("GIH:refreshSettings", "call");
        for (GameItem item : this.items) item.setRelativeTo(parent);
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
        return this.items[position] == null ? -1L : this.items[position].getId();
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
        this.next.setStateOverride(this.blocks[count]);
        this.timer.start();
        if (!this.gameState || (this.start != 0 && this.count == 0))
            this.placeStarters();
        System.err.println("Next: " + this.blocks[count]);
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
        this.next.setStateOverride(0);
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

    public long getTime() {
        if (this.timer == null)
            return 0L;
        return this.timer.getRaw();
    }

    private int getNext() {
        Log.d("GIH:getNext", "call");
        if (this.blocks.length <= this.count) {
            Log.i("GIH:getNext", "RETURN: 0 (Default)");
            return 0;
        }
        Log.i("GIH:getNext", "RETURN: " + this.blocks[count]);
        return this.blocks[count];
    }

    public GameItem getNextItem() {
        return this.next;
    }

}
