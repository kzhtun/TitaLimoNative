package com.info121.nativelimo.activities;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;

import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.messaging.FirebaseMessaging;
import com.info121.nativelimo.App;
import com.info121.nativelimo.R;

import java.util.List;

import pub.devrel.easypermissions.EasyPermissions;


public class TiramisuPermissionActivity extends AppCompatActivity implements EasyPermissions.PermissionCallbacks  {
    private static final int REQUEST_OVERLAY_PERMISSION = 9007;
    private static final String TAG = TiramisuPermissionActivity.class.getSimpleName();
    String[] perms = {
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.CAMERA,
            Manifest.permission.CALL_PHONE,
            Manifest.permission.POST_NOTIFICATIONS,
            Manifest.permission.SYSTEM_ALERT_WINDOW
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
            getFCMToken();
           // startActivity(new Intent(TiramisuPermissionActivity.this, LoginActivity.class));

            // requesting overlay permission


       }else{
            EasyPermissions.requestPermissions(TiramisuPermissionActivity.this, "You must have to allow each permission.", 102, perms);
       }
    }

    private void getFCMToken(){
        FirebaseMessaging.getInstance().getToken().addOnSuccessListener(token -> {
            if (!TextUtils.isEmpty(token)) {
                App.FCM_TOKEN = token;
                Log.e("Token: ", token);
                Log.d(TAG, "retrieve token successful : " + token);
                startActivity(new Intent(TiramisuPermissionActivity.this, LoginActivity.class));
            } else {
                showFCMTokenError();
                Log.w(TAG, "token should not be null...");
            }
        }).addOnFailureListener(e -> {
            //handle e
            showFCMTokenError();
        }).addOnCanceledListener(() -> {
            //handle cancel
        }).addOnCompleteListener(task ->{
                    Log.v(TAG, "This is the token : " + task.getResult());

                }
        );
    }

    private void showFCMTokenError(){
        AlertDialog alertDialog = new AlertDialog.Builder(TiramisuPermissionActivity.this).create();
        alertDialog.setTitle("Warning");
        alertDialog.setMessage("Token can not be retrieve. Notificaitons will not work properly. Please exit the application and re-launch.");
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        finish();
                    }
                });
        alertDialog.show();
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
               // startActivity(new Intent(TiramisuPermissionActivity.this, LoginActivity.class));

                // ask for overlay permission
                if(Settings.canDrawOverlays(this)) {
                    startActivity(new Intent(TiramisuPermissionActivity.this, LoginActivity.class));
                }else{
                    // ask for setting
                    Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                            Uri.parse("package:" + getPackageName()));
                    startActivityForResult(intent, REQUEST_OVERLAY_PERMISSION);
                }
        }

    }

    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_OVERLAY_PERMISSION) {
            if (Settings.canDrawOverlays(this)) {
                startActivity(new Intent(TiramisuPermissionActivity.this, LoginActivity.class));
            }else{
                // permission not granted...

                AlertDialog alertDialog = new AlertDialog.Builder(TiramisuPermissionActivity.this).create();
                alertDialog.setTitle("Warning");
                alertDialog.setMessage("If not allow overlay permission, full screen alert window can not be display when urgent job is coming.\nAre you sure want to proceed without it?");
                alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                startActivity(new Intent(TiramisuPermissionActivity.this, LoginActivity.class));
                            }
                        });

                alertDialog.show();
            }
        }
    }
}