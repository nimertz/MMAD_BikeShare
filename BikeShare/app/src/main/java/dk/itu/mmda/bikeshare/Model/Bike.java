package dk.itu.mmda.bikeshare.Model;

import java.util.UUID;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class Bike extends RealmObject {
    @PrimaryKey
    private String id;
    private String mBikeName;
    private String mType;
    private RealmList<Ride> mRides;
    private byte[] mImage;
    private double mPricePerHour;
    private boolean mFreeStatus;

    public String getBikeName() {
        return mBikeName;
    }

    public void setBikeName(String bikeName) {
        mBikeName = bikeName;
    }

    public Bike(String bikeName, String type, double pricePerHour) {
        id = UUID.randomUUID().toString();
        mBikeName = bikeName;
        mType = type;
        mFreeStatus = true;
        mRides = new RealmList<>();
        mImage = new byte[]{};
        mPricePerHour = pricePerHour;
    }

    public Bike() {
        id = UUID.randomUUID().toString();
        mFreeStatus = true;
        mImage = new byte[]{};
    }

    public String getId() {
        return id;
    }

    public BikeType getType() {
        return BikeType.valueOf(mType);
    }

    public void setType(BikeType type) {
        mType = type.toString();
    }

    public RealmList<Ride> getRides() {
        return mRides;
    }

    public void setRides(RealmList<Ride> rides) {
        mRides = rides;
    }

    public byte[] getImage() {
        return mImage;
    }

    public void setImage(byte[] image) {
        mImage = image;
    }

    public double getPricePerHour() {
        return mPricePerHour;
    }

    public void setPricePerHour(double pricePerHour) {
        mPricePerHour = pricePerHour;
    }

    public void addRide(Ride ride) {
        mRides.add(ride);
    }

    public boolean isFreeStatus() {
        return mFreeStatus;
    }

    public void setFreeStatus(boolean freeStatus) {
        mFreeStatus = freeStatus;
    }

    @Override
    public String toString() {
        return "Bike{" +
                "id=" + id +
                ", mBikeName='" + mBikeName + '\'' +
                ", mType='" + mType + '\'' +
                ", mRides=" + mRides +
                ", mPricePerHour=" + mPricePerHour +
                '}';
    }
}
