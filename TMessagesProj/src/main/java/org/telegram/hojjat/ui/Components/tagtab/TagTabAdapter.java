package org.telegram.hojjat.ui.Components.tagtab;

import android.content.res.ColorStateList;
import android.graphics.Typeface;

public abstract class TagTabAdapter {
    public abstract String getLabel(int position);

    public abstract int getIconResource(int position);

    public abstract int getTabBackgroundResource(int position);

    public abstract int getCount();

    public int getTextAppearance(int position) {
        return 0;
    }

    public Typeface getTextTypeface(int position) {
        return null;
    }

    public int getDefaultSelectedTab(){
        return 0;
    }
}