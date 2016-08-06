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
        removeAllViews();
        addTabs();
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
            ((BottomTab) getChildAt(adapter.getDefaultSelectedTab())).setSelectedSilently(true);
    }

    private void addTab(BottomTab tab) {
        tab.setOnClickListener(this);
        addView(tab, LayoutHelper.createLinear(0, LayoutHelper.MATCH_PARENT, 1f));
    }

    @Override
    public void onClick(View v) {
        int clicked = ((int) v.getTag());
        for (int i = 0; i < getChildCount(); i++)
            getChildAt(i).setSelected(clicked == i);
        if (delegate != null)
            delegate.onTabSelected(clicked);
    }
}