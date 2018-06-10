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
 * <h1>GameItemHandler.java</h1>
 * Class used to handle the GameItem's within the GridView
 *
 * @author Lee Tzilantonis
 * @version 1.0.0
 * @since 10/6/2018
 * @see BaseAdapter
 * @see GridView.OnItemClickListener
 */
public class GameItemHandler extends BaseAdapter implements GridView.OnItemClickListener {

    /**
     * The amount of starting items to place
     */
    private static final int START = 4;

    /**
     * The array of GridItem's currently on the board
     */
    private final GameItem[] items;

    /**
     * The next GridItem to be placed
     */
    private final GameItem nextItem;

    /**
     * The size of the grid on both x and y axis
     */
    private final int size;

    /**
     * The current GridItem being placed. Used to track the next item and game finish states
     */
    private int current = 0;

    /**
     * The array of integer states of the next items to be placed
     */
    private int[] nextItems;

    /**
     * Whether or not the current game is stopped / finished
     */
    private boolean stopped = false;

    /**
     * Whether or not the current game is paused
     */
    private boolean paused = false;

    /**
     * The GameTimer used to track the time of the current game
     */
    private final GameTimer timer = new GameTimer();

    /**
     * The listener to call when a game finishes
     */
    private OnGameFinishedListener listener = null;

    /**
     * Instantiates the GameItemHandler using the context for View creation and the size as the games
     * grid size.
     *
     * @param context - The Context to be used to create GridItem's
     * @param size - The size of the game grid
     */
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

    /**
     * Places the starting GameItem's on the board. Only places if the game is on its first move
     */
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

    /**
     * Called when an item within the GridView has been clicked
     *
     * @param parent - The parent of the item clicked
     * @param view - The View that was clicked
     * @param position - The position of the View that was clicked
     * @param id - The id of the View that was clicked
     */
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

    /**
     * Refreshes the settings for the game from the default SharedPreferences
     *
     * @param parent - The GridView to get the context from
     */
    public static void refreshSettings(GridView parent) {
        SharedPreferences s = PreferenceManager.getDefaultSharedPreferences(parent.getContext());
        GameItem.COLORS[0] = Color.parseColor(s.getString(parent.getContext().getString(R.string.pref_key_blank), "#696969"));
        GameItem.COLORS[1] = Color.parseColor(s.getString(parent.getContext().getString(R.string.pref_key_color_a), "#0000FF"));
        GameItem.COLORS[2] = Color.parseColor(s.getString(parent.getContext().getString(R.string.pref_key_color_b), "#FF0000"));
        GameItem.COLORS[3] = Color.parseColor(s.getString(parent.getContext().getString(R.string.pref_key_border), "#000000"));
        GameItem.COLORS[4] = Color.parseColor(s.getString(parent.getContext().getString(R.string.pref_key_highlight), "#FFFFFF"));
    }

    /**
     * Refreshes all of the GridItem's within the GridView parent using the parent to resize the items
     *
     * @param parent - The GridView containing all of the GridItems, should be the GridView that has
     *               its adapter set to this Object
     */
    public void refreshBlocks(GridView parent) {
        Log.d("GIH:refreshSettings", "call");
        for (GameItem item : this.items) item.setRelativeTo(parent);
    }

    /**
     * Gets the amount of GridItems inside the GridView's Adapter
     *
     * @return - The amount of GridItems inside the grid
     */
    @Override
    public int getCount() {
        Log.v("GIH:getCount", "call");
        return this.items.length;
    }

    /**
     * Gets the GridItem Object in the specified position
     *
     * @param position - The position to get
     * @return - The GridItem Object in the specified position
     */
    @Override
    public Object getItem(int position) {
        Log.d("GIH:getItem", "call");
        return this.items[position];
    }

    /**
     * Gets the ID of the GridItem in the specified position
     *
     * @param position - The position to get the ID
     * @return - The ID of the GridItem in the specified position
     */
    @Override
    public long getItemId(int position) {
        Log.d("GIH:getItemId", "call");
        return this.items[position] == null ? -1L : this.items[position].getId();
    }

    /**
     * Gets the View in the specified position. Using the parent to update the view
     *
     * @param position - The position of the View to get
     * @param convertView - The recycled view or previous View that was in that position
     * @param parent - The parent of the View to get
     * @return - The View or recycled View that was in the specified position
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Log.d("GIH:getView", "call to pos: " + position + ", Parent: " + parent.getId());
        if (convertView != null) {
            this.items[position] = (GameItem) convertView;
            return convertView;
        }
        this.items[position].setRelativeTo((GridView) parent);
        return this.items[position];
    }

    /**
     * Gets the View in the center of the grid
     *
     * @return - The View in the center of the grid
     */
    public View getMiddleView() {
        return this.items[(this.size * this.size) / 2];
    }

    /**
     * Starts the game from its current state
     */
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

    /**
     * Pauses the game if it is not already paused
     */
    private void pause() {
        Log.d("GIH:pause", "call");
        if (this.stopped && !this.paused) {
            this.paused = true;
            this.timer.pause();
        }
    }

    /**
     * Stops the game in its current state
     *
     * @param cause - The cause of the stop with -1 being a silent stop
     */
    public void stop(int cause) {
        Log.d("GIH:stop", "call");
        this.stopped = false;
        this.paused = false;
        this.timer.stop();
        this.nextItem.setStateOverride(0);
        if (this.listener != null) {
            switch (cause) {
                case 1:
                    this.listener.onSuccess();
                    break;
                case 2:
                    this.listener.onFail();
                    break;
                case -1:
                    break;
                default:
                    this.listener.onEnd();
            }
        }
    }

    /**
     * Updates the TextView with the current GameTimer's formatted time
     *
     * @param t - The TextView to put the formatted time
     */
    public void updateTime(TextView t) {
        Log.v("GIH:updateTime", "call");
        t.setText(this.timer.getFormatted());
    }

    /**
     * Gets the current progressed time in milliseconds
     *
     * @return - The current progressed time in milliseconds
     */
    public long getTime() {
        if (this.timer == null)
            return 0L;
        return this.timer.getRaw();
    }

    /**
     * Gets the state of the next GridItem being placed
     *
     * @return - The state of the next GridItem being placed
     */
    private int getNext() {
        Log.d("GIH:getNext", "call");
        if (this.nextItems.length <= this.current) {
            Log.i("GIH:getNext", "RETURN: 0 (Default)");
            return 0;
        }
        Log.i("GIH:getNext", "RETURN: " + this.nextItems[current]);
        return this.nextItems[current];
    }

    /**
     * Gets the next GridItem being placed
     *
     * @return - The next GridItem being placed
     */
    public GameItem getNextItem() {
        return this.nextItem;
    }

    /**
     * Sets the listener that gets called when the game finishes
     *
     * @param listener - The listener that gets called when the game finishes
     */
    public void setOnGameFinishedListener(OnGameFinishedListener listener) {
        this.listener = listener == null ? this.listener : listener;
    }

    /**
     * Gets the state of all the GameItems currently on the board
     *
     * @return - The state of all the GameItems currently on the board
     */
    public int[] getItems() {
        if (this.items == null)
            return GameItemHandler.genBlankItems(this.size);
        int[] temp = new int[this.items.length];
        for (int i = 0; i < this.items.length; i++) {
            temp[i] = this.items[i].getState();
        }
        return temp;
    }

    /**
     * Gets the state of all the next items (including placed items)
     *
     * @return - The state of all the next items
     */
    public int[] getNextItems() {
        return this.nextItems;
    }

    /**
     * Whether or not the current game is stopped
     *
     * @return - Whether or not the current game is stopped
     */
    public boolean isStopped() {
        return this.stopped;
    }

    /**
     * Whether or not the current game is paused
     *
     * @return - Whether or not the current game is paused
     */
    public boolean isPaused() {
        return this.paused;
    }

    /**
     * Gets the current grid size
     *
     * @return - The current grid size
     */
    public int getSize() {
        return this.size;
    }

    /**
     * Updates the Grid Handler to reflect its current state
     */
    private void pushState() {
        if (this.stopped)
            this.stop(0);
        else if (this.paused)
            this.pause();
        else
            this.start();
    }

    /**
     * Creates a new GameItemHandler with the state set to the parameters parsed
     *
     * @param c - The Context to create the Handler with
     * @param size - The size of the grid
     * @param time - The current progressed time
     * @param items - The state of all the items on the grid (length should be size*size)
     * @param nextItems - The state of all of the new items (length should be size*size)
     * @param paused - Whether or not the current game is paused
     * @param stopped - Whether or not the current game is stopped
     * @return - The GameItemHandler with its state restored
     */
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

    /**
     * Generates an array of empty item states based on the size of the grid
     *
     * @param size - The size of the grid
     * @return - The array of empty item states
     */
    public static int[] genBlankItems(int size) {
        int[] normal = new int[size * size];
        for (int i = 0; i < normal.length; i++) {
            normal[i] = 0;
        }
        return normal;
    }

    /**
     * Generates an array of alternating item states based on the size of the grid
     *
     * @param size - The size of the grid
     * @return - The array of alternating item states
     */
    private static int[] genSequentialItems(int size) {
        int[] normal = new int[size * size];
        int temp = 2;
        for (int i = 0; i < normal.length; i++) {
            normal[i] = temp;
            temp = temp == 2 ? 1 : 2;
        }
        return normal;
    }

    /**
     * Generates an array of equally balances but randomly orders item states based on the size of
     * the grid
     *
     * @param size - The size of the grid
     * @return - The array of randomly ordered item states
     */
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
