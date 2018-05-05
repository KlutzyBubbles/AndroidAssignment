package com.klutzybubbles.assignment1.logic;

import android.util.Log;

import java.util.concurrent.TimeUnit;

/**
 * <h1>GameTimer.class</h1>
 * Class used to manage the timer and it's operations
 *
 * @author Lee Tzilantonis
 * @version 1.0.0
 * @since 5/5/2018
 */
public class GameTimer {

    private long startedFrom;

    private long current = 0L;

    /**
     * The format of the String output, defaults to %02d:%02d.%03d
     */
    private String format;

    /**
     * The current state of the GameTimer, paused or running
     */
    private boolean state = false;

    /**
     * The current end state of the GameTimer, stopped or running
     */
    private boolean stopped = true;

    /**
     * Instantiates the GameTimer with the default format (%02d:%02d.%03d)
     */
    GameTimer() {
        this(null);
    }

    /**
     * Instantiates the GameTimer with a custom format
     *
     * @param format - The format of the output
     */
    private GameTimer(String format) {
        Log.d("GameTimer:CONSTRUCT", "call");
        if (format == null || format.equals(""))
            format = "%02d:%02d.%03d";
        Log.i("GameTimer:CONSTRUCT", "format: " + format);
        this.format = format;
    }

    /**
     * Starts the GameTimer, erasing previous times if the timer was stopped, or resuming if the
     * GameTimer was previously paused
     */
    protected void start() {
        Log.d("GameTimer:start", "call");
        if (state)
            return;
        if (stopped)
            this.current = 0;
        this.startedFrom = System.currentTimeMillis();
        this.stopped = false;
        this.state = true;
    }

    /**
     * Stops the GameTimer, this causes the start method to erase the current time for a fresh time
     */
    protected void stop() {
        Log.d("GameTimer:stop", "call");
        if (!this.state && !this.stopped) {
            this.stopped = true;
            return;
        }
        if (!this.state || this.stopped)
            return;
        this.state = false;
        this.stopped = true;
        this.current += System.currentTimeMillis() - this.startedFrom;
        this.startedFrom = 0;
    }

    /**
     * Pauses the GameTimer, saving any times so that the start method can resume where it paused
     */
    protected void pause() {
        Log.d("GameTimer:pause", "call");
        if (!this.state || this.stopped)
            return;
        this.state = false;
        this.stopped = false;
        this.current += System.currentTimeMillis() - this.startedFrom;
        this.startedFrom = 0;
    }

    /**
     * Gets the formatted String representation of the timers current time, regardless of it's state
     *
     * @return - Formatted time String
     */
    protected String getFormatted() {
        Log.v("GameTimer:getFormatted", "call");
        long time = this.getRaw();
        return String.format(this.format, TimeUnit.MILLISECONDS.toMinutes(time),
                TimeUnit.MILLISECONDS.toSeconds(time) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(time)),
                time % 1000);
    }

    /**
     * Gets the raw time (milliseconds) recorded with the GameTimer Object
     *
     * @return - Raw time long
     */
    protected long getRaw() {
        Log.v("GameTimer:getRaw", "call");
        if (!this.state || this.stopped)
            return this.current;
        return this.current + (System.currentTimeMillis() - this.startedFrom);
    }

}
