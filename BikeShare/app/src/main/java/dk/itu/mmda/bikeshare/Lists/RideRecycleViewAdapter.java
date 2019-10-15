package dk.itu.mmda.bikeshare.Lists;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import dk.itu.mmda.bikeshare.EndRideActivity;
import dk.itu.mmda.bikeshare.Model.Ride;
import dk.itu.mmda.bikeshare.Model.RideRepository;
import io.realm.OrderedRealmCollection;
import io.realm.RealmRecyclerViewAdapter;

public class RideRecycleViewAdapter extends RealmRecyclerViewAdapter<Ride, RideHolder> {
    private Context mContext;
    private RideRepository mRideRepository;

    public RideRecycleViewAdapter(OrderedRealmCollection<Ride> data, Context context) {
        super(data, true);
        mContext = context;
        mRideRepository = RideRepository.getInstance();
        // Only set this if the model class has a primary key that is also a integer or long.
        // In that case, {@code getItemId(int)} must also be overridden to return the key.
        // See https://developer.android.com/reference/android/support/v7/widget/RecyclerView.Adapter.html#hasStableIds()
        // See https://developer.android.com/reference/android/support/v7/widget/RecyclerView.Adapter.html#getItemId(int)
        setHasStableIds(true);
    }


    @NonNull
    @Override
    public RideHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater =
                LayoutInflater.from(parent.getContext());
        return new RideHolder(layoutInflater, parent);
    }

    @Override
    public void onBindViewHolder(@NonNull RideHolder holder, int position) {
        Ride ride = getItem(position);
        holder.bind(ride);

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(mContext, EndRideActivity.class);
            intent.putExtra("rideId", ride.getId());
            mContext.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return getData().size();
    }

}
