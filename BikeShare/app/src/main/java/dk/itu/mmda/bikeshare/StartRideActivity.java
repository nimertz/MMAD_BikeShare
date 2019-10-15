package dk.itu.mmda.bikeshare;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.widget.Button;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;

import dk.itu.mmda.bikeshare.Lists.DeviceAdapter;
import dk.itu.mmda.bikeshare.Model.Bike;
import dk.itu.mmda.bikeshare.Model.BikeRepository;
import dk.itu.mmda.bikeshare.Model.Ride;
import dk.itu.mmda.bikeshare.Model.RideLocation;
import dk.itu.mmda.bikeshare.Model.Specifications.BikeByIdSpecification;

public class StartRideActivity extends AppCompatActivity {
    // GUI variables
    private Button mAddRide;
    private TextView mStartLocation;
    private TextView mBikeId;
    private Ride mRide;
    private Bike mBike;

    private BikeRepository mBikeRepository;

    private ArrayList<String> mPermissions = new ArrayList<>();
    private static final int ALL_PERMISSIONS_RESULT = 1011;

    //GPS
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private LocationCallback mLocationCallback;
    private RideLocation startLocation;

    //Bluetooth
    private BluetoothAdapter mBluetoothAdapter;
    private Set<BluetoothDevice> mPairedDevices;
    private List<String> mDevices;
    private Switch mToggleBluetooth;
    private Button mRefreshButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_ride);

        checkPermissions();
        setupBluetoothPairing();

        LocationManager service = (LocationManager) getSystemService(LOCATION_SERVICE);
        boolean enabled = service
                .isProviderEnabled(LocationManager.GPS_PROVIDER);
        // check if enabled and if not send user to the GSP settings
        // go to the settings
        if (!enabled) {
            Toast t = Toast.makeText(this, "Please turn on GPS before returning to BikeShare", Toast.LENGTH_LONG);
            t.setGravity(Gravity.CENTER, 0, 0);
            t.show();
            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivity(intent);
        }


        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) return;
                for (Location location : locationResult.getLocations()) {
                    double longitude = location.getLongitude();
                    double latitude = location.getLatitude();

                    startLocation = new RideLocation(longitude, latitude, getAddress(longitude, latitude));
                    mStartLocation.setText(startLocation.toString());
                }
            }
        };

        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        mBikeRepository = BikeRepository.getInstance();

        String bikeId = getIntent().getStringExtra(CheckBikeFragment.ARG_BIKE_ID);

        //get bike you're starting ride for
        mBike = mBikeRepository.query(new BikeByIdSpecification(bikeId)).get(0);

        //Button
        mAddRide = findViewById(R.id.add_button);
        mAddRide.setEnabled(false);

        //Texts
        mStartLocation = findViewById(R.id.start_location_textview);
        mStartLocation.addTextChangedListener(watcher);

        mBikeId = findViewById(R.id.bike_id_textview_start);
        mBikeId.setText(mBike.getId());

        //View products click event
        mAddRide.setOnClickListener(view -> {
            if (mStartLocation.getText().length() > 0) {

                mRide = new Ride();
                mRide.setBike(mBike);
                mRide.setStartLocation(startLocation);
                mRide.setRideStartDate();

                mBikeRepository.startRide(mRide, mBike);

                //Reset text fields
                mStartLocation.setText("");

                //Go to ride created
                Intent intent = new Intent(this, EndRideActivity.class);
                intent.putExtra("rideId", mRide.getId());
                startActivity(intent);
                finish();
            }
        });
    }

    /**
     * sets up the bluetooth functionality - naive proof of concept
     * is not fully implemented since bikes aren't setup with beacons and bluetooth identifiers.
     * After selecting a bike the bluetooth address would be compared and verified with the bike id,
     * unlocking the bike in the process - the same process would happen when locking the bike.
     */
    private void setupBluetoothPairing() {
        mToggleBluetooth = findViewById(R.id.bluetooth_switch);
        mRefreshButton = findViewById(R.id.refresh_bluetooth_list_button);
        mRefreshButton.setEnabled(false);

        final DeviceAdapter deviceAdapter = new DeviceAdapter();
        RecyclerView recyclerView = findViewById(R.id.bluetooth_device_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(deviceAdapter);

        mDevices = new ArrayList<>();

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter != null) {
            mToggleBluetooth.setChecked(mBluetoothAdapter.isEnabled());
            mToggleBluetooth.setOnClickListener(view -> {
                boolean status = mToggleBluetooth.isChecked();
                if (status) {
                    if (!mBluetoothAdapter.isEnabled()) {
                        Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                        startActivityForResult(intent, 0);
                    }
                    mRefreshButton.setEnabled(true);
                } else {
                    mBluetoothAdapter.disable();
                    mRefreshButton.setEnabled(false);
                }

            });

            mRefreshButton.setOnClickListener(view -> {
                mDevices.clear();
                mPairedDevices = mBluetoothAdapter.getBondedDevices();
                for (BluetoothDevice device : mPairedDevices)
                    mDevices.add(device.getName() + " (" + device.getAddress() + ")");
                deviceAdapter.setDevices(mDevices);
                if (!mBluetoothAdapter.isDiscovering()) {
                    Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
                    startActivityForResult(intent, 0);
                }
            });

            //mBluetoothAdapter.disable();

        } else {
            mToggleBluetooth.setEnabled(false);
            mRefreshButton.setEnabled(false);
            mDevices.add("This device does NOT support Bluetooth");
            deviceAdapter.setDevices(mDevices);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopLocationUpdates();
    }

    @Override
    protected void onResume() {
        super.onResume();
        startLocationUpdates();
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter != null) {
            mRefreshButton.setEnabled(mBluetoothAdapter.isEnabled());
        }
    }

    private void startLocationUpdates() {
        LocationRequest locationRequest = new LocationRequest();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(5000);
        locationRequest.setFastestInterval(5000);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mFusedLocationProviderClient.requestLocationUpdates(locationRequest, mLocationCallback, null);
    }

    private void stopLocationUpdates() {
        mFusedLocationProviderClient
                .removeLocationUpdates(mLocationCallback);
    }

    private String getAddress(double longitude, double latitude) {
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        StringBuilder stringBuilder = new StringBuilder();
        try {
            List<Address> addresses =
                    geocoder.getFromLocation(latitude, longitude, 1);
            if (addresses.size() > 0) {
                Address address = addresses.get(0);
                stringBuilder.append(address.getAddressLine(0)).append("\n");
                stringBuilder.append(address.getLocality()).append("\n");
                stringBuilder.append(address.getPostalCode()).append("\n");
                stringBuilder.append(address.getCountryName());
            } else
                return "No address found";
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return stringBuilder.toString();
    }

    private void checkPermissions() {
        mPermissions.add(Manifest.permission.ACCESS_FINE_LOCATION);
        mPermissions.add(Manifest.permission.ACCESS_COARSE_LOCATION);

        ArrayList<String> permissionsToRequest =
                permissionsToRequest(mPermissions);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.
                M) {
            if
            (permissionsToRequest.size() >
                    0) {
                requestPermissions(permissionsToRequest.toArray(
                        new String[permissionsToRequest.size()]),
                        ALL_PERMISSIONS_RESULT);
            }
        }
    }

    private ArrayList<String>
    permissionsToRequest(ArrayList<String> permissions) {
        ArrayList<String> result = new ArrayList<>();
        for (String permission : permissions)
            if (!hasPermission(permission))
                result.add(permission);
        return result;
    }

    private boolean hasPermission(String permission) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            return Objects.requireNonNull(this)
                    .checkSelfPermission(permission) ==
                    PackageManager.PERMISSION_GRANTED;
        return true;
    }

    private final TextWatcher watcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        @Override
        public void afterTextChanged(Editable s) {
            if (mStartLocation.getText().toString().trim().length() == 0) {
                mAddRide.setEnabled(false);
            } else {
                mAddRide.setEnabled(true);
            }
        }
    };

}
