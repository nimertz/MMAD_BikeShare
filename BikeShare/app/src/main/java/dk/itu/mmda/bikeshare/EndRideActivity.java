package dk.itu.mmda.bikeshare;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
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
import android.text.Editable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextWatcher;
import android.text.style.URLSpan;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import dk.itu.mmda.bikeshare.Lists.CheckBikePagerActivity;
import dk.itu.mmda.bikeshare.Model.Account;
import dk.itu.mmda.bikeshare.Model.AccountRepository;
import dk.itu.mmda.bikeshare.Model.Ride;
import dk.itu.mmda.bikeshare.Model.RideLocation;
import dk.itu.mmda.bikeshare.Model.RideRepository;
import dk.itu.mmda.bikeshare.Model.Specifications.RideByIdSpecification;

public class EndRideActivity extends AppCompatActivity {
    // GUI variables
    private TextView mRideID;
    private TextView mBikeID;
    private TextView mStartLocation;
    private TextView mStartTime;
    private TextView mEndTime;
    private EditText mEndLocation;
    private EditText mRideCost;
    private LinearLayout mEndTimeLayout;
    private Button mEndRide;
    private Button mShowMap;
    private TextView mDistance;

    private Ride mRide;

    private RideRepository mRideRepository;

    private ArrayList<String> mPermissions = new ArrayList<>();
    private static final int ALL_PERMISSIONS_RESULT = 1011;

    private FusedLocationProviderClient mFusedLocationProviderClient;
    private LocationCallback mLocationCallback;
    private RideLocation endLocation;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_end_ride);

        checkPermissions();

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


        mRideRepository = RideRepository.getInstance();
        String rideId = getIntent().getStringExtra("rideId");

        mRide = mRideRepository.query(new RideByIdSpecification(rideId)).get(0);

        //Texts
        mRideID = findViewById(R.id.ride_id_textview);
        mRideID.setText(mRide.getId());

        mBikeID = findViewById(R.id.bike_id_textview_ride);
        mBikeID.setText(mRide.getBike().getId());
        makeTextViewHyperlink(mBikeID);
        mBikeID.setOnClickListener(v -> {
            Intent intent = new Intent(getBaseContext(), CheckBikePagerActivity.class);
            intent.putExtra("bikeId", mRide.getBike().getId());
            startActivity(intent);
            finish();
        });

        mStartLocation = findViewById(R.id.start_location_textview);
        mStartLocation.setText(mRide.getStartLocation().toString());


        mStartTime = findViewById(R.id.start_time_textview);
        SimpleDateFormat ft = new SimpleDateFormat("HH:mm - dd/MM/yyyy ");
        String startD = ft.format(mRide.getStartDate());
        mStartTime.setText(startD);



        mEndTime = findViewById(R.id.end_time_textview);
        mEndTimeLayout = findViewById(R.id.end_time_layout);
        mEndTimeLayout.setVisibility(View.GONE);

        mEndLocation = findViewById(R.id.end_location_textview);
        mEndLocation.addTextChangedListener(watcher);

        mRideCost = findViewById(R.id.ride_cost_textview);
        mRideCost.setText(String.format("%s $", mRide.getCost()));

        mDistance = findViewById(R.id.distance_textview);
        mDistance.setVisibility(View.GONE);

        //Button
        mEndRide = findViewById(R.id.end_button);
        mEndRide.setEnabled(false);

        mShowMap = findViewById(R.id.show_map_button);
        mShowMap.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), MapsActivity.class);
            intent.putExtra("StartLongitude", mRide.getStartLocation().longitude);
            intent.putExtra("StartLatitude", mRide.getStartLocation().latitude);
            intent.putExtra("StartAddress", mRide.getStartLocation().locationText);

            intent.putExtra("EndLongitude", mRide.getEndLocation().longitude);
            intent.putExtra("EndLatitude", mRide.getEndLocation().latitude);
            intent.putExtra("EndAddress", mRide.getEndLocation().locationText);

            startActivity(intent);
        });
        mShowMap.setVisibility(View.GONE);

        //if ride has ended
        if (!mRide.getEndLocation().toString().equals("")) {
            mEndRide.setVisibility(View.GONE);
            mEndLocation.setText(mRide.getEndLocation().toString());
            mEndLocation.setKeyListener(null);
            mEndLocation.setBackground(null);
            mEndTimeLayout.setVisibility(View.VISIBLE);

            String endD = ft.format(mRide.getEndDate());
            mEndTime.setText(endD);

            mShowMap.setVisibility(View.VISIBLE);

            mDistance.setVisibility(View.VISIBLE);
            mDistance.setText(String.format("%dm", CalculateTraveledDistance(mRide.getStartLocation(), mRide.getEndLocation())));
        } else {
            //show much the ride would cost if ended now
            mRideCost.setText(String.format("%s$", calculateCurrentCost()));
            mLocationCallback = new LocationCallback() {
                @Override
                public void onLocationResult(LocationResult locationResult) {
                    if (locationResult == null) return;
                    for (Location location : locationResult.getLocations()) {
                        double longitude = location.getLongitude();
                        double latitude = location.getLatitude();

                        endLocation = new RideLocation(longitude, latitude, getAddress(longitude, latitude));
                        mEndLocation.setText(endLocation.toString());
                    }
                }
            };
            mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        }

        //end ride button
        mEndRide.setOnClickListener(view -> {

            Account acc = AccountRepository.getInstance().getCurrentAccount();
            if (acc.getBalance() < calculateCurrentCost()) {
                Dialog d = balanceToLowDialog(acc);
                d.show();
            } else {

                mRideRepository.endRide(mRide, endLocation);

                Toast.makeText(this, "Ride ended", Toast.LENGTH_LONG).show();

                Intent intent = new Intent(this, EndRideActivity.class);
                intent.putExtra("rideId", mRide.getId());
                startActivity(intent);
                finish();
            }
        });

    }

    private Dialog balanceToLowDialog(Account acc) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        // Add the buttons
        builder.setPositiveButton("OK", (dialog, id) -> {
            // User clicked OK button
            Intent intent = new Intent(this, AccountActivity.class);
            startActivity(intent);
        });

        builder.setTitle("Balance to low");
        builder.setMessage(
                "Your balance is currently: " + acc.getBalance() +
                        " $ and the ride cost is " + calculateCurrentCost()
                        + " $. Please add at least " +
                        Math.abs(acc.getBalance() - calculateCurrentCost()) + " $ to your account.");

        // Create the AlertDialog

        return builder.create();
    }


    @Override
    protected void onPause() {
        super.onPause();
        //only use gps if ride hasn't ended
        if (mRide.getEndLocation().toString().equals("")) {
            stopLocationUpdates();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        //only use gps if ride hasn't ended
        if (mRide.getEndLocation().toString().equals("")) {
            startLocationUpdates();
        }
    }

    private void startLocationUpdates() {
        LocationRequest locationRequest = new LocationRequest();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(5000);
        locationRequest.setFastestInterval(5000);
        if (ActivityCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mFusedLocationProviderClient.requestLocationUpdates(locationRequest, mLocationCallback, null);
    }

    private void stopLocationUpdates() {
        mFusedLocationProviderClient
                .removeLocationUpdates(mLocationCallback);
    }

    /**
     * gets address using coordinates
     */
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
            if (mEndLocation.getText().toString().trim().length() == 0) {
                mEndRide.setEnabled(false);
            } else {
                mEndRide.setEnabled(true);
            }
        }
    };

    /**
     * calculates cost based on price per hour of the ride's bike
     *
     * @return
     */
    private double calculateCurrentCost() {
        long diff = Math.abs(new Date().getTime() - mRide.getStartDate().getTime());
        long hours = TimeUnit.HOURS.convert(diff, TimeUnit.MILLISECONDS);

        double currentCost = mRide.getBike().getPricePerHour() * hours;
        if (currentCost <= 0.0) currentCost = mRide.getBike().getPricePerHour();

        return currentCost;
    }

    /**
     * calculates distance between start and end location
     */
    private int CalculateTraveledDistance(RideLocation start, RideLocation end) {

        Location loc1 = new Location("");
        loc1.setLatitude(start.latitude);
        loc1.setLongitude(start.longitude);

        Location loc2 = new Location("");
        loc2.setLatitude(end.latitude);
        loc2.setLongitude(end.longitude);

        return (int) loc1.distanceTo(loc2);
    }

    /**
     * Sets a hyperlink style to the textView.
     */
    private static void makeTextViewHyperlink(TextView tv) {
        SpannableStringBuilder ssb = new SpannableStringBuilder();
        ssb.append(tv.getText());
        ssb.setSpan(new URLSpan("#"), 0, ssb.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        tv.setText(ssb, TextView.BufferType.SPANNABLE);
    }
}
