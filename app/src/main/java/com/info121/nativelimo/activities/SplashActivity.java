package com.info121.nativelimo.activities;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.messaging.FirebaseMessaging;
import com.info121.nativelimo.App;
import com.info121.nativelimo.R;
import com.info121.nativelimo.utils.PrefDB;
import com.info121.nativelimo.utils.Util;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.util.List;
import java.util.Spliterator;

public class SplashActivity extends AppCompatActivity {
    private static final String TAG = SplashActivity.class.getSimpleName();
    private static final int REQUEST_OVERLAY_PERMISSION = 9007;
    Context mContext = SplashActivity.this;

    PrefDB prefDB;

    // android 11 to 14
    String[] permissionT =  {
            Manifest.permission.POST_NOTIFICATIONS,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.CAMERA,
            Manifest.permission.READ_MEDIA_IMAGES,
            Manifest.permission.CALL_PHONE
    };


    // api 25 ~ 29
    // android 7.1 to 10
    String[] permissionN =  {
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.CAMERA,
            Manifest.permission.CALL_PHONE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE
            };

    String[] permission;


    ActivityResultLauncher<Intent> requestOverLay = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();

                        if (Settings.canDrawOverlays(mContext)) {
                           // startActivity(new Intent(mContext, LoginActivity.class));
                            openLoginActivity();
                        }else{
                            // permission not granted...
                            AlertDialog alertDialog = new AlertDialog.Builder(mContext).create();
                            alertDialog.setTitle("Warning");
                            alertDialog.setMessage("If not allow overlay permission, full screen alert window can not be display when urgent job is coming.\nAre you sure want to proceed without it?");
                            alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                            //startActivity(new Intent(mContext, LoginActivity.class));
                                            getFCMToken();
                                        }
                                    });

                            alertDialog.show();
                        }
                    }
                }
            }
    );

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_OVERLAY_PERMISSION) {
            if (Settings.canDrawOverlays(this)) {
               // startActivity(new Intent(mContext, LoginActivity.class));
                openLoginActivity();
            }else{
                // permission not granted...

                AlertDialog alertDialog = new AlertDialog.Builder(mContext).create();
                alertDialog.setTitle("Warning");
                alertDialog.setMessage("If not allow overlay permission, full screen alert window can not be display when urgent job is coming.\nAre you sure want to proceed without it?");
                alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                //startActivity(new Intent(mContext, LoginActivity.class));
                                getFCMToken();
                            }
                        });

                alertDialog.show();
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        prefDB = new PrefDB(getApplicationContext());

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU){
            permission = permissionT;
        } else{
            permission = permissionN;
        }

        requestPermissions();

       // Log.e("Adaptive Battery : ", Util.isAdaptiveBatteryEnabled(mContext) + "");

    }

    @Override
    protected void onResume() {
        super.onResume();
      //  requestPermissions();
    }


    // permission related functions
    private void requestPermissions() {
        // below line is use to request permission in the current activity.
        // this method is use to handle error in runtime permissions
        Dexter.withContext(mContext)
                // below line is use to request the number of permissions which are required in our app.
                .withPermissions(permission)
                // after adding permissions we are calling an with listener method.
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport multiplePermissionsReport) {
                        // this method is called when all permissions are granted
                        if (multiplePermissionsReport.areAllPermissionsGranted()) {
                            // do you work now
                            Toast.makeText(mContext, "Permissions are granted..", Toast.LENGTH_SHORT).show();
                            //validateDevice();

                            // check overlay permission
                            checkOverlayPermission();
                        }
                        // check for permanent denial of any permission
                        if (multiplePermissionsReport.isAnyPermissionPermanentlyDenied()) {
                            // permission is denied permanently, we will show user a dialog message.
                            showSettingsDialog();
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> list, PermissionToken permissionToken) {
                        // this method is called when user grants some permission and denies some of them.
                        permissionToken.continuePermissionRequest();
                    }
                }).withErrorListener(error -> {
                    // we are displaying a toast message for error message.
                    Toast.makeText(getApplicationContext(), "Error occurred! ", Toast.LENGTH_SHORT).show();
                })
                // below line is use to run the permissions on same thread and to check the permissions
                .onSameThread().check();
    }

    // below is the shoe setting dialog method which is use to display a dialogue message.
    private void showSettingsDialog() {
        // we are displaying an alert dialog for permissions
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);

        // below line is the title for our alert dialog.
        builder.setTitle("Need Permissions");

        // below line is our message for our dialog
        builder.setMessage("This app needs permission to use this feature. You can grant them in app settings.");
        builder.setPositiveButton("GOTO SETTINGS", (dialog, which) -> {
            // this method is called on click on positive button and on clicking shit button
            // we are redirecting our user from our app to the settings page of our app.
            dialog.cancel();
            // below is the intent from which we are redirecting our user.
            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            Uri uri = Uri.fromParts("package", getPackageName(), null);
            intent.setData(uri);
            startActivityForResult(intent, 101);
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> {
            // this method is called when user click on negative button.
            dialog.cancel();
        });
        // below line is used to display our dialog
        builder.show();
    }

    private void checkOverlayPermission(){
        if (!Settings.canDrawOverlays(this)){
            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:" + getPackageName()));

            finish();
            requestOverLay.launch(intent);

            //startActivityForResult(intent, REQUEST_OVERLAY_PERMISSION);
        }else{
            //startActivity(new Intent(mContext, LoginActivity.class));
            getFCMToken();
        }
    }



    private void getFCMToken(){
        FirebaseMessaging.getInstance().getToken().addOnSuccessListener(token -> {
            if (!TextUtils.isEmpty(token)) {
                App.FCM_TOKEN = token;
                Log.e("Token: ", token);
                Log.d(TAG, "retrieve token successful : " + token);
                //startActivity(new Intent(mContext, LoginActivity.class));
                openLoginActivity();

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


    private void openLoginActivity(){

        startActivity(new Intent(mContext, LoginActivity.class));

//       if(Util.isAdaptiveBatteryEnabled(mContext) && prefDB.getString(App.CONST_DONT_ASK_AB) != "TRUE") {
//           AlertDialog alertDialog = new AlertDialog.Builder(mContext).create();
//           alertDialog.setTitle("Warning");
//           alertDialog.setMessage("Adaptive battery is currently set ON. If you do not want notification delay, please set OFF by Setting-> Battery-> Adaptive Preferences-> Adaptive Battery.\n" +
//                   "(Setting hierarchy may varies depend on the custom ROMs and android versions)");
//           alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Don't Ask Again", new DialogInterface.OnClickListener() {
//               @Override
//               public void onClick(DialogInterface dialogInterface, int i) {
//                   prefDB.putString(App.CONST_DONT_ASK_AB, "TRUE");
//                   alertDialog.dismiss();
//                   startActivity(new Intent(mContext, LoginActivity.class));
//               }
//           });
//
//           alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "OK", new DialogInterface.OnClickListener() {
//               @Override
//               public void onClick(DialogInterface dialogInterface, int i) {
//                   prefDB.putString(App.CONST_DONT_ASK_AB, "FALSE");
//                   alertDialog.dismiss();
//                   startActivity(new Intent(mContext, LoginActivity.class));
//               }
//           });
//       }else{

    }

    private void showFCMTokenError(){
        AlertDialog alertDialog = new AlertDialog.Builder(mContext).create();
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
}