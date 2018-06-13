package com.klutzybubbles.threeinarow.activities;

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
import com.klutzybubbles.threeinarow.interfaces.OnNavigationClickListener;
import com.klutzybubbles.threeinarow.utils.DatabaseHelper;
import com.klutzybubbles.threeinarow.utils.RecordItem;
import com.klutzybubbles.threeinarow.utils.RecordListAdapter;
import com.klutzybubbles.threeinarow.utils.StringListAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * <h1>ScoreboardView.java</h1>
 * Class used as a Fragment that contains the content for the Scoreboard Screen
 *
 * @author Lee Tzilantonis
 * @version 1.0.0
 * @since 10/6/2018
 * @see android.support.v4.app.Fragment
 */
public class ScoreboardView extends android.support.v4.app.Fragment {

    /**
     * The current selected grid size
     */
    private int selectedSize = 3;

    /**
     * The progress bar used to overlay database updating
     */
    private ProgressBar bar;

    /**
     * The RecyclerView containing overall stats
     */
    private RecyclerView list;

    /**
     * The ConstraintLayout that contains the list and button for all records
     */
    private ConstraintLayout allLayout;

    /**
     * The DatabaseHelper used to access the data
     */
    private DatabaseHelper db;

    /**
     * Record adapter used to display all success records for the selected grid size
     */
    private final RecordListAdapter a = new RecordListAdapter(new ArrayList<>());

    /**
     * String adapter used to display all overall stats for the selected grid size
     */
    private final StringListAdapter s = new StringListAdapter(new ArrayList<>());

    /**
     * Main view that holds all content for the Scoreboard Screen
     */
    private View view;

    /**
     * Called when the View within the fragment is to be inflated
     *
     * @param i - The LayoutInflater to be used
     * @param g - The ViewGroup the view should be contained within
     * @param b - The Bundle object parsed to the View
     * @return - The created View
     */
    @Override
    public View onCreateView(@NonNull LayoutInflater i, ViewGroup g, Bundle b) {
        if (this.view == null)
            this.view = i.inflate(R.layout.activity_scoreboard_view, g, false);
        return this.view;
    }

    /**
     * Called when the containing activity has been created, letting us know is is safe to use
     * getActivity()
     *
     * @param savedInstanceState - The Bundle state parsed to the Activity
     */
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
        size.setAdapter(a);

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
        b.setOnClickListener(v -> l.requestChange(SplashScreen.MAIN_MENU));

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

    /**
     * Refreshes the data on screen to show updated data based on the grid size selected
     */
    private void refresh() {
        this.bar.setVisibility(View.VISIBLE);
        this.list.setVisibility(View.INVISIBLE);
        this.allLayout.setVisibility(View.INVISIBLE);
        final ScoreboardView inst = this;
        (new Thread(() -> {
            final List<RecordItem> items = db.getAllFrom(selectedSize);
            final List<String> records = db.getInfo(selectedSize);
            Activity ac = inst.getActivity();
            if (ac != null) {
                inst.getActivity().runOnUiThread(() -> {
                    a.setRecords(items);
                    s.setRecords(records);
                    list.setVisibility(View.VISIBLE);
                    bar.setVisibility(View.INVISIBLE);
                });
            }
        })).start();
    }

    /**
     * <h1>ScoreboardView.SelectionAdapter</h1>
     * Class used to inflate the grid selection options
     *
     * @author Lee Tzilantonis
     * @version 1.0.0
     * @since 10/6/2018
     * @see ArrayAdapter
     */
    private class SelectionAdapter extends ArrayAdapter<String> {

        /**
         * The LayoutInflater to use to inflate the spinner item View's
         */
        private final LayoutInflater inflater;

        SelectionAdapter(@NonNull Activity context, @NonNull String[] objects) {
            super(context, R.layout.spinner_item, R.id.text_item, objects);
            this.inflater = context.getLayoutInflater();
        }

        /**
         * Gets the view that is to be placed in the spinner in the defined position
         *
         * @param pos - The position the View will be in
         * @param view - The recycled View used before (if any)
         * @param group - The ViewGroup of the spinner items
         * @return - The created View to be inserted
         */
        @NonNull
        @Override
        public View getView(int pos, View view, @NonNull ViewGroup group) {
            if (view != null)
                return view;
            else {
                View r = this.inflater.inflate(R.layout.spinner_item, group, false);
                ((TextView) r.findViewById(R.id.text_item)).setText(super.getItem(pos));
                return r;
            }
        }
    }

}
