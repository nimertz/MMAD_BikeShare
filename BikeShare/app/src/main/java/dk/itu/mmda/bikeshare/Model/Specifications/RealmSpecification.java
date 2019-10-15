package dk.itu.mmda.bikeshare.Model.Specifications;

import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.RealmResults;

public interface RealmSpecification<T extends RealmObject> {
    RealmResults<T> toRealmResults(Realm realm);
}
