package org.telegram.hojjat.ui.Modules.Nearby;

import org.telegram.hojjat.DTOS.NearbyUser;

import java.util.List;

public interface NearbyView {
    void showFoundedNearbies(List<NearbyUser> nearbies);

    void startSearchingAnim();

    void continueSearchingAnim();

    void endSearchingAnim();

    void setCurrentUserInfo(NearbyUser user);

    void showAboutNearby(boolean show);

    void showAfterSearchLayout(boolean show);

    void showBeforeSearchLayout(boolean show);
}