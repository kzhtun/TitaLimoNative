package com.info121.nativelimo.activities;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;


import com.info121.nativelimo.R;


import java.util.List;

public class MainActivity extends AppCompatActivity {
    Context mContext = MainActivity.this;

    int PERMISSION_ALL = 1;
    String[] PERMISSIONS = {Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA,
            Manifest.permission.CALL_PHONE};

    private static final String TAG = LoginActivity.class.getSimpleName();


    public static boolean hasPermissions(Context context, String... permissions) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }

//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
//            if (ActivityCompat.checkSelfPermission(this,Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
//
//                requestPermissions(new String[] {Manifest.permission.POST_NOTIFICATIONS}, 1);
//
//            }
//            else {
//                // repeat the permission or open app details
//            }
//        }

        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (!hasPermissions(this, PERMISSIONS))
            ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_ALL);
        else
            startActivity(new Intent(MainActivity.this, LoginActivity.class));


//        Dexter.withContext(this)
//                .withPermissions(
//                        Manifest.permission.CAMERA,
//                        Manifest.permission.READ_CONTACTS,
//                        Manifest.permission.RECORD_AUDIO
//                ).withListener(new MultiplePermissionsListener() {
//                    @Override public void onPermissionsChecked(MultiplePermissionsReport report) {
//                        /* ... */
//                        Toast.makeText(mContext, "onPermissionsChecked", Toast.LENGTH_SHORT).show();
//                    }
//                    @Override public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
//                        /* ... */
//                        Toast.makeText(mContext, "onPermissionRationaleShouldBeShown", Toast.LENGTH_SHORT).show();
//                    }
//                }).check();

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

         if(hasPermissions(this, PERMISSIONS))
            startActivity(new Intent(MainActivity.this, LoginActivity.class));

    }



}
