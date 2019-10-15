package dk.itu.mmda.bikeshare.Model.Specifications;

import dk.itu.mmda.bikeshare.Model.Ride;
import io.realm.Realm;
import io.realm.RealmResults;

public class RideByIdSpecification implements RealmSpecification {
    private final String id;

    public RideByIdSpecification(final String id) {
        this.id = id;
    }

    @Override
    public RealmResults<Ride> toRealmResults(Realm realm) {
        return realm.where(Ride.class)
                .equalTo("id", id)
                .findAll();
    }
}
