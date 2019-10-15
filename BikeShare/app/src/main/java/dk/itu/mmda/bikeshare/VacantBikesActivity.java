package dk.itu.mmda.bikeshare;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;

import dk.itu.mmda.bikeshare.Lists.BikeRecycleViewAdapter;
import dk.itu.mmda.bikeshare.Model.BikeRepository;

public class VacantBikesActivity extends AppCompatActivity {
    private BikeRepository mBikeRepository;
    private RecyclerView mRecyclerView;
    private BikeRecycleViewAdapter mAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vacant_bikes);

        //repository for getting realm objects (rides)
        mBikeRepository = BikeRepository.getInstance();

        // Create and set the adapter - use all rides as data
        mAdapter = new BikeRecycleViewAdapter(mBikeRepository.getAllVacantBikes(), this);
        mRecyclerView = findViewById(R.id.bike_recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(
                new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        mRecyclerView.setAdapter(mAdapter);
    }
}
