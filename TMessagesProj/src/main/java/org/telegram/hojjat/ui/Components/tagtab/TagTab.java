package org.telegram.hojjat.ui.Components.tagtab;

import android.content.Context;
import android.content.res.ColorStateList;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.R;
import org.telegram.ui.Components.LayoutHelper;

public class TagTab extends LinearLayout {
    ImageView icon;
    TextView label;

    public TagTab(Context context) {
        super(context);
        setup();
    }

    private void setup() {
        setOrientation(HORIZONTAL);
        int pad = (int) getResources().getDimension(R.dimen.space_medium);
        setPadding(pad, AndroidUtilities.dp(2), pad, AndroidUtilities.dp(2));
        setGravity(Gravity.CENTER);
        setFocusable(true);
        initSubViews();
        addSubviews();
    }

    private void initSubViews() {
        initIcon();
        initLabel();
    }

    private void initLabel() {
        label = new TextView(getContext());
        label.setFocusable(true);
        label.setTextSize(13);
        label.setTypeface(AndroidUtilities.getTypeface(AndroidUtilities.PERSIAN_FONT));
    }

    private void initIcon() {
        icon = new ImageView(getContext());
        icon.setFocusable(true);
    }

    private void addSubviews() {
        if (LocaleController.isRTL) {
            addView(label, LayoutHelper.createLinear(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT));
            addView(icon, LayoutHelper.createLinear(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT, AndroidUtilities.dp(2), 0, 0, 0));
        } else {
            addView(icon, LayoutHelper.createLinear(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT, 0,0, AndroidUtilities.dp(2), 0));
            addView(label, LayoutHelper.createLinear(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT));
        }
    }

    void setIcon(int resId) {
        icon.setBackgroundResource(resId);
    }

    void setText(String text) {
        label.setText(text);
    }
}