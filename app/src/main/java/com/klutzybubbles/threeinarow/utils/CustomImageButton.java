package com.klutzybubbles.threeinarow.utils;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;

/**
 * <h1>CustomImageButton.java</h1>
 * Class used to allow the onTouchListener of an ImageButton to be overridden without warnings
 *
 * @author Lee Tzilantonis
 * @version 1.0.0
 * @since 10/6/2018
 * @see android.support.v7.widget.AppCompatImageButton
 */
public class CustomImageButton extends android.support.v7.widget.AppCompatImageButton {

    /**
     * Instantiates the CustomImageButton parsing the Context to the super constructor
     *
     * @param context - The Context to parse to the super constructor
     */
    public CustomImageButton(Context context) {
        super(context);
    }

    /**
     * Instantiates the CustomImageButton parsing the Context and AttributeSet to the super constructor
     *
     * @param context - The Context to parse to the super constructor
     * @param attrs - The AttributeSet to parse to the super constructor
     */
    public CustomImageButton(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    /**
     * Instantiates the CustomImageButton parsing the Context, AttributeSet and int attribute to the
     * super constructor
     *
     * @param context - The Context to parse to the super constructor
     * @param attrs - The AttributeSet to parse to the super constructor
     * @param defStyleAttr - The in attribute to parse to the super constructor
     */
    public CustomImageButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    /**
     * Called when a click has been performed on the CustomImageButton
     *
     * @return - Whether or not to continue with the click
     */
    @Override
    public boolean performClick() {
        Log.v("CustomImageButton", "performClick");
        return super.performClick();
    }

}
