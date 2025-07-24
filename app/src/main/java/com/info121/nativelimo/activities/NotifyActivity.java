package com.info121.nativelimo.activities;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Rect;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.Build;
import android.os.Handler;
import android.os.VibrationEffect;
import android.os.Vibrator;

import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.app.adprogressbarlib.AdCircleProgress;
import com.info121.nativelimo.AbstractActivity;
import com.info121.nativelimo.App;
import com.info121.nativelimo.R;
import com.info121.nativelimo.api.RestClient;
import com.info121.nativelimo.models.Action;
import com.info121.nativelimo.models.JobRes;
import com.info121.nativelimo.models.ObjectRes;
import com.info121.nativelimo.models.RequestMobileLog;
import com.info121.nativelimo.utils.Util;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutionException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.view.View.GONE;
import static com.info121.nativelimo.App.prefDB;

import androidx.appcompat.app.AppCompatActivity;

public class NotifyActivity  extends AbstractActivity {
    int i = 0;

    @BindView(R.id.pgb_progress)
    AdCircleProgress mProgress;


    @BindView(R.id.root_layout)
    LinearLayout mRootLayout;


    @BindView(R.id.date)
    TextView mJobDate;

    @BindView(R.id.name)
    TextView mCustName;

    @BindView(R.id.pickup_time)
    TextView mPickupTime;

    @BindView(R.id.pickup)
    TextView mPickup;

    @BindView(R.id.dropoff)
    TextView mDropOff;

    @BindView(R.id.vehicle_type)
    TextView mVehicleType;

    @BindView(R.id.adjust_view)
    View mAdjustView;

    ViewGroup.LayoutParams params;

    AudioManager audioManager;

    ToneGenerator toneGen = new ToneGenerator(AudioManager.STREAM_ALARM, 100);
    Timer t1 = new Timer();
    Timer t2 = new Timer();

    int count = 10;

    int screenWidth, screenHeight, rootHeight, rootWidth;
    DisplayMetrics displayMetrics;

    Boolean startFromLandscape = false;

//    @BindView(R.id.address)
//    AdCircleProgress mAddress;

    Context mContext = NotifyActivity.this;

    String jobNo, driverName;

    Vibrator vibrator;

    Boolean getHeightAlready = false;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_notify);
        this.setFinishOnTouchOutside(false);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            setShowWhenLocked(true);
            setTurnScreenOn(true);
        } else {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON |
                    WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON |
                    WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
        }


        ButterKnife.bind(this);

        // Display Message
        Intent intent = getIntent();

//        bundle.putString("JOB_NO", jobNo);
//        bundle.putString("JOB_TYPE", jobType);
//        bundle.putString("JOB_DATE", jobDate);
//        bundle.putString("PICKUP", pickup);
//        bundle.putString("DROPOFF", dropoff);
//        bundle.putString("CUST_NAME", clientName);
        Bundle extras = intent.getExtras();
         jobNo = extras != null ? extras.getString("JOB_NO", "N/A") : "N/A";
        String jobDate = extras != null ? extras.getString("JOB_DATE", "N/A") : "N/A";
        String jobTime = extras != null ? extras.getString("JOB_TIME", "N/A") : "N/A";
        String pickup = extras != null ? extras.getString("PICKUP", "N/A") : "N/A";
        String dropoff = extras != null ? extras.getString("DROPOFF", "N/A") : "N/A";
        String vehicleType = extras != null ? extras.getString("VEHICLE_TYPE", "N/A") : "N/A";
        String custName = extras != null ? extras.getString("CUST_NAME", "N/A") : "N/A";
         driverName = extras != null ? extras.getString("DRIVER", "N/A") : "N/A";


//        mJobType.setText(jobType);
        mJobDate.setText(jobDate);
        mCustName.setText(custName);
        mVehicleType.setText(vehicleType);
        mPickupTime.setText(jobTime);
        mPickup.setText(pickup);
        mDropOff.setText(dropoff);
        mVehicleType.setText(vehicleType);


        ButterKnife.bind(this);

        vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);

        final Timer t = new Timer();
        t.scheduleAtFixedRate(new TimerTask() {
            public void run() {
                runOnUiThread(new Runnable() {
                    public void run() {
                        mProgress.setAdProgress(i);
                        //vibrate();
                        i++;

                    }
                });
            }
        }, 1000, 90);

        // toneGen.startTone(ToneGenerator.TONE_CDMA_ALERT_CALL_GUARD,50);

        mProgress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                acceptJob();
            }
        });


        App.notiActivityIsShowing = true;
        playBeep1();
        vibrate();

        displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        screenHeight = displayMetrics.heightPixels;
        screenWidth = displayMetrics.widthPixels;


        params = mAdjustView.getLayoutParams();

        final ViewTreeObserver observer = mRootLayout.getViewTreeObserver();
        observer.addOnGlobalLayoutListener(
                new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        if (!getHeightAlready) {

                            rootHeight = mRootLayout.getHeight();
                            rootWidth = mRootLayout.getWidth();

                            params.height = (screenHeight - rootHeight) / 2;
                            mAdjustView.setLayoutParams(params);
                            getHeightAlready = true;
                        }
//                        if (screenHeight > rootHeight)
//                            mRootLayout.setLayoutParams(params);
                    }
                });

        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE)
            startFromLandscape = true;
        else
            startFromLandscape = false;

    }


    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        //  rootHeight = mRootLayout.getHeight();


//        displayMetrics = new DisplayMetrics();
//        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
//        screenHeight = displayMetrics.heightPixels;
//        screenWidth = displayMetrics.widthPixels;

        // Checks the orientation of the screen
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            if (startFromLandscape)
                params.height = (screenHeight - rootHeight) / 2;
            else
                params.height = (screenWidth - rootHeight) / 2;

        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            if (startFromLandscape)
                params.height = (screenWidth - rootHeight) / 2;
            else
                params.height = (screenHeight - rootHeight) / 2;

        }
        mAdjustView.setLayoutParams(params);
    }


    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    protected void onStart() {
        super.onStart();

    }

    @OnClick(R.id.root_layout)
    public void rootLayoutOnClick() {
        acceptJob();

        //Toast.makeText(mContext, "RootLayoutOnClick", Toast.LENGTH_SHORT).show();

    }



    private void acceptJob() {
        // before update the job call validate driver to get new token
       // callValidateDriver(prefDB.getString(App.CONST_USER_NAME).trim());


        Activity loginActivity = new LoginActivity();


        new Thread(new Runnable() {
            @Override
            public void run() {
                callValidateDriver(prefDB.getString(App.CONST_USER_NAME).trim());
//                updateJobStatus(jobNo, "Confirm");
//                callUpdateDriverLocation();
            }
        }).start();
//        startActivity(new Intent(NotifyActivity.this, JobOverviewActivity.class));
        finish();
    }


    private void vibrate() {
        if (Build.VERSION.SDK_INT >= 26) {
            vibrator.vibrate(VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE));
        } else {
            vibrator.vibrate(200);
        }

    }

    private void playAcceptBeep() {
        toneGen.startTone(ToneGenerator.TONE_CDMA_ALERT_INCALL_LITE, 100);
        toneGen.startTone(ToneGenerator.TONE_CDMA_ABBR_ALERT, 20);
    }


    private void playBeep1() {
        t1.scheduleAtFixedRate(new TimerTask() {
            public void run() {
                runOnUiThread(new Runnable() {
                    public void run() {

                        playSound();
                        count--;

                        if (count == 0) {
                            t1.cancel();
                            playBeep2();
                        }

                        vibrate();
                    }
                });
            }
        }, 500, 1000);
    }

    private void playBeep2() {
        t2.scheduleAtFixedRate(new TimerTask() {
            public void run() {
                runOnUiThread(new Runnable() {
                    public void run() {
                        playSound();
                    }
                });

                vibrate();

            }
        }, 500, 500);
    }

    private void playSound() {
        if (audioManager.getRingerMode() == 2)
            toneGen.startTone(ToneGenerator.TONE_CDMA_ALERT_CALL_GUARD, 200);
    }

    @Override
    protected void onStop() {
        super.onStop();
        t1.cancel();
        t2.cancel();

        vibrator.cancel();

        App.notiActivityIsShowing = false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        t1.cancel();
        t2.cancel();

        vibrator.cancel();

        App.notiActivityIsShowing = false;

    }

    private void callUpdateDriverLocation() {
        if (App.gpsStatus == 0) return;


        Call<JobRes> call = RestClient.COACH().getApiService().UpdateDriverLocation(
                String.valueOf(App.location.getLatitude()),
                String.valueOf(App.location.getLongitude()),
                App.gpsStatus,
                App.fullAddress
        );

        call.enqueue(new Callback<JobRes>() {
            @Override
            public void onResponse(Call<JobRes> call, Response<JobRes> response) {
                Log.e("Update Driver Location", " Success");


            }

            @Override
            public void onFailure(Call<JobRes> call, Throwable t) {
                Log.e("Update Driver Location", " Failed");
            }
        });
    }

    private void updateJobStatus(final String jobNo, final String status) {
        App.fullAddress = (App.fullAddress.isEmpty()) ? " " : App.fullAddress;

       // Toast.makeText(mContext, "Update Job Called", Toast.LENGTH_SHORT).show();

      //  Log.e("Token : ", App.authToken);

        Call<JobRes> call = RestClient.COACH().getApiService().UpdateJobStatus(
                jobNo,
                App.fullAddress,
                status
        );

        call.enqueue(new Callback<JobRes>() {
            @Override
            public void onResponse(Call<JobRes> call, Response<JobRes> response) {
               if (response.isSuccessful() && response.body() != null) {
                    if (response.body().getResponsemessage().equalsIgnoreCase("Success")) {
                        EventBus.getDefault().postSticky("UPDATE_JOB_COUNT");
                        playAcceptBeep();
                        Toast.makeText(mContext, "Update Job Successful", Toast.LENGTH_SHORT).show();

                        Util.addLog("NotifiyActivity : " + "updateJobStatus successful");
                    }

                    if (response.body().getResponsemessage().equalsIgnoreCase("BAD TOKEN")) {
                        refreshToken(driverName, status);
                        Util.addLog("NotifiyActivity : " + "updateJobStatus bad token");
                    }
               }else{
                   Util.addLog("NotifiyActivity : " + "updateJobStatus response failed");
               }

               RequestMobileLog req = new RequestMobileLog(App.StackTraceLog, "updateJobStatus call on NotifyActivity");
               App.callUpdateMobileLog(req);

            }

            @Override
            public void onFailure(Call<JobRes> call, Throwable t) {
                Log.e("Update Job Failed ", t.getMessage());
                Toast.makeText(mContext, "Update Job Failed ", Toast.LENGTH_SHORT).show();
            }
        });
    }

//
//    private void callPendingIntent(){
//        Intent mStartActivity = new Intent(mContext, LoginActivity.class);
//        int mPendingIntentId = 123456;
//        PendingIntent mPendingIntent = PendingIntent.getActivity(mContext, mPendingIntentId,    mStartActivity, PendingIntent.FLAG_CANCEL_CURRENT);
//        AlarmManager mgr = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);
//        mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 100, mPendingIntent);
//        System.exit(0);
//    }

//
//    public void updateJobWithNewToken(){
//
//    }


    public void callValidateDriver(String userName) {
        Call<ObjectRes> call = RestClient.COACH().getApiService().ValidateDriver(userName.trim());

        call.enqueue(new Callback<ObjectRes>() {
            @Override
            public void onResponse(Call<ObjectRes> call, Response<ObjectRes> response) {

                if (response.body().getResponsemessage().equalsIgnoreCase("VALID")) {
                    App.userName = userName;
                    App.deviceID = Util.getDeviceID(getApplicationContext());
                    App.authToken = response.body().getToken();

                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            startActivity(new Intent(NotifyActivity.this, JobOverviewActivity.class));
                            updateJobStatus(jobNo, "Confirm");
                            callUpdateDriverLocation();
                        }
                    }).start();
                    finish();
                }

            }

            @Override
            public void onFailure(Call<ObjectRes> call, Throwable t) {
                Log.e("callValidateDriver Error : ", t.getMessage());
            }
        });
    }

    @Subscribe(sticky = false)
    public void onEvent(Action action) {
        //  Toast.makeText(getContext(), "Action Done", Toast.LENGTH_SHORT).show();

       // if (action.getAction().equalsIgnoreCase("Cancel Full Notification"))
        if (action.getAction().equalsIgnoreCase("Unassign"))
            if (action.getJobNo().equalsIgnoreCase(jobNo)) {
                finish();
            } else {
                for (int i = 0; i < App.intents.size(); i++) {
                    String jNo = App.intents.get(i).getStringExtra("JOB_NO");
                    if (action.getJobNo().equalsIgnoreCase(jNo))
                        App.intents.remove(App.intents.get(i));
                }
            }

        startActivity(new Intent(NotifyActivity.this, JobOverviewActivity.class));
    }


    public void refreshToken(final String driver, final String action){
        App.userName = driver;

        RestClient.COACH().getApiService().ValidateDriver(driver).enqueue(new Callback<ObjectRes>() {
            @Override
            public void onResponse(Call<ObjectRes> call, retrofit2.Response<ObjectRes> response) {
                if(response.body().getResponsemessage().equalsIgnoreCase("VALID")) {
                    App.authToken = response.body().getToken();

                    updateJobStatus(jobNo, action);
                }
            }

            @Override
            public void onFailure(Call<ObjectRes> call, Throwable t) {

            }
        });
    }

//    @Subscribe(sticky = false)
//    public void onEvent(String event) {
//       // Toast.makeText(mContext,  "Job is unassigned!", Toast.LENGTH_SHORT).show();
//       // EventBus.getDefault().removeStickyEvent("CANCEL_FULL_SCREEN_NOTI");
//        finish();
//
//    }

}
