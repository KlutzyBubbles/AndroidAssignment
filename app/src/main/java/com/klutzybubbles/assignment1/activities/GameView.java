package com.klutzybubbles.assignment1.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import com.klutzybubbles.assignment1.logic.GameItem;
import com.klutzybubbles.assignment1.logic.GameItemHandler;

public class GameView extends AppCompatActivity {

    private int width, height, difficulty;

    private GridView grid;
    private TextView text;

    private GameItemHandler a;

    private Button newGame;

    private boolean paused = false;

    private GameItem next;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Log.d("GameView:onCreateOpti..", "call");
        MenuInflater i = getMenuInflater();
        i.inflate(R.menu.game_menu, menu);
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("GameView:onCreate", "call");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_view);

        this.grid = findViewById(R.id.main_grid);
        this.text = findViewById(R.id.text_timer);
        this.newGame = findViewById(R.id.button_new_game);
        this.next = new GameItem(this);
        ((ViewGroup) findViewById(R.id.next_item)).addView(this.next);
        System.out.println(this.getRequestedOrientation());
        System.out.println(this.getResources().getConfiguration().orientation);
        this.initialStart();
        if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            System.out.println("HIHIHHIHI");
            //this.grid.setColumnWidth(this.grid.getLayoutParams().height / 4);
            //ViewGroup.LayoutParams l = this.grid.getLayoutParams();
            //l.width = l.height;
            //this.grid.setLayoutParams(l);
            //DisplayMetrics d = this.getResources().getDisplayMetrics();
            System.out.println(this.grid.getLayoutParams().height);
            System.out.println(this.grid.getHeight());
            this.grid.setColumnWidth(this.grid.getLayoutParams().height / 5);
            this.grid.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    System.out.println(grid.getLayoutParams().height);
                    System.out.println(grid.getHeight());
                    ConstraintLayout.LayoutParams l = (ConstraintLayout.LayoutParams) grid.getLayoutParams();
                    ConstraintLayout c = findViewById(R.id.game_constraint);
                    System.out.println(c.getHeight());
                    int height = c.getHeight() - grid.getPaddingTop() - grid.getPaddingBottom() - l.topMargin - l.bottomMargin;
                    l.width = height;
                    l.height = height;
                    grid.setLayoutParams(l);
                    grid.setColumnWidth(height / 5);
                }
            });
        }
    }

    @Override
    protected void onPause() {
        Log.d("GameView:onPause", "call");
        super.onPause();
        this.a.pause();
        this.paused = true;
    }

    @Override
    protected void onResume() {
        Log.d("GameView:onResume", "call");
        super.onResume();
        this.a.start();
        this.paused = false;
    }

    @Override
    protected  void onDestroy() {
        Log.d("GameView:onDestroy", "call");
        super.onDestroy();
        this.a.stop(0);
        this.paused = true;
    }

    public void setText(String text) {
        Log.d("GameView:setText", "call");
        this.text.setText(text);
    }

    public void setNext(int state) {
        Log.d("GameView:setNext", "call");
        this.next.setStateOverride(state);
        Log.i("GameView:GameItem-next", next.toString());
    }

    public void toastMessage(String text) {
        Log.d("GameView:toastMessage", "call");
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
    }

    private void loadSettings() {
        Log.d("GameView:loadSettings", "call");
        SharedPreferences s = this.getSharedPreferences(getString(R.string.shared_preferences), Context.MODE_PRIVATE);
        this.width = s.getInt("width", GameItemHandler.MIN_WIDTH);
        this.height = s.getInt("height", GameItemHandler.MIN_HEIGHT);
        this.difficulty = s.getInt("difficulty", 1);
    }

    public void initialStart() {
        Log.d("GameView:initialStart", "call");
        this.a = new GameItemHandler(this, this.grid.getContext(), 5, 5, 3);
        this.grid.setAdapter(a);
        a.refreshSettings(this.grid);
        this.grid.setOnItemClickListener(a);
    }

    public void onNewGameClick(View view) {
        Log.d("GameView:onNewGameClick", "call");
        ((ViewGroup) findViewById(R.id.next_item)).removeAllViews();
        ((ViewGroup) findViewById(R.id.next_item)).addView(this.next);
        this.a.stop(-1);
        this.a = new GameItemHandler(this, this.grid.getContext(), 5, 5, 3);
        this.grid.setAdapter(a);
        a.refreshSettings(this.grid);
        this.grid.setOnItemClickListener(a);
        this.newGame.setEnabled(false);
        this.a.start();
        this.paused = false;
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
        this.a.stop(0);
    }

    public void onFail() {
        Log.d("GameView:onFail", "call");
        this.paused = true;
        this.toastMessage("You Failed");
        this.newGame.setEnabled(true);
        this.next.setStateOverride(0);
        this.next = new GameItem(this);
    }

    public void onSuccess() {
        Log.d("GameView:onSuccess", "call");
        this.paused = true;
        this.toastMessage("You Win");
        this.newGame.setEnabled(true);
        this.next.setStateOverride(0);
        this.next = new GameItem(this);
    }

    public void onEnd() {
        Log.d("GameView:onEnd", "call");
        this.paused = true;
        this.toastMessage("Game Stopped");
        this.newGame.setEnabled(true);
        this.next.setStateOverride(0);
        this.next = new GameItem(this);
    }

    private class UpdateTime implements Runnable {

        private GameView instance;

        public UpdateTime(GameView instance) {
            if (instance == null)
                throw new IllegalArgumentException("Cannot parse null instance");
            this.instance = instance;
        }

        @Override
        public void run() {
            while (!paused) {
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
