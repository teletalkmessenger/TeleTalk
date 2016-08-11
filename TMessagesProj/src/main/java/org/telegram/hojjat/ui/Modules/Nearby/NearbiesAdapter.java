package org.telegram.hojjat.ui.Modules.Nearby;

import android.view.View;
import android.view.ViewGroup;

import com.squareup.picasso.Picasso;

import org.telegram.hojjat.DTOS.NearbyUser;
import org.telegram.hojjat.ui.Cells.PartyInfo;
import org.telegram.hojjat.ui.Transformations.CircleTransformation;
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
        PartyInfo partyInfo = new PartyInfo(parent.getContext());
        return new Holder(partyInfo);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        View view = holder.itemView;
        PartyInfo partyInfo = (PartyInfo) view;
        NearbyUser user = nearbies.get(position);
        partyInfo.title.setText(user.name);
        partyInfo.desc.setText(user.status);
        Picasso.with(view.getContext())
                .load("http://about.library.ubc.ca/files/2014/09/Mark_Christensen.jpeg")
                .fit()
                .centerCrop()
                .transform(new CircleTransformation())
                .into(partyInfo.avatar);
    }

    @Override
    public int getItemCount() {
        return nearbies.size();
    }
}
