package dk.itu.mmda.bikeshare.Model.Specifications;

import dk.itu.mmda.bikeshare.Model.Bike;
import io.realm.Realm;
import io.realm.RealmResults;

public class BikeByIdSpecification implements RealmSpecification {
    private final String bikeId;

    public BikeByIdSpecification(String bikeId) {
        this.bikeId = bikeId;
    }

    @Override
    public RealmResults<Bike> toRealmResults(Realm realm) {
        return realm.where(Bike.class)
                .equalTo("id", bikeId)
                .findAll().sort("id");
    }
}
