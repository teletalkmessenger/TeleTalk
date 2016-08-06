package org.telegram.hojjat.ui.Cells;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import org.telegram.hojjat.ui.Widgets.TextView;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.R;
import org.telegram.ui.Components.LayoutHelper;

public class ItemInfo extends RelativeLayout {
    public ImageView avatar;
    public TextView title;
    public TextView desc;

    public ItemInfo(Context context) {
        super(context);
        init();
    }

    public ItemInfo(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ItemInfo(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        int pad = AndroidUtilities.dp(10);
        setPadding(pad, pad, pad, pad);
        avatar = new ImageView(getContext());
        avatar.setId(R.id.avatar);
        title = new TextView(getContext());
        title.setTextColor(getResources().getColor(R.color.darkGray));
        title.setId(R.id.desc);
        desc = new TextView(getContext());
        desc.setTextColor(getResources().getColor(R.color.gray));
        desc.setId(R.id.channel);
        if (LocaleController.isRTL) {
            RelativeLayout.LayoutParams lp = LayoutHelper.createRelative(50, 50, ALIGN_PARENT_RIGHT);
            lp.addRule(ALIGN_PARENT_TOP);
            lp.setMargins(AndroidUtilities.dp(10), 0, 0, 0);
            addView(avatar, lp);

            addView(title, LayoutHelper.createRelative(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT, LEFT_OF, avatar.getId()));
            RelativeLayout.LayoutParams lp2 = LayoutHelper.createRelative(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT, LEFT_OF, avatar.getId());
            lp2.addRule(BELOW, title.getId());
            addView(desc, lp2);

        } else {
            RelativeLayout.LayoutParams lp = LayoutHelper.createRelative(50, 50, ALIGN_PARENT_LEFT);
            lp.addRule(ALIGN_PARENT_TOP);
            lp.setMargins(0, 0, AndroidUtilities.dp(10), 0);
            addView(avatar, lp);

            addView(title, LayoutHelper.createRelative(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT, RIGHT_OF, avatar.getId()));
            RelativeLayout.LayoutParams lp2 = LayoutHelper.createRelative(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT, RIGHT_OF, avatar.getId());
            lp2.addRule(BELOW, title.getId());
            addView(desc, lp2);
        }
    }
}
