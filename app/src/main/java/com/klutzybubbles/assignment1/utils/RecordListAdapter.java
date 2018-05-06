package com.klutzybubbles.assignment1.utils;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.klutzybubbles.assignment1.activities.R;

import java.util.List;

/**
 * <h1>RecordListAdapter.java</h1>
 * Class used to format the high score records into a RecyclerView
 *
 * @author Lee Tzilantonis
 * @version 1.0.0
 * @since 6/5/2018
 * @see RecyclerView
 */
public class RecordListAdapter extends RecyclerView.Adapter<RecordListAdapter.RecordViewHolder> {

    /**
     * All records to be contained within the Adapter
     */
    private List<RecordItem> records;

    /**
     * Instantiates the RecordListAdapter using the records provided
     *
     * @param records - The records to be formatted
     */
    public RecordListAdapter(List<RecordItem> records) {
        Log.d("RecordLA:CONSTRUCT", "call");
        if (records == null)
            throw new IllegalArgumentException("There must be a list to work with");
        this.records = records;
    }

    /**
     * Sets the List of records to the new data set and updates the Adapter
     *
     * @param records - The List of updates records
     */
    public void setRecords(List<RecordItem> records) {
        Log.d("RecordLA:setRecords", "call");
        if (records == null)
            return;
        this.records = records;
        this.notifyDataSetChanged();
    }

    /**
     * Listener for when a ViewHolder is to be created
     *
     * @param parent - The parent view that will contain the ViewHolder
     * @param viewType - The type of view created (IGNORED)
     * @return - The updated ViewHolder to be added
     */
    @NonNull
    @Override
    public RecordViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Log.v("RecordLA:onCreateVH", "call");
        View recordView = LayoutInflater.from(parent.getContext()).inflate(R.layout.record_list_row, parent, false);
        return new RecordViewHolder(recordView);
    }

    /**
     * Listener for when a ViewHolder is bound to a row
     *
     * @param holder - The ViewHolder to be bound
     * @param position - The position of the row of data to be inserted
     */
    @Override
    public void onBindViewHolder(@NonNull RecordViewHolder holder, int position) {
        Log.v("RecordLA:onBindVH", "call");
        RecordItem r = this.records.get(position);
        holder.getTime().setText(r.getTime(holder.getContext()));
        holder.getTimestamp().setText(r.getSetOn(holder.getContext()));
    }

    /**
     * Gets the amount of items in the List of records
     *
     * @return - The amount of items in the List of records
     */
    @Override
    public int getItemCount() {
        Log.d("RecordLA:getItemCount", "call");
        return this.records.size();
    }

    /**
     * <h1>RecordListAdapter.RecordViewHolder.class</h1>
     * Class used to hold the layout for the record data to be viewed on
     *
     * @author Lee Tzilantonis
     * @version 1.0.0
     * @since 6/5/2018
     */
    class RecordViewHolder extends RecyclerView.ViewHolder {

        /**
         * The context used to get formats from strings.xml
         */
        private Context context;

        /**
         * The TextView's that are to contain the records data respectively
         */
        private TextView time, timestamp;

        /**
         * Instantiates the RecordViewHolder using the View as its context and layout
         *
         * @param view - The View to extract context and layout from
         */
        private RecordViewHolder(@NonNull View view) {
            super(view);
            Log.d("RecordVH:CONSTRUCT", "call");
            this.context = view.getContext();
            this.time = view.findViewById(R.id.text_time);
            this.timestamp = view.findViewById(R.id.text_timestamp);
        }

        /**
         * Gets the RecordViewHolder's context
         *
         * @return - The RecordViewHolder's context
         */
        private Context getContext() {
            Log.d("RecordVH:getContext", "call");
            return context;
        }

        /**
         * Gets the time TextView
         *
         * @return - The time TextView
         */
        private TextView getTime() {
            Log.d("RecordVH:getTime", "call");
            return time;
        }

        /**
         * Gets the timestamp TextView
         *
         * @return - The timestamp TextView
         */
        private TextView getTimestamp() {
            Log.d("RecordVH:getTimestamp", "call");
            return timestamp;
        }

    }

}
