package com.info121.nativelimo.activities;


import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;

import android.net.Uri;
import android.os.Build;

import android.provider.Settings;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import androidx.appcompat.app.AlertDialog;




import com.info121.nativelimo.AbstractActivity;
import com.info121.nativelimo.App;

import com.info121.nativelimo.R;
import com.info121.nativelimo.api.RestClient;

import com.info121.nativelimo.models.ObjectRes;
import com.info121.nativelimo.models.RequestValidateDriver;
import com.info121.nativelimo.models.SearchParams;

import com.info121.nativelimo.services.SmartLocationService;
import com.info121.nativelimo.utils.PrefDB;
import com.info121.nativelimo.utils.Util;


import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AbstractActivity {
    private static final int REQUEST_OVERLAY_PERMISSION = 9007;

    static boolean active = false;
    static boolean autoLogin = true;

    Context mContext = LoginActivity.this;
    PrefDB prefDB;

    @BindView(R.id.user_name)
    EditText mUserName;

    @BindView(R.id.password)
    EditText mPassword;

    @BindView(R.id.remember_me)
    CheckBox mRemember;

    @BindView(R.id.progressBar)
    ProgressBar mProgressBar;

    @BindView(R.id.api_version)
    TextView mApiVersion;

    @BindView(R.id.ui_version)
    TextView mUiVersion;

    @BindView(R.id.login)
    Button btnLogin;




    @Override
    protected void onStart() {
        super.onStart();
        active = true;

        App.futureSearchParams = new SearchParams("","", "", "0", "");
        App.historySearchParams = new SearchParams("","", "", "0", "");

        Log.e("Login : " , "on Start");
    }

    @Override
    protected void onStop() {
        super.onStop();
        active = false;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        ButterKnife.bind(this);

        prefDB = new PrefDB(getApplicationContext());

    //    btnLogin.setEnabled(false);


        // location
        startLocationService();

        // Release
        callCheckVersion();


//        // Debug
//        if (prefDB.getBoolean(App.CONST_REMEMBER_ME)) {
//            mUserName.setText(prefDB.getString(App.CONST_USER_NAME));
//            mRemember.setChecked(true);
//
//        }else{
//            btnLogin.setEnabled(true);
//        }

        mApiVersion.setText("Api " + Util.getVersionCode(mContext));
        mUiVersion.setText("Ver " + Util.getVersionName(mContext));

    }


//    @Override
//    protected void onNewIntent(Intent intent) {
//        super.onNewIntent(intent);
//
//        Bundle extras = getIntent().getExtras();
//
//        if(extras != null){
//            String jobNo = getIntent().getStringExtra("JOB_NO");
//            if (jobNo != null)
//                Log.e("JOB_NO", jobNo);
//        }
//    }

//    private void performLogin(){
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
//            // check notification permissions after login success
//            if (hasPermissions(mContext, permissions)) {
//                callValidateDriver(mUserName.getText().toString());
//            } else {
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
//                    requestPermissions(permissions, 80);
//            }
//        } else {
//            callValidateDriver(mUserName.getText().toString());
//        }
//    }

    @OnClick(R.id.login)
    public void login_onClick(){
        mProgressBar.setVisibility(View.VISIBLE);
        callValidateDriverCredential();
//        if (mRemember.isChecked()) {
//
//        }else{
//            AlertDialog alertDialog = new AlertDialog.Builder(LoginActivity.this).create();
//            alertDialog.setTitle("Warning");
//            alertDialog.setMessage("By logging in without remember me checked, application will not automatically login whenever user touch the incoming job notifications. You will need to login manually.\nAre you sure you want to login?");
//            alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK",
//                    (dialog, which) -> {
//                        dialog.dismiss();
//                        callValidateDriverCredential();
//
//                    });
//            alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "CANCEL",
//                    (dialog, which) -> dialog.dismiss());
//            alertDialog.show();
//        }
    }

//    public void loginOnClickOld() {
//        mProgressBar.setVisibility(View.VISIBLE);
//
//        if (mRemember.isChecked()) {
//            performLogin();
//        }else{
//            AlertDialog alertDialog = new AlertDialog.Builder(LoginActivity.this).create();
//            alertDialog.setTitle("Warning");
//            alertDialog.setMessage("By logging in without remember me checked, application will not automatically login whenever user touch the incomming job notifcations. You will need to login manually.\nAre you sure you want to login?");
//            alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK",
//                    new DialogInterface.OnClickListener() {
//                        public void onClick(DialogInterface dialog, int which) {
//                            dialog.dismiss();
//
//                                performLogin();
//
//                        }
//                    });
//            alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "CANCEL",
//                    new DialogInterface.OnClickListener() {
//                        public void onClick(DialogInterface dialog, int which) {
//                            dialog.dismiss();
//                        }
//                    });
//            alertDialog.show();
//        }
//
//
//    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 80) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Toast.makeText(mContext, "Permission Granted", Toast.LENGTH_SHORT).show();

            } else {
                // Toast.makeText(mContext, "Permission Denied", Toast.LENGTH_SHORT).show();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    AlertDialog alertDialog = new AlertDialog.Builder(LoginActivity.this).create();
                    alertDialog.setTitle("Warning");
                    alertDialog.setMessage("Notification permission is not allow.\n" +
                            "To allow notification again, go to Setting-> Apps-> Tita Limo-> Permissions-> Notifications-> Allow Notifications. ");
                    alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                    alertDialog.show();
                }
            }
        }
        //callValidateDriver(mUserName.getText().toString());
    }

    public void callValidateDriverCredential() {
        mProgressBar.setVisibility(View.GONE);

        Call<ObjectRes> call = RestClient.COACH().getApiService().ValidateDriverCredential(
                new RequestValidateDriver(mUserName.getText().toString().trim(),
                        mPassword.getText().toString().trim())
        );

        call.enqueue(new Callback<ObjectRes>() {
            @Override
            public void onResponse(Call<ObjectRes> call, Response<ObjectRes> response) {

                if (response.body() == null) {
                    showRefreshDialog();
                }

                if (response.body().getResponsemessage().equalsIgnoreCase("Invalid")) {
                    mUserName.setError("Invalid user name or password.");
                    mUserName.requestFocus();
                    mProgressBar.setVisibility(View.GONE);
                }

                if (response.body().getResponsemessage().equalsIgnoreCase("FirstLogin")) {
                    App.userName = mUserName.getText().toString().trim();
                    App.password = mPassword.getText().toString().trim();
                    App.deviceID = Util.getDeviceID(getApplicationContext());
                    App.authToken = response.body().getToken();
                    startActivity(new Intent(LoginActivity.this, ChangePasswordActivity.class));

                    mUserName.setError(null);
                }

                if (response.body().getResponsemessage().equalsIgnoreCase("VALID")) {
                    App.userName = mUserName.getText().toString().trim();
                    App.password = mPassword.getText().toString().trim();
                    App.deviceID = Util.getDeviceID(getApplicationContext());
                    App.authToken = response.body().getToken();
                    App.timerDelay = 6000;
                    mUserName.setError(null);

                    callUpdateDevice();
                }
            }

            @Override
            public void onFailure(Call<ObjectRes> call, Throwable t) {
                mUserName.setError("Error in connection.");
                mUserName.requestFocus();
                mProgressBar.setVisibility(View.GONE);
            }
        });

        btnLogin.setEnabled(true);
    }


    private void saveUserInfo(){

    }

//    public void callValidateDriver(String userName) {
//        Call<ObjectRes> call = RestClient.COACH().getApiService().ValidateDriver(userName.trim());
//
//        call.enqueue(new Callback<ObjectRes>() {
//            @Override
//            public void onResponse(Call<ObjectRes> call, Response<ObjectRes> response) {
//
//                if (response.body() == null) {
//                    showRefreshDialog();
//                }
//
//                if (response.body().getResponsemessage().equalsIgnoreCase("VALID")) {
//                    App.userName = userName;
//                    App.deviceID = Util.getDeviceID(getApplicationContext());
//                    App.authToken = response.body().getToken();
//                    App.timerDelay = 6000;
//
//                    callUpdateDevice();
//
//                } else {
//                    mUserName.setError("Invalid user name");
//                    mUserName.requestFocus();
//                    mProgressBar.setVisibility(View.GONE);
//                }
//
//            }
//
//            @Override
//            public void onFailure(Call<ObjectRes> call, Throwable t) {
//                mUserName.setError("Error in connection.");
//                mUserName.requestFocus();
//                mProgressBar.setVisibility(View.GONE);
//            }
//        });
//
//        btnLogin.setEnabled(true);
//    }



    private void callCheckVersion() {

        Call<ObjectRes> call = RestClient.COACH().getApiService().CheckVersion(String.valueOf(Util.getVersionName(mContext)));

        call.enqueue(new Callback<ObjectRes>() {
            @Override
            public void onResponse(Call<ObjectRes> call, Response<ObjectRes> response) {
                if (response.body().getResponsemessage().equalsIgnoreCase("OUTDATED")) {
                    showOutdatedDialog();
                }else{
                    btnLogin.setEnabled(true);
                    if (prefDB.getBoolean(App.CONST_REMEMBER_ME)) {
                        mUserName.setText(prefDB.getString(App.CONST_USER_NAME));
                        mPassword.setText(prefDB.getString(App.CONST_PASSWORD));
                        mRemember.setChecked(true);
                    }
//                        if(mPassword.getText().toString().trim().length()>0 && autoLogin)
//                            callValidateDriverCredential();
//                        else
//                            btnLogin.setEnabled(true);
//                     }else{
//                        btnLogin.setEnabled(true);
//                     }
                }
            }

            @Override
            public void onFailure(Call<ObjectRes> call, Throwable t) {
                if(t.getMessage().contains("Unable to resolve host")) {
                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(mContext);
                    alertDialog.setTitle("Connection Issue");
                    alertDialog.setMessage("Unable to resolve host. Please check your internet connection.");
                    alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });

                    alertDialog.show();
                }
                btnLogin.setEnabled(true);
            }
        });


    }

    private void callUpdateDevice() {
        Log.e("====", "=========================================");
        Log.e("DEVICE ID: ", Util.getDeviceID(getApplicationContext()));
        Log.e("DEVICE TYPE ", App.DEVICE_TYPE);
        Log.e("FCM TOKEN", App.FCM_TOKEN);
        Log.e("====", "=========================================");


        Call<ObjectRes> call = RestClient.COACH().getApiService().UpdateDevice(Util.getDeviceID(getApplicationContext()),
                App.DEVICE_TYPE,
                App.FCM_TOKEN);

        call.enqueue(new Callback<ObjectRes>() {
            @Override
            public void onResponse(Call<ObjectRes> call, Response<ObjectRes> response) {
                // Add to Appication Varialbles

                loginSuccessful();
            }

            @Override
            public void onFailure(Call<ObjectRes> call, Throwable t) {
                Toast.makeText(mContext, "Getting error in firebase token request.", Toast.LENGTH_SHORT).show();
                mProgressBar.setVisibility(View.GONE);
            }
        });
    }

    private void loginSuccessful() {
        mProgressBar.setVisibility(View.GONE);

        // instantiate wiht new Token
        //RestClient.Dismiss();

        prefDB.putString(App.CONST_USER_NAME, App.userName);
        prefDB.putString(App.CONST_PASSWORD, App.password);
        prefDB.putString(App.CONST_DEVICE_ID, App.deviceID);
        prefDB.putLong(App.CONST_TIMER_DELAY, App.timerDelay);



        if (mRemember.isChecked()) {
            prefDB.putBoolean(App.CONST_REMEMBER_ME, true);
        }else {
            mUserName.setText("");
            mPassword.setText("");
            prefDB.putBoolean(App.CONST_REMEMBER_ME, false);
        }


//        FirebaseInstanceId.getInstance().getInstanceId()
//                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
//                    @Override
//                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
//                        if (!task.isSuccessful()) {
//                            Log.e("FCM", "getInstanceId failed", task.getException());
//                            return;
//                        }
//
//
//                        // Get new Instance ID token
//                        App.FCM_TOKEN = task.getResult().getToken();
//                        Log.e("TOKEN : ", App.FCM_TOKEN);
//                    }
//                });


        // login successful
        startActivity(new Intent(LoginActivity.this, JobOverviewActivity.class));
    }

    private void startLocationService() {
        if (isGPSEnabled()) {
            Intent serviceIntent = new Intent(LoginActivity.this, SmartLocationService.class);
            LoginActivity.this.startService(serviceIntent);
        }

    }

    private boolean isGPSEnabled() {

        mContext = LoginActivity.this;

        final LocationManager manager = (LocationManager) getSystemService(mContext.LOCATION_SERVICE);

        if (!manager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(mContext);

            alertDialog.setTitle("GPS Settings");
            alertDialog.setMessage("Your GPS/Location service is off. \n Do you want to turn on location service?");

            // On pressing Settings button
            alertDialog.setPositiveButton("Settings", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    mContext.startActivity(intent);
                }
            });

            // on pressing cancel button
            alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });

            // Showing Alert Message
            alertDialog.show();

            return false;
        } else
            return true;
    }


    private void showOutdatedDialog() {
        if (App.CONST_REST_API_URL.indexOf(":83") > 0) return;

        btnLogin.setEnabled(true);

        AlertDialog dialog = new AlertDialog.Builder(mContext)
                .setTitle(R.string.AppName)
                .setMessage(R.string.message_version_outdated)


                .setPositiveButton("Exit", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        finishAffinity();
                    }
                })

                .setNegativeButton("Ignore",new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();

//                        if (prefDB.getBoolean(App.CONST_REMEMBER_ME)) {
//                            mUserName.setText(prefDB.getString(App.CONST_USER_NAME));
//                            mRemember.setChecked(true);
//
//                            if(autoLogin)
//                                performLogin();
//                            else
//                                btnLogin.setEnabled(true);
//                        }else{
//                            btnLogin.setEnabled(true);
//                        }
                    }
                })


                .setNeutralButton("Go to Play Store", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finishAffinity();
                        final String appPackageName = getPackageName(); // getPackageName() from Context or Activity object
                        try {
                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
                        } catch (android.content.ActivityNotFoundException anfe) {
                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
                        }
                    }
                })
                .create();


        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }


//    private void showNotification(String title, String body) {
//        String OLD_CH = App.getOldChannelId();
//        String NEW_CH = App.getNewChannelId();
//
//        Intent intent = new Intent(this, JobOverviewActivity.class);
//        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);
//
//        Uri soundUri = App.getNotificationSoundUri();
//        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, NEW_CH)
//                .setSmallIcon(R.mipmap.my_limo_launcher)
//                .setContentTitle(title)
//                .setContentText(body)
//                .setAutoCancel(true)
//                .setSound(soundUri)
//                .setContentIntent(pendingIntent);
//
//
//        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
//            if (soundUri != null) {
//                // Changing Default mode of notification
//                notificationBuilder.setDefaults(Notification.DEFAULT_VIBRATE);
//                // Creating an Audio Attribute
//                AudioAttributes audioAttributes = new AudioAttributes.Builder()
//                        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
//                        .setUsage(AudioAttributes.USAGE_ALARM)
//                        .build();
//
//
////                //it will delete existing channel if it exists
//                if (mNotificationManager.getNotificationChannel(OLD_CH) != null) {
//                    mNotificationManager.deleteNotificationChannel(OLD_CH);
//                }
//
//                // Creating Channel
//                NotificationChannel notificationChannel = new NotificationChannel(NEW_CH, NEW_CH, NotificationManager.IMPORTANCE_HIGH);
//
//                notificationChannel.enableLights(true);
//                notificationChannel.enableVibration(true);
//                notificationChannel.setSound(soundUri, audioAttributes);
//                mNotificationManager.createNotificationChannel(notificationChannel);
//            }
//        }
//
//        mNotificationManager.notify(0, notificationBuilder.build());
//    }


    private static void showRefreshDialog() {

        AlertDialog dialog = new AlertDialog.Builder(App.targetContent)
                .setTitle(R.string.AppName)
                .setMessage("End point can not reach")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })

                .create();


        dialog.show();
    }

}
