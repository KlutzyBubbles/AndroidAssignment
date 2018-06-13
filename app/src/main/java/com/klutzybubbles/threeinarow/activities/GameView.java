package com.klutzybubbles.threeinarow.activities;

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
import com.klutzybubbles.threeinarow.interfaces.OnGameFinishedListener;
import com.klutzybubbles.threeinarow.logic.GameItemHandler;
import com.klutzybubbles.threeinarow.utils.BundledState;
import com.klutzybubbles.threeinarow.utils.CustomImageButton;
import com.klutzybubbles.threeinarow.utils.DatabaseHelper;

/**
 * <h1>GameView.java</h1>
 * Class used to hold the content required to play the game
 *
 * @author Lee Tzilantonis
 * @version 1.0.0
 * @since 10/6/2018
 * @see AppCompatActivity
 * @see OnGameFinishedListener
 */
public class GameView extends AppCompatActivity implements OnGameFinishedListener {

    /**
     * The maximum size of the grid
     */
    public static final int MAX_SIZE = 6;

    /**
     * The minimum size of the grid
     */
    public static final int MIN_SIZE = 3;

    /**
     * The current set size of the grid
     */
    private int size;

    /**
     * The GridView container currently being used
     */
    private GridView grid;

    /**
     * The Timer TextView currently being used
     */
    private TextView text;

    /**
     * The current GameItemHandler being used
     */
    private GameItemHandler a;

    /**
     * The New Game button, contained as a class variable to change visibility on game state changes
     */
    private ImageButton newGame;

    /**
     * The Stop Game button, contained as a class variable to change visibility on game state changes
     */
    private CustomImageButton stopGame;

    /**
     * paused - Whether or not the current game in play is paused
     * noTimer - Whether or not there is no timer to be displayed
     */
    private boolean paused = false, noTimer = true;

    /**
     * DatabaseHelper used to store scores
     */
    private DatabaseHelper db;

    /**
     * Called when the AppCompatActivity is created
     *
     * @param savedInstanceState - The Bundle parsed to the Activity
     */
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

    /**
     * Called when the Activity state is changed to paused
     */
    @Override
    protected void onPause() {
        Log.d("GameView:onPause", "call");
        super.onPause();
        //this.a.pause();
        //this.paused = true;
        this.db.close();
    }

    /**
     * Called when a paused Activity is changed to resumed
     */
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

    /**
     * Called when the Activity is to be destroyed
     */
    @Override
    protected  void onDestroy() {
        Log.d("GameView:onDestroy", "call");
        super.onDestroy();
        //this.a.stop(0);
        this.paused = true;
        this.db.close();
    }

    /**
     * Called when a Toolbar item is selected
     *
     * @param item - The item that has been selected
     * @return - Whether or not to continue with the event
     */
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

    /**
     * Called when the Activity's instance state needs to be saved
     *
     * @param b - The Bundle of the Activity
     */
    @Override
    public void onSaveInstanceState(Bundle b) {
        Log.d("GameView:onSIS", "call");
        BundledState.handlerToBundle(b, this.a);
        super.onSaveInstanceState(b);
    }

    /**
     * Called when the Activity's instance state needs to be restored
     *
     * @param b - The Bundle state parsed
     */
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

    /**
     * Used to display a short toast message over the Activity
     *
     * @param text - The text to be shown inside the toast
     */
    private void toastMessage(String text) {
        Log.d("GameView:toastMessage", "call");
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
    }

    /**
     * Loads the size into this.size from the default SharedPreferences
     */
    private void loadSettings() {
        Log.d("GameView:loadSettings", "call");
        SharedPreferences s = PreferenceManager.getDefaultSharedPreferences(this);
        try {
            this.size = Integer.parseInt(s.getString(getString(R.string.pref_key_size), getString(R.string.pref_default_size)));
        } catch (NumberFormatException e) {
            Log.w("GameView:loadSettings", "Size or Difficulty isn't a number");
        }
    }

    /**
     * Called when the new game button registers a click
     *
     * @param view - The new game button view that was clicked
     */
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

    /**
     * Called when the stop game button registers a click
     *
     * @param view - The stop game button view that was clicked
     */
    public void onStopGameClick(View view) {
        Log.d("GameView:stopGameClick", "call id: " + view.getId());
        this.a.stop(0);
        this.newGame.setVisibility(View.VISIBLE);
        this.stopGame.setVisibility(View.INVISIBLE);
    }

    /**
     * Creates a new grid container (GameItemHandler) which overrides the previous one
     */
    private void createGridContainer() {
        Log.d("GameView:createGC", "call");
        this.a = new GameItemHandler(this.grid.getContext(), this.size);
        GameItemHandler.refreshSettings(this.grid);
        this.grid.setAdapter(a);
        this.grid.setOnItemClickListener(a);
        this.a.setOnGameFinishedListener(this);
    }

    /**
     * Called when the GameItemHandler registers that the user successfully completed the game
     */
    @Override
    public void onSuccess() {
        Log.d("GameView:onSuccess", "call");
        this.noTimer = true;
        this.paused = true;
        this.toastMessage(getString(R.string.text_win));
        this.newGame.setVisibility(View.VISIBLE);
        this.stopGame.setVisibility(View.INVISIBLE);
        this.db.insert(this.a.getTime(), this.size, false);
    }

    /**
     * Called when the GameItemHandler registers that the user lost the game
     */
    @Override
    public void onFail() {
        Log.d("GameView:onFail", "call");
        this.noTimer = true;
        this.paused = true;
        this.toastMessage(getString(R.string.text_lose));
        this.newGame.setVisibility(View.VISIBLE);
        this.stopGame.setVisibility(View.INVISIBLE);
        this.db.insert(this.a.getTime(), this.size, true);
    }

    /**
     * Called when the GameItemHandler registers that the game was stopped (not lost)
     */
    @Override
    public void onEnd() {
        Log.d("GameView:onEnd", "call");
        this.noTimer = true;
        this.paused = true;
        this.newGame.setVisibility(View.VISIBLE);
        this.stopGame.setVisibility(View.INVISIBLE);
        this.text.setText(getString(R.string.timer_text));
    }

    /**
     * <h1>GameSettingsView.UpdateTime</h1>
     * Class used to update the timer text
     *
     * @author Lee Tzilantonis
     * @version 1.0.0
     * @since 10/6/2018
     * @see Runnable
     */
    private class UpdateTime implements Runnable {

        /**
         * The instance of the GameView to update the text for
         */
        private GameView instance;

        /**
         * Instantiate the UpdateTime Runnable using the parsed instance as a reference
         *
         * @param instance - The reference to the GameView container containing the text view
         */
        private UpdateTime(GameView instance) {
            if (instance == null)
                throw new IllegalArgumentException("Cannot parse null instance");
            this.instance = instance;
        }

        /**
         * Called when the GameView requests a timer text update
         */
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
