package com.klutzybubbles.assignment1.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.klutzybubbles.assignment1.interfaces.OnNavigationClickListener;
import com.klutzybubbles.assignment1.utils.DatabaseHelper;
import com.klutzybubbles.assignment1.utils.RecordItem;
import com.klutzybubbles.assignment1.utils.RecordListAdapter;

import java.util.ArrayList;
import java.util.List;

public class ScoreboardView extends android.support.v4.app.Fragment {

    private int selectedSize = 3;

    private ProgressBar bar;

    private RecyclerView list;

    private DatabaseHelper db;

    private RecordListAdapter a = new RecordListAdapter(new ArrayList<>());

    private View view;

    @Override
    public View onCreateView(LayoutInflater i, ViewGroup g, Bundle b) {
        if (this.view == null)
            this.view = i.inflate(R.layout.activity_scoreboard_view, g, false);
        return this.view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        /*
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(R.string.title_activity_scoreboard_view);
        }*/

        if (this.view == null || this.getActivity() == null)
            return;

        this.db = new DatabaseHelper(this.view.getContext(), this.getResources().getInteger(R.integer.database_version));
        this.bar = this.view.findViewById(R.id.loading);
        this.list = this.view.findViewById(R.id.recycle_list);

        this.list.setAdapter(a);
        this.list.setItemAnimator(new DefaultItemAnimator());
        this.list.setLayoutManager(new LinearLayoutManager(this.view.getContext()));
        this.list.addItemDecoration(new DividerItemDecoration(this.view.getContext(), LinearLayoutManager.VERTICAL));

        Spinner size = this.view.findViewById(R.id.spinner_size);

        String[] sizeNames = this.view.getResources().getStringArray(R.array.list_size);
        SelectionAdapter a = new SelectionAdapter(this.getActivity(), R.layout.spinner_item, R.id.text_item, sizeNames);
//size.setBackgroundColor(Color.BLACK);
        //ArrayAdapter<String> a = new ArrayAdapter<>(this.getActivity(), android.R.layout.simple_spinner_item, sizeNames);
        //a.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        size.setAdapter(a);

        //size.setSelection(2);

        size.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedSize = Integer.parseInt(size.getSelectedItem().toString());
                refresh();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                refresh();
            }
        });

        OnNavigationClickListener l;
        try {
            l = (OnNavigationClickListener) this.getActivity();
        } catch (ClassCastException e) {
            return;
        }
        Button b = view.findViewById(R.id.button_home);
        b.setOnClickListener(v -> l.onClick(SplashScreen.MAIN_MENU));

        this.refresh();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (this.getView() == null)
            return false;
        int id = item.getItemId();
        if (id == android.R.id.home) {
            startActivity(new Intent(this.getView().getContext(), MainMenuView.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void refresh() {
        this.bar.setVisibility(View.VISIBLE);
        this.list.setVisibility(View.INVISIBLE);
        final ScoreboardView inst = this;
        (new Thread(new Runnable() {
            @Override
            public void run() {
                final List<RecordItem> items = db.getAllFrom(selectedSize);
                inst.getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        a.setRecords(items);
                        list.setVisibility(View.VISIBLE);
                        bar.setVisibility(View.INVISIBLE);
                    }
                });
            }
        })).start();
    }

    private class SelectionAdapter extends ArrayAdapter<String> {

        private final LayoutInflater inflater;

        public SelectionAdapter(@NonNull Activity context, int resource, int textViewResourceId, @NonNull String[] objects) {
            super(context, resource, textViewResourceId, objects);
            this.inflater = context.getLayoutInflater();
        }

        @Override
        public View getView(int pos, View view, ViewGroup group) {
            View r = this.inflater.inflate(R.layout.spinner_item, null, false);
            TextView item = r.findViewById(R.id.text_item);
            item.setText(super.getItem(pos));
            return r;
        }
    }

}
