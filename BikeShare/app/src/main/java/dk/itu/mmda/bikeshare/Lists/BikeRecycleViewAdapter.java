package dk.itu.mmda.bikeshare.Lists;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.util.Objects;

import dk.itu.mmda.bikeshare.Model.Bike;
import dk.itu.mmda.bikeshare.Model.BikeRepository;
import io.realm.OrderedRealmCollection;
import io.realm.RealmRecyclerViewAdapter;

public class BikeRecycleViewAdapter extends RealmRecyclerViewAdapter<Bike, BikeHolder> {
    private Context mContext;
    private BikeRepository mBikeRepository;

    public BikeRecycleViewAdapter(OrderedRealmCollection<Bike> data, Context context) {
        super(data, true);
        mContext = context;
        mBikeRepository = BikeRepository.getInstance();
        // Only set this if the model class has a primary key that is also a integer or long.
        // In that case, {@code getItemId(int)} must also be overridden to return the key.
        // See https://developer.android.com/reference/android/support/v7/widget/RecyclerView.Adapter.html#hasStableIds()
        // See https://developer.android.com/reference/android/support/v7/widget/RecyclerView.Adapter.html#getItemId(int)
        setHasStableIds(true);
    }


    @NonNull
    @Override
    public BikeHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater =
                LayoutInflater.from(parent.getContext());
        return new BikeHolder(layoutInflater, parent);
    }

    @Override
    public void onBindViewHolder(@NonNull BikeHolder holder, int position) {
        Bike bike = getItem(position);
        holder.bind(bike);

        holder.itemView.setOnClickListener(v -> {
            Intent intent = CheckBikePagerActivity.newIntent(mContext, Objects.requireNonNull(bike).getId());
            mContext.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return Objects.requireNonNull(getData()).size();
    }
}
