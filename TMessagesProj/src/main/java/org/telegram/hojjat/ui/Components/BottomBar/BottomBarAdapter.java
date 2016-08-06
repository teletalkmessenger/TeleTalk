package org.telegram.hojjat.ui.Components.BottomBar;

import android.graphics.Typeface;

/**
 * Created by hojjatimani on 8/2/2016 AD.
 */
abstract public class BottomBarAdapter {
    public abstract String getLabel(int position);

    public abstract int getIconResource(int position);

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