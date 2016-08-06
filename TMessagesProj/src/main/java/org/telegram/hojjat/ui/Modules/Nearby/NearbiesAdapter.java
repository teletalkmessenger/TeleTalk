package org.telegram.hojjat.ui.Modules.Nearby;

import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import org.telegram.hojjat.DTOS.NearbyUser;
import org.telegram.hojjat.ui.Widgets.TextView;
import org.telegram.messenger.support.widget.RecyclerView;

import java.util.List;

public class NearbiesAdapter extends RecyclerView.Adapter {
    private static final String TAG = "NearbiesAdapter";
    List<NearbyUser> nearbies;

    public NearbiesAdapter(List<NearbyUser> nearbies) {
        this.nearbies = nearbies;
    }

    class Holder extends RecyclerView.ViewHolder {

        public Holder(View itemView) {
            super(itemView);
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        TextView v = new TextView(parent.getContext());
        return new Holder(v);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        View view = holder.itemView;
        Log.i(TAG, "onBindViewHolder: KOOOOFT");
        ((TextView) view).setText("item number " + position);
    }

    @Override
    public int getItemCount() {
        return nearbies.size();
    }
}
