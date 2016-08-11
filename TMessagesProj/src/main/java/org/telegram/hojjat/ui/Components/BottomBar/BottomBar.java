package org.telegram.hojjat.ui.Components.BottomBar;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.R;
import org.telegram.ui.Components.LayoutHelper;

public class BottomBar extends LinearLayout implements View.OnClickListener {
    BottomBarAdapter adapter;
    OnTabSelectedListener delegate;

    int activeColor = Color.RED;
    int deactiveColor = Color.BLUE;

    boolean isRtl;

    public void setActiveColor(int activeColor) {
        this.activeColor = activeColor;
    }

    public void setDeactiveColor(int deactiveColor) {
        this.deactiveColor = deactiveColor;
    }

    public interface OnTabSelectedListener {
        void onTabSelected(int position);
    }

    public void setOnTabSelectedListener(OnTabSelectedListener delegate) {
        this.delegate = delegate;
    }

    public BottomBar(Context context) {
        super(context);
        setup();
    }

    public BottomBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        setup();
    }

    private void setup() {
        setOrientation(HORIZONTAL);
        setBackgroundColor(getResources().getColor(R.color.gray));
        setMinimumHeight(AndroidUtilities.dp(56));
    }

    public void setAdapter(BottomBarAdapter adapter) {
        if (this.adapter == adapter)
            return;
        this.adapter = adapter;
        setWeightSum(adapter.getCount());
        recreate();
    }

    public void recreate() {
        removeAllViews();
        addTabs();
    }

    public void setRtl(boolean rtl) {
        isRtl = rtl;
    }

    private void addTabs() {
        for (int i = 0; i < adapter.getCount(); i++) {
            BottomTab tab = new BottomTab(getContext());
            tab.setColors(activeColor, deactiveColor);
            tab.icon.setImageResource(adapter.getIconResource(i));
            tab.label.setText(adapter.getLabel(i));
            if (adapter.getTextAppearance(i) != 0)
                tab.label.setTextAppearance(getContext(), adapter.getTextAppearance(i));
            if (adapter.getTextTypeface(i) != null)
                tab.label.setTypeface(adapter.getTextTypeface(i));
            tab.setTag(i);
            addTab(tab);
        }
        if (adapter.getDefaultSelectedTab() < adapter.getCount())
            setSelectedTab(adapter.getDefaultSelectedTab(), true);
    }

    private void addTab(BottomTab tab) {
        tab.setOnClickListener(this);
        if (isRtl)
            addView(tab, 0, LayoutHelper.createLinear(0, LayoutHelper.MATCH_PARENT, 1f));
        else
            addView(tab, LayoutHelper.createLinear(0, LayoutHelper.MATCH_PARENT, 1f));
    }

    public void setSelectedTab(int tag, boolean silently) {
        BottomTab tab;
        for (int i = 0; i < getChildCount(); i++) {
            tab = ((BottomTab) getChildAt(i));
            tab.setSelected(((int) tab.getTag()) == tag);
        }
        if (!silently && delegate != null)
            delegate.onTabSelected(tag);
    }

    @Override
    public void onClick(View v) {
        int tag = ((int) v.getTag());
        setSelectedTab(tag, false);
    }
}