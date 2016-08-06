package org.telegram;

import android.content.Context;
import android.os.Build;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import org.telegram.messenger.AndroidUtilities;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.PagerSlidingTabStrip;


public class PagerSlidingTabStripWithBadge extends PagerSlidingTabStrip {
    public PagerSlidingTabStripWithBadge(Context context) {
        super(context);
    }

    public interface BadgeProvider {
        View getBadge(Context context, int position);
    }

    @Override
    public void notifyDataSetChanged() {
        tabsContainer.removeAllViews();
        tabCount = pager.getAdapter().getCount();
        for (int i = 0; i < tabCount; i++) {
            if (pager.getAdapter() instanceof PagerSlidingTabStrip.IconTabProvider) {
                int iconResId = ((IconTabProvider) pager.getAdapter()).getPageIconResId(i);
                View badge = null;
                if (pager.getAdapter() instanceof BadgeProvider)
                    badge = ((BadgeProvider) pager.getAdapter()).getBadge(getContext(), i);
                if (badge != null)
                    addIconTabWithBadge(i, iconResId, badge);
                else
                    addIconTab(i, iconResId);
            }
        }
        updateTabStyles();
        getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (Build.VERSION.SDK_INT < 16) {
                    getViewTreeObserver().removeGlobalOnLayoutListener(this);
                } else {
                    getViewTreeObserver().removeOnGlobalLayoutListener(this);
                }
                currentPosition = pager.getCurrentItem();
                scrollToChild(currentPosition, 0);
            }
        });
    }

    protected void addIconTabWithBadge(final int position, int resId, View badge) {
        FrameLayout tab = new FrameLayout(getContext());
        ImageView img = new ImageView(getContext());
        img.setFocusable(true);
        img.setImageResource(resId);
        img.setScaleType(ImageView.ScaleType.CENTER);
        tab.addView(img, LayoutHelper.createFrame(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT, Gravity.CENTER));
        tab.addView(badge, LayoutHelper.createFrame(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT, Gravity.BOTTOM | Gravity.RIGHT, 0, 0, AndroidUtilities.dp(3), 0));
        tab.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                pager.setCurrentItem(position);
            }
        });
        tabsContainer.addView(tab);
        tab.setSelected(position == currentPosition);
    }
}
