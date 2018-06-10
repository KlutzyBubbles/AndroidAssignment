package com.klutzybubbles.assignment1.activities;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.getkeepsafe.taptargetview.TapTarget;
import com.getkeepsafe.taptargetview.TapTargetSequence;
import com.klutzybubbles.assignment1.interfaces.OnNavigationClickListener;
import com.klutzybubbles.assignment1.utils.DatabaseHelper;
import com.klutzybubbles.assignment1.utils.RecordItem;
import com.klutzybubbles.assignment1.utils.RecordListAdapter;
import com.klutzybubbles.assignment1.utils.StringListAdapter;

import java.util.ArrayList;
import java.util.List;

public class ScoreboardView extends android.support.v4.app.Fragment {

    private int selectedSize = 3;

    private ProgressBar bar;

    private RecyclerView list;

    private ConstraintLayout allLayout;

    private DatabaseHelper db;

    private final RecordListAdapter a = new RecordListAdapter(new ArrayList<>());
    private final StringListAdapter s = new StringListAdapter(new ArrayList<>());

    private View view;

    @Override
    public View onCreateView(@NonNull LayoutInflater i, ViewGroup g, Bundle b) {
        if (this.view == null)
            this.view = i.inflate(R.layout.activity_scoreboard_view, g, false);
        return this.view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (this.view == null || this.getActivity() == null)
            return;

        this.db = new DatabaseHelper(this.view.getContext(), this.getResources().getInteger(R.integer.database_version));
        this.bar = this.view.findViewById(R.id.loading);
        this.list = this.view.findViewById(R.id.recycle_list);
        RecyclerView all = this.view.findViewById(R.id.recycle_list_all);
        this.allLayout = this.view.findViewById(R.id.all_layout);

        this.list.setAdapter(s);
        all.setAdapter(a);
        this.list.setItemAnimator(new DefaultItemAnimator());
        this.list.setLayoutManager(new LinearLayoutManager(this.view.getContext()));
        this.list.addItemDecoration(new DividerItemDecoration(this.view.getContext(), LinearLayoutManager.VERTICAL));
        all.setItemAnimator(new DefaultItemAnimator());
        all.setLayoutManager(new LinearLayoutManager(this.view.getContext()));
        all.addItemDecoration(new DividerItemDecoration(this.view.getContext(), LinearLayoutManager.VERTICAL));

        Spinner size = this.view.findViewById(R.id.spinner_size);

        String[] sizeNames = this.view.getResources().getStringArray(R.array.list_size);
        SelectionAdapter a = new SelectionAdapter(this.getActivity(), sizeNames);
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

        Button bAll = view.findViewById(R.id.button_all);
        bAll.setOnClickListener(v -> allLayout.setVisibility(View.VISIBLE));

        Button bHide = view.findViewById(R.id.button_hide);
        bHide.setOnClickListener(v -> allLayout.setVisibility(View.INVISIBLE));

        ImageView help = view.findViewById(R.id.button_help);

        View[] targets = new View[4];

        targets[0] = size;
        targets[1] = bAll;
        targets[2] = bHide;
        targets[3] = b;

        String[] title = this.getResources().getStringArray(R.array.scoreboard_help_titles);
        String[] text = this.getResources().getStringArray(R.array.scoreboard_help_text);

        help.setOnClickListener(v -> {
            TapTargetSequence seq = new TapTargetSequence(this.getActivity());
            for (int i = 0; i < targets.length; i++) {
                if (i == 0 || i == 2) {
                    seq.target(TapTarget.forView(targets[i], title[i], text[i])
                            .cancelable(true)
                            .transparentTarget(true)
                            .textColor(R.color.White)
                            .outerCircleColor(R.color.RoyalBlue)
                            .outerCircleAlpha(0.95F)
                            .targetRadius(10)
                            .drawShadow(true)
                            .dimColor(R.color.Black));
                } else {
                    seq.target(TapTarget.forView(targets[i], title[i], text[i])
                            .cancelable(true)
                            .transparentTarget(true)
                            .textColor(R.color.White)
                            .outerCircleColor(R.color.RoyalBlue)
                            .outerCircleAlpha(0.95F)
                            .targetRadius(70)
                            .drawShadow(true)
                            .dimColor(R.color.Black));
                }
            }
            final int[] count = {0};
            seq.listener(new TapTargetSequence.Listener() {
                @Override
                public void onSequenceFinish() {

                }

                @Override
                public void onSequenceStep(TapTarget lastTarget, boolean targetClicked) {
                    if (count[0] == 1)
                        allLayout.setVisibility(View.VISIBLE);
                    else if (allLayout.getVisibility() == View.VISIBLE)
                        allLayout.setVisibility(View.INVISIBLE);
                    count[0]++;
                }

                @Override
                public void onSequenceCanceled(TapTarget lastTarget) {

                }
            });
            seq.continueOnCancel(true).considerOuterCircleCanceled(true);
            seq.start();
        });

        this.refresh();
    }

    private void refresh() {
        this.bar.setVisibility(View.VISIBLE);
        this.list.setVisibility(View.INVISIBLE);
        this.allLayout.setVisibility(View.INVISIBLE);
        final ScoreboardView inst = this;
        (new Thread(() -> {
            final List<RecordItem> items = db.getAllFrom(selectedSize);
            final List<String> records = db.getInfo(selectedSize);
            //noinspection ConstantConditions
            inst.getActivity().runOnUiThread(() -> {
                a.setRecords(items);
                s.setRecords(records);
                list.setVisibility(View.VISIBLE);
                bar.setVisibility(View.INVISIBLE);
            });
        })).start();
    }

    private class SelectionAdapter extends ArrayAdapter<String> {

        private final LayoutInflater inflater;

        SelectionAdapter(@NonNull Activity context, @NonNull String[] objects) {
            super(context, R.layout.spinner_item, R.id.text_item, objects);
            this.inflater = context.getLayoutInflater();
        }

        @NonNull
        @Override
        public View getView(int pos, View view, @NonNull ViewGroup group) {
            @SuppressLint("ViewHolder") View r = this.inflater.inflate(R.layout.spinner_item, group, false);
            TextView item = r.findViewById(R.id.text_item);
            item.setText(super.getItem(pos));
            return r;
        }
    }

}
