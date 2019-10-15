package dk.itu.mmda.bikeshare.Model.Specifications;

import dk.itu.mmda.bikeshare.Model.Account;
import io.realm.Realm;
import io.realm.RealmResults;

public class AccountByIdSpecification implements RealmSpecification {
    private final String accountId;

    public AccountByIdSpecification(String accountId) {
        this.accountId = accountId;
    }

    @Override
    public RealmResults toRealmResults(Realm realm) {
        return realm.where(Account.class)
                .equalTo("id", accountId)
                .findAll();
    }
}
