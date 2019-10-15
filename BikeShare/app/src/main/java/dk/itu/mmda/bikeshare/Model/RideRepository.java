package dk.itu.mmda.bikeshare.Model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import dk.itu.mmda.bikeshare.Model.Specifications.RealmSpecification;
import io.realm.Realm;
import io.realm.RealmResults;

public class RideRepository implements Repository<Ride> {
    private AccountRepository mAccountRepository = AccountRepository.getInstance();


    private RideRepository() {
    }

    private static RideRepository INSTANCE = null;

    // other instance variables can be here

    public static RideRepository getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new RideRepository();
        }
        return (INSTANCE);
    }


    @Override
    public void add(final Ride item) {
        final Realm realm = Realm.getDefaultInstance();


        realm.executeTransaction(realm1 -> {
            Account acc = mAccountRepository.getCurrentAccount();

            realm1.copyToRealm(item);
            //add owner to ride and ride to account
            item.setAccount(acc);
            acc.addRide(item);
        });

        realm.close();
    }

    @Override
    public void add(Iterable<Ride> items) {

    }

    @Override
    public void update(final Ride item) {
        final Realm realm = Realm.getDefaultInstance();

        realm.executeTransaction(realm1 -> {
            final Ride ride = realm1.where(Ride.class).equalTo("id", item.getId()).findFirst();
            if (ride != null) {
                ride.setBike(item.getBike());
                ride.setStartLocation(item.getStartLocation());
                ride.setEndLocation(item.getEndLocation());
                realm1.insertOrUpdate(ride);
            }
        });

        realm.close();
    }

    public void endRide(final Ride ride, RideLocation endLocation) {
        final Realm realm = Realm.getDefaultInstance();

        realm.executeTransaction(realm1 -> {
            //to make sure object is managed - realm
            RideLocation item = realm1.copyToRealm(endLocation);

            ride.setEndLocation(item);
            ride.setRideEndDate();
            //calculate cost
            double cost = calculateRideCost(ride);
            ride.setCost(cost);

            //deduct from account
            Account acc = mAccountRepository.getCurrentAccount();
            acc.subtractFromBalance(cost);

            ride.getBike().setFreeStatus(true);
            realm1.insertOrUpdate(ride);
        });

        realm.close();
    }

    /**
     * calculates the cost of the ride based on the bike.
     *
     * @param ride
     * @return total cost of ride based on bike and hours rented
     */
    private double calculateRideCost(Ride ride) {
        double cost;
        long diff = Math.abs(ride.getEndDate().getTime() - ride.getStartDate().getTime());
        long hours = TimeUnit.HOURS.convert(diff, TimeUnit.MILLISECONDS);

        cost = ride.getBike().getPricePerHour() * hours;
        //lowest rent price is for one hour.
        if (cost <= 0.0) cost = ride.getBike().getPricePerHour();

        return cost;
    }

    @Override
    public void remove(final Ride item) {
        final Realm realm = Realm.getDefaultInstance();

        realm.executeTransaction(realm1 -> {
            final Ride ride = realm1.where(Ride.class).equalTo("id", item.getId()).findFirst();
            if (ride != null) {
                ride.deleteFromRealm();
            }
        });

        realm.close();
    }

    @Override
    public void remove(RealmSpecification specification) {
    }

    @Override
    public List<Ride> query(RealmSpecification specification) {

        final Realm realm = Realm.getDefaultInstance();
        final RealmResults<Ride> realmResults = specification.toRealmResults(realm);

        final List<Ride> rides = new ArrayList<>(realmResults);

        realm.close();

        return rides;
    }

    public RealmResults<Ride> getAllRides() {

        final Realm realm = Realm.getDefaultInstance();
        return realm.where(Ride.class).findAll().sort("id");

    }

    public RealmResults<Ride> getActiveRides() {
        final Realm realm = Realm.getDefaultInstance();
        return realm.where(Ride.class).equalTo("mEndDate", (Date) null).findAll().sort("mStartDate");
    }


    public void clean() {
        final Realm realm = Realm.getDefaultInstance();
        realm.executeTransaction(realm1 -> realm1.where(Ride.class).findAll().deleteAllFromRealm());

        realm.close();
    }
}
