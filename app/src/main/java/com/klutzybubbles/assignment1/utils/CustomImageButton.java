package com.klutzybubbles.assignment1.utils;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;

public class CustomImageButton extends android.support.v7.widget.AppCompatImageButton {

    public CustomImageButton(Context context) {
        super(context);
    }

    public CustomImageButton(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomImageButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean performClick() {
        Log.v("CustomImageButton", "performClick");
        return super.performClick();
    }

}
