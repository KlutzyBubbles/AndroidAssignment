package com.klutzybubbles.assignment1.interfaces;

public interface OnGameFinished {

    void onSuccess(long time, int size, int difficulty);
    void onFail(long time, int size, int difficulty);
    void onEnd(long time, int size, int difficulty);

}
