package com.info121.nativelimo.activities;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
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
import com.info121.nativelimo.api.RestClient;
import com.info121.nativelimo.models.JobRes;
import com.info121.nativelimo.models.RequestMobileLog;
import com.info121.nativelimo.models.RequestUpdateRemark;
import com.info121.nativelimo.utils.PrefDB;
import com.info121.nativelimo.utils.Util;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.util.List;
import java.util.Spliterator;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
public class SplashActivity extends AppCompatActivity {
    private static final String TAG = SplashActivity.class.getSimpleName();
    private static final int REQUEST_OVERLAY_PERMISSION = 9007;
    Context mContext = SplashActivity.this;

    Boolean isSettingDialogShow = false;
    Boolean isOverlayDialogShow = false;

    PrefDB prefDB;

    // android 11 to 14
    String[] permissionT = {
            Manifest.permission.POST_NOTIFICATIONS,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.CAMERA,
            Manifest.permission.READ_MEDIA_IMAGES,
            Manifest.permission.CALL_PHONE
    };


    // api 25 ~ 29
    // android 7.1 to 10
    String[] permissionN = {
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
//                    Intent data = result.getData();
//                    Log.e("data", data.getData().toString());

                    if (!Settings.canDrawOverlays(mContext)){
                        Log.e("OnActivityResult", "not allow");
                        showOverlayPermissionWarning();
                    }else{
                        Log.e("OnActivityResult", " allow");
                        getFCMToken();
                    }
                }
            }
    );




//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//
//        if (requestCode == REQUEST_OVERLAY_PERMISSION) {
//            if (Settings.canDrawOverlays(this)) {
//                isOverlayDialogShow = false;
//                // startActivity(new Intent(mContext, LoginActivity.class));
//                openLoginActivity();
//                Log.e("OnActivityResult", "2 Allow");
//            } else {
//                // permission not granted...
//                showOverlayPermissionWarning();
//                Log.e("OnActivityResult", "2 Denied");
//            }
//        }else{
//            Log.e("OnActivityResult", "2 Cancel");
//        }
//    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        prefDB = new PrefDB(getApplicationContext());

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permission = permissionT;
        } else {
            permission = permissionN;
        }


        String details = "VERSION.RELEASE : " + Build.VERSION.RELEASE
                + "\nVERSION.INCREMENTAL : " + Build.VERSION.INCREMENTAL
                + "\nVERSION.SDK.NUMBER : " + Build.VERSION.SDK_INT
                + "\nBOARD : " + Build.BOARD
                + "\nBOOTLOADER : " + Build.BOOTLOADER
                + "\nBRAND : " + Build.BRAND
                + "\nCPU_ABI : " + Build.CPU_ABI
                + "\nCPU_ABI2 : " + Build.CPU_ABI2
                + "\nDISPLAY : " + Build.DISPLAY
                + "\nFINGERPRINT : " + Build.FINGERPRINT
                + "\nHARDWARE : " + Build.HARDWARE
                + "\nHOST : " + Build.HOST
                + "\nID : " + Build.ID
                + "\nMANUFACTURER : " + Build.MANUFACTURER
                + "\nMODEL : " + Build.MODEL
                + "\nPRODUCT : " + Build.PRODUCT
                + "\nSERIAL : " + Build.SERIAL
                + "\nTAGS : " + Build.TAGS
                + "\nTIME : " + Build.TIME
                + "\nTYPE : " + Build.TYPE
                + "\nUNKNOWN : " + Build.UNKNOWN
                + "\nUSER : " + Build.USER;

        Util.addLog("DEVICE DETAILS : " + details);


        // requestPermissions();

        // Log.e("Adaptive Battery : ", Util.isAdaptiveBatteryEnabled(mContext) + "");

    }

    @Override
    protected void onResume() {
        super.onResume();

        if (!isSettingDialogShow && !isOverlayDialogShow)
            requestPermissions();

//        if(!Settings.canDrawOverlays(this)  && isOverlayDialogShow)
//            showOverlayPermissionWarning();



        //   Log.e("Overlay permission : " , (Settings.canDrawOverlays(this)) ? "true" : "false");

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

                            // Logs
                            for (PermissionGrantedResponse gpr : multiplePermissionsReport.getGrantedPermissionResponses()) {
                                Util.addLog("GRANTED : " + gpr.getPermissionName());
                            }
                            for (PermissionDeniedResponse dpr : multiplePermissionsReport.getDeniedPermissionResponses()) {
                                Util.addLog("DENINED : " + dpr.getPermissionName());
                            }

                            // do you work now
                            Toast.makeText(mContext, "Permissions are granted..", Toast.LENGTH_SHORT).show();
                            //validateDevice();

                            // check overlay permission
                            checkOverlayPermission();
                        }
                        // check for permanent denial of any permission
                        if (!multiplePermissionsReport.areAllPermissionsGranted() || multiplePermissionsReport.isAnyPermissionPermanentlyDenied()) {
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
        isSettingDialogShow = true;

        // we are displaying an alert dialog for permissions
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);

        // below line is the title for our alert dialog.
        builder.setTitle("Need Permissions");

        // below line is our message for our dialog
        builder.setMessage("This app needs to grant all of the permissions. Otherwise some functions may not work well. \nYou can grant them anytime in app settings.");
        builder.setPositiveButton("GOTO SETTINGS", (dialog, which) -> {
            // this method is called on click on positive button and on clicking shit button
            // we are redirecting our user from our app to the settings page of our app.
            dialog.cancel();
            // below is the intent from which we are redirecting our user.
            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            Uri uri = Uri.fromParts("package", getPackageName(), null);
            intent.setData(uri);
            startActivityForResult(intent, 101);
            isSettingDialogShow = false;
            // finish();
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> {
            // this method is called when user click on negative button.
            dialog.cancel();
            // check overlay permission
            checkOverlayPermission();
            isSettingDialogShow = false;
            // showWarningDialog();
        });
        // below line is used to display our dialog
        builder.show();

    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        //  requestPermissions();

        Log.e("Application state : ", "Active");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.e("Application state : ", "Background");
    }

    private void checkOverlayPermission() {
       // finish();
        if (!Settings.canDrawOverlays(this)) {
            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:" + getPackageName()));

            //finish();
            requestOverLay.launch(intent);
            isOverlayDialogShow = true;

            Util.addLog("GRANTED : " + "REQUEST_OVERLAY_PERMISSION");
            //startActivityForResult(intent, REQUEST_OVERLAY_PERMISSION);

            RequestMobileLog req = new RequestMobileLog(App.StackTraceLog, "Android_Permission_Asked");
            callUpdateMobileLog(req);

        } else {
            //startActivity(new Intent(mContext, LoginActivity.class));
            Util.addLog("GRANTED : " + "REQUEST_OVERLAY_PERMISSION");
            getFCMToken();

            RequestMobileLog req = new RequestMobileLog(App.StackTraceLog, "Android_Permission_Asked");
            callUpdateMobileLog(req);
        }


    }


    private void getFCMToken() {
        FirebaseMessaging.getInstance().getToken().addOnSuccessListener(token -> {
            if (!TextUtils.isEmpty(token)) {
                App.FCM_TOKEN = token;
                Log.e("Token: ", token);
                Log.d(TAG, "retrieve token successful : " + token);
                //startActivity(new Intent(mContext, LoginActivity.class));
                Util.addLog("SUCCESS : " + "GET FCM Token");
                Util.addLog("FCM_TOKEN : " + App.FCM_TOKEN);
                openLoginActivity();

            } else {
                Util.addLog("ERROR : " + "GET FCM Token");
                showFCMTokenError();
                Log.w(TAG, "token should not be null...");
            }
        }).addOnFailureListener(e -> {
            //handle e
            showFCMTokenError();
        }).addOnCanceledListener(() -> {
            //handle cancel
        }).addOnCompleteListener(task -> {
                    Log.v(TAG, "This is the token : " + task.getResult());

                }
        );
    }


    private void openLoginActivity() {

       // showOverlayPermissionWarning();


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

    private void showFCMTokenError() {
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


    private void callUpdateMobileLog(RequestMobileLog request) {
        Call<JobRes> call = RestClient.COACH().getApiService().UpdateMobileLog(request);

        call.enqueue(new Callback<JobRes>() {
            @Override
            public void onResponse(Call<JobRes> call, Response<JobRes> response) {
                if (response.body().getResponsemessage().equalsIgnoreCase("SUCCESS"))
                    Log.e("Mobile Log Update : ", "Successful");
            }

            @Override
            public void onFailure(Call<JobRes> call, Throwable t) {
                Log.e("Mobile Log Update : ", "Failed");
            }
        });
    }


    private void showWarningDialog() {
        AlertDialog alertDialog = new AlertDialog.Builder(mContext).create();
        alertDialog.setTitle("Warning");
        alertDialog.setMessage("Some functions may not work well as some permissions are not granted.\nYou can grant the permissions anytime via Setting-> Permissions.");
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        // check overlay permission
                        checkOverlayPermission();
                    }
                });

        alertDialog.show();
    }

    private void showOverlayPermissionWarning() {
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