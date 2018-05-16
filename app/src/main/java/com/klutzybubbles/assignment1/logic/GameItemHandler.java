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
import com.klutzybubbles.assignment1.activities.R;
import com.klutzybubbles.assignment1.interfaces.OnGameFinishedListener;

import java.util.Random;

/**
 * Created by  on 18/03/2018.
 */

public class GameItemHandler extends BaseAdapter implements GridView.OnItemClickListener {

    private static final int START = 4;

    private final GameItem[] items;
    private final GameItem nextItem;

    private final int size;
    private int current = 0;
    private int[] nextItems;

    private boolean stopped = false;
    private boolean paused = false;

    private final GameTimer timer = new GameTimer();

    private OnGameFinishedListener listener = null;

    public GameItemHandler(Context context, int size) {
        super();
        Log.d("GIH:CONSTRUCT", "call");
        if (context == null)
            throw new IllegalArgumentException("Context parsed cannot be null");
        if (size > GameView.MAX_SIZE)
            throw new IllegalArgumentException(context.getString(R.string.error_grid_max));
        if (size < GameView.MIN_SIZE)
            throw new IllegalArgumentException(context.getString(R.string.error_grid_min));
        this.size = size;
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
        this.nextItems = normal;
        this.nextItem = new GameItem(context);
        int index;
        Random r = new Random();
        for (int i = normal.length - 1; i > GameItemHandler.START; i--) {
            index = r.nextInt((i - GameItemHandler.START) + 1) + GameItemHandler.START;
            temp = normal[index];
            normal[index] = normal[i];
            normal[i] = temp;
        }
        this.nextItems = normal;
    }

    private void placeStarters() {
        Log.v("GIH:placeStarters", "call");
        if (this.current > 0)
            return;
        int temp = GameItemHandler.START;
        Random r = new Random();
        for (int i = 0; i < temp; i++) {
            Log.v("GIH:placeStarters", "length - " + this.nextItems.length);
            Log.v("GIH:placeStarters", "count - " + this.current);
            Log.v("GIH:placeStarters", "start - " + temp);
            int slot = r.nextInt(this.nextItems.length - this.current) + 1;
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
        if (!this.stopped)
            return;
        Log.v("GIH:onItemClick", "after call");
        GameItem g = null;
        if (position > this.items.length) {
            Log.v("GIH:onItemClick", "position greater than");
            for (GameItem t : this.items) {
                if (t == null)
                    continue;
                if (t.getId() == view.getId()) {
                    Log.v("GIH:onItemClick", "ID's match");
                    g = t;
                    break;
                }
            }
        } else
            g = this.items[position];
        Log.v("GIH:onItemClick", "position" + position);
        if (g != null && g.canClick()) {
            Log.v("GIH:onItemClick", "Not null and can click");
            try {
                int state = this.nextItems[current];
                g.setState(state);
                current++;
                this.nextItem.setStateOverride(this.getNext());
                // top
                if (position >= this.size * 2) {
                    Log.v("GIH:onItemClick", "top");
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
                    Log.v("GIH:onItemClick", "bottom");
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
                    Log.v("GIH:onItemClick", "left");
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
                    Log.v("GIH:onItemClick", "right");
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
                    Log.v("GIH:onItemClick", "middle vertical");
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
                    Log.v("GIH:onItemClick", "middle horizontal");
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
        GameItem.COLORS[0] = Color.parseColor(s.getString(parent.getContext().getString(R.string.pref_key_blank), "#696969"));
        GameItem.COLORS[1] = Color.parseColor(s.getString(parent.getContext().getString(R.string.pref_key_color_a), "#0000FF"));
        GameItem.COLORS[2] = Color.parseColor(s.getString(parent.getContext().getString(R.string.pref_key_color_b), "#FF0000"));
        GameItem.COLORS[3] = Color.parseColor(s.getString(parent.getContext().getString(R.string.pref_key_border), "#000000"));
        GameItem.COLORS[4] = Color.parseColor(s.getString(parent.getContext().getString(R.string.pref_key_highlight), "#FFFFFF"));
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
        this.stopped = true;
        this.paused = false;
        this.nextItem.setStateOverride(this.nextItems[current]);
        this.timer.start();
        if (!this.stopped || this.current == 0)
            this.placeStarters();
        System.err.println("Next: " + this.nextItems[current]);
    }

    public void pause() {
        Log.d("GIH:pause", "call");
        if (this.stopped && !this.paused) {
            this.paused = true;
            this.timer.pause();
        }
    }

    public void stop(int cause) {
        Log.d("GIH:stop", "call");
        this.stopped = false;
        this.paused = false;
        this.timer.stop();
        this.nextItem.setStateOverride(0);
        if (this.listener != null) {
            switch (cause) {
                case 1:
                    this.listener.onSuccess(this.timer.getRaw(), this.size);
                    break;
                case 2:
                    this.listener.onFail(this.timer.getRaw(), this.size);
                    break;
                case -1:
                    break;
                default:
                    this.listener.onEnd(this.timer.getRaw(), this.size);
            }
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
        if (this.nextItems.length <= this.current) {
            Log.i("GIH:getNext", "RETURN: 0 (Default)");
            return 0;
        }
        Log.i("GIH:getNext", "RETURN: " + this.nextItems[current]);
        return this.nextItems[current];
    }

    public GameItem getNextItem() {
        return this.nextItem;
    }

    public void setOnGameFinishedListener(OnGameFinishedListener listener) {
        this.listener = listener == null ? this.listener : listener;
    }

    public int[] getItems() {
        if (this.items == null)
            return GameItemHandler.genBlankItems(this.size);
        int[] temp = new int[this.items.length];
        for (int i = 0; i < this.items.length; i++) {
            temp[i] = this.items[i].getState();
        }
        return temp;
    }

    public int[] getNextItems() {
        return this.nextItems;
    }

    public boolean isStopped() {
        return this.stopped;
    }

    public boolean isPaused() {
        return this.paused;
    }

    public int getSize() {
        return this.size;
    }

    private void pushState() {
        if (this.stopped)
            this.stop(0);
        else if (this.paused)
            this.pause();
        else
            this.start();
    }

    public static GameItemHandler fromState(Context c, int size, long time, int[] items, int[] nextItems, boolean paused, boolean stopped) {
        if (items.length != nextItems.length || c == null || size < GameView.MIN_SIZE || size > GameView.MAX_SIZE)
            throw new IllegalArgumentException("Cannot create GameItemHandler from state with invalid variables");
        GameItemHandler g = new GameItemHandler(c, size);
        g.timer.setTime(time);
        g.nextItems = nextItems;
        g.paused = paused;
        g.stopped = stopped;
        int total = 0;
        for (int item : items) {
            if (item == 1 || item == 2)
                total++;
        }
        g.current = total;
        int last = -1;
        int count = 0;
        for (int item : items) {
            if (count == total && item == 0)
                break;
            last++;
            if (item == 1 || item == 2)
                count++;
        }
        for (int i = 0; i < items.length; i++) {
            g.items[i].setStateOverride(items[i]);
            if (i == last)
                g.onItemClick(null, g.items[i], i, g.items[i].getId());
        }
        g.pushState();
        return g;
    }

    public static int[] genBlankItems(int size) {
        int[] normal = new int[size * size];
        for (int i = 0; i < normal.length; i++) {
            normal[i] = 0;
        }
        return normal;
    }

    private static int[] genSequentialItems(int size) {
        int[] normal = new int[size * size];
        int temp = 2;
        for (int i = 0; i < normal.length; i++) {
            normal[i] = temp;
            temp = temp == 2 ? 1 : 2;
        }
        return normal;
    }

    public static int[] genRandomItems(int size) {
        int[] normal = GameItemHandler.genSequentialItems(size);
        Random r = new Random();
        for (int i = normal.length - 1; i > GameItemHandler.START; i--) {
            int index = r.nextInt((i - GameItemHandler.START) + 1) + GameItemHandler.START;
            int temp = normal[index];
            normal[index] = normal[i];
            normal[i] = temp;
        }
        return normal;
    }

}
