package dk.itu.mmda.bikeshare;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.List;

import dk.itu.mmda.bikeshare.Model.RideLocation;


/**
 * A simple {@link Fragment} subclass.
 */
public class MapsFragment extends Fragment {


    public MapsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_maps, container, false);
        // Inflate the layout for this fragment
        assert getArguments() != null;
        final double startLongitude = getArguments().getDouble("StartLongitude");
        final double startLatitude = getArguments().getDouble("StartLatitude");
        final String startAddress = getArguments().getString("StartAddress");

        final double endLongitude = getArguments().getDouble("EndLongitude");
        final double endLatitude = getArguments().getDouble("EndLatitude");
        final String endAddress = getArguments().getString("EndAddress");

        List<RideLocation> locations = new ArrayList<>();
        locations.add(new RideLocation(startLongitude, startLatitude, startAddress));
        locations.add(new RideLocation(endLongitude, endLatitude, endAddress));

        SupportMapFragment mapFragment = SupportMapFragment.newInstance();
        mapFragment.getMapAsync(googleMap -> {
            googleMap.setMapType(
                    GoogleMap.MAP_TYPE_NORMAL);
            addMarks(googleMap, locations);
            //googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom( new LatLng(startLatitude, startLongitude), 18f));
        });
        getChildFragmentManager().beginTransaction().replace(R.id.map, mapFragment).commit();

        return v;


    }

    private void addMarks(GoogleMap googleMap, List<RideLocation> locations) {
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        for (RideLocation location : locations) {
            LatLng latLng = new LatLng(location.latitude, location.longitude);
            builder.include(latLng);
            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.position(latLng);
            markerOptions.title(location.locationText);
            googleMap.addMarker(markerOptions);
        }
        final LatLngBounds bounds = builder.build();
        int padding = ((2000 * 10) / 100); // offset from edges of the map in pixels
        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds,
                padding);
        googleMap.animateCamera(cu);


    }


}
