package com.klutzybubbles.assignment1.activities;

import android.content.res.Configuration;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.ViewTreeObserver;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import com.klutzybubbles.assignment1.logic.GameItemHandler;

public class GameView extends AppCompatActivity {

    private GridView grid;
    private TextView text;
    private static GameView instance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_view);

        this.grid = findViewById(R.id.main_grid);
        this.text = findViewById(R.id.text_timer);
        System.out.println(this.getRequestedOrientation());
        System.out.println(this.getResources().getConfiguration().orientation);
        GameItemHandler a = new GameItemHandler(this.grid, this.grid.getContext(), 5, 5);
        this.grid.setAdapter(a);
        a.refreshSettings(this.grid);
        this.grid.setOnItemClickListener(a);
        if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            System.out.println("HIHIHHIHI");
            //this.grid.setColumnWidth(this.grid.getLayoutParams().height / 4);
            //ViewGroup.LayoutParams l = this.grid.getLayoutParams();
            //l.width = l.height;
            //this.grid.setLayoutParams(l);
            //DisplayMetrics d = this.getResources().getDisplayMetrics();
            System.out.println(this.grid.getLayoutParams().height);
            System.out.println(this.grid.getHeight());
            this.grid.setColumnWidth(this.grid.getLayoutParams().height / 4);
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
                    grid.setColumnWidth(height / 4);
                }
            });
        }
        GameView.instance = this;
    }

    public static GameView getInstance() {
        return GameView.instance;
    }

    public void setText(String text) {
        this.text.setText(text);
    }

    public void toastMessage(String text) {
        Toast.makeText(this, text, Toast.LENGTH_SHORT);
    }

}
