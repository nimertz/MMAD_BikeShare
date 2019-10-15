package dk.itu.mmda.bikeshare.Lists;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import dk.itu.mmda.bikeshare.CheckBikeFragment;
import dk.itu.mmda.bikeshare.Model.Bike;
import dk.itu.mmda.bikeshare.Model.BikeRepository;
import dk.itu.mmda.bikeshare.R;
import io.realm.OrderedRealmCollection;

public class CheckBikePagerActivity extends AppCompatActivity {

    private ViewPager mViewPager;
    private OrderedRealmCollection<Bike> mBikes;

    private static final String EXTRA_BIKE_ID = "bikeId";

    private BikeRepository mBikeRepository;

    public static Intent newIntent(Context packageContext, String bikeId) {
        Intent intent = new Intent(packageContext, CheckBikePagerActivity.class);
        intent.putExtra(EXTRA_BIKE_ID, bikeId);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_bike_pager);

        mBikeRepository = BikeRepository.getInstance();
        mBikes = mBikeRepository.getAllBikes();

        mViewPager = findViewById(R.id.bike_view_pager);

        String bikeId = getIntent().getStringExtra(EXTRA_BIKE_ID);

        FragmentManager fragmentManager = getSupportFragmentManager();
        mViewPager.setAdapter(new FragmentStatePagerAdapter(fragmentManager) {
            @Override
            public Fragment getItem(int position) {
                Bike bike = mBikes.get(position);
                return CheckBikeFragment.newInstance(bike.getId());
            }

            @Override
            public int getCount() {
                return mBikes.size();
            }
        });

        //set correct indexing
        for (int i = 0; i < mBikes.size(); i++) {
            if (mBikes.get(i).getId().equals(bikeId)) {
                mViewPager.setCurrentItem(i);
                break;
            }
        }

    }
}
