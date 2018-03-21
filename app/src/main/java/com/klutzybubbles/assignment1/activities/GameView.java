package com.klutzybubbles.assignment1.activities;

import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.drawable.GradientDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Layout;
import android.util.DisplayMetrics;
import android.view.ViewGroup;
import android.widget.GridView;

import com.klutzybubbles.assignment1.logic.GameItemHandler;

public class GameView extends AppCompatActivity {

    private GridView grid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_view);

        this.grid = findViewById(R.id.main_grid);
        System.out.println(this.getRequestedOrientation());
        System.out.println(this.getResources().getConfiguration().orientation);
        if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            System.out.println("HIHIHHIHI");
            //this.grid.setColumnWidth(this.grid.getLayoutParams().height / 4);
            //ViewGroup.LayoutParams l = this.grid.getLayoutParams();
            //l.width = l.height;
            //this.grid.setLayoutParams(l);
            DisplayMetrics d = this.getResources().getDisplayMetrics();
            this.grid.setColumnWidth(d.heightPixels - this.getParent().getActionBar().getHeight());
        }
        GameItemHandler a = new GameItemHandler(this.grid.getContext(), 4, 4);
        this.grid.setAdapter(a);
        a.refreshSettings(this.grid);
        this.grid.setOnItemClickListener(a);
    }

}
