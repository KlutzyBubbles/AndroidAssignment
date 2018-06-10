package com.klutzybubbles.assignment1.activities;

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
import android.view.GestureDetector;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.getkeepsafe.taptargetview.TapTarget;
import com.getkeepsafe.taptargetview.TapTargetSequence;
import com.klutzybubbles.assignment1.interfaces.OnGameFinishedListener;
import com.klutzybubbles.assignment1.logic.GameItemHandler;
import com.klutzybubbles.assignment1.utils.BundledState;
import com.klutzybubbles.assignment1.utils.CustomImageButton;
import com.klutzybubbles.assignment1.utils.DatabaseHelper;

public class GameView extends AppCompatActivity implements OnGameFinishedListener {

    public static final int MAX_SIZE = 6;
    public static final int MIN_SIZE = 1;

    private int size;

    private GridView grid;
    private TextView text;

    private GameItemHandler a;

    private ImageButton newGame;

    private CustomImageButton stopGame;

    private boolean paused = false, noTimer = true;

    private DatabaseHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("GameView:onCreate", "call");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_view);
        android.support.v7.widget.Toolbar toolbar = findViewById(R.id.my_toolbar);
        this.setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
            actionBar.setTitle(R.string.title_activity_game_view);
        }
        this.db = new DatabaseHelper(this, this.getResources().getInteger(R.integer.database_version));
        this.loadSettings();
        this.grid = findViewById(R.id.main_grid);
        GameItemHandler.refreshSettings(this.grid);
        this.text = findViewById(R.id.text_timer);
        this.newGame = findViewById(R.id.button_new_game);
        this.stopGame = findViewById(R.id.button_stop_game);
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

        final GameView instance = this;

        GestureDetector gestureDetector = new GestureDetector(this, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                Log.v("Main:onFling", "single");
                if (velocityY < 0) {
                    Log.v("Main:onFling", "Fling Up");
                    instance.a.stop(-1);
                    instance.createGridContainer();
                    ((ViewGroup) findViewById(R.id.next_item)).removeAllViews();
                    ((ViewGroup) findViewById(R.id.next_item)).addView(instance.a.getNextItem());
                    instance.newGame.setEnabled(false);
                    instance.a.start();
                    instance.paused = false;
                    instance.noTimer = false;
                    (new Thread(new UpdateTime(instance))).start();
                }
                // If something wishes to cancel the touch events return the super method
                return super.onFling(e1, e2, velocityX, velocityY);
            }
        });

        this.stopGame.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_UP ||
                    event.getAction() == MotionEvent.ACTION_POINTER_UP)
                v.performClick();
            gestureDetector.onTouchEvent(event);
            return stopGame.onTouchEvent(event);
        });

        ImageView help = findViewById(R.id.button_help);

        View[] targets = new View[5];

        targets[0] = this.newGame;
        targets[1] = this.text;
        targets[4] = toolbar.getChildAt(1);

        String[] title = this.getResources().getStringArray(R.array.game_view_help_titles);
        String[] text = this.getResources().getStringArray(R.array.game_view_help_text);

        help.setOnClickListener(v -> {
            targets[2] = instance.a.getMiddleView();
            targets[3] = instance.a.getNextItem();
            TapTargetSequence seq = new TapTargetSequence(this);
            for (int i = 0; i < targets.length; i++)
                seq.target(TapTarget.forView(targets[i], title[i], text[i])
                        .cancelable(true)
                        .transparentTarget(true)
                        .textColor(R.color.White)
                        .outerCircleColor(R.color.RoyalBlue)
                        .outerCircleAlpha(0.95F)
                        .targetRadius(70)
                        .drawShadow(true)
                        .dimColor(R.color.Black));
            seq.continueOnCancel(true).considerOuterCircleCanceled(true);
            seq.start();
        });
    }

    @Override
    protected void onPause() {
        Log.d("GameView:onPause", "call");
        super.onPause();
        //this.a.pause();
        //this.paused = true;
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
        //this.a.stop(0);
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
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSaveInstanceState(Bundle b) {
        Log.d("GameView:onSIS", "call");
        BundledState.handlerToBundle(b, this.a);
        super.onSaveInstanceState(b);
    }

    @Override
    public void onRestoreInstanceState(Bundle b) {
        Log.d("GameView:onRIS", "call");
        this.a = BundledState.bundleToHandler(this.grid.getContext(), b);
        GameItemHandler.refreshSettings(this.grid);
        this.grid.setAdapter(a);
        this.grid.setOnItemClickListener(a);
        this.a.setOnGameFinishedListener(this);
        (new Thread(new UpdateTime(this))).start();
        super.onRestoreInstanceState(b);
    }

    private void toastMessage(String text) {
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
        Log.d("GameView:onNewGameClick", "call id: " + view.getId());
        this.a.stop(-1);
        this.createGridContainer();
        ((ViewGroup) findViewById(R.id.next_item)).removeAllViews();
        ((ViewGroup) findViewById(R.id.next_item)).addView(this.a.getNextItem());
        this.newGame.setVisibility(View.INVISIBLE);
        this.stopGame.setVisibility(View.VISIBLE);
        this.a.start();
        this.paused = false;
        this.noTimer = false;
        (new Thread(new UpdateTime(this))).start();
    }

    public void onStopGameClick(View view) {
        Log.d("GameView:stopGameClick", "call id: " + view.getId());
        this.a.stop(0);
        this.newGame.setVisibility(View.VISIBLE);
        this.stopGame.setVisibility(View.INVISIBLE);
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
    public void onSuccess() {
        Log.d("GameView:onSuccess", "call");
        this.noTimer = true;
        this.paused = true;
        this.toastMessage(getString(R.string.text_win));
        this.newGame.setEnabled(true);
        this.db.insert(this.a.getTime(), this.size, false);
    }

    @Override
    public void onFail() {
        Log.d("GameView:onFail", "call");
        this.noTimer = true;
        this.paused = true;
        this.toastMessage(getString(R.string.text_lose));
        this.newGame.setEnabled(true);
        this.db.insert(this.a.getTime(), this.size, true);
    }

    @Override
    public void onEnd() {
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
