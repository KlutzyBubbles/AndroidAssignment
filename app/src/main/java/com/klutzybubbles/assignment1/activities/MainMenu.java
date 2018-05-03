package com.klutzybubbles.assignment1.activities;

import android.app.ActionBar;
import android.content.Intent;
import android.net.Uri;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridView;

import com.klutzybubbles.assignment1.logic.GameItemHandler;

public class MainMenu extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        Button b = findViewById(R.id.button_game);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), GameView.class);
                startActivity(i);
            }
        });
        b = findViewById(R.id.button_high_scores);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), GameView.class);
                startActivity(i);
            }
        });
        b = findViewById(R.id.button_settings);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), GameSettings.class);
                startActivity(i);
            }
        });
        b = findViewById(R.id.button_help);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.help_doc)));
                startActivity(browserIntent);
                //Intent i = new Intent(getApplicationContext(), HelpScreen.class);
                //startActivity(i);
            }
        });
    }
}
