package com.info121.nativelimo.activities;

import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

import com.app.adprogressbarlib.AdCircleProgress;
import com.info121.nativelimo.App;
import com.info121.nativelimo.R;
import com.info121.nativelimo.api.RestClient;
import com.info121.nativelimo.models.JobRes;

import org.greenrobot.eventbus.EventBus;

import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.view.View.GONE;

public class NotifyActivity extends AppCompatActivity {
    int i = 0;

    @BindView(R.id.pgb_progress)
    AdCircleProgress mProgress;

    @BindView(R.id.job_type)
    TextView mJobType;

    @BindView(R.id.job_date)
    TextView mJobDate;

    @BindView(R.id.pickup_time)
    TextView mPickupTime;

    @BindView(R.id.pickup)
    TextView mPickup;

    @BindView(R.id.dropoff)
    TextView mDropOff;

    @BindView(R.id.client_name)
    TextView mCustName;


    ToneGenerator toneGen = new ToneGenerator(AudioManager.STREAM_ALARM, 100);
    Timer t1 = new Timer();
    Timer t2 = new Timer();

    int count = 10;

//    @BindView(R.id.address)
//    AdCircleProgress mAddress;

    Context mContext = NotifyActivity.this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_notify);
        this.setFinishOnTouchOutside(false);

        ButterKnife.bind(this);

        // Display Message
        Intent intent = getIntent();

//        bundle.putString("JOB_NO", jobNo);
//        bundle.putString("JOB_TYPE", jobType);
//        bundle.putString("JOB_DATE", jobDate);
//        bundle.putString("PICKUP", pickup);
//        bundle.putString("DROPOFF", dropoff);
//        bundle.putString("CUST_NAME", clientName);

        final String jobNo = intent.getExtras().getString("JOB_NO");
        final String jobType = intent.getExtras().getString("JOB_TYPE");
        final String jobDate = intent.getExtras().getString("JOB_DATE");
        final String jobTime = intent.getExtras().getString("JOB_TIME");
        final String pickup = intent.getExtras().getString("PICKUP");
        final String dropoff = intent.getExtras().getString("DROPOFF");
        final String custName = intent.getExtras().getString("CUST_NAME");


        mJobType.setText(jobType);
        mJobDate.setText(jobDate);
        mPickupTime.setText(jobTime);
        mPickup.setText(pickup);
        mDropOff.setText(dropoff);
        mCustName.setText(custName);


        ButterKnife.bind(this);


        final Timer t = new Timer();
        t.scheduleAtFixedRate(new TimerTask() {
            public void run() {
                runOnUiThread(new Runnable() {
                    public void run() {
                        mProgress.setAdProgress(i);
                        i++;
                    }
                });
            }
        }, 1000, 90);


        // toneGen.startTone(ToneGenerator.TONE_CDMA_ALERT_CALL_GUARD,50);

        mProgress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateJobStatus(jobNo, "Confirm");
                callUpdateDriverLocation();
            }
        });

        playBeep1();
    }


    private  void playBeep1(){
        t1.scheduleAtFixedRate(new TimerTask() {
            public void run() {
                runOnUiThread(new Runnable() {
                    public void run() {
                        toneGen.startTone(ToneGenerator.TONE_CDMA_ALERT_CALL_GUARD, 250);
                        count--;

                        if(count==0){
                            t1.cancel();
                            playBeep2();
                        }
                    }
                });
            }
        }, 500, 1000);
    }

    private  void playBeep2(){
        t2.scheduleAtFixedRate(new TimerTask() {
            public void run() {
                runOnUiThread(new Runnable() {
                    public void run() {
                        toneGen.startTone(ToneGenerator.TONE_CDMA_ALERT_CALL_GUARD, 250);
                    }
                });
            }
        }, 500, 500);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        t1.cancel();
        t2.cancel();
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


        Call<JobRes> call = RestClient.COACH().getApiService().UpdateJobStatus(
                jobNo,
                App.fullAddress,
                status

        );

        call.enqueue(new Callback<JobRes>() {
            @Override
            public void onResponse(Call<JobRes> call, Response<JobRes> response) {
                Log.e("Update Job Successful", response.toString());

                finish();

                Toast.makeText(mContext, "Update Successful", Toast.LENGTH_SHORT).show();
                EventBus.getDefault().postSticky("UPDATE_JOB_COUNT");

            }

            @Override
            public void onFailure(Call<JobRes> call, Throwable t) {
                Log.e("Update Job Failed ", t.getMessage());
            }
        });
    }
}
