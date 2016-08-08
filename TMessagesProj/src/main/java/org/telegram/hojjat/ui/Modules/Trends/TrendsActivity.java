package org.telegram.hojjat.ui.Modules.Trends;

import android.content.Context;
import android.graphics.Typeface;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import org.telegram.hojjat.DTOS.TrendMessage;
import org.telegram.hojjat.MessageType;
import org.telegram.hojjat.ui.Cells.VideoTrendMsgCell;
import org.telegram.hojjat.ui.Components.ListItemSpaceDecoration;
import org.telegram.hojjat.ui.Components.tagtab.TagTabAdapter;
import org.telegram.hojjat.ui.Components.tagtab.TagTabBar;
import org.telegram.hojjat.ui.Widgets.TextView;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.R;
import org.telegram.messenger.support.widget.LinearLayoutManager;
import org.telegram.messenger.support.widget.RecyclerView;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.RecyclerListView;

import java.util.ArrayList;
import java.util.List;

public class TrendsActivity extends BaseFragment {
    private static final String TAG = "TrendsActivity";
    TagTabBar tabsBar;
    Context context;
    TagTabAdapter tabsAdapter;

    RecyclerListView listView;
    LinearLayoutManager listLayoutManager;
    ListAdapter listAdapter;

    List<TrendMessage> messages = new ArrayList();

    @Override
    public View createView(Context context) {
        if (fragmentView != null)
            return fragmentView;
        this.context = context;
        LinearLayout container = new LinearLayout(context);
        container.setBackgroundColor(context.getResources().getColor(R.color.lightGray));
        container.setOrientation(LinearLayout.VERTICAL);
        addTabsBar(container);
        addTrendsList(container);
        fragmentView = container;
        for (int i = 0; i < 10; i++) {
            TrendMessage msg = new TrendMessage();
            msg.text = "این است پیام شماره " + i;
            msg.channelName = "چیزمیز";
            msg.hits = "23k ویو";
            messages.add(msg);
        }
        listAdapter.notifyDataSetChanged();
        return fragmentView;
    }

    private void addTrendsList(LinearLayout container) {
        listView = new RecyclerListView(context);
        listView.setVerticalScrollBarEnabled(true);
        listLayoutManager = new LinearLayoutManager(context) {
            @Override
            public boolean supportsPredictiveItemAnimations() {
                return false;
            }
        };
        listLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        listView.setLayoutManager(listLayoutManager);
        listAdapter = new ListAdapter();
        listView.setAdapter(listAdapter);
        listView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return false;
            }
        });
        listView.setOnInterceptTouchListener(new RecyclerListView.OnInterceptTouchListener() {
            @Override
            public boolean onInterceptTouchEvent(MotionEvent event) {
                return false;
            }
        });
        int padMed = (int) context.getResources().getDimension(R.dimen.space_medium);
        ListItemSpaceDecoration decor = new ListItemSpaceDecoration(0, 0, 0, padMed);
        decor.setNoBottomSpaceForLastItem(true);
        listView.addItemDecoration(decor);
        container.addView(listView, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT, Gravity.CENTER));
    }

    private void addTabsBar(LinearLayout container) {
        tabsBar = new TagTabBar(context);
        tabsBar.setBackgroundColor(context.getResources().getColor(R.color.lightGray));
        tabsBar.setRtl(LocaleController.isRTL);
        tabsBar.setAdapter(tabsAdapter = new TagTabsAdapter());
        tabsBar.setOnTabSelectedListener(((TagTabsAdapter) tabsAdapter));
        container.addView(tabsBar, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT));
    }

    class TagTabsAdapter extends TagTabAdapter implements TagTabBar.OnTabSelectedListener {
        int count = 5;
        private final Typeface typeface;

        public TagTabsAdapter() {
            typeface = AndroidUtilities.getTypeface(AndroidUtilities.PERSIAN_FONT);
        }

        @Override
        public String getLabel(int position) {
            MessageType type = getMessageTypeForPosition(position);
            switch (type) {
                case video:
                    return LocaleController.getString("MessageTypeVideo", R.string.MessageTypeVideo);
                case image:
                    return LocaleController.getString("MessageTypeImage", R.string.MessageTypeImage);
                case gif:
                    return LocaleController.getString("MessageTypeGif", R.string.MessageTypeGif);
                case text:
                    return LocaleController.getString("MessageTypeText", R.string.MessageTypeText);
                case music:
                    return LocaleController.getString("MessageTypeMusic", R.string.MessageTypeMusic);
                default:
                    return "ERROR!";
            }
        }

        @Override
        public int getIconResource(int position) {
            MessageType type = getMessageTypeForPosition(position);
            switch (type) {
                case video:
                    return R.drawable.ic_tag_tab_video;
                case image:
                    return R.drawable.ic_tag_tab_image;
                case gif:
                    return R.drawable.ic_tag_tab_gif;
                case text:
                    return R.drawable.ic_tag_tab_text;
                case music:
                    return R.drawable.ic_tag_tab_music;
                default:
                    return R.drawable.ic_tag_tab_music;
            }
        }

        MessageType getMessageTypeForPosition(int position) {
            switch (position) {
                case 0:
                    return MessageType.video;
                case 1:
                    return MessageType.image;
                case 2:
                    return MessageType.gif;
                case 3:
                    return MessageType.text;
                case 4:
                    return MessageType.music;
                default:
                    return MessageType.unknown;
            }
        }

        @Override
        public int getTabBackgroundResource(int position) {
            return R.drawable.tag_tab_background;
        }

        @Override
        public int getTextAppearance(int position) {
            return R.style.TagTabTextStyle;
        }

        @Override
        public Typeface getTextTypeface(int position) {
            return typeface;
        }

        @Override
        public int getCount() {
            return count;
        }

        @Override
        public int getDefaultSelectedTab() {
            return 0;
        }

        @Override
        public void onTabSelected(int position) {
            Log.d(TAG, "onTabSelected: " + getMessageTypeForPosition(position));
        }
    }

    class ListAdapter extends RecyclerView.Adapter {

        class Holder extends RecyclerView.ViewHolder {

            public Holder(View itemView) {
                super(itemView);
            }
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = null;
            if (viewType == 1) {
                VideoTrendMsgCell msgCell = new VideoTrendMsgCell(context);
                v = msgCell;
            } else if (viewType == 0) {
                TextView textView = new TextView(context);
                v = textView;
            }
            return new Holder(v);
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            View view = holder.itemView;
            int type = getItemViewType(position);
            if (type == 1) {
                VideoTrendMsgCell msgCell = (VideoTrendMsgCell) view;
                msgCell.setMessage(messages.get(position));
            } else if (type == 0) {
                TextView loadingView = (TextView) view;
                loadingView.setText("Loading...");
            }
        }

        @Override
        public int getItemCount() {
            return messages.size();
        }

        @Override
        public int getItemViewType(int position) {
            if (position == getItemCount() - 1)
                return 0;
            return 1;
        }
    }
}