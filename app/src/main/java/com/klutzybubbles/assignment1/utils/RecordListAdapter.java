package com.klutzybubbles.assignment1.utils;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.klutzybubbles.assignment1.activities.R;

import java.util.List;

public class RecordListAdapter extends RecyclerView.Adapter<RecordListAdapter.RecordViewHolder> {

    private List<RecordItem> records;

    public RecordListAdapter(List<RecordItem> records) {
        if (records == null)
            throw new IllegalArgumentException("There must be a list to work with");
        this.records = records;
    }

    public void setRecords(List<RecordItem> records) {
        if (records == null)
            return;
        this.records = records;
        this.notifyDataSetChanged();
    }

    @Override
    public RecordViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View recordView = LayoutInflater.from(parent.getContext()).inflate(R.layout.record_list_row, parent, false);
        return new RecordViewHolder(recordView);
    }

    @Override
    public void onBindViewHolder(RecordViewHolder holder, int position) {
        RecordItem r = this.records.get(position);
        holder.time.setText(r.getTime());
        holder.timestamp.setText(r.getTimestamp());
    }

    @Override
    public int getItemCount() {
        return this.records.size();
    }

    public class RecordViewHolder extends RecyclerView.ViewHolder {

        public TextView time, timestamp;

        public RecordViewHolder(View view) {
            super(view);
            this.time = view.findViewById(R.id.text_time);
            this.timestamp = view.findViewById(R.id.text_timestamp);
        }

    }

}
