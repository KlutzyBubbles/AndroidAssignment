package com.klutzybubbles.assignment1.logic;

import android.util.Log;

import java.util.concurrent.TimeUnit;

/**
 * <h1>GameTimer.java</h1>
 * Class used to manage the timer and it's operations
 *
 * @author Lee Tzilantonis
 * @version 1.0.0
 * @since 6/5/2018
 */
public class GameTimer {

    /**
     * The timestamp in which the current state of the timer has started from
     */
    private long startedFrom;

    /**
     * The amount of milliseconds currently recorded
     */
    private long current = 0L;

    /**
     * The format of the String output, defaults to %02d:%02d.%03d
     */
    private String format;

    /**
     * The current state of the GameTimer, paused or running
     */
    private boolean paused = false;

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
        if (paused)
            return;
        if (stopped)
            this.current = 0;
        this.startedFrom = System.currentTimeMillis();
        this.stopped = false;
        this.paused = true;
    }

    /**
     * Stops the GameTimer, this causes the start method to erase the current time for a fresh time
     */
    protected void stop() {
        Log.d("GameTimer:stop", "call");
        if (!this.paused && !this.stopped) {
            Log.d("GameTimer:stop", "Already paused, not stopped");
            this.stopped = true;
            return;
        }
        if (!this.paused || this.stopped) {
            Log.d("GameTimer:stop", "Already paused / stopped");
            return;
        }
        this.paused = false;
        this.stopped = true;
        long now = System.currentTimeMillis();
        Log.v("GameTimer:pause", "Current Time - " + now);
        Log.v("GameTimer:pause", "Started from - " + this.startedFrom);
        Log.v("GameTimer:pause", "Current Total - " + this.current);
        this.current += now - this.startedFrom;
        Log.v("GameTimer:pause", "Current Total After - " + this.current);
        this.startedFrom = 0;
    }

    /**
     * Pauses the GameTimer, saving any times so that the start method can resume where it paused
     */
    protected void pause() {
        Log.d("GameTimer:pause", "call");
        if (!this.paused || this.stopped) {
            Log.d("GameTimer:pause", "Already paused / stopped");
            return;
        }
        this.paused = false;
        this.stopped = false;
        long now = System.currentTimeMillis();
        Log.v("GameTimer:pause", "Current Time - " + now);
        Log.v("GameTimer:pause", "Started from - " + this.startedFrom);
        Log.v("GameTimer:pause", "Current Total - " + this.current);
        this.current += now - this.startedFrom;
        Log.v("GameTimer:pause", "Current Total After - " + this.current);
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
        Log.v("GameTimer:getFormatted", "Time - " + time);
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
        if (!this.paused || this.stopped) {
            Log.v("GameTimer:getRaw", "Static time returned - " + this.current);
            return this.current;
        }
        long r = this.current + (System.currentTimeMillis() - this.startedFrom);
        Log.v("GameTimer:getRaw", "Moving time returned - " + r);
        return r;
    }

    public void setTime(long timeProgressed) {
        if (this.stopped)
            this.stopped = false;
        this.startedFrom = System.currentTimeMillis();
        if (this.paused)
            this.startedFrom = 0;
        this.current = timeProgressed;
    }

}
