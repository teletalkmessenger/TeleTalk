package org.telegram.hojjat.ui.Modules.Nearby;

public interface NearbyPresenter {

    void setView(NearbyView v);

    void finish();

    void onSubviewsInitiated();

    void searchForNearbyPeople();
}