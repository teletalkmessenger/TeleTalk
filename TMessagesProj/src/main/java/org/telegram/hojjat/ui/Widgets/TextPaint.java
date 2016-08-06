package org.telegram.hojjat.ui.Widgets;

import android.graphics.Paint;

import org.telegram.Util;
import org.telegram.messenger.AndroidUtilities;

public class TextPaint extends android.text.TextPaint {
    public TextPaint() {
        setFont();
    }

    public TextPaint(int flags) {
        super(flags);
        setFont();
    }

    public TextPaint(Paint p) {
        super(p);
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
}