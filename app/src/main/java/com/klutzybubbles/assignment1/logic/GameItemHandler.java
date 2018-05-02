package com.klutzybubbles.assignment1.logic;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.constraint.ConstraintLayout;
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
    private int count = 0;
    private final int[] blocks;

    private boolean gameState = false;
    private boolean paused = false;

    private final Context gridContext;
    private final GameView parent;

    private final GameTimer timer;

    public GameItemHandler(GameView parent, Context context, int width, int height) {
        super();
        if (width > GameItemHandler.MAX_WIDTH || height > GameItemHandler.MAX_HEIGHT || context == null)
            throw new IllegalArgumentException("Grid size can not be bigger than the MAX");
        if (width < GameItemHandler.MIN_WIDTH || height < GameItemHandler.MIN_HEIGHT || parent == null)
            throw new IllegalArgumentException("Grid size can not be smaller than the MIN");
        this.width = width;
        this.height = height;
        this.gridContext = context;
        this.parent = parent;
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
        this.timer = new GameTimer();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
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
                System.out.println("POSITION: " + position);
                // top
                if (position >= this.width * 2) {
                    System.out.println("top");
                    System.out.println(this.items[position - this.width].getState());
                    System.out.println(this.items[position - (this.width * 2)].getState());
                    if (this.items[position - this.width].getState() == state && this.items[position - (this.width * 2)].getState() == state) {
                        System.err.println("Top End");
                        //Toast.makeText(parent.getContext(), "End", Toast.LENGTH_SHORT);
                    }
                }

                // bottom
                if (position <= (this.width * this.height) - (this.width * 2)) {
                    System.out.println("bottom");
                    System.out.println(this.items[position + this.width].getState());
                    System.out.println(this.items[position + (this.width * 2)].getState());
                    if (this.items[position + this.width].getState() == state && this.items[position + (this.width * 2)].getState() == state) {
                        System.err.println("Bottom End");
                        //Toast.makeText(parent.getContext(), "End", Toast.LENGTH_SHORT);
                    }
                }

                // left
                if (position % this.width >= 2) {
                    System.out.println("left");
                    System.out.println(this.items[position - 1].getState());
                    System.out.println(this.items[position - 2].getState());
                    if (this.items[position - 1].getState() == state && this.items[position - 2].getState() == state) {
                        System.err.println("Left End");
                        //Toast.makeText(parent.getContext(), "End", Toast.LENGTH_SHORT);
                    }
                }

                // right
                if (position % this.width <= this.width - 3) {
                    System.out.println("right");
                    System.out.println("");
                    System.out.println("");
                    if (this.items[position + 1].getState() == state && this.items[position + 2].getState() == state) {
                        //Toast.makeText(parent.getContext(), "End", Toast.LENGTH_SHORT);
                    }
                }

                // middle vert
                if (position >= this.width && position <= (this.width * this.height) - this.width) {
                    System.out.println("middle vert");
                    System.out.println("");
                    System.out.println("");
                    if (this.items[position + this.width].getState() == state && this.items[position - this.width].getState() == state) {
                        //Toast.makeText(parent.getContext(), "End", Toast.LENGTH_SHORT);
                    }
                }

                // middle hori
                if (position % this.width >= 1 && position % this.width <= this.width - 2) {
                    System.out.println("middle hori");
                    System.out.println("");
                    System.out.println("");
                    if (this.items[position + 1].getState() == state && this.items[position - 1].getState() == state) {
                        //Toast.makeText(this.gridContext, "End", Toast.LENGTH_SHORT);
                    }
                }
                //TextView v = ((ConstraintLayout) this.parent.getParent()).findViewById(R.id.textView2);
                //TextView v = this.parent.findViewById(R.id.textView2);
                //v.setText(this.blocks[count]);
                //Toast.makeText(null, this.blocks[count], Toast.LENGTH_SHORT);
                this.parent.setNext(this.getNext());
                System.err.println("Next: " + this.getNext());
                if (this.getNext() == 0)
                    this.stop(1);
            } catch (ArrayIndexOutOfBoundsException e) {
                e.printStackTrace();
                System.err.println("Ouch");
            }
        }
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

    public void start() {
        this.gameState = true;
        this.paused = false;
        this.parent.setNext(this.blocks[count]);
        System.err.println("Next: " + this.blocks[count]);
        this.timer.start();
    }

    public void pause() {
        if (this.gameState && !this.paused) {
            this.paused = true;
            this.timer.pause();
        }
    }

    public void stop(int cause) {
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
            default:
                this.parent.onEnd();
        }
    }

    public void updateTime(TextView t) {
        t.setText(this.timer.getFormatted());
    }

    public int getNext() {
        if (this.blocks.length <= this.count)
            return 0;
        return this.blocks[count];
    }

}
