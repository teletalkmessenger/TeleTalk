package org.telegram.hojjat.ui.Cells;

import android.content.Context;
import android.widget.ImageView;

import org.telegram.messenger.LocaleController;
import org.telegram.messenger.R;
import org.telegram.ui.Components.LayoutHelper;

public class PartyInfo extends ItemInfo {
    ImageView txtIcon;

    public PartyInfo(Context context) {
        super(context);
    }

    @Override
    protected void init() {
        super.init();
        txtIcon = new ImageView(getContext());
        txtIcon.setImageResource(R.drawable.message);
        txtIcon.setColorFilter(getResources().getColor(R.color.primary));
        if (LocaleController.isRTL)
            txtIcon.setScaleX(-1); //flip
        LayoutParams lp = LayoutHelper.createRelative(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT, CENTER_VERTICAL);
        lp.addRule(LocaleController.isRTL ? ALIGN_PARENT_LEFT : ALIGN_PARENT_RIGHT);
        addView(txtIcon, lp);
    }

    public void showTextIcon(boolean show) {
        txtIcon.setVisibility(show ? VISIBLE : GONE);
    }
}