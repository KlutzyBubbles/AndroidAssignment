package com.klutzybubbles.assignment1.logic;

import android.util.Log;

import java.util.concurrent.TimeUnit;

/**
 * Created by KlutzyBubbles on 22/03/2018.
 */

public class GameTimer {

    private long[][] times;
    private String format;
    private boolean state;
    private boolean stopped = true;

    public GameTimer() {
        this(null);
    }

    public GameTimer(String format) {
        if (format == null || format.equals(""))
            format = "%02d:%02d";
        this.format = format;
        this.state = false;
    }

    public void start() {
        if (state)
            return;
        if (times == null || stopped) {
            this.stopped = false;
            this.state = true;
            this.times = new long[][]{{System.currentTimeMillis(), -1L}, {-1L, -1L}};
        } else {
            this.stopped = false;
            this.state = true;
            boolean reconstruct = false;
            for (int i = 0; i < this.times.length; i++) {
                if (this.times[i][0] == -1L) {
                    if (i + 1 == this.times.length)
                        reconstruct = true;
                    else
                        reconstruct = false;
                    this.times[i][0] = System.currentTimeMillis();
                } else
                    reconstruct = true;
            }
            if (reconstruct) {
                this.expand(1);
            }
        }
    }

    public void stop() {
        if (!this.state || this.stopped)
            return;
        this.state = false;
        this.stopped = true;
        for (int i = 0; i < this.times.length; i++) {
            if (this.times[i][1] == -1L) {
                if (this.times[i][0] == -1L)
                    return;
                this.times[i][1] = System.currentTimeMillis();
            }
        }
    }

    public void pause() {
        if (!this.state || this.stopped)
            return;
        this.state = false;
        this.stopped = false;
        boolean reconstruct = false;
        for (int i = 0; i < this.times.length; i++) {
            if (this.times[i][1] == -1L) {
                if (this.times[i][0] == -1L)
                    return;
                if (i + 1 == this.times.length)
                    reconstruct = true;
                else
                    reconstruct = false;
                this.times[i][1] = System.currentTimeMillis();
            } else
                reconstruct = false;
        }
        if (reconstruct)
            this.expand(1);
    }

    public void expand(int size) {
        if (size < this.times.length * 2)
            size = this.times.length * 2 + 1;
        long[][] temp = new long[size][2];
        for (int i = 0; i < temp.length; i++) {
            if (i < this.times.length) {
                temp[i][0] = this.times[i][0];
                temp[i][1] = this.times[i][1];
            } else {
                temp[i][0] = temp[i][1] = -1;
            }
        }
        this.times = temp;
    }

    public String getFormatted() {
        Log.v("GameTimer:getFormatted", "call");
        long time = 0L;
        for (int i = 0; i < this.times.length; i++) {
            Log.v("GameTimer:getFormatted", "Loop in times No. " + i);
            if (this.times[i][0] != -1L) {
                Log.v("GameTimer:getFormatted", "this.times[i][0] != -1L");
                if (this.times[i][1] == -1L) {
                    Log.v("GameTimer:getFormatted", "this.times[i][1] == -1");
                    time += System.currentTimeMillis() - this.times[i][0];
                } else {
                    Log.v("GameTimer:getFormatted", "this.times[i][1] != -1");
                    time += this.times[i][1] - this.times[i][0];
                }
            }
            Log.v("GameTimer:getFormatted", "time = " + time);
        }
        return String.format(this.format, TimeUnit.MILLISECONDS.toMinutes(time),
                TimeUnit.MILLISECONDS.toSeconds(time) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(time)));

        /*
        long hours = time / TimeUnit.HOURS.toMillis(1);
        long minutes = time - TimeUnit.HOURS.toMillis(hours) / TimeUnit.MINUTES.toMillis(1);
        long seconds = time - TimeUnit.HOURS.toMillis(hours) - TimeUnit.MINUTES.toMillis(minutes) / TimeUnit.SECONDS.toMillis(1);
        long millis= time - TimeUnit.HOURS.toMillis(hours) - TimeUnit.MINUTES.toMillis(minutes) - TimeUnit.SECONDS.toMillis(seconds) / TimeUnit.SECONDS.toMillis(1);

        String temp = this.format;
        temp.replace("hh", String.valueOf(hours));
        temp.replace("mm", String.valueOf(minutes));
        temp.replace("ss", String.valueOf(seconds));
        temp.replace("i", String.valueOf(millis / 100));
        temp.replace("ii", String.valueOf(millis / 10));
        temp.replace("iii", String.valueOf(millis));

        String h = String.valueOf(hours);
        String m = String.valueOf(hours);
        String s = String.valueOf(hours);
        String i = String.valueOf(hours);
        */
    }

}
