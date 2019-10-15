package dk.itu.mmda.bikeshare;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.InputStream;

import dk.itu.mmda.bikeshare.Lists.RideRecycleViewAdapter;
import dk.itu.mmda.bikeshare.Model.Account;
import dk.itu.mmda.bikeshare.Model.AccountRepository;
import dk.itu.mmda.bikeshare.Model.Ride;
import io.realm.OrderedRealmCollection;

public class AccountActivity extends AppCompatActivity {
    private TextView mId;
    private TextView mBalance;
    private TextView mName;
    private TextView mEmail;
    private Button mSetBalance;

    private RecyclerView mRecyclerView;
    private RideRecycleViewAdapter mAdapter;

    private AccountRepository mAccountRepository;
    private Account mAccount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);

        mAccountRepository = AccountRepository.getInstance();
        mAccount = mAccountRepository.getCurrentAccount();

        if (mAccount.getImageUrl() != null) {
            new DownloadImageTask(findViewById(R.id.googleImage)).execute(mAccount.getImageUrl());
        }


        mId = findViewById(R.id.account_id_textview);
        mId.setText(mAccount.getId());

        mName = findViewById(R.id.account_name_textview);
        mName.setText(mAccount.getName());

        mEmail = findViewById(R.id.account_email_textview);
        mEmail.setText(mAccount.getEmail());

        mBalance = findViewById(R.id.account_balance_textview);
        mBalance.setText(String.format("%s $", mAccount.getBalance()));

        mSetBalance = findViewById(R.id.add_balance_button);
        mSetBalance.setOnClickListener(v -> {
            mAccount.setBalance(1000);
            finish();
            startActivity(getIntent());
        });

        // Create and set the adapter - use all rides as data
        mAdapter = new RideRecycleViewAdapter((OrderedRealmCollection<Ride>) mAccount.getRides(), this);
        mRecyclerView = findViewById(R.id.account_recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    protected void onResume() {
        //make sure actual balance is showed
        mBalance.setText(String.format("%s $", mAccount.getBalance()));

        super.onResume();
    }


    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urlDisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urlDisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }
    }
}
