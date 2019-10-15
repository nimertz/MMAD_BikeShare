package dk.itu.mmda.bikeshare;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;


import dk.itu.mmda.bikeshare.Lists.RideRecycleViewAdapter;
import dk.itu.mmda.bikeshare.Model.Bike;
import dk.itu.mmda.bikeshare.Model.BikeRepository;
import dk.itu.mmda.bikeshare.Model.Specifications.BikeByIdSpecification;

public class CheckBikeFragment extends Fragment {
    private ImageView mImageViewBike;
    private TextView mTextViewBikeId;
    private TextView mTextViewBikeName;
    private TextView mTextViewBikePrice;
    private TextView mTextViewStatus;
    private Button mStartRideButton;

    //list of rides
    private RecyclerView mRecyclerView;
    private RideRecycleViewAdapter mAdapter;

    private Bike mBike;
    private BikeRepository mBikeRepository;

    public static final String ARG_BIKE_ID = "bike_id";

    public static android.support.v4.app.Fragment newInstance(String bikeId) {
        Bundle args = new Bundle();
        args.putString(ARG_BIKE_ID, bikeId);
        CheckBikeFragment fragment = new CheckBikeFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBikeRepository = BikeRepository.getInstance();
        String bikeId = getArguments().getString(ARG_BIKE_ID);
        mBike = mBikeRepository.query(new BikeByIdSpecification(bikeId)).get(0);

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_check_bike, container, false);

        mImageViewBike = v.findViewById(R.id.imageView_bike_image);
        byte[] imageBytes = mBike.getImage();
        if (imageBytes != null && imageBytes.length != 0) {
            Bitmap bitmap = BitmapFactory.decodeByteArray(mBike.getImage(), 0, mBike.getImage().length);
            mImageViewBike.setImageBitmap(bitmap);
        } else {
            mImageViewBike.setImageResource(R.drawable.default_bike);
        }

        mTextViewBikeId = v.findViewById(R.id.textview_bike_id);
        mTextViewBikeId.setText(mBike.getId());

        mTextViewBikeName = v.findViewById(R.id.textview_bike_name);
        mTextViewBikeName.setText(mBike.getBikeName());

        mTextViewBikePrice = v.findViewById(R.id.textview_price);
        String price = mBike.getPricePerHour() + " $";
        mTextViewBikePrice.setText(price);

        mStartRideButton = v.findViewById(R.id.button_start_ride);
        mStartRideButton.setVisibility(View.GONE);
        mStartRideButton.setOnClickListener(v1 -> {
            Intent intent = new Intent(getActivity(), StartRideActivity.class);
            intent.putExtra(ARG_BIKE_ID, mBike.getId());
            startActivity(intent);
            getActivity().finish();
        });

        mTextViewStatus = v.findViewById(R.id.textview_status);
        if (mBike.isFreeStatus()) {
            mTextViewStatus.setText(R.string.free);
            mStartRideButton.setVisibility(View.VISIBLE);
        } else {
            mTextViewStatus.setText(R.string.in_use);
            mStartRideButton.setVisibility(View.GONE);
        }

        // Create and set the adapter - use all rides as data
        mAdapter = new RideRecycleViewAdapter(mBike.getRides(), getActivity());
        mRecyclerView = v.findViewById(R.id.bike_ride_recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.setAdapter(mAdapter);


        return v;
    }

}
