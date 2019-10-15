package dk.itu.mmda.bikeshare.Model;

import io.realm.RealmObject;

public class RideLocation extends RealmObject {
    public double longitude;
    public double latitude;
    public String locationText;

    public RideLocation(double longitude, double latitude, String address) {
        this.longitude = longitude;
        this.latitude = latitude;
        this.locationText = address;
    }

    public RideLocation() {
        locationText = "";
    }

    public RideLocation(String locationText) {
        this.locationText = locationText;
        this.longitude = 0.0;
        this.latitude = 0.0;
    }

    @Override
    public String toString() {
        return locationText;
    }
}
