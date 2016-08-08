package org.telegram.hojjat.ui.Components.tagtab;

import android.content.Context;
import android.content.res.Configuration;
import android.os.Parcel;
import android.os.Parcelable;
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
    LinearLayout currentRow;
    int currentRowWidthSoFar;
    int screenOrientation;

    int currentSelectedTab = -1;

    boolean isRtl;

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
        recreate();
    }

    public void setRtl(boolean rtl) {
        isRtl = rtl;
    }

    private void setup() {
        int padBig = (int) getResources().getDimension(R.dimen.space_medium);
        int padSmall = (int) getResources().getDimension(R.dimen.space_small);
        setPadding(padSmall, padBig, padBig, padSmall);
        setOrientation(VERTICAL);
        screenOrientation = getResources().getConfiguration().orientation;
    }

    private void recreate() {
        removeAllViews();
        currentRow = null;
        currentRowWidthSoFar = 0;
        addTabs();
    }

    private void addTab(TagTab tab) {
        if (currentRow == null)
            createNewRow();
        Log.i(TAG, "addTab: KOOOft");
        tab.setOnClickListener(this);
        float margin = 5f;
        float marginDp = AndroidUtilities.dpf2(margin);
        if (isRtl)
            currentRow.addView(tab, 0, LayoutHelper.createLinear(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT, margin, margin, margin, margin));
        else
            currentRow.addView(tab, LayoutHelper.createLinear(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT, margin, margin, margin, margin));
        tab.measure(0, 0);
        currentRowWidthSoFar += tab.getMeasuredWidth() + (2 * marginDp);
        if (currentRowWidthSoFar >= AndroidUtilities.displaySize.x) {
            currentRow.removeView(tab);
            createNewRow();
            addTab(tab);
        }
    }

    private void createNewRow() {
        currentRow = new LinearLayout(getContext());
        currentRowWidthSoFar = getPaddingRight() + getPaddingLeft();
        currentRow.setOrientation(HORIZONTAL);
        if (LocaleController.isRTL)
            currentRow.setGravity(Gravity.RIGHT);
        else
            currentRow.setGravity(Gravity.LEFT);
        addView(currentRow, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT));
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
            setSelectedTab(adapter.getDefaultSelectedTab(), true);
    }

    @Override
    public void onClick(View v) {
        int tag = ((int) v.getTag());
        setSelectedTab(tag, false);
    }

    private void setSelectedTab(int tag, boolean silently) {
        currentSelectedTab = tag;
        TagTab tab;
        for (int i = 0; i < getChildCount(); i++)
            for (int j = 0; j < ((LinearLayout) getChildAt(i)).getChildCount(); j++) {
                tab = ((TagTab) ((LinearLayout) getChildAt(i)).getChildAt(j));
                tab.setSelected(((int) tab.getTag()) == tag);
            }
        if (!silently && delegate != null)
            delegate.onTabSelected(tag);
    }


    @Override
    protected void onConfigurationChanged(Configuration newConfig) {
        if (newConfig.orientation != screenOrientation) {
            screenOrientation = newConfig.orientation;
            recreate();
        }
        super.onConfigurationChanged(newConfig);
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        Parcelable parcelable = super.onSaveInstanceState();
        SavedState ss = new SavedState(parcelable);
        ss.selectedTab = currentSelectedTab;
        return ss;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        //begin boilerplate code so parent classes can restore state
        if(!(state instanceof SavedState)) {
            super.onRestoreInstanceState(state);
            return;
        }

        SavedState ss = (SavedState)state;
        super.onRestoreInstanceState(ss.getSuperState());
        //end
        setSelectedTab(ss.selectedTab, true);
    }

    static class SavedState extends BaseSavedState {
        int selectedTab;

        SavedState(Parcelable superState) {
            super(superState);
        }

        private SavedState(Parcel in) {
            super(in);
            this.selectedTab = in.readInt();
        }

        @Override
        public void writeToParcel(Parcel out, int flags) {
            super.writeToParcel(out, flags);
            out.writeInt(this.selectedTab);
        }

        //required field that makes Parcelables from a Parcel
        public static final Parcelable.Creator<SavedState> CREATOR =
                new Parcelable.Creator<SavedState>() {
                    public SavedState createFromParcel(Parcel in) {
                        return new SavedState(in);
                    }

                    public SavedState[] newArray(int size) {
                        return new SavedState[size];
                    }
                };
    }
}
