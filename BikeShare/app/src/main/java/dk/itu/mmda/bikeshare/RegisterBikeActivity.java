package dk.itu.mmda.bikeshare;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;

import dk.itu.mmda.bikeshare.Lists.CheckBikePagerActivity;
import dk.itu.mmda.bikeshare.Model.Bike;
import dk.itu.mmda.bikeshare.Model.BikeRepository;
import dk.itu.mmda.bikeshare.Model.BikeType;
import io.realm.RealmList;


public class RegisterBikeActivity extends AppCompatActivity {
    // GUI variables
    private Button mAddBike;
    private Button mStartCamera;
    private Spinner mTypeSpinner;
    private ImageView mBikeImage;
    private EditText mEnterBikeName;
    private EditText mEnterPrice;

    private Bike currentBike;
    private BikeRepository mBikeRepository;
    private Bitmap mBitmap;
    private byte[] mImageSave;

    private ArrayList<String> mPermissions = new ArrayList<>();
    private static final int ALL_PERMISSIONS_RESULT = 1011;
    static final int REQUEST_TAKE_PHOTO = 1;
    private String currentPhotoPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_bike);

        //bike being built
        currentBike = new Bike();

        //repository for getting realm objects (bikes)
        mBikeRepository = BikeRepository.getInstance();

        //make sure camera access has been granted
        if (!hasPermission(Manifest.permission.CAMERA)) setupCameraPermission();

        mStartCamera = findViewById(R.id.start_camera_button);
        mStartCamera.setOnClickListener(v -> {
            if (hasPermission(Manifest.permission.CAMERA)) {
                // OpenCV camera
                //Intent intent = new Intent(this, CameraActivity.class);
                //startActivity(intent);
                dispatchTakePictureIntent();
            }
        });

        mTypeSpinner = findViewById(R.id.bike_type_spinner);
        mTypeSpinner.setAdapter(new ArrayAdapter<>(this, R.layout.list_item_bike_type, BikeType.values()));

        mBikeImage = findViewById(R.id.bike_imageView_holder);

        mEnterBikeName = findViewById(R.id.enter_bike_name);
        mEnterBikeName.addTextChangedListener(watcher);

        mEnterPrice = findViewById(R.id.enter_bike_price);
        mEnterPrice.addTextChangedListener(watcher);
        //set acceptable price range.
        mEnterPrice.setFilters(new InputFilter[]{new InputFilterMinMax(1, 100)});

        mAddBike = findViewById(R.id.add_bike_button);
        mAddBike.setEnabled(false);
        mAddBike.setOnClickListener(v -> {

            currentBike.setBikeName(mEnterBikeName.getText().toString());
            currentBike.setType((BikeType) mTypeSpinner.getSelectedItem());
            currentBike.setRides(new RealmList<>());
            currentBike.setPricePerHour(Double.parseDouble(mEnterPrice.getText().toString()));
            //check if image is there
            if (mBikeImage.getDrawable() != null) {
                currentBike.setImage(mImageSave);
                File f = new File(currentPhotoPath);
                if (f.exists()) f.delete();
            }

            if (currentBike != null) mBikeRepository.add(currentBike);
            assert currentBike != null;
            Toast.makeText(this, currentBike.getBikeName() + " successfully registered", Toast.LENGTH_LONG).show();

            Intent intent = new Intent(getBaseContext(), CheckBikePagerActivity.class);
            intent.putExtra("bikeId", currentBike.getId());
            startActivity(intent);
            finish();

        });
    }

    private final TextWatcher watcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        @Override
        public void afterTextChanged(Editable s) {
            if (mEnterBikeName.getText().toString().trim().length() == 0 || mEnterPrice.getText().toString().trim().length() == 0) {
                mAddBike.setEnabled(false);
            } else {
                mAddBike.setEnabled(true);
            }
        }
    };

    @Override
    protected void onResume() {
        super.onResume();

        //when returned to after having taking picture in OpenCV
        /*File cacheDir = getBaseContext().getCacheDir();
        File f = new File(cacheDir, "pic");
        if (f.exists()) {
            FileInputStream fis = null;
            try {
                fis = new FileInputStream(f);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            mBitmap = BitmapFactory.decodeStream(fis);

            mBikeImage.setImageBitmap(mBitmap);
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            mBitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
            mImageSave = stream.toByteArray();

        }*/

    }

    private void setupCameraPermission() {
        mPermissions.add(Manifest.permission.CAMERA);

        ArrayList<String> permissionsToRequest = permissionsToRequest(mPermissions);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (permissionsToRequest.size() > 0) {
                requestPermissions(permissionsToRequest.toArray(
                        new String[permissionsToRequest.size()]),
                        ALL_PERMISSIONS_RESULT);
            }
        }
    }

    private ArrayList<String> permissionsToRequest(ArrayList<String> permissions) {
        ArrayList<String> result = new ArrayList<>();
        for (String permission : permissions)
            if (!hasPermission(permission))
                result.add(permission);
        return result;
    }

    private boolean hasPermission(String permission) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            return Objects.requireNonNull(this)
                    .checkSelfPermission(permission) ==
                    PackageManager.PERMISSION_GRANTED;
        return true;
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File

            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "dk.itu.mmda.bikeshare.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }
        }
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_TAKE_PHOTO && resultCode == RESULT_OK) {
            try {
                File f = new File(currentPhotoPath);
                Uri contentUri = Uri.fromFile(f);
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(),contentUri);
                mBikeImage.setImageBitmap(bitmap);
                mBitmap = bitmap;
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                mBitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                mImageSave = stream.toByteArray();


            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mBikeImage.setImageResource(android.R.color.transparent);
    }


}
