package com.klutzybubbles.threeinarow.interfaces;

/**
 * <h1>OnGameFinishedListener.java</h1>
 * Class used as a listener interface for different game finish states
 *
 * @author Lee Tzilantonis
 * @version 1.0.0
 * @since 10/6/2018
 */
public interface OnGameFinishedListener {

    /**
     * Called when the game registers that the finish was caused by a success
     */
    void onSuccess();

    /**
     * Called when the game registers that the finish was caused by a fail (3 in a row)
     */
    void onFail();

    /**
     * Called when the game registers that the finish was caused by an external source (no fail or win)
     */
    void onEnd();

}
