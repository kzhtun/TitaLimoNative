package com.info121.nativelimo.activities;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
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
import androidx.core.app.ActivityCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.info121.nativelimo.AbstractActivity;
import com.info121.nativelimo.App;
import com.info121.nativelimo.BuildConfig;
import com.info121.nativelimo.R;
import com.info121.nativelimo.api.RestClient;
import com.info121.nativelimo.models.ObjectRes;
import com.info121.nativelimo.services.FirebaseMessagingService;
import com.info121.nativelimo.services.SmartLocationService;
import com.info121.nativelimo.utils.PrefDB;
import com.info121.nativelimo.utils.Util;

import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AbstractActivity {

    Context mContext = LoginActivity.this;
    PrefDB prefDB;

    @BindView(R.id.user_name)
    EditText mUserName;

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


    String[] permissions = {
            Manifest.permission.POST_NOTIFICATIONS
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        ButterKnife.bind(this);

        prefDB = new PrefDB(getApplicationContext());

        btnLogin.setEnabled(false);

        // Release
     //   callCheckVersion();


        // Debug
        if (prefDB.getBoolean(App.CONST_REMEMBER_ME)) {
            mUserName.setText(prefDB.getString(App.CONST_USER_NAME));
            mRemember.setChecked(true);

            loginOnClick();
        }else{
            btnLogin.setEnabled(true);
        }


        mApiVersion.setText("Api " + Util.getVersionCode(mContext));
        mUiVersion.setText("Ver " + Util.getVersionName(mContext));

    }


    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        Bundle extras = getIntent().getExtras();

        if(extras != null){
            String jobNo = getIntent().getStringExtra("JOB_NO");
            if (jobNo != null)
                Log.e("JOB_NO", jobNo);
        }
    }

    @OnClick(R.id.login)
    public void loginOnClick() {
        mProgressBar.setVisibility(View.VISIBLE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            // check notification permissions after login success
            if (hasPermissions(mContext, permissions)) {
                callValidateDriver(mUserName.getText().toString());
            } else {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                    requestPermissions(permissions, 80);
            }
        }else{
            callValidateDriver(mUserName.getText().toString());
        }


    }


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
        callValidateDriver(mUserName.getText().toString());
    }

    public void callValidateDriver(String userName) {
        Call<ObjectRes> call = RestClient.COACH().getApiService().ValidateDriver(userName.trim());

        call.enqueue(new Callback<ObjectRes>() {
            @Override
            public void onResponse(Call<ObjectRes> call, Response<ObjectRes> response) {

                if (response.body() == null) {
                    showRefreshDialog();
                }

                if (response.body().getResponsemessage().equalsIgnoreCase("VALID")) {
                    App.userName = userName;
                    App.deviceID = Util.getDeviceID(getApplicationContext());
                    App.authToken = response.body().getToken();
                    App.timerDelay = 6000;

                    callUpdateDevice();

                } else {
                    mUserName.setError("Wrong user name");
                    mUserName.requestFocus();
                    mProgressBar.setVisibility(View.GONE);
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

    public static boolean hasPermissions(Context context, String... permissions) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }

        return true;
    }

    private void callCheckVersion() {
        Call<ObjectRes> call = RestClient.COACH().getApiService().CheckVersion(String.valueOf(Util.getVersionCode(mContext)));

        call.enqueue(new Callback<ObjectRes>() {
            @Override
            public void onResponse(Call<ObjectRes> call, Response<ObjectRes> response) {
                if (response.body().getResponsemessage().equalsIgnoreCase("OUTDATED")) {
                    showOutdatedDialog();
                }else{
                    if (prefDB.getBoolean(App.CONST_REMEMBER_ME)) {
                        mUserName.setText(prefDB.getString(App.CONST_USER_NAME));
                        mRemember.setChecked(true);

                        loginOnClick();
                    }else{
                        btnLogin.setEnabled(true);
                    }
                }
            }

            @Override
            public void onFailure(Call<ObjectRes> call, Throwable t) {
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
        prefDB.putString(App.CONST_DEVICE_ID, App.deviceID);
        prefDB.putLong(App.CONST_TIMER_DELAY, App.timerDelay);

        // location
        startLocationService();

        if (mRemember.isChecked())
            prefDB.putBoolean(App.CONST_REMEMBER_ME, true);
        else
            prefDB.putBoolean(App.CONST_REMEMBER_ME, false);


        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if (!task.isSuccessful()) {
                            Log.e("FCM", "getInstanceId failed", task.getException());
                            return;
                        }


                        // Get new Instance ID token
                        App.FCM_TOKEN = task.getResult().getToken();
                        Log.e("TOKEN : ", App.FCM_TOKEN);
                    }
                });


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
                .setNegativeButton("Go to Play Store", new DialogInterface.OnClickListener() {
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
                .setMessage("End pint can not reach")
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
