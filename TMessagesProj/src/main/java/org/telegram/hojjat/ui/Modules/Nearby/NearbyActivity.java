package org.telegram.hojjat.ui.Modules.Nearby;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Color;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.squareup.picasso.Picasso;

import org.telegram.hojjat.DTOS.NearbyUser;
import org.telegram.hojjat.ui.Cells.PartyInfo;
import org.telegram.hojjat.ui.ListItemDividerDecoration;
import org.telegram.hojjat.ui.Transformations.CircleTransformation;
import org.telegram.hojjat.ui.Widgets.TextView;

import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.R;
import org.telegram.messenger.support.widget.LinearLayoutManager;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.RecyclerListView;

import java.util.List;

public class NearbyActivity extends BaseFragment implements NearbyView, View.OnClickListener {
    Context context;
    FrameLayout root;
    LinearLayout beforeSerachLayout;
    TextView aboutNearby;
    PartyInfo userInfo;
    TextView letsSearch;
    TextView searchBtn;

    LinearLayout afterSearchLayout;
    TextView foundNearbiesLabel;
    RecyclerListView nearbiesList;
    NearbiesAdapter listAdapter;
    LinearLayoutManager layoutManager;


    NearbyPresenter presenter;
    private LinearLayout searchPannel;

    @Override
    public View createView(Context context) {
        if (fragmentView != null)
            return fragmentView;
        this.context = context;
        presenter = NearbyPresenterImpl.getInstance();
        root = new FrameLayout(context);
        addBeforeSearchLayout();
        addAfterSearchLayout();
        fragmentView = root;
        return fragmentView;
    }

    @Override
    protected void onBecomeFullyVisible() {
        super.onBecomeFullyVisible();
        presenter.setView(this);
        presenter.onSubviewsInitiated();
    }

    @Override
    public boolean onBackPressed() {
        presenter.finish();
        return super.onBackPressed();
    }

    private void addBeforeSearchLayout() {
        beforeSerachLayout = new LinearLayout(context);
        beforeSerachLayout.setOrientation(LinearLayout.VERTICAL);
        addAboutNearby();
        addUserInfoBar();
        addSearchPanel();
        root.addView(beforeSerachLayout, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT));
    }

    private void addAboutNearby() {
        aboutNearby = new TextView(context);
        int pad = (int) context.getResources().getDimension(R.dimen.space_medium);
        aboutNearby.setPadding(pad, pad, pad, pad);
        aboutNearby.setBackgroundColor(context.getResources().getColor(R.color.lightGray));
        aboutNearby.setTextColor(context.getResources().getColor(R.color.gray));
        aboutNearby.setTextSize(TypedValue.COMPLEX_UNIT_PX, context.getResources().getDimension(R.dimen.text_size_normal));
        aboutNearby.setText(LocaleController.getString("AboutNearby", R.string.AboutNearby));
        beforeSerachLayout.addView(aboutNearby, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT));
    }

    private void addUserInfoBar() {
        userInfo = new PartyInfo(context);
        userInfo.showTextIcon(false);
        beforeSerachLayout.addView(userInfo, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT));
    }

    private void addSearchPanel() {
        searchPannel = new LinearLayout(context);
        searchPannel.setWeightSum(1);
        searchPannel.setOrientation(LinearLayout.VERTICAL);
        searchPannel.setGravity(Gravity.CENTER);
        addLetsSearch(searchPannel);
        addSearchBtn(searchPannel);
        beforeSerachLayout.addView(searchPannel, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT));
    }

    private void addLetsSearch(LinearLayout holder) {
        letsSearch = new TextView(context);
        letsSearch.setBackgroundResource(R.drawable.lets_search);
        letsSearch.setTextColor(Color.WHITE);
        letsSearch.setGravity(Gravity.CENTER);
        letsSearch.setTextSize(TypedValue.COMPLEX_UNIT_PX, context.getResources().getDimension(R.dimen.text_size_large));
        int pad = (int) context.getResources().getDimension(R.dimen.space_large);
        letsSearch.setPadding(pad, pad, pad, pad);
        letsSearch.setText(LocaleController.getString("LetsSearchForNearbies", R.string.LetsSearchForNearbies));
        int widthDp = (int) (AndroidUtilities.displaySize.x * 0.7 / AndroidUtilities.density);
        holder.addView(letsSearch, LayoutHelper.createLinear(widthDp, LayoutHelper.WRAP_CONTENT, 0, 0, 0, 15));
    }

    private void addSearchBtn(LinearLayout holder) {
        searchBtn = new TextView(context);
        searchBtn.setBackgroundResource(R.drawable.search_for_nearbies);
        searchBtn.setTextColor(Color.WHITE);
        searchBtn.setGravity(Gravity.CENTER);
        searchBtn.setTextSize(TypedValue.COMPLEX_UNIT_PX, context.getResources().getDimension(R.dimen.text_size_huge));
        searchBtn.setText(LocaleController.getString("SearchForNearbies", R.string.SearchForNearbies));
        searchBtn.setOnClickListener(this);
        int size = (int) (AndroidUtilities.displaySize.x * 0.25);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(size, size);
        holder.addView(searchBtn, lp);
    }

    private void addAfterSearchLayout() {
        afterSearchLayout = new LinearLayout(context);
        afterSearchLayout.setOrientation(LinearLayout.VERTICAL);
        addFoundNearbiesLabel();
        addFoundedNearbiesList();
        root.addView(afterSearchLayout, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT));
    }

    private void addFoundNearbiesLabel() {
        foundNearbiesLabel = new TextView(context);
        int pad = (int) context.getResources().getDimension(R.dimen.space_large);
        foundNearbiesLabel.setPadding(pad, pad, pad, pad);
        foundNearbiesLabel.setGravity(Gravity.CENTER);
        foundNearbiesLabel.setBackgroundColor(context.getResources().getColor(R.color.lightGray));
        foundNearbiesLabel.setTextColor(context.getResources().getColor(R.color.gray));
        foundNearbiesLabel.setTextSize(TypedValue.COMPLEX_UNIT_PX, context.getResources().getDimension(R.dimen.text_size_normal));
        foundNearbiesLabel.setText(LocaleController.getString("AboutNearby", R.string.AboutNearby));
        afterSearchLayout.addView(foundNearbiesLabel, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT));
    }

    private void addFoundedNearbiesList() {
        nearbiesList = new RecyclerListView(context);
        nearbiesList.setVerticalScrollBarEnabled(true);
        layoutManager = new LinearLayoutManager(context) {
            @Override
            public boolean supportsPredictiveItemAnimations() {
                return false;
            }
        };
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        nearbiesList.setLayoutManager(layoutManager);
        nearbiesList.addItemDecoration(new ListItemDividerDecoration(context));
        nearbiesList.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return false;
            }
        });
        nearbiesList.setOnInterceptTouchListener(new RecyclerListView.OnInterceptTouchListener() {
            @Override
            public boolean onInterceptTouchEvent(MotionEvent event) {
                return false;
            }
        });
        afterSearchLayout.addView(nearbiesList, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT));
    }

    @Override
    public void showFoundedNearbies(List<NearbyUser> nearbies) {
        setFoundNearbiesLabelText(nearbies.size());
        listAdapter = new NearbiesAdapter(nearbies);
        nearbiesList.setAdapter(listAdapter);
    }

    private void setFoundNearbiesLabelText(int count) {
        String s = LocaleController.getString("N_PeopleAreAround", R.string.N_PeopleAreAround);
        foundNearbiesLabel.setText(String.format(s, count));
    }

    @Override
    public void startSearchingAnim() {
        aboutNearby.setVisibility(View.GONE);
        userInfo.setVisibility(View.GONE);
        letsSearch.animate().scaleX(0).scaleY(0).setDuration(100).start();
        searchBtn.animate().scaleX(2).scaleY(2)
                .translationY(-letsSearch.getHeight())
                .setDuration(200).start();
        ObjectAnimator rotation = ObjectAnimator.ofFloat(searchBtn, "rotation", 0f, 7200f)
                .setDuration(20 * 1000);
        rotation.setInterpolator(new LinearInterpolator());
        rotation.start();
    }

    public void continueSearchingAnim(){
        aboutNearby.setVisibility(View.GONE);
        userInfo.setVisibility(View.GONE);
        letsSearch.setVisibility(View.GONE);
        searchBtn.setScaleX(2);
        searchBtn.setScaleY(2);
        ObjectAnimator rotation = ObjectAnimator.ofFloat(searchBtn, "rotation", 0f, 7200f)
                .setDuration(20 * 1000);
        rotation.setInterpolator(new LinearInterpolator());
        rotation.start();
    }

    @Override
    public void endSearchingAnim() {
        searchPannel.setVisibility(View.GONE);
    }

    @Override
    public void setCurrentUserInfo(NearbyUser user) {
        userInfo.title.setText(user.name);
        userInfo.desc.setText(user.status);
        Picasso.with(context)
                .load("http://about.library.ubc.ca/files/2014/09/Mark_Christensen.jpeg")
                .fit()
                .centerCrop()
                .transform(new CircleTransformation())
                .into(userInfo.avatar);
    }

    @Override
    public void showAboutNearby(boolean show) {
        aboutNearby.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    @Override
    public void showAfterSearchLayout(boolean show) {
        afterSearchLayout.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    @Override
    public void showBeforeSearchLayout(boolean show) {
        beforeSerachLayout.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    @Override
    public void onClick(View v) {
        if (v.equals(searchBtn))
            presenter.searchForNearbyPeople();
    }
}