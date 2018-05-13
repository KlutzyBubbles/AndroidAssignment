package com.klutzybubbles.assignment1.interfaces;

public interface OnGameFinishedListener {

    void onSuccess(long time, int size);
    void onFail(long time, int size);
    void onEnd(long time, int size);

}
