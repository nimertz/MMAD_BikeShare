package dk.itu.mmda.bikeshare.Lists;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import dk.itu.mmda.bikeshare.Model.Bike;
import dk.itu.mmda.bikeshare.R;

public class BikeHolder extends RecyclerView.ViewHolder {
    private TextView mBikeName;
    private TextView mBikeType;
    private ImageView mBikeImage;


    public BikeHolder(LayoutInflater inflater, ViewGroup parent) {
        super(inflater.inflate(R.layout.list_item_bike, parent, false));
        mBikeName = itemView.findViewById(R.id.bike_name_textView_holder);
        mBikeType = itemView.findViewById(R.id.bike_type_textView_holder);
        mBikeImage = itemView.findViewById(R.id.bike_imageView_holder);

    }

    public void bind(Bike bike) {
        mBikeName.setText(bike.getBikeName());
        mBikeType.setText(bike.getType().toString());

        byte[] imageBytes = bike.getImage();
        if (imageBytes != null && imageBytes.length != 0) {
            Bitmap bitmap = BitmapFactory.decodeByteArray(bike.getImage(), 0, bike.getImage().length);
            mBikeImage.setImageBitmap(bitmap);
        } else {
            mBikeImage.setImageResource(R.drawable.default_bike);
        }

    }
}
