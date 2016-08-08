package org.telegram.hojjat.ui.Components.BottomBar;

import android.animation.Animator;
import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import org.telegram.messenger.AndroidUtilities;
import org.telegram.ui.Components.LayoutHelper;

public class BottomTab extends FrameLayout {
    private static final String TAG = "BottomTab";
    ImageView icon;
    TextView label;

    int activeColor = Color.RED;
    int deactiveColor = Color.BLUE;

    public static final int ACTIVATION_ANIM_DURATION = 150;
    public static final int DEACTIVATION_ANIM_DURATION = 100;

    public BottomTab(Context context) {
        super(context);
        setup();
    }

    public BottomTab(Context context, AttributeSet attrs) {
        super(context, attrs);
        setup();
    }

    public BottomTab(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setup();
    }

    public void setColors(int activeColor, int deactiveColor) {
        this.activeColor = activeColor;
        this.deactiveColor = deactiveColor;
        if (isSelected())
            setColor(activeColor);
        else
            setColor(deactiveColor);
    }

    private void setColor(int color) {
        icon.setColorFilter(color);
        label.setTextColor(color);
    }

    private void setup() {
        setFocusable(true);
        icon = new ImageView(getContext());
        label = new TextView(getContext());
        label.setVisibility(INVISIBLE);
        addView(label, LayoutHelper.createFrame(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT, Gravity.TOP | Gravity.CENTER_HORIZONTAL, 0, 32, 0, 0));
        addView(icon, LayoutHelper.createFrame(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT, Gravity.CENTER));
    }

    @Override
    public void setSelected(boolean selected) {
        if (isSelected() != selected)
            startSelectionAnim(selected);
        super.setSelected(selected);
    }

    private void startSelectionAnim(boolean selected) {
        int fromColor = selected ? deactiveColor : activeColor;
        int toColor = selected ? activeColor : deactiveColor;
        int duration = selected ? ACTIVATION_ANIM_DURATION : DEACTIVATION_ANIM_DURATION;
        int translateY = selected ? -AndroidUtilities.dp(8) : 0;

        label.setVisibility(selected ? VISIBLE : INVISIBLE);

        ValueAnimator anim = new ValueAnimator();
        anim.setIntValues(fromColor, toColor);
        anim.setEvaluator(new ArgbEvaluator());
        anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                setColor((Integer) valueAnimator.getAnimatedValue());
            }
        });

        anim.setDuration(duration);
        anim.start();
        icon.animate().translationY(translateY).setDuration(duration).start();
    }
}