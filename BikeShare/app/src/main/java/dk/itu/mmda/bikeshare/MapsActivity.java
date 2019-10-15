package dk.itu.mmda.bikeshare;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;


public class MapsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);


        double startLongitude = getIntent().getDoubleExtra("StartLongitude", 0);
        double startLatitude = getIntent().getDoubleExtra("StartLatitude", 0);
        String startAddress = getIntent().getStringExtra("StartAddress");

        double endLongitude = getIntent().getDoubleExtra("EndLongitude", 0);
        double endLatitude = getIntent().getDoubleExtra("EndLatitude", 0);
        String endAddress = getIntent().getStringExtra("EndAddress");

        Bundle bundle = new Bundle();
        bundle.putDouble("StartLongitude", startLongitude);
        bundle.putDouble("StartLatitude", startLatitude);
        bundle.putString("StartAddress", startAddress);

        bundle.putDouble("EndLongitude", endLongitude);
        bundle.putDouble("EndLatitude", endLatitude);
        bundle.putString("EndAddress", endAddress);

        Fragment fragment = new MapsFragment();
        fragment.setArguments(bundle);
        FragmentManager fm = getSupportFragmentManager();
        fm.beginTransaction().add(R.id.fragment_maps_container, fragment).commit();
    }
}
