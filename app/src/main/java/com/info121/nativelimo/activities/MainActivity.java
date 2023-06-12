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
import android.util.Log;
import android.widget.Toast;


import com.info121.nativelimo.App;
import com.info121.nativelimo.R;

import java.util.ArrayList;
import java.util.List;

import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;


public class MainActivity extends AppCompatActivity  {

    Context mContext = MainActivity.this;

    int PERMISSION_ALL = 1;
    private static final int RC_PRE_PERMISSION = 111;
    private static final int ACCESS_COARSE_LOCATION = 9001;
    private static final int ACCESS_FINE_LOCATION = 9002;
    private static final int READ_EXTERNAL_STORAGE = 9003;
    private static final int WRITE_EXTERNAL_STORAGE = 9004;
    private static final int CAMERA = 9005;
    private static final int CALL_PHONE = 9006;

//    Manifest.permission.READ_EXTERNAL_STORAGE,
//    Manifest.permission.WRITE_EXTERNAL_STORAGE,

    String[] permissions = {Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.CAMERA,
            Manifest.permission.CALL_PHONE,
            Manifest.permission.POST_NOTIFICATIONS
    };

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


        if (hasPermissions(this, permissions))
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
        else
            ActivityCompat.requestPermissions(this, permissions, PERMISSION_ALL);


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(hasPermissions(this, permissions))
            startActivity(new Intent(MainActivity.this, LoginActivity.class));

    }




}
