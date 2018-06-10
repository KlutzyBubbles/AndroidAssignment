package com.klutzybubbles.assignment1.utils;

import android.content.Context;
import android.util.Log;

import com.klutzybubbles.assignment1.activities.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

/**
 * <h1>RecordItem.java</h1>
 * Class used to represent a record stored in the database
 *
 * @author Lee Tzilantonis
 * @version 1.0.0
 * @since 6/5/2018
 */
public class RecordItem {

    /**
     * The timestamp of the records time
     */
    private final long time;

    /**
     * The timestamp the record was set on
     */
    private final long setOn;

    /**
     * Instantiates the RecordItem
     *
     * @param time - The timestamp of the records time
     * @param setOn - The timestamp the record was set on
     */
    RecordItem(long time, long setOn) {
        Log.d("RecordItem:CONSTRUCT", "call");
        this.time = time;
        this.setOn = setOn;
    }

    /**
     * Gets the formatted record time
     *
     * @return - The formatted record time
     */
    String getTime(Context context) {
        Log.d("RecordItem:getTime", "call");
        String format = "%02d:%02d.%03d";
        if (context != null)
            format = context.getString(R.string.format_default_timer);
        Log.v("RecordItem:getTime", "Format - " + format);
        Log.v("RecordItem:getTime", "Time - " + this.time);
        return String.format(Locale.ENGLISH, format, TimeUnit.MILLISECONDS.toMinutes(this.time),
                TimeUnit.MILLISECONDS.toSeconds(time) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(time)),
                time % 1000);
    }

    /**
     * Gets the formatted date that the record was set on
     *
     * @return - The formatted date the record was set on
     */
    String getSetOn(Context context) {
        Log.d("RecordItem:getSetOn", "call");
        String format = "dd/MM/yyyy HH:mm";
        if (context != null)
            format = context.getString(R.string.format_default_date_time);
        Log.v("RecordItem:getSetOn", "Format - " + format);
        Log.v("RecordItem:getSetOn", "Set On - " + this.setOn);
        return (new SimpleDateFormat(format, Locale.ENGLISH)).format(new Date(this.setOn));
    }

}
