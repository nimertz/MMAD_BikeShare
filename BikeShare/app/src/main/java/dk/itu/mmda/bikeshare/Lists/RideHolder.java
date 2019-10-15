package dk.itu.mmda.bikeshare.Lists;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.SimpleDateFormat;

import dk.itu.mmda.bikeshare.Model.Ride;
import dk.itu.mmda.bikeshare.R;

public class RideHolder extends RecyclerView.ViewHolder {
    private ImageView mBikeImageView;
    private TextView mBikeNameView;
    private TextView mStartDate;
    private TextView mBikePrice;


    public RideHolder(LayoutInflater inflater, ViewGroup parent) {
        super(inflater.inflate(R.layout.list_item_ride, parent, false));
        mBikeNameView = itemView.findViewById(R.id.what_bike_ride);
        mStartDate = itemView.findViewById(R.id.start_ride);
        mBikePrice = itemView.findViewById(R.id.ride_price);


    }

    public void bind(Ride ride) {
        mBikeImageView = itemView.findViewById(R.id.imageView_ride_holder);
        byte[] imageBytes = ride.getBike().getImage();
        if (imageBytes != null && imageBytes.length != 0) {
            Bitmap bitmap = BitmapFactory.decodeByteArray(ride.getBike().getImage(), 0, ride.getBike().getImage().length);
            mBikeImageView.setImageBitmap(bitmap);
        } else {
            mBikeImageView.setImageResource(R.drawable.default_bike);
        }

        mBikeNameView.setText(ride.getBike().getBikeName());

        SimpleDateFormat ft = new SimpleDateFormat("HH:mm - dd/MM/yyyy ");
        String date = ft.format(ride.getStartDate());

        mStartDate.setText(date);

        mBikePrice.setText(String.format("%s$", ride.getCost()));


    }
}
