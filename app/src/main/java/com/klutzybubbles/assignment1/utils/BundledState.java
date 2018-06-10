package com.klutzybubbles.assignment1.utils;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;

import com.klutzybubbles.assignment1.logic.GameItemHandler;

public class BundledState {

    private static final String TIME = "time";
    private static final String ITEMS = "items";
    private static final String NEXT_ITEMS = "next_items";
    private static final String PAUSED = "paused";
    private static final String STOPPED = "stopped";
    private static final String SIZE = "size";

    public static void handlerToBundle(@NonNull Bundle b, @NonNull GameItemHandler g) {
        b.putLong(BundledState.TIME, g.getTime());
        b.putIntArray(BundledState.ITEMS, g.getItems());
        b.putIntArray(BundledState.NEXT_ITEMS, g.getNextItems());
        b.putBoolean(BundledState.PAUSED, g.isPaused());
        b.putBoolean(BundledState.STOPPED, g.isStopped());
        b.putInt(BundledState.SIZE, g.getSize());
    }

    public static GameItemHandler bundleToHandler(Context c, @NonNull Bundle b) {
        int[] items = b.getIntArray(BundledState.ITEMS);
        int[] nextItems = b.getIntArray(BundledState.NEXT_ITEMS);
        int size = b.getInt(BundledState.SIZE);
        if (items == null)
            items = GameItemHandler.genBlankItems(size);
        if (nextItems == null)
            nextItems = GameItemHandler.genRandomItems(size);
        return GameItemHandler.fromState(c, b.getInt(BundledState.SIZE),
                b.getLong(BundledState.TIME), items,
                nextItems, b.getBoolean(BundledState.PAUSED),
                b.getBoolean(BundledState.STOPPED));
    }
}
