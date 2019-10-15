package dk.itu.mmda.bikeshare.Model;

import java.util.Date;
import java.util.UUID;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class Ride extends RealmObject {
    @PrimaryKey
    private String id;
    private Bike mBike;
    private Account mAccount;

    private RideLocation mStartLocation;
    private RideLocation mEndLocation;

    private Date mStartDate;
    private Date mEndDate;
    private double mCost;

    public Ride() {
        id = UUID.randomUUID().toString();
        mStartLocation = new RideLocation("");
        mEndLocation = new RideLocation("");

    }

    public Bike getBike() {
        return mBike;
    }

    public void setBike(Bike bike) {
        mBike = bike;
    }

    public RideLocation getStartLocation() {
        return mStartLocation;
    }

    public void setStartLocation(RideLocation startLocation) {
        mStartLocation = startLocation;
    }

    public RideLocation getEndLocation() {
        return mEndLocation;
    }

    public void setEndLocation(RideLocation endLocation) {
        mEndLocation = endLocation;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setRideStartDate() {
        mStartDate = new Date();
    }

    public void setRideEndDate() {
        mEndDate = new Date();
    }

    public double getCost() {
        return mCost;
    }

    public void setCost(double cost) {
        mCost = cost;
    }

    public Date getStartDate() {
        return mStartDate;
    }

    public Date getEndDate() {
        return mEndDate;
    }

    public Account getAccount() {
        return mAccount;
    }

    public void setAccount(Account account) {
        mAccount = account;
    }
}
