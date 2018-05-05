package com.klutzybubbles.assignment1.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.graphics.Point;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.TypedValue;
import android.view.Display;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import com.klutzybubbles.assignment1.logic.GameItemHandler;
import com.klutzybubbles.assignment1.utils.DatabaseHelper;

public class GameView extends AppCompatActivity {

    private int size, difficulty;

    private GridView grid;
    private TextView text;

    private GameItemHandler a;

    private Button newGame;

    private boolean paused = false, noTimer = true;

    private DatabaseHelper db;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Log.d("GameView:onCOM", "call");
        MenuInflater i = getMenuInflater();
        i.inflate(R.menu.game_menu, menu);
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("GameView:onCreate", "call");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_view);
        this.db = new DatabaseHelper(this, this.getResources().getInteger(R.integer.database_version));
        this.loadSettings();
        this.grid = findViewById(R.id.main_grid);
        GameItemHandler.refreshSettings(this.grid);
        this.text = findViewById(R.id.text_timer);
        this.newGame = findViewById(R.id.button_new_game);
        System.out.println(this.getRequestedOrientation());
        System.out.println(this.getResources().getConfiguration().orientation);
        this.grid.setNumColumns(this.size);
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;
        int height = size.y;

        /*
        Rect rectangle = new Rect();
        Window window = getWindow();
        window.getDecorView().getWindowVisibleDisplayFrame(rectangle);
        int statusBarHeight = rectangle.top;
        int contentViewTop =
                window.findViewById(Window.ID_ANDROID_CONTENT).getTop();
        int titleBarHeight = contentViewTop - statusBarHeight;

        int aHeight = getSupportActionBar().getHeight();
        */

        int[] textSizeAttr = new int[] { android.R.attr.actionBarSize, R.attr.actionBarSize };
        TypedArray a = obtainStyledAttributes(new TypedValue().data, textSizeAttr);
        float aHeight = a.getDimension(0, -1);
        a.recycle();

        height -= aHeight;

        ViewGroup.LayoutParams g = this.grid.getLayoutParams();

        if (width > height) {
            Log.i("GameView:onCreate", "W W");
            Log.i("GameView:onCreate", "1: " + height);
            Log.i("GameView:onCreate", "2: " + this.size);
            Log.i("GameView:onCreate", "E: " + height / this.size);
            g.height = height;
            g.width = (height / this.size) * this.size;
            grid.setColumnWidth(height / this.size);
        } else {
            Log.i("GameView:onCreate", "W H");
            Log.i("GameView:onCreate", "1: " + width);
            Log.i("GameView:onCreate", "2: " + this.size);
            Log.i("GameView:onCreate", "E: " + width / this.size);
            //noinspection SuspiciousNameCombination
            g.height = width;
            g.width = (width / this.size) * this.size;
            grid.setColumnWidth(width / this.size);
        }

        Log.i("GameView:onCreate", "Width: " + width);
        Log.i("GameView:onCreate", "Height: " + height);
        Log.i("GameView:onCreate", "Col Width: " + this.grid.getColumnWidth());

        this.grid.setLayoutParams(g);
        this.initialStart();
        ((ViewGroup) findViewById(R.id.next_item)).addView(this.a.getNextItem());
    }

    @Override
    protected void onPause() {
        Log.d("GameView:onPause", "call");
        super.onPause();
        this.a.pause();
        this.paused = true;
        this.db.close();
    }

    @Override
    protected void onResume() {
        Log.d("GameView:onResume", "call");
        super.onResume();
        GameItemHandler.refreshSettings(this.grid);
        if (this.paused)
            this.a.start();
        this.paused = false;
        this.a.refreshBlocks(this.grid);
        this.db = new DatabaseHelper(this, this.getResources().getInteger(R.integer.database_version));
        (new Thread(new UpdateTime(this))).start();
    }

    @Override
    protected  void onDestroy() {
        Log.d("GameView:onDestroy", "call");
        super.onDestroy();
        this.a.stop(0);
        this.paused = true;
        this.db.close();
    }

    public void setText(String text) {
        Log.d("GameView:setText", "call");
        this.text.setText(text);
    }

    public void toastMessage(String text) {
        Log.d("GameView:toastMessage", "call");
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
    }

    private void loadSettings() {
        Log.d("GameView:loadSettings", "call");
        SharedPreferences s = PreferenceManager.getDefaultSharedPreferences(this);
        try {
            this.difficulty = Integer.parseInt(s.getString("difficulty", (GameItemHandler.MAX_SIZE - GameItemHandler.MIN_SIZE) + ""));
            this.size = Integer.parseInt(s.getString("size", "1"));
        } catch (NumberFormatException e) {
            Log.w("GameView:loadSettings", "Size or Difficulty isn't a number");
        }
        if (this.difficulty >= this.size * this.size)
            this.difficulty = (this.size * this.size - 1);
    }

    public void initialStart() {
        Log.d("GameView:initialStart", "call");
        this.a = new GameItemHandler(this, this.grid.getContext(), this.size, this.difficulty);
        GameItemHandler.refreshSettings(this.grid);
        this.grid.setAdapter(a);
        this.grid.setOnItemClickListener(a);
    }

    public void onNewGameClick(View view) {
        Log.d("GameView:onNewGameClick", "call");
        this.a.stop(-1);
        this.a = new GameItemHandler(this, this.grid.getContext(), this.size, this.difficulty);
        GameItemHandler.refreshSettings(this.grid);
        this.grid.setAdapter(a);
        this.grid.setOnItemClickListener(a);
        ((ViewGroup) findViewById(R.id.next_item)).removeAllViews();
        ((ViewGroup) findViewById(R.id.next_item)).addView(this.a.getNextItem());
        this.newGame.setEnabled(false);
        this.a.start();
        this.paused = false;
        this.noTimer = false;
        (new Thread(new UpdateTime(this))).start();
    }

    public void onMenuClick(MenuItem item) {
        Log.d("GameView:onMenuClick", "call");
        this.a.stop(0);
        Intent i = new Intent(getApplicationContext(), MainMenu.class);
        startActivity(i);
    }

    public void onRestartClick(MenuItem item) {
        Log.d("GameView:onRestartClick", "call");
        this.noTimer = true;
        this.a.stop(0);
        this.a = new GameItemHandler(this, this.grid.getContext(), this.size, this.difficulty);
        GameItemHandler.refreshSettings(this.grid);
        this.grid.setAdapter(a);
        this.grid.setOnItemClickListener(a);
        (new Thread(new UpdateTime(this))).start();
    }

    public void onFail() {
        Log.d("GameView:onFail", "call");
        this.noTimer = true;
        this.paused = true;
        this.toastMessage("You Failed");
        this.newGame.setEnabled(true);
    }

    public void onSuccess() {
        Log.d("GameView:onSuccess", "call");
        this.noTimer = true;
        this.paused = true;
        this.toastMessage("You Win");
        this.newGame.setEnabled(true);
        this.db.insert(this.a.getTime(), this.size, this.difficulty);

    }

    public void onEnd() {
        Log.d("GameView:onEnd", "call");
        this.noTimer = true;
        this.paused = true;
        this.toastMessage("Game Stopped");
        this.newGame.setEnabled(true);
        this.text.setText(getString(R.string.timer_text));
    }

    private class UpdateTime implements Runnable {

        private GameView instance;

        private UpdateTime(GameView instance) {
            if (instance == null)
                throw new IllegalArgumentException("Cannot parse null instance");
            this.instance = instance;
        }

        @Override
        public void run() {
            if (noTimer) {
                try {
                    Thread.sleep(100L);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        instance.text.setText(getString(R.string.timer_text));
                    }
                });
            } else {
                while (!paused) {
                    if (noTimer)
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                instance.text.setText(getString(R.string.timer_text));
                            }
                        });
                    else {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                instance.a.updateTime(instance.text);
                            }
                        });
                        try {
                            Thread.sleep(10L);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }

    }

}
