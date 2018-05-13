package com.klutzybubbles.assignment1.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.graphics.Point;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.TypedValue;
import android.view.Display;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.klutzybubbles.assignment1.interfaces.OnGameFinishedListener;
import com.klutzybubbles.assignment1.logic.GameItemHandler;
import com.klutzybubbles.assignment1.utils.DatabaseHelper;

public class GameView extends AppCompatActivity implements OnGameFinishedListener {

    public static int MAX_SIZE = 6, MIN_SIZE = 1;

    private int size;

    private GridView grid;
    private TextView text;

    private GameItemHandler a;

    private ImageButton newGame;

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
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(R.string.title_activity_game_view);
        }
        this.db = new DatabaseHelper(this, this.getResources().getInteger(R.integer.database_version));
        this.loadSettings();
        this.grid = findViewById(R.id.main_grid);
        GameItemHandler.refreshSettings(this.grid);
        this.text = findViewById(R.id.text_timer);
        this.newGame = findViewById(R.id.button_new_game);
        this.grid.setNumColumns(this.size);
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;
        int height = size.y;
        int[] textSizeAttr = new int[] { android.R.attr.actionBarSize, R.attr.actionBarSize };
        TypedArray a = obtainStyledAttributes(new TypedValue().data, textSizeAttr);
        float aHeight = a.getDimension(0, -1);
        a.recycle();

        height -= aHeight;

        ViewGroup.LayoutParams g = this.grid.getLayoutParams();

        if (width > height) {
            Log.v("GameView:onCreate", "W W");
            Log.v("GameView:onCreate", "1: " + height);
            Log.v("GameView:onCreate", "2: " + this.size);
            Log.v("GameView:onCreate", "E: " + height / this.size);
            g.height = height;
            g.width = (height / this.size) * this.size;
            grid.setColumnWidth(height / this.size);
        } else {
            Log.v("GameView:onCreate", "W H");
            Log.v("GameView:onCreate", "1: " + width);
            Log.v("GameView:onCreate", "2: " + this.size);
            Log.v("GameView:onCreate", "E: " + width / this.size);
            //noinspection SuspiciousNameCombination
            g.height = width;
            g.width = (width / this.size) * this.size;
            grid.setColumnWidth(width / this.size);
        }

        Log.v("GameView:onCreate", "Width: " + width);
        Log.v("GameView:onCreate", "Height: " + height);

        this.grid.setLayoutParams(g);
        this.createGridContainer();
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.d("GameView:onOIS", "call");
        int id = item.getItemId();
        if (id == android.R.id.home) {
            this.a.stop(0);
            this.paused = true;
            this.db.close();
            startActivity(new Intent(this.getBaseContext(), SplashScreen.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
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
            this.size = Integer.parseInt(s.getString(getString(R.string.pref_key_size), getString(R.string.pref_default_size)));
        } catch (NumberFormatException e) {
            Log.w("GameView:loadSettings", "Size or Difficulty isn't a number");
        }
    }

    public void onNewGameClick(View view) {
        Log.d("GameView:onNewGameClick", "call");
        this.a.stop(-1);
        this.createGridContainer();
        ((ViewGroup) findViewById(R.id.next_item)).removeAllViews();
        ((ViewGroup) findViewById(R.id.next_item)).addView(this.a.getNextItem());
        this.newGame.setEnabled(false);
        this.a.start();
        this.paused = false;
        this.noTimer = false;
        (new Thread(new UpdateTime(this))).start();
    }

    public void onRestartClick(MenuItem item) {
        Log.d("GameView:onRestartClick", "call");
        this.a.stop(0);
        (new Thread(new UpdateTime(this))).start();
    }

    private void createGridContainer() {
        Log.d("GameView:createGC", "call");
        this.a = new GameItemHandler(this.grid.getContext(), this.size);
        GameItemHandler.refreshSettings(this.grid);
        this.grid.setAdapter(a);
        this.grid.setOnItemClickListener(a);
        this.a.setOnGameFinishedListener(this);
    }

    @Override
    public void onSuccess(long time, int size) {
        Log.d("GameView:onSuccess", "call");
        this.noTimer = true;
        this.paused = true;
        this.toastMessage(getString(R.string.text_win));
        this.newGame.setEnabled(true);
        this.db.insert(this.a.getTime(), this.size);
    }

    @Override
    public void onFail(long time, int size) {
        Log.d("GameView:onFail", "call");
        this.noTimer = true;
        this.paused = true;
        this.toastMessage(getString(R.string.text_lose));
        this.newGame.setEnabled(true);
    }

    @Override
    public void onEnd(long time, int size) {
        Log.d("GameView:onEnd", "call");
        this.noTimer = true;
        this.paused = true;
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
                runOnUiThread(() -> instance.text.setText(getString(R.string.timer_text)));
            } else {
                while (!paused) {
                    if (noTimer)
                        runOnUiThread(() -> instance.text.setText(getString(R.string.timer_text)));
                    else {
                        runOnUiThread(() -> instance.a.updateTime(instance.text));
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
