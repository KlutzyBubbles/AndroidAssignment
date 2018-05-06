package com.klutzybubbles.assignment1.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class RecordItem {

    private long time;
    private long setOn;

    RecordItem(long time, long setOn) {
        this.time = time;
        this.setOn = setOn;
    }

    public String getTime() {
        return String.format(Locale.ENGLISH,"%02d:%02d.%03d", TimeUnit.MILLISECONDS.toMinutes(this.time),
                TimeUnit.MILLISECONDS.toSeconds(time) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(time)),
                time % 1000);
    }

    public String getTimestamp() {
        return (new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.ENGLISH)).format(new Date(this.setOn));
    }

}
