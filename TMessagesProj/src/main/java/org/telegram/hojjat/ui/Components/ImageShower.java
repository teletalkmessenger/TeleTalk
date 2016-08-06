package org.telegram.hojjat.ui.Components;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.R;
import org.telegram.ui.Components.LayoutHelper;

public class ImageShower extends FrameLayout {
    public ImageView img;
    public ImageView play;

    public ImageShower(Context context) {
        super(context);
        initItems();
    }

    public ImageShower(Context context, AttributeSet attrs) {
        super(context, attrs);
        initItems();
    }

    public ImageShower(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initItems();
    }

    private void initItems() {
        img = new ImageView(getContext());
        img.setAdjustViewBounds(true);
        img.setScaleType(ImageView.ScaleType.CENTER_CROP);
        img.setMaxHeight(AndroidUtilities.dp(200));
        play = new ImageView(getContext());
        play.setImageResource(R.drawable.ic_action_play);
        addView(img, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT));
        addView(play, LayoutHelper.createFrame(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT, Gravity.CENTER));
    }
}
