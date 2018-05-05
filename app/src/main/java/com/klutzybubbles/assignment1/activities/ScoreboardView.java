package com.klutzybubbles.assignment1.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.Spinner;

import com.klutzybubbles.assignment1.utils.DatabaseHelper;
import com.klutzybubbles.assignment1.utils.RecordItem;
import com.klutzybubbles.assignment1.utils.RecordListAdapter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ScoreboardView extends AppCompatActivity {

    private List<String> sizeValues, difficultyValues;

    private int selectedSize = 3, selectedDifficulty = 4;

    private ProgressBar bar;

    private RecyclerView list;

    private DatabaseHelper db;

    private RecordListAdapter a = new RecordListAdapter(new ArrayList<RecordItem>());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scoreboard_view);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null)
            actionBar.setDisplayHomeAsUpEnabled(true);

        this.sizeValues = new ArrayList<>(Arrays.asList(this.getResources().getStringArray(R.array.list_size)));
        this.difficultyValues = new ArrayList<>(Arrays.asList(this.getResources().getStringArray(R.array.list_difficulty)));

        this.db = new DatabaseHelper(this, this.getResources().getInteger(R.integer.database_version));
        this.bar = this.findViewById(R.id.loading);
        this.list = this.findViewById(R.id.recycle_list);

        this.list.setAdapter(a);
        this.list.setItemAnimator(new DefaultItemAnimator());
        this.list.setLayoutManager(new LinearLayoutManager(this.getApplicationContext()));
        this.list.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));

        Spinner size = this.findViewById(R.id.spinner_size);
        Spinner difficulty = this.findViewById(R.id.spinner_difficulty);

        size.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item,
                new ArrayList<>(Arrays.asList(this.getResources().getStringArray(R.array.list_size_names)))));
        difficulty.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item,
                new ArrayList<>(Arrays.asList(this.getResources().getStringArray(R.array.list_difficulty_names)))));

        size.setSelection(2);
        difficulty.setSelection(4);

        size.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedSize = Integer.parseInt(sizeValues.get(position));
                refresh();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                refresh();
            }
        });

        difficulty.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedDifficulty = Integer.parseInt(difficultyValues.get(position));
                refresh();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                refresh();
            }
        });

        this.refresh();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            startActivity(new Intent(this, MainMenu.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void refresh() {
        this.bar.setVisibility(View.VISIBLE);
        this.list.setVisibility(View.INVISIBLE);
        this.a.setRecords(db.getAllFrom(this.selectedSize, this.selectedDifficulty));
        this.list.setVisibility(View.VISIBLE);
        this.bar.setVisibility(View.INVISIBLE);
    }

}
