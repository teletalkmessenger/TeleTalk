package org.telegram.hojjat.ui.Components.tagtab;

import android.content.Context;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;

import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.R;
import org.telegram.ui.Components.LayoutHelper;

public class TagTabBar extends LinearLayout implements View.OnClickListener {
    private static final String TAG = "TagTabBar";
    TagTabAdapter adapter;
    OnTabSelectedListener delegate;

    public interface OnTabSelectedListener {
        void onTabSelected(int position);
    }

    public void setOnTabSelectedListener(OnTabSelectedListener delegate) {
        this.delegate = delegate;
    }

    public TagTabBar(Context context) {
        super(context);
        setup();
    }

    public void setAdapter(TagTabAdapter adapter) {
        if (this.adapter == adapter)
            return;
        this.adapter = adapter;
        removeAllViews();
        addTabs();
    }

    private void setup() {
        int padBig = (int) getResources().getDimension(R.dimen.space_medium);
        int padSmall = (int) getResources().getDimension(R.dimen.space_small);
        setPadding(padSmall, padBig, padBig, padSmall);
        setOrientation(HORIZONTAL);
        if (LocaleController.isRTL)
            setGravity(Gravity.RIGHT);
        else
            setGravity(Gravity.LEFT);
    }

    private void addTab(TagTab tab) {
        tab.setOnClickListener(this);
        float margin = 5f;
        addView(tab, LayoutHelper.createLinear(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT, margin, margin, margin, margin));
    }


    private void addTabs() {
        for (int i = 0; i < adapter.getCount(); i++) {
            TagTab tagTab = new TagTab(getContext());
            tagTab.setBackgroundResource(adapter.getTabBackgroundResource(i));
            tagTab.icon.setImageResource(adapter.getIconResource(i));
            tagTab.label.setText(adapter.getLabel(i));
            if (adapter.getTextAppearance(i) != 0)
                tagTab.label.setTextAppearance(getContext(), adapter.getTextAppearance(i));
            if (adapter.getTextTypeface(i) != null)
                tagTab.label.setTypeface(adapter.getTextTypeface(i));
            tagTab.setTag(i);
            addTab(tagTab);
        }
        if (adapter.getDefaultSelectedTab() < adapter.getCount())
            getChildAt(adapter.getDefaultSelectedTab()).setSelected(true);
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
