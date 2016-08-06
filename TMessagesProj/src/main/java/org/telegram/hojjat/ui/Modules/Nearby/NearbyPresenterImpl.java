package org.telegram.hojjat.ui.Modules.Nearby;


import android.os.Handler;

import org.telegram.hojjat.DTOS.NearbyUser;

import java.util.ArrayList;
import java.util.List;

public class NearbyPresenterImpl implements NearbyPresenter {
    NearbyView view;
    public static final int STATE_BEFORE_SEARCH = 0;
    public static final int STATE_SEARCHING = 1;
    public static final int STATE_SEARCH_COMPLETED = 2;

    int currentState;

    List<NearbyUser> foundNearbies = new ArrayList<>();

    private static NearbyPresenterImpl instance;

    public static NearbyPresenterImpl getInstance() {
        NearbyPresenterImpl localInstance = instance;
        if (localInstance == null) {
            synchronized (NearbyPresenterImpl.class) {
                if (localInstance == null) {
                    localInstance = instance = new NearbyPresenterImpl();
                }
            }
        }
        return localInstance;
    }

    public void setView(NearbyView view) {
        this.view = view;
    }

    public NearbyPresenterImpl() {
        currentState = STATE_BEFORE_SEARCH;
    }

    @Override
    public void finish() {
        instance = null;
    }

    @Override
    public void searchForNearbyPeople() {
        if (view == null)
            return;
        if (currentState != STATE_BEFORE_SEARCH)
            return;
        currentState = STATE_SEARCHING;
        //TODO startSearching
        setViewToSearching(true);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                currentState = STATE_SEARCH_COMPLETED;
                foundNearbies.add(getCurrentUserInfo());
                foundNearbies.add(getCurrentUserInfo());
                foundNearbies.add(getCurrentUserInfo());
                foundNearbies.add(getCurrentUserInfo());
                setViewToAfterSearch();
            }
        }, 3000);
    }

    @Override
    public void onSubviewsInitiated() {
        if (view == null)
            return;
        if (currentState == STATE_BEFORE_SEARCH) {
            setViewToBeforeSearch();
        } else if (currentState == STATE_SEARCHING) {
            setViewToSearching(false);
        } else if (currentState == STATE_SEARCH_COMPLETED) {
            setViewToAfterSearch();
        }
    }

    private void setViewToBeforeSearch() {
        view.showBeforeSearchLayout(true);
        view.showAfterSearchLayout(false);
        boolean shouldShowAboutNearby = true;
        view.showAboutNearby(shouldShowAboutNearby);
        view.setCurrentUserInfo(getCurrentUserInfo());
    }

    private void setViewToSearching(boolean start) {
        view.showBeforeSearchLayout(true);
        view.showAfterSearchLayout(false);
        if (start)
            view.startSearchingAnim();
        else
            view.continueSearchingAnim();
    }

    private void setViewToAfterSearch() {
        view.showBeforeSearchLayout(false);
        view.showAfterSearchLayout(true);
        view.showFoundedNearbies(foundNearbies);
    }

    public NearbyUser getCurrentUserInfo() {
        NearbyUser currentUser = new NearbyUser();
        currentUser.username = "@gholi";
        currentUser.name = "حجت ایمانی";
        currentUser.status = "نگام نکن آب میشم!";
        return currentUser;
    }
}