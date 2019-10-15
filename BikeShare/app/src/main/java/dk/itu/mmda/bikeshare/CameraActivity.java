package dk.itu.mmda.bikeshare;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;
import android.view.WindowManager;


public class CameraActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_camera);

        FragmentManager fragmentManager =
                getSupportFragmentManager();
        Fragment fragment = fragmentManager
                .findFragmentById(R.id.fragment_container_camera);

        if (fragment == null) {
            fragment = new CameraFragment();
            fragmentManager.beginTransaction()
                    .add(R.id.fragment_container_camera, fragment)
                    .commit();
        }


    }


}
