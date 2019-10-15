package dk.itu.mmda.bikeshare.Model;

import java.util.ArrayList;
import java.util.List;

import dk.itu.mmda.bikeshare.Model.Specifications.RealmSpecification;
import io.realm.Realm;
import io.realm.RealmResults;

public class BikeRepository implements Repository<Bike> {
    private static BikeRepository INSTANCE = null;
    private AccountRepository mAccountRepository = AccountRepository.getInstance();

    // other instance variables can be here

    public static BikeRepository getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new BikeRepository();
        }
        return (INSTANCE);
    }


    private BikeRepository() {
    }


    @Override
    public void add(final Bike item) {
        final Realm realm = Realm.getDefaultInstance();

        realm.executeTransaction(realm1 -> {
            final Bike bike = realm1.copyToRealm(item);
        });

        realm.close();
    }

    @Override
    public void add(Iterable<Bike> items) {

    }

    @Override
    public void update(final Bike item) {
        final Realm realm = Realm.getDefaultInstance();

        realm.executeTransaction(realm1 -> {
            final Bike bike = realm1.where(Bike.class).equalTo("id", item.getId()).findFirst();
            if (bike != null) {
                bike.setImage(item.getImage());
                bike.setPricePerHour(item.getPricePerHour());
                bike.setRides(item.getRides());
                bike.setType(item.getType());
                realm1.insertOrUpdate(bike);
            }
        });

        realm.close();
    }

    public void startRide(final Ride ride, final Bike bike) {
        final Realm realm = Realm.getDefaultInstance();

        realm.executeTransaction(realm1 -> {
            if (bike != null) {

                Account acc = mAccountRepository.getCurrentAccount();


                ride.setCost(bike.getPricePerHour());
                bike.addRide(ride);
                bike.setFreeStatus(false);

                //add owner to ride and ride to account
                ride.setAccount(acc);
                acc.addRide(ride);

                realm1.insertOrUpdate(bike);
            }
        });

        realm.close();
    }


    @Override
    public void remove(final Bike item) {
        final Realm realm = Realm.getDefaultInstance();

        realm.executeTransaction(realm1 -> {
            final Bike bike = realm1.where(Bike.class).equalTo("id", item.getId()).findFirst();
            if (bike != null) {
                bike.deleteFromRealm();
            }
        });

        realm.close();
    }


    public RealmResults<Bike> getAllVacantBikes() {
        final Realm realm = Realm.getDefaultInstance();
        return realm.where(Bike.class)
                .equalTo("mFreeStatus", true)
                .findAll().sort("id");
    }

    @Override
    public void remove(RealmSpecification specification) {
    }

    @Override
    public List<Bike> query(RealmSpecification specification) {

        final Realm realm = Realm.getDefaultInstance();
        final RealmResults<Bike> realmResults = specification.toRealmResults(realm);

        final List<Bike> rides = new ArrayList<>(realmResults);

        realm.close();

        return rides;
    }

    public RealmResults<Bike> getAllBikes() {

        final Realm realm = Realm.getDefaultInstance();
        return realm.where(Bike.class).findAll().sort("mBikeName");

    }
}

