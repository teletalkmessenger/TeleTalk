package org.telegram.hojjat.ui.Cells;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.squareup.picasso.Picasso;

import org.telegram.hojjat.DTOS.TrendMessage;
import org.telegram.hojjat.ui.Components.ImageShower;
import org.telegram.hojjat.ui.Transformations.CircleTransformation;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.R;
import org.telegram.ui.Components.LayoutHelper;

public class VideoTrendMsgCell extends LinearLayout {
    LinearLayout container;
    ImageShower imgShower;
    MessageInfo info;
    TrendMessage message;

    public VideoTrendMsgCell(Context context) {
        super(context);
        initItems();
    }

    public VideoTrendMsgCell(Context context, AttributeSet attrs) {
        super(context, attrs);
        initItems();
    }

    public VideoTrendMsgCell(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initItems();
    }

    private void initItems() {
        container = new LinearLayout(getContext());
        container.setOrientation(VERTICAL);
        container.setBackgroundResource(R.drawable.card_background);
        imgShower = new ImageShower(getContext());
        info = new MessageInfo(getContext());
        container.addView(imgShower, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT));
        container.addView(info, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT));
        int width = Math.min(AndroidUtilities.displaySize.x, AndroidUtilities.displaySize.y) - 2 * AndroidUtilities.dp(10);
        LayoutParams lp = new LayoutParams(width, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp.gravity = Gravity.CENTER;
        addView(container, lp);
        setGravity(Gravity.CENTER);
        setLayoutParams(LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT));
    }

    public void setMessage(TrendMessage msg) {
        if (message == msg)
            return;
        message = msg;
        setup();
    }

    private void setup() {
        info.title.setText(message.text);
        info.desc.setText(message.channelName + " - " + message.hits);
        Picasso.with(getContext())
                .load("http://dirindirin.com/wp-content/uploads/2015/07/yad-girande-.jpg")
                .into(imgShower.img);
        Picasso.with(getContext())
                .load("http://www.tele-wall.ir/static/channel/s128_919629download_423728649_104444.jpg")
                .fit()
                .centerCrop()
                .transform(new CircleTransformation())
                .into(info.avatar);
    }
}