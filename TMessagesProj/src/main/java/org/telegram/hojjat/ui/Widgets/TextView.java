package org.telegram.hojjat.ui.Widgets;

import android.content.Context;
import android.util.AttributeSet;

import org.telegram.Util;
import org.telegram.messenger.AndroidUtilities;

/**
 * Created by hojjatimani on 7/26/2016 AD.
 */
public class TextView extends android.widget.TextView {
    public TextView(Context context) {
        super(context);
        setFont();
    }

    public TextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setFont();
    }

    public TextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setFont();
    }

    public TextView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        setFont();
    }

    private void setFont() {
        if (Util.hojjatUi)
            setTypeface(AndroidUtilities.getTypeface(AndroidUtilities.PERSIAN_FONT));
    }

    @Override
    public void setTextSize(float size) {
        if (Util.hojjatUi)
            size *= Util.niceTextSizeFactor;
        super.setTextSize(size);
    }

    @Override
    public void setTextSize(int unit, float size) {
        if (Util.hojjatUi)
            size *= Util.niceTextSizeFactor;
        super.setTextSize(unit, size);
    }
}
