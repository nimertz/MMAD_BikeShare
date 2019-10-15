package dk.itu.mmda.bikeshare.Model;

import android.content.Context;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;

import java.util.ArrayList;
import java.util.List;

import dk.itu.mmda.bikeshare.Model.Specifications.AccountByIdSpecification;
import dk.itu.mmda.bikeshare.Model.Specifications.RealmSpecification;
import io.realm.Realm;
import io.realm.RealmResults;

public class AccountRepository {

    private static AccountRepository INSTANCE = null;
    private static String currentAccount = null;

    // other instance variables can be here

    public static AccountRepository getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new AccountRepository();
        }
        return (INSTANCE);
    }

    public Account getCurrentAccount() {
        return query(new AccountByIdSpecification(currentAccount)).get(0);
    }

    public void setCurrentAccount(String currentAccount) {
        AccountRepository.currentAccount = currentAccount;
    }


    public void addAccount(Account item, Context context) {
        final Realm realm = Realm.getDefaultInstance();

        realm.executeTransaction(realm1 -> {
            GoogleSignInAccount gsa = GoogleSignIn.getLastSignedInAccount(context);

            if (gsa != null) {
                item.setEmail(gsa.getEmail());
                item.setName(gsa.getDisplayName());
                if (gsa.getPhotoUrl() != null) {
                    item.setImageUrl(gsa.getPhotoUrl().toString());
                }

                final Account account = realm1.copyToRealm(item);
            }
        });

        realm.close();
    }

    public List<Account> query(RealmSpecification specification) {
        final Realm realm = Realm.getDefaultInstance();
        final RealmResults<Account> realmResults = specification.toRealmResults(realm);

        final List<Account> rides = new ArrayList<>(realmResults);

        realm.close();

        return rides;
    }
}
