package dk.itu.mmda.bikeshare;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;

import java.util.List;

import dk.itu.mmda.bikeshare.Lists.RideRecycleViewAdapter;
import dk.itu.mmda.bikeshare.Model.Account;
import dk.itu.mmda.bikeshare.Model.AccountRepository;
import dk.itu.mmda.bikeshare.Model.RideRepository;
import dk.itu.mmda.bikeshare.Model.Specifications.AccountByIdSpecification;

public class BikeShareFragment extends Fragment {
    // GUI variables
    private Button mRegisterBike;
    private Button mVacantBikes;
    private Button mCheckAccount;
    private Button mSignOut;

    private RecyclerView mRecyclerView;
    private RideRecycleViewAdapter mAdapter;

    private RideRepository mRideRepository;
    private AccountRepository mAccountRepository;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_bike_share, container, false);

        //repository for getting realm objects (rides)
        mRideRepository = RideRepository.getInstance();
        mAccountRepository = AccountRepository.getInstance();

        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(getActivity());

        if (account != null) {
            Account account1 = setUpAccount(account.getId());
        }

        // Create and set the adapter - use all rides as data
        mAdapter = new RideRecycleViewAdapter(mRideRepository.getActiveRides(), getActivity());
        mRecyclerView = v.findViewById(R.id.main_recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.setAdapter(mAdapter);

        mRegisterBike = v.findViewById(R.id.register_bike_activity_button);
        mRegisterBike.setOnClickListener(v13 -> {
            Intent intent = new Intent(getActivity(), RegisterBikeActivity.class);
            startActivity(intent);
        });

        mVacantBikes = v.findViewById(R.id.vacant_bike_activity_button);
        mVacantBikes.setOnClickListener(v14 -> {
            Intent intent = new Intent(getActivity(), VacantBikesActivity.class);
            startActivity(intent);
        });

        mCheckAccount = v.findViewById(R.id.check_account_activity_button);
        mCheckAccount.setOnClickListener(v1 -> {
            Intent intent = new Intent(getActivity(), AccountActivity.class);
            startActivity(intent);
        });

        mSignOut = v.findViewById(R.id.sign_out_button);
        mSignOut.setOnClickListener(v12 -> signOut());

        return v;
    }

    /**
     * Creates a new account at first login, otherwise finds the one based on google sign in id.
     *
     * @param id gotten from google api
     * @return account currently being used
     */
    public Account setUpAccount(String id) {
        List<Account> searchAccount = mAccountRepository.query(new AccountByIdSpecification(id));
        if (searchAccount.isEmpty()) {
            Account newAccount = new Account(id);
            mAccountRepository.addAccount(newAccount, getActivity());
            mAccountRepository.setCurrentAccount(id);
            return newAccount;
        } else {
            mAccountRepository.setCurrentAccount(id);
            return searchAccount.get(0);
        }
    }

    private void signOut() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail().build();
        GoogleSignInClient mGoogleSignInClient = GoogleSignIn.getClient(getActivity(), gso);
        mGoogleSignInClient.signOut().addOnCompleteListener(getActivity(), (task -> {
            Toast.makeText(getActivity(), "Successfully signed out", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(getActivity(), SignInActivity.class));
            getActivity().finish();
        }));
    }

}
