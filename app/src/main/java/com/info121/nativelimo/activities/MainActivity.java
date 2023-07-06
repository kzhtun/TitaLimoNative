package com.info121.nativelimo.activities;

import android.Manifest;
import android.app.job.JobInfo;
import android.content.Context;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;

import android.os.Handler;
import android.os.Looper;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.info121.nativelimo.App;
import com.info121.nativelimo.R;
import com.info121.nativelimo.api.RestClient;
import com.info121.nativelimo.models.ObjectRes;
import com.info121.nativelimo.utils.PrefDB;
import com.info121.nativelimo.utils.Util;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class MainActivity extends AppCompatActivity {

    Context mContext = MainActivity.this;
    PrefDB prefDB;

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

    public String[] permissions = {Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.CAMERA,
            Manifest.permission.CALL_PHONE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
            // Manifest.permission.POST_NOTIFICATIONS
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

        prefDB = new PrefDB(getApplicationContext());


//        Bundle bundle = getIntent().getExtras();
//
//        String userName = prefDB.getString(App.CONST_USER_NAME);
//
//        if (bundle != null) {
//            if (userName.length() > 0) {
//                callValidateDriver(userName);
//            }
//        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            startActivity(new Intent(MainActivity.this, TiramisuPermissionActivity.class));
        } else {

            if (hasPermissions(this, permissions))
                startActivity(new Intent(MainActivity.this, LoginActivity.class));
            else {
                ActivityCompat.requestPermissions(this, permissions, PERMISSION_ALL);
            }
        }

        finish();
    }


    public void callValidateDriver(String userName) {
        Call<ObjectRes> call = RestClient.COACH().getApiService().ValidateDriver(userName.trim());

        call.enqueue(new Callback<ObjectRes>() {
            @Override
            public void onResponse(Call<ObjectRes> call, Response<ObjectRes> response) {

                if (response.body().getResponsemessage().equalsIgnoreCase("VALID")) {
                    App.userName = userName;
                    App.deviceID = Util.getDeviceID(getApplicationContext());
                    App.authToken = response.body().getToken();
                    //  EventBus.getDefault().post("GET_TODAY_JOBS");
                    startActivity(new Intent(MainActivity.this, JobOverviewActivity.class));
                }

            }

            @Override
            public void onFailure(Call<ObjectRes> call, Throwable t) {
            }
        });
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

 //       Bundle bundle = intent.getExtras();

//        String userName = prefDB.getString(App.CONST_USER_NAME);
//
//        if (bundle != null) {
//            if (userName.length() > 0) {
//                callValidateDriver(userName);
//            }
//        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (hasPermissions(this, permissions)) {
            final Handler handler = new Handler(Looper.getMainLooper());
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    startActivity(new Intent(MainActivity.this, LoginActivity.class));
                }
            }, 20);

        }

    }


}
