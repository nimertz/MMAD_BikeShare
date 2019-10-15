package dk.itu.mmda.bikeshare.Model;

import java.util.List;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.Sort;
import io.realm.annotations.PrimaryKey;

public class Account extends RealmObject {
    @PrimaryKey
    private String id;
    private double mBalance;
    private String mName;
    private String mEmail;
    private String mImageUrl;
    private RealmList<Ride> mRides;

    public Account() {
    }

    public String getImageUrl() {
        return mImageUrl;
    }

    public void setImageUrl(String imageUrl) {
        mImageUrl = imageUrl;
    }

    public Account(String id) {
        this.id = id;
        this.mBalance = 1000.0;
        mRides = new RealmList<>();
    }

    public double getBalance() {
        return mBalance;
    }

    public void setBalance(double balance) {
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        this.mBalance = balance;
        realm.commitTransaction();
        realm.close();
    }

    public void subtractFromBalance(double amount) {
        mBalance = mBalance - amount;
    }

    public List<Ride> getRides() {
        Realm realm = Realm.getDefaultInstance();

        realm.beginTransaction();
        mRides.sort("mStartDate", Sort.ASCENDING);
        realm.commitTransaction();
        realm.close();


        return mRides;
    }

    public void addRide(Ride ride) {
        mRides.add(ride);
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public String getEmail() {
        return mEmail;
    }

    public void setEmail(String email) {
        mEmail = email;
    }
}


