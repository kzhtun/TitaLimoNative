package com.info121.nativelimo.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.os.PowerManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.info121.nativelimo.R;
import com.info121.nativelimo.api.RestClient;
import com.info121.nativelimo.models.JobRes;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DialogActivity extends AppCompatActivity {
    public static final String JOB_NO = "JOB_NO";
    public static final String NAME = "NAME";
    public static final String PHONE = "PHONE";
    public static final String MESSAGE = "MESSAGE";

    Spinner mPhones;
    Button mDismiss, mCall, mConfirm;
    TextView mMessage;

    @BindView(R.id.phone_layout_main)
    LinearLayout mPhoneLayoutMain;

    @BindView(R.id.phone_layout1)
    LinearLayout mPhoneLayout1;

    @BindView(R.id.phone_layout2)
    LinearLayout mPhoneLayout2;

    @BindView(R.id.phone_no1)
    TextView mPhoneNo1;

    @BindView(R.id.phone_no2)
    TextView mPhoneNo2;

    @BindView(R.id.call1)
    ImageView mCall1;

    @BindView(R.id.call2)
    ImageView mCall2;

    private String mJobNo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        requestWindowFeature(Window.FEATURE_NO_TITLE);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.activity_dialog);

        ButterKnife.bind(this);


        this.setFinishOnTouchOutside(false);


        mMessage = findViewById(R.id.message);
       // mPhones = findViewById(R.id.phones);
  //      mCall = findViewById(R.id.btn_call);
        mConfirm = findViewById(R.id.btn_confirm);
        mDismiss = findViewById(R.id.btn_remind_later);


        // Display Message
        Intent intent = getIntent();

        final String phones = intent.getExtras().getString(PHONE);
        final String message = intent.getExtras().getString(MESSAGE);
        final String jobNo = intent.getExtras().getString(JOB_NO);

        mJobNo = jobNo;


//        // for testing purpose only
//        String phones = "09965042219/095137664";
//        String message = "Hi askldfalk adsf asdf adsfj alsfaklsf klasdfj aldsf aldfs afsl";


        // Fill the data
        mMessage.setText(message);

        final List<String> phoneList = new ArrayList<String>();

        if(phones.trim().length() > 0) {
            String p[] = phones.split("/");
            for (String s : p) {
                phoneList.add(s.trim());
            }

            mPhoneLayoutMain.setVisibility(View.VISIBLE);
        }else{
            mPhoneLayoutMain.setVisibility(View.GONE);
        }


        if(phoneList.size()==0){
            mPhoneLayout1.setVisibility(View.GONE);
            mPhoneLayout2.setVisibility(View.GONE);
        }

        if(phoneList.size()==1){
            mPhoneLayout1.setVisibility(View.VISIBLE);
            mPhoneLayout2.setVisibility(View.GONE);

            mPhoneNo1.setText(phoneList.get(0));
        }

        if(phoneList.size()==2){
            mPhoneLayout1.setVisibility(View.VISIBLE);
            mPhoneLayout2.setVisibility(View.VISIBLE);

            mPhoneNo1.setText(phoneList.get(0));
            mPhoneNo2.setText(phoneList.get(1));
        }


//        mCall.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                finish();
//                phoneCall(mPhones.getSelectedItem().toString());
//              //  APIClient.ConfirmJob(jobNo);
//            }
//        });


        mCall1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                phoneCall(phoneList.get(0));
            }
        });


        mCall2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                phoneCall(phoneList.get(1));
            }
        });

        mDismiss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
             //   APIClient.RemindLater(jobNo);
            }
        });

        mConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                callConfirmJobReminder();
             //   APIClient.ConfirmJob(jobNo);
            }
        });



        // resize dialog
        Rect displayRectangle = new Rect();
        Window window = getWindow();
        window.getDecorView().getWindowVisibleDisplayFrame(displayRectangle);

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(getWindow().getAttributes());
        lp.width = (int) (displayRectangle.width() * 0.89f);

        getWindow().setAttributes(lp);

        // Wake Screen
        @SuppressLint("InvalidWakeLockTag") PowerManager.WakeLock screenLock = ((PowerManager) getSystemService(POWER_SERVICE)).newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP, "TAG");
        screenLock.acquire();
        screenLock.release();
    }

    public void phoneCall(String phoneNo) {

        Uri number = Uri.parse("tel:" + phoneNo);
        Intent callIntent = new Intent(Intent.ACTION_DIAL, number);
        callIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        try {
            startActivity(callIntent);
            Toast.makeText(getApplicationContext(), "Phone Call .... to  " + phoneNo, Toast.LENGTH_SHORT).show();
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(getApplicationContext(), ex.getMessage().toString(), Toast.LENGTH_LONG).show();
        }
    }

    private ArrayAdapter<String> fillPhoneNumbers(String phones) {
        String p[] = phones.split("/");

        List<String> phoneList = new ArrayList<String>();

        for (String s : p) {
            phoneList.add(s.trim());
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                this, android.R.layout.simple_spinner_item, phoneList);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        return adapter;
    }


    private void callConfirmJobReminder(){

            Call<JobRes> call = RestClient.COACH().getApiService().ConfirmJobReminder(
                    mJobNo
            );

            call.enqueue(new Callback<JobRes>() {
                @Override
                public void onResponse(Call<JobRes> call, Response<JobRes> response) {
                    Toast.makeText(DialogActivity.this, "Job confirmed", Toast.LENGTH_SHORT).show();
                    finish();

                }

                @Override
                public void onFailure(Call<JobRes> call, Throwable t) {
                    Toast.makeText(DialogActivity.this, "Job confirm failed", Toast.LENGTH_SHORT).show();
                    finish();
                }
            });

    }
}
