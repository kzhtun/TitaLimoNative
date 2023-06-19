package com.info121.nativelimo.activities;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.info121.nativelimo.R;

import java.util.List;

import pub.devrel.easypermissions.EasyPermissions;


public class TiramisuPermissionActivity extends AppCompatActivity implements EasyPermissions.PermissionCallbacks  {
    String[] perms = {
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.CAMERA,
            Manifest.permission.CALL_PHONE,
            Manifest.permission.POST_NOTIFICATIONS
//           // Manifest.permission.READ_EXTERNAL_STORAGE,
//            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    Context mContext = TiramisuPermissionActivity.this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tiramisu_permission);

       if (EasyPermissions.hasPermissions(TiramisuPermissionActivity.this, perms)){
           // permissions already granted
         //  displayToast("Permissions are already granted");
           startActivity(new Intent(TiramisuPermissionActivity.this, LoginActivity.class));
       }else{
            EasyPermissions.requestPermissions(TiramisuPermissionActivity.this, "You must have to allow each permission.", 102, perms);
       }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        // Handle the request result
       EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    private void displayToast(String msg){
        Toast.makeText(mContext, msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {
        switch (requestCode){
            case 102:
               // displayToast("Fuck Granted All");
                startActivity(new Intent(TiramisuPermissionActivity.this, LoginActivity.class));
        }

    }

    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {

    }
}