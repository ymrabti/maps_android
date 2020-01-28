package com.ymrabti.osmmaps.tests;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;

import com.ymrabti.osmmaps.R;

public class YounesView extends View {
    private boolean mShowText;private int textPos;
    public YounesView(Context context) {
        super(context);
    }

    public YounesView(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray a = context.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.YounesView,
                0, 0);

        try {
            mShowText = a.getBoolean(R.styleable.YounesView_AfficherText, false);
            textPos = a.getInteger(R.styleable.YounesView_PositiondeLabel, 0);
        } finally {
            a.recycle();
        }
    }
    public boolean isShowText() {
        return mShowText;
    }

    public void setShowText(boolean showText) {
        mShowText = showText;
        invalidate();
        requestLayout();
    }
}
