package com.info121.nativelimo.fragments;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;

import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.gcacace.signaturepad.views.SignaturePad;
import com.info121.nativelimo.R;
import com.info121.nativelimo.AbstractFragment;
import com.info121.nativelimo.App;
import com.info121.nativelimo.activities.DialogActivity;
import com.info121.nativelimo.activities.NotifyActivity;
import com.info121.nativelimo.adapters.ContactAdapter;
import com.info121.nativelimo.api.RestClient;
import com.info121.nativelimo.models.Action;
import com.info121.nativelimo.models.Job;
import com.info121.nativelimo.models.JobRes;
import com.info121.nativelimo.services.ShowDialogService;
import com.info121.nativelimo.utils.FtpHelper;
import com.info121.nativelimo.utils.GeocodingLocation;
import com.info121.nativelimo.utils.Util;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.app.Activity.RESULT_OK;
import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static com.info121.nativelimo.utils.FtpHelper.getImageUri;
import static com.info121.nativelimo.utils.FtpHelper.getRealPathFromURI;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
//import androidx.browser.customtabs.CustomTabsIntent;

import androidx.browser.customtabs.CustomTabsIntent;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;


public class JobDetailFragment extends AbstractFragment {
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {Manifest.permission.WRITE_EXTERNAL_STORAGE};

    Job job;
    int index;
    private static final int REQUEST_SHOW_CAMERA = 2001;
    private static final int REQUEST_NO_SHOW_CAMERA = 2002;

    List<String> phoneList = new ArrayList<String>();

    Dialog dialog;
    String mCurrentTab;

    @BindView(R.id.job_no)
    TextView mJobNo;

    @BindView(R.id.job_type)
    TextView mJobType;

    @BindView(R.id.job_status)
    TextView mJobStatus;

    @BindView(R.id.date)
    TextView mDate;

    @BindView(R.id.time)
    TextView mTime;

    @BindView(R.id.passenger)
    TextView mPassenger;

    @BindView(R.id.flight_no)
    TextView mFlightNo;

    @BindView(R.id.file_no)
    TextView mFileNo;

    @BindView(R.id.label_file_no)
    TextView mLabelFileNo;

    @BindView(R.id.eta)
    TextView mETA;

    @BindView(R.id.pickup)
    TextView mPickup;

    @BindView(R.id.dropoff)
    TextView mDropOff;

    @BindView(R.id.layout_remarks)
    LinearLayout mLayoutRemarks;

    @BindView(R.id.remarks)
    TextView mRemarks;

    @BindView(R.id.line_remarks)
    View mLineRemarks;

    @BindView(R.id.line_itinerary)
    View mLineItinerary;

    @BindView(R.id.assign_layout)
    LinearLayout mAssignLayout;

    @BindView(R.id.itinerary)
    Button mItinerary;

    @BindView(R.id.label_eta)
    TextView mLabelETA;

    @BindView(R.id.label_flight_no)
    TextView mLabelFlightNo;


    @BindView(R.id.no_adult)
    TextView mNoOfAdult;

    @BindView(R.id.no_child)
    TextView mNoOfChild;

    @BindView(R.id.no_infant)
    TextView mNoOfInfant;

    @BindView(R.id.layout_pax)
    LinearLayout mLayoutPax;

    @BindView(R.id.divider_pax)
    View mDividerPax;

    @BindView(R.id.vehicle_type)
    TextView mVehicleType;

    @BindView(R.id.staff)
    TextView mStaff;

    @BindView(R.id.layout_flight)
    LinearLayout mLayoutFlight;

    @BindView(R.id.list_contact)
    ListView mContactList;

    @BindView(R.id.pax_lower_line)
    View mPaxLowerLine;

//    @BindView(R.id.update)
//    EditText mUpdate;


//    @BindView(R.id.hs_view)
//    HorizontalScrollView mHSView;
//
//    @BindView(R.id.hs_root_layout)
//    LinearLayout mHSLayout;


    @BindView(R.id.action_layout)
    LinearLayout mActionLayout;


    @BindView(R.id.prev)
    Button mActionPrev;

    @BindView(R.id.next)
    Button mActionNext;

//    @BindView(R.id.confirm)
//    Button mActionConfirm;
//
//    @BindView(R.id.otw)
//    Button mActionOTW;

//    @BindView(R.id.os)
//    Button mActionOS;
//
//    @BindView(R.id.pob)
//    Button mActionPOB;
//
//    @BindView(R.id.pns)
//    Button mActionPNS;
//
//    @BindView(R.id.pns1)
//    Button mActionPNS1;

//    @BindView(R.id.complete)
//    Button mActionComplete;


    int bWidth, bHeight, bPadding;

    View rootView;

    TextView photoLabel, signatureLabel, clear, done;
    ImageView addPhoto, passengerPhoto, signaturePhoto;
    SignaturePad signaturePad;
    View signatureBackground;

    ToneGenerator toneGen = new ToneGenerator(AudioManager.STREAM_ALARM, 100);

    Boolean visible = false;
    Boolean hasSignature = false;

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            try {

                Bundle extras = data.getExtras();

                Bitmap photo = (Bitmap) data.getExtras().get("data");

                // display passenger photo
                ImageView passenger_photo = dialog.findViewById(R.id.passenger_photo);
                passenger_photo.setImageBitmap(photo);

//                // CALL THIS METHOD TO GET THE URI FROM THE BITMAP
//                Uri tempUri = getImageUri(getActivity().getApplicationContext(), photo);
//
//                // CALL THIS METHOD TO GET THE ACTUAL PATH
//                File finalFile = new File(getRealPathFromURI(getActivity().getApplicationContext(), tempUri));
//
//                InputStream inputStream = getActivity().getContentResolver().openInputStream(tempUri);

                InputStream inputStream = bitmap2InputStream(photo);

                // FTP Uploading
                FtpHelper.uploadTask async = new FtpHelper.uploadTask(getContext(), inputStream);

                if (requestCode == REQUEST_SHOW_CAMERA)
                    async.execute(App.FTP_URL,
                            App.FTP_USER,
                            App.FTP_PASSWORD,
                            App.FTP_DIR,
                            job.getJobNo() + App.CONST_PHOTO_SHOW_FILE_NAME,
                            job.getJobNo(),
                            "SHOW", null);   //Passing arguments to AsyncThread

                if (requestCode == REQUEST_NO_SHOW_CAMERA)
                    async.execute(App.FTP_URL,
                            App.FTP_USER,
                            App.FTP_PASSWORD,
                            App.FTP_DIR,
                            job.getJobNo() + App.CONST_PHOTO_NO_SHOW_FILE_NAME,
                            job.getJobNo(),
                            "NOSHOW", null);

            } catch (Exception e) {
                Log.e("Camera Error : ", e.getLocalizedMessage().toString());
            }
        }
    }

    public JobDetailFragment() {
        // Required empty public constructor

    }


    public static JobDetailFragment newInstance(Job job, String currentTab, int index) {
        JobDetailFragment fragment = new JobDetailFragment();
        Bundle args = new Bundle();

        fragment.mCurrentTab = currentTab;
        fragment.job = job;
        fragment.index = index;
        fragment.setArguments(args);

        return fragment;
    }

//    public void showNotifyJob(String jobNo, String jobType, String jobDate, String jobTime, String pickup, String dropoff, String clientName) {
//
//        // bundle
//        Bundle bundle = new Bundle();
//
//        bundle.putString("JOB_NO", jobNo);
//        bundle.putString("JOB_TYPE", jobType);
//        bundle.putString("JOB_DATE", jobDate);
//        bundle.putString("JOB_TIME", jobTime);
//        bundle.putString("PICKUP", pickup);
//        bundle.putString("DROPOFF", dropoff);
//        bundle.putString("CUST_NAME", clientName);
//
//        Intent intent = new Intent(getActivity(), NotifyActivity.class);
//        intent.putExtras(bundle);
//        startActivity(intent);
//    }

//    public void showDialog(String jobNo, String name, String phone, String displayMessage) {
//
//
//        Intent intent = new Intent(getContext(), DialogActivity.class);
//        //   intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//
//
//        Bundle bundle = new Bundle();
//
//        bundle.putString(ShowDialogService.JOB_NO, jobNo);
//        bundle.putString(ShowDialogService.NAME, name);
//        bundle.putString(ShowDialogService.PHONE, phone);
//        bundle.putString(ShowDialogService.MESSAGE, displayMessage);
//
//        intent.putExtras(bundle);
//        // startService(intent);
//
//        startActivity(intent);
//    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //TODO: remove when release.

//        showDialog("9293",
//                "Kyaw Zin",
//                "029393/0293939",
//        "Testing");

//        showNotifyJob("77338",
//                "MEDICAL",
//                "12/12/2020",
//                "12:00",
//                "",
//                "",
//                "Kyaw");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_job_detail, container, false);

        ButterKnife.bind(this, rootView);

        if (job.getJobStatus().equalsIgnoreCase("JOB ASSIGNED")) {
            mAssignLayout.setVisibility(VISIBLE);
            mActionLayout.setVisibility(GONE);
        } else {
            mAssignLayout.setVisibility(GONE);
            mActionLayout.setVisibility(VISIBLE);
        }

        setActionButtonsListener();
        changeCurrentStatusButton(job.getJobStatus());

        return rootView;
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        displayJobDetail();
        changeCurrentStatusButton(job.getJobStatus());
    }

    @OnClick(R.id.flight_no)
    public void flightNoOnClick() {
        showFlightAppsDialog();
    }

    @OnClick(R.id.layout_pickup)
    public void pickUpOnClick() {
        showNavAppSelectionDialog(App.location.getLatitude(), App.location.getLongitude(), job.getPickUp());
    }

    @OnClick(R.id.layout_dropoff)
    public void dropOffOnClick() {
        showNavAppSelectionDialog(App.location.getLatitude(), App.location.getLongitude(), job.getDestination());
    }


    private void setButtonWidth() {

//        Rect displayRectangle = new Rect();
//        Window window = getActivity().getWindow();
//        window.getDecorView().getWindowVisibleDisplayFrame(displayRectangle);
//
//        bPadding = Util.convertDpToPx(16) * 3 ;
//        bWidth = (displayRectangle.width() - bPadding) / 2;
//        bHeight = Util.convertDpToPx(42);
//
//        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(bWidth, bHeight);
//        params.setMargins(16, 0, 16, 0 );
//
//
//        mActionConfirm.setLayoutParams(params);
//        mActionOTW.setLayoutParams(params);
//        mActionOS.setLayoutParams(params);
//        mActionPOB.setLayoutParams(params);
//        mActionPNS.setLayoutParams(params);
//        mActionComplete.setLayoutParams(params);
//        mActionPNS1.setLayoutParams(params);


    }

    private void changeCurrentStatusButton(String status) {

        switch (status.toUpperCase()) {
            case "CONFIRM":
                mAssignLayout.setVisibility(GONE);
                mActionLayout.setVisibility(VISIBLE);

                // mJobStatus.setText("CONFIRM");

                mActionPrev.setVisibility(View.INVISIBLE);
                mActionNext.setText("ON THE WAY");
                break;

            case "ON THE WAY":
                //     mJobStatus.setText("On The Way");

                mActionPrev.setVisibility(VISIBLE);
                mActionNext.setText("ON SITE");
                break;

            case "ON SITE":
                //    mJobStatus.setText("On Site");

                mActionPrev.setVisibility(VISIBLE);
                mActionNext.setText("POB");
                break;

            case "PASSENGER ON BOARD":
                //   mJobStatus.setText("Passenger On Board");

                mActionPrev.setVisibility(VISIBLE);
                mActionNext.setText("COMPLETE");
                break;

            case "PASSENGER NO SHOW":
                //    mJobStatus.setText("Passenger No Show");

                mActionPrev.setVisibility(VISIBLE);
                mActionNext.setText("NEXT");
                break;
//
//            case "ON SITE":
//                mActionOTW.setBackgroundResource(R.drawable.rounded_button_red);
//                mActionOS.setVisibility(GONE);
//
//                mActionPOB.setBackgroundResource(R.drawable.rounded_button_green);
//                mActionPNS.setBackgroundResource(R.drawable.rounded_button_green);
//                scrollActionButtons(1);
//                break;
//
//            case "PASSENGER ON BOARD":
//                mActionOS.setBackgroundResource(R.drawable.rounded_button_red);
//                mActionPOB.setVisibility(GONE);
//                mActionPNS.setVisibility(GONE);
//                mActionPNS1.setVisibility(View.VISIBLE);
//
//                mActionComplete.setBackgroundResource(R.drawable.rounded_button_green);
//                scrollActionButtons(2);
//                break;
//
//            case "PASSENGER NO SHOW":
//                mActionOS.setBackgroundResource(R.drawable.rounded_button_red);
//                mActionPNS.setVisibility(GONE);
//
//                mActionComplete.setBackgroundResource(R.drawable.rounded_button_green);
//                scrollActionButtons(3);
//                break;

        }

//        setButtonWidth();
    }


    @OnClick(R.id.prev)
    public void prevOnClick() {
        actionButtonClick(job.getJobStatus(), 0);
        playBackBeep();
    }

    @OnClick(R.id.next)
    public void nextOnClick() {
        actionButtonClick(job.getJobStatus(), 1);
        playNextBeep();
    }

    private void actionButtonClick(String status, int button) {

        switch (status.toUpperCase()) {
            case "CONFIRM":
                updateJobStatus("On The Way");
                break;

            case "ON THE WAY":
                if (button == 0)
                    updateJobStatus("Confirm");
                else
                    updateJobStatus("On Site");
                break;

            case "ON SITE":
                if (button == 0)
                    updateJobStatus("On The Way");
                else
                    showActionButtonsDialog();
                break;

            case "PASSENGER ON BOARD":
                if (button == 0)
                    updateJobStatus("On Site");
                else
                    showCompleteDialog();
                break;
        }


    }

    private void showActionButtonsDialog() {
        dialog = new Dialog(getActivity());

        dialog.setContentView(R.layout.dialog_actions);
        dialog.setTitle("");
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        // register events
        Button pob = dialog.findViewById(R.id.pob);
        Button pns = dialog.findViewById(R.id.pns);
        Button cancel = dialog.findViewById(R.id.cancel);

        pob.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                showPassengerOnBoardDialog();
                playNextBeep();
            }
        });

        pns.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                showPassengerNoShowDialog();
                playNextBeep();
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                changeCurrentStatusButton("On Site");
                playBackBeep();
            }
        });

        // resize dialog
        Rect displayRectangle = new Rect();
        Window window = getActivity().getWindow();
        window.getDecorView().getWindowVisibleDisplayFrame(displayRectangle);

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = (int) (displayRectangle.width() * 0.85f);

        dialog.show();
        dialog.getWindow().setAttributes(lp);
    }

    private void setActionButtonsListener() {

//        mActionConfirm.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                updateJobStatus("Confirm");
//
//            }
//        });
//
//        mActionOTW.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                updateJobStatus("On The Way");
//
//            }
//        });
//
//        mActionOS.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                updateJobStatus("On Site");
//
//            }
//        });
//
//        mActionPOB.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                showPassengerOnBoardDialog();
//
//            }
//        });
//
//        mActionPNS.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                showPassengerNoShowDialog();
//            }
//        });
//
//        mActionPNS1.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                showPassengerNoShowDialog();
//            }
//        });
//
//        mActionComplete.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                showCompleteDialog();
//            }
//        });
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

    private void showFlightAppsDialog() {

        final String package_changi = "com.changiairport.cagapp";
        final String package_flight_rader = "com.flightradar24free";
        final String package_flight_aware = "com.flightaware.android.liveFlightTracker";

        final Dialog dialog = new Dialog(getContext());
        dialog.setContentView(R.layout.dialog_flight_app_selection);
        dialog.setTitle("App Selection");

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        Window window = dialog.getWindow();
        window.setAttributes(lp);
        window.setGravity(Gravity.CENTER);

        //adding dialog animation sliding up and down
        // dialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);

        // to cancel when outside touch
        dialog.setCanceledOnTouchOutside(true);
        dialog.setCancelable(true);
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation_Fade;

        // window.setFlags(WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH, WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH);

        LinearLayout mAppListContainer = (LinearLayout) dialog.findViewById(R.id.app_list);
        Button mChangi = (Button) dialog.findViewById(R.id.btn_ichangi);
        Button mFlightRader = (Button) dialog.findViewById(R.id.btn_flight_rader);
        Button mFlightAware = (Button) dialog.findViewById(R.id.btn_flight_aware);


        mChangi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchApp(package_changi);
                dialog.dismiss();
            }
        });

        mFlightRader.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchApp(package_flight_rader);
                dialog.dismiss();
            }
        });

        mFlightAware.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchApp(package_flight_aware);
                dialog.dismiss();
            }
        });


        dialog.show();

    }

    private void launchApp(String appPackageName) {
        Intent launchIntent = getContext().getPackageManager().getLaunchIntentForPackage(appPackageName);
        if (launchIntent != null) {
            startActivity(launchIntent);//null pointer check in case package name was not found
        } else
            gotoPlayStore(appPackageName);
    }


    private void showCompleteDialog() {
        dialog = new Dialog(getActivity());

        dialog.setContentView(R.layout.dialog_complete);
        dialog.setTitle("");
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));


        TextView date = dialog.findViewById(R.id.date);
        TextView time = dialog.findViewById(R.id.time);
        TextView location = dialog.findViewById(R.id.location);

        date.setText(Util.convertDateToString(Calendar.getInstance().getTime(), "EEE, dd MMM yyyy "));
        time.setText(Util.convertDateToString(Calendar.getInstance().getTime(), "hh:mm a"));

        location.setText(App.fullAddress.replace("#.#", ","));

        Button complete = dialog.findViewById(R.id.complete);

        complete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callCompletedJob();
            }
        });

        // resize dialog
        Rect displayRectangle = new Rect();
        Window window = getActivity().getWindow();
        window.getDecorView().getWindowVisibleDisplayFrame(displayRectangle);

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = (int) (displayRectangle.width() * 0.85f);

        dialog.show();
        dialog.getWindow().setAttributes(lp);
    }

    private void showPassengerNoShowDialog() {
        dialog = new Dialog(getActivity());

        dialog.setContentView(R.layout.dialog_pob);
        dialog.setTitle("");
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        // Inititalize Label
        clear = dialog.findViewById(R.id.clear);
        done = dialog.findViewById(R.id.done);
        photoLabel = dialog.findViewById(R.id.photo_label);
        signatureLabel = dialog.findViewById(R.id.signature_label);
        signaturePad = dialog.findViewById(R.id.signature_pad);
        signatureBackground = dialog.findViewById(R.id.signature_background);
        addPhoto = dialog.findViewById(R.id.add_photo);
        passengerPhoto = dialog.findViewById(R.id.passenger_photo);
        TextView title = dialog.findViewById(R.id.title);
        TextView label = dialog.findViewById(R.id.photo_label);
        TextView remarks = dialog.findViewById(R.id.remarks);

        Button save = dialog.findViewById(R.id.save);

        title.setText("NO SHOW");
        label.setText("TAKE PHOTO");
        remarks.setText(job.getNoShowRemarks().replaceAll("##-##", "\n"));

        // hide signature pad
        signatureLabel.setVisibility(GONE);
        signaturePad.setVisibility(GONE);
        signatureBackground.setVisibility(GONE);
        clear.setVisibility(GONE);
        done.setVisibility(GONE);

        passengerPhoto.setImageResource(0);

        Picasso.get().load(App.CONST_PHOTO_URL + job.getJobNo() + App.CONST_PHOTO_NO_SHOW_FILE_NAME)
                .networkPolicy(NetworkPolicy.NO_CACHE)
                .memoryPolicy(MemoryPolicy.NO_CACHE)
                //    .placeholder(R.drawable.bv_logo_default).stableKey(id)
                .into(passengerPhoto);

        addPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openCamera(REQUEST_NO_SHOW_CAMERA, job.getJobNo());
            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callUpdateNoShowPassenger();
            }
        });

        // resize dialog
        Rect displayRectangle = new Rect();
        Window window = getActivity().getWindow();
        window.getDecorView().getWindowVisibleDisplayFrame(displayRectangle);

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = (int) (displayRectangle.width() * 0.85f);

        dialog.show();
        dialog.getWindow().setAttributes(lp);
    }

    private void showPassengerOnBoardDialog() {

        dialog = new Dialog(getActivity());

        dialog.setContentView(R.layout.dialog_pob);
        dialog.setTitle("");
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));


        // Inititalize Label
        clear = dialog.findViewById(R.id.clear);
        done = dialog.findViewById(R.id.done);
        photoLabel = dialog.findViewById(R.id.photo_label);
        signatureLabel = dialog.findViewById(R.id.signature_label);
        signatureBackground = dialog.findViewById(R.id.signature_background);
        signaturePad = dialog.findViewById(R.id.signature_pad);
        addPhoto = dialog.findViewById(R.id.add_photo);
        passengerPhoto = dialog.findViewById(R.id.passenger_photo);
        //  signaturePhoto = dialog.findViewById(R.id.signature_photo);

        TextView title = dialog.findViewById(R.id.title);
        TextView label = dialog.findViewById(R.id.photo_label);
        TextView remarks = dialog.findViewById(R.id.remarks);

        Button save = dialog.findViewById(R.id.save);

        title.setText("PASSENGER ON BOARD");
        label.setText("TAKE PHOTO");
        remarks.setText(job.getShowRemarks().replaceAll("##-##", "\n"));

        passengerPhoto.setImageResource(0);

        Picasso.get().load(App.CONST_PHOTO_URL + job.getJobNo() + App.CONST_PHOTO_SHOW_FILE_NAME)
                .networkPolicy(NetworkPolicy.NO_CACHE)
                .memoryPolicy(MemoryPolicy.NO_CACHE)
                //    .placeholder(R.drawable.bv_logo_default).stableKey(id)
                .into(passengerPhoto);


        Picasso.get().load(App.CONST_PHOTO_URL + job.getJobNo() + App.CONST_SIGNATURE_FILE_NAME)
                .networkPolicy(NetworkPolicy.NO_CACHE)
                .memoryPolicy(MemoryPolicy.NO_CACHE)
                //    .placeholder(R.drawable.bv_logo_default).stableKey(id)
                .into(new Target() {
                    @Override
                    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                        signaturePad.setSignatureBitmap(bitmap);
                        done.setText("SAVED");
                        signaturePad.setEnabled(false);
                    }

                    @Override
                    public void onBitmapFailed(Exception e, Drawable errorDrawable) {

                    }

                    @Override
                    public void onPrepareLoad(Drawable placeHolderDrawable) {

                    }
                });


        addPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openCamera(REQUEST_SHOW_CAMERA, job.getJobNo());
            }
        });


        clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signaturePad.clear();
                done.setText("DONE");
                signaturePad.setEnabled(true);
            }
        });


        signaturePad.setOnSignedListener(new SignaturePad.OnSignedListener() {
            @Override
            public void onStartSigning() {

            }

            @Override
            public void onSigned() {
                hasSignature = true;
            }

            @Override
            public void onClear() {
                hasSignature = false;
            }
        });


        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (done.getText().toString().equalsIgnoreCase("DONE")) {

                    if(hasSignature){
                        saveSignature(signaturePad.getSignatureBitmap(), null);
                        done.setText("SAVED");
                    }else{
                        AlertDialog dialog = new AlertDialog.Builder(getContext())
                                .setTitle(R.string.AppName)
                                .setMessage("Signature can not be left blank.")
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
            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (done.getText().toString().equalsIgnoreCase("DONE")) {
                    AlertDialog dialog = new AlertDialog.Builder(getContext())
                            .setTitle(R.string.AppName)
                            .setMessage("Either signature is blank or has not been done.")
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            })
                            .create();

                    dialog.setCancelable(false);
                    dialog.setCanceledOnTouchOutside(false);
                    dialog.show();
                }

                if (done.getText().toString().equalsIgnoreCase("SAVED"))
                    callUpdateShowPassenger();

            }
        });

        // resize dialog
        Rect displayRectangle = new Rect();
        Window window = getActivity().getWindow();
        window.getDecorView().getWindowVisibleDisplayFrame(displayRectangle);

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = (int) (displayRectangle.width() * 0.85f);


        togglePlaceHolder(1);

        signatureLabel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                togglePlaceHolder(1);
            }
        });
        photoLabel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                togglePlaceHolder(2);
            }
        });


        dialog.show();
        dialog.getWindow().setAttributes(lp);
    }

    private void togglePlaceHolder(int tab) {
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        if (tab == 1) {
            addPhoto.setVisibility(GONE);
            passengerPhoto.setVisibility(GONE);
            signaturePad.setVisibility(VISIBLE);
            signatureBackground.setVisibility(VISIBLE);
            clear.setVisibility(VISIBLE);
            done.setVisibility(View.VISIBLE);

            // signatureLabel.setLayoutParams(params);
            photoLabel.setTextColor(ContextCompat.getColor(getContext(), R.color.aluminum));
            signatureLabel.setTextColor(ContextCompat.getColor(getContext(), R.color.cell_label));

        } else {

            addPhoto.setVisibility(VISIBLE);
            passengerPhoto.setVisibility(VISIBLE);
            signaturePad.setVisibility(GONE);
            signatureBackground.setVisibility(GONE);
            clear.setVisibility(View.GONE);
            done.setVisibility(View.GONE);

            //photoLabel.setLayoutParams(params);
            photoLabel.setTextColor(ContextCompat.getColor(getContext(), R.color.cell_label));
            signatureLabel.setTextColor(ContextCompat.getColor(getContext(), R.color.aluminum));
        }
    }

    private void callUpdateShowPassenger() {
        App.fullAddress = (App.fullAddress.isEmpty()) ? " " : App.fullAddress;

        EditText ed = dialog.findViewById(R.id.remarks);
        String remark = ed.getText().toString().replaceAll("\n", "##-##");

        Call<JobRes> call = RestClient.COACH().getApiService().UpdateShowConfirmJob(
                job.getJobNo(),
                App.fullAddress,
                Util.replaceEscapeChr(remark),
                "Passenger On Board"
        );

        call.enqueue(new Callback<JobRes>() {
            @Override
            public void onResponse(Call<JobRes> call, Response<JobRes> response) {
                if (response.body().getResponsemessage().equalsIgnoreCase("Success")) {
                    Toast.makeText(getContext(), "Passenger On Board Successful", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();

                    callJobDetail();
                }

                if (response.body().getResponsemessage().equalsIgnoreCase("BAD TOKEN")) {
                    RestClient.refreshToken("UPDATE_SHOW_PASSENGER");
                }
            }

            @Override
            public void onFailure(Call<JobRes> call, Throwable t) {
                dialog.dismiss();
            }
        });
    }

    private void callUpdateNoShowPassenger() {
        App.fullAddress = (App.fullAddress.isEmpty()) ? " " : App.fullAddress;

        EditText ed = (EditText) dialog.findViewById(R.id.remarks);
        String remark = ed.getText().toString().replaceAll("\n", "##-##");

        Call<JobRes> call = RestClient.COACH().getApiService().UpdateNoShowConfirmJob(
                job.getJobNo(),
                App.fullAddress,
                Util.replaceEscapeChr(remark),
                "Passenger No Show"
        );

        call.enqueue(new Callback<JobRes>() {
            @Override
            public void onResponse(Call<JobRes> call, Response<JobRes> response) {
                if (response.body().getResponsemessage().equalsIgnoreCase("Success")) {
                    Toast.makeText(getContext(), "Passenger No Show Successful", Toast.LENGTH_SHORT).show();

                    dialog.dismiss();
                    getActivity().finish();
                    EventBus.getDefault().postSticky("UPDATE_JOB_COUNT");
                }

                if (response.body().getResponsemessage().equalsIgnoreCase("BAD TOKEN")) {
                    RestClient.refreshToken("UPDATE_NO_SHOW");
                }

            }

            @Override
            public void onFailure(Call<JobRes> call, Throwable t) {
                dialog.dismiss();
            }
        });
    }

    private void callCompletedJob() {
        App.fullAddress = (App.fullAddress.isEmpty()) ? " " : App.fullAddress;

        EditText ed = dialog.findViewById(R.id.remarks);
        String remark = ed.getText().toString().replaceAll("\n", "##-##");

        Call<JobRes> call = RestClient.COACH().getApiService().UpdateCompletJob(
                job.getJobNo(),
                App.fullAddress,
                Util.replaceEscapeChr(remark),
                "Completed"
        );

        call.enqueue(new Callback<JobRes>() {
            @Override
            public void onResponse(Call<JobRes> call, Response<JobRes> response) {
                if (response.body().getResponsemessage().equalsIgnoreCase("Success")) {
                    Toast.makeText(getContext(), "Job Complete Successful", Toast.LENGTH_SHORT).show();

                    dialog.dismiss();
                    getActivity().finish();
                    EventBus.getDefault().postSticky("UPDATE_JOB_COUNT");
                }

                if (response.body().getResponsemessage().equalsIgnoreCase("BAD TOKEN")) {
                    RestClient.refreshToken("COMPLETE_JOB");
                }
            }

            @Override
            public void onFailure(Call<JobRes> call, Throwable t) {
                dialog.dismiss();
            }
        });
    }


//    @OnClick(R.id.call1)
//    public void phone1OnClick() {
//        String phoneNo = phoneList.get(0);
//        Uri number = Uri.parse("tel:" + phoneNo);
//        Intent callIntent = new Intent(Intent.ACTION_DIAL, number);
//        startActivity(callIntent);
//
//        Toast.makeText(getContext(), "Phone Call .... to  " + phoneNo, Toast.LENGTH_SHORT).show();
//    }
//
//    @OnClick(R.id.sms1)
//    public void sms1OnClick() {
//        String phoneNo = phoneList.get(0);
//        String msg = "";
//        Uri number = Uri.parse("sms:" + phoneNo);
//        Intent smsIntent = new Intent(Intent.ACTION_VIEW, number);
//        startActivity(smsIntent);
//
//        Toast.makeText(getContext(), "Send SMS .... to  " + phoneNo, Toast.LENGTH_SHORT).show();
//    }


//    @OnClick(R.id.call2)
//    public void phone2OnClick() {
//        String phoneNo = phoneList.get(1);
//        Uri number = Uri.parse("tel:" + phoneNo);
//        Intent callIntent = new Intent(Intent.ACTION_DIAL, number);
//        startActivity(callIntent);
//
//        Toast.makeText(getContext(), "Phone Call .... to  " + phoneNo, Toast.LENGTH_SHORT).show();
//    }
//
//    @OnClick(R.id.sms2)
//    public void sms2OnClick() {
//        String phoneNo = phoneList.get(1);
//        String msg = "";
//        Uri number = Uri.parse("sms:" + phoneNo);
//        Intent smsIntent = new Intent(Intent.ACTION_VIEW, number);
//        startActivity(smsIntent);
//
//        Toast.makeText(getContext(), "Send SMS .... to  " + phoneNo, Toast.LENGTH_SHORT).show();
//    }







    public void openCamera(final int requestCode, final String jobNo) {

        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {

//                File f = new File(Environment.getExternalStorageDirectory(), "temp.jpg");
//                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//                intent.putExtra(MediaStore.EXTRA_OUTPUT, FileProvider.getUriForFile(MainActivity.this, AUTHORITY, f));
//                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

                //Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                //intent.putExtra("filename", fileName);
                startActivityForResult(intent, requestCode);
            }
        });
    }


    @Override
    public void setMenuVisibility(boolean menuVisible) {
        super.setMenuVisibility(menuVisible);
        if (menuVisible) {

        }
    }


//    private void scrollActionButtons(final int buttonIndex) {
//        rootView.post(new Runnable() {
//            @Override
//            public void run() {
//                Log.v("", "Left position of 12th child = " + mHSLayout.getChildAt(buttonIndex).getLeft());
//                mHSView.smoothScrollTo(mHSLayout.getChildAt(buttonIndex).getLeft(), 0);
//            }
//        });
//    }


//    private void displayUpdateStatus(String status) {
//        job.setJobStatus(status);
//        displayJobDetail();
//        changeCurrentStatusButton(status);
//    }

    private void displayJobDetail() {

        Log.e("Detail Fragment", mCurrentTab);

        phoneList = new ArrayList<>();

        if (job.getCustomerTel().trim().length() > 0) {
            String p[] = job.getCustomerTel().trim().split("/");
            for (String s : p) {
                phoneList.add(s.trim());
            }
        }


        ContactAdapter contactAdapter = new ContactAdapter(phoneList, getContext());
        mContactList.setAdapter(contactAdapter);
        setListViewHeightBasedOnItems(mContactList);

//        if (phoneList.size() == 0) {
//            mPhoneLayout1.setVisibility(View.GONE);
//            mPhoneLayout2.setVisibility(View.GONE);
//        }
//
//        if (phoneList.size() == 1) {
//            mPhoneLayout1.setVisibility(View.VISIBLE);
//            mPhoneLayout2.setVisibility(View.GONE);
//
//            mMobile1.setText(phoneList.get(0));
//        }
//
//        if (phoneList.size() == 2) {
//            mPhoneLayout1.setVisibility(View.VISIBLE);
//            mPhoneLayout2.setVisibility(View.VISIBLE);
//
//            mMobile1.setText(phoneList.get(0));
//            mMobile2.setText(phoneList.get(1));
//        }


        if (mCurrentTab.equalsIgnoreCase("HISTORY")) {
//            mPhoneLayout1.setVisibility(GONE);
//            mPhoneLayout2.setVisibility(GONE);

            mActionLayout.setVisibility(GONE);
            //mHSView.setVisibility(GONE);
        }

        // UAE
        if (job.getJobType().equalsIgnoreCase("MEDICAL") || job.getJobType().equalsIgnoreCase("UAE")) {

//            // File No Show/Hide
//            if (job.getFileNo() != null && job.getFileNo().length() > 0) {
//                mFileNo.setText(job.getFileNo());
//
//            } else {
//                mLabelFileNo.setVisibility(View.GONE);
//                mFileNo.setVisibility(View.GONE);
//            }

            mFileNo.setText(job.getFileNo());
            mLabelFileNo.setVisibility(VISIBLE);
            mFileNo.setVisibility(VISIBLE);

            mLayoutPax.setVisibility(View.GONE);
            mDividerPax.setVisibility(View.GONE);


            //  mLayoutFlight.setVisibility(GONE);
            mLabelFlightNo.setVisibility(GONE);
            mFlightNo.setVisibility(GONE);

            mLabelETA.setVisibility(GONE);
            mETA.setVisibility(GONE);

        } else { // NOT UAE  (MEDICAL)

            mLabelFileNo.setVisibility(GONE);
            mFileNo.setVisibility(GONE);

            mLabelFlightNo.setText("FLIGHT NO");
            mFlightNo.setText(job.getFlight());

            mLabelETA.setVisibility(VISIBLE);
            mETA.setVisibility(VISIBLE);

            // PAX
            int adult = Integer.parseInt(job.getNoOfAdult());
            int child = Integer.parseInt(job.getNoOfChild());
            int infant = Integer.parseInt(job.getNoOfInfant());

            mNoOfAdult.setText(job.getNoOfAdult());
            mNoOfChild.setText(job.getNoOfChild());
            mNoOfInfant.setText(job.getNoOfInfant());

            if (adult > 0 || child > 0 || infant > 0) {
                mLayoutPax.setVisibility(VISIBLE);
                mDividerPax.setVisibility(VISIBLE);
            } else {
                mLayoutPax.setVisibility(View.GONE);
                mDividerPax.setVisibility(View.GONE);
            }
        }


        // DISPOSAL & FLIGHT NO NULL?
        // UAE
        if (job.getJobType().equalsIgnoreCase("DISPOSAL") && job.getFlight().length() == 0) {
            mLayoutFlight.setVisibility(GONE);

            mLabelFlightNo.setVisibility(GONE);
            mFlightNo.setVisibility(GONE);

            mLabelETA.setVisibility(GONE);
            mETA.setVisibility(GONE);
        }


        // ARRIVAL
        mLabelETA.setText(
                (job.getJobType().
                        equalsIgnoreCase("ARRIVAL")) ?
                        "ETA" : "ETD"
        );


        if (job.getJobType().equalsIgnoreCase("ARRIVAL") || job.getJobType().equalsIgnoreCase("DISPOSAL")) {
            mLabelETA.setText("ETA");
        }


//        if (job.getRemarks() == null || job.getRemarks().length() == 0) {
//            mRemarks.setVisibility(GONE);
//            mLayoutRemarks.setVisibility(GONE);
//            mLineRemarks.setVisibility(GONE);
//        }

        // Set Mobile Numbers


        //  mMobile.setText(job.getCustomerTel());


        // check data on FlightNo, ETA
        if (Util.isNullOrEmpty(job.getFlight())) {
            mLabelFlightNo.setVisibility(GONE);
            mFlightNo.setVisibility(GONE);
        }

        if (Util.isNullOrEmpty(job.getFlight()) || job.getETA().equalsIgnoreCase("00:00")) {
            mLabelETA.setVisibility(GONE);
            mETA.setVisibility(GONE);
        }

        mJobNo.setText(job.getJobNo());
       // mJobType.setText(Util.capitalize(job.getJobType()));
        mJobType.setText(job.getJobType());

        mJobStatus.setText(job.getJobStatus().equalsIgnoreCase("JOB NEW") ? "JOB ASSIGNED" : job.getJobStatus().toUpperCase());
        mDate.setText(job.getUsageDate());
        mTime.setText(job.getPickUpTime());

        mStaff.setText(job.getStaff());

        mPassenger.setText(job.getCustomer());
        mPassenger.setVisibility(VISIBLE);

        mETA.setText(job.getETA());
        //  mType.setText(job.getVehicleType());
        mPickup.setText(job.getPickUp());
        mDropOff.setText(job.getDestination());
        mRemarks.setText(job.getRemarks());
        mVehicleType.setText(job.getVehicleType());
        // mUpdate.setText(job.getRemark());


        if (job.getFile1().isEmpty()) {
            mItinerary.setVisibility(GONE);
            //  mLineItinerary.setVisibility(GONE);
        } else {
            mItinerary.setVisibility(VISIBLE);
            //  mLineItinerary.setVisibility(View.VISIBLE);
        }

    }

    @OnClick(R.id.itinerary)
    public void itineraryOnClick() {

        Uri uri = Uri.parse(App.CONST_PDF_URL + job.getFile1().toString().trim());

        // create an intent builder
        CustomTabsIntent.Builder intentBuilder = new CustomTabsIntent.Builder();

        // Begin customizing
        // set toolbar colors
        intentBuilder.setToolbarColor(ContextCompat.getColor(getActivity().getBaseContext(), R.color.colorPrimary));
        intentBuilder.setSecondaryToolbarColor(ContextCompat.getColor(getActivity().getBaseContext(), R.color.colorPrimaryDark));

        // set start and exit animations
        //            intentBuilder.setStartAnimations(this, R.anim.slide_in_right, R.anim.slide_out_left);
        //            intentBuilder.setExitAnimations(this, android.R.anim.slide_in_left,
        //                    android.R.anim.slide_out_right);

        // build custom tabs intent
        CustomTabsIntent customTabsIntent = intentBuilder.build();
        customTabsIntent.intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        // launch the url
        customTabsIntent.launchUrl(getActivity().getBaseContext(), uri);


//        CustomTabsIntent customTabsIntent = new CustomTabsIntent.Builder().build();
//        CustomTabActivityHelper.openCustomTab(
//                this,// activity
//                customTabsIntent,
//                Uri.parse("http://www.google.com"),
//                new WebviewFallback()
//        );

    }


    @OnClick(R.id.accept)
    public void acceptOnClick() {
        updateJobStatus("Confirm");
        callUpdateDriverLocation();
    }

    @OnClick(R.id.reject)
    public void rejectOnClick() {
        updateJobStatus("Rejected");
        callUpdateDriverLocation();
    }

    private void updateJobStatus(final String status) {
        App.fullAddress = (App.fullAddress.isEmpty()) ? " " : App.fullAddress;


//        Util.copyToClipboard(getActivity().getBaseContext(), App.fullAddress);
//        Toast.makeText(getActivity().getBaseContext(), "Address Copied", Toast.LENGTH_SHORT).show();

//        AlertDialog alertDialog = new AlertDialog.Builder(getContext()).create();
//        alertDialog.setTitle("Your Full Address #33");
//        alertDialog.setMessage(App.fullAddress);
//        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
//                new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int which) {
//                        dialog.dismiss();
//                    }
//                });
//        alertDialog.show();


        //  App.fullAddress = "FullAddress";

        //  Log.e("Address Update", App.fullAddress);

        Call<JobRes> call = RestClient.COACH().getApiService().UpdateJobStatus(
                job.getJobNo(),
                App.fullAddress,
                status
        );

        call.enqueue(new Callback<JobRes>() {
            @Override
            public void onResponse(Call<JobRes> call, Response<JobRes> response) {

                if (response.body().getResponsemessage().equalsIgnoreCase("Success")) {
                    Log.e("Update Job Successful", response.toString());

                    Toast.makeText(getContext(), "Update Successful", Toast.LENGTH_SHORT).show();

                    callJobDetail();

                    EventBus.getDefault().postSticky("UPDATE_JOB_COUNT");

                    if (status.equalsIgnoreCase("REJECTED"))
                        getActivity().finish();

                }

                if (response.body().getResponsemessage().equalsIgnoreCase("BAD TOKEN")) {
                    RestClient.refreshToken("ACTION_" + status);
                }
            }

            @Override
            public void onFailure(Call<JobRes> call, Throwable t) {
                Log.e("Update Job Failed ", t.getMessage());
            }
        });
    }

    public void onButtonPressed(Uri uri) {

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }

    @Override
    public void onDetach() {
        super.onDetach();
    }


    public void showNavAppSelectionDialog(final double lat, final double lng, final String address) {
        Button mGoogleMap, mWaze;

        final Dialog dialog = new Dialog(getContext());
        dialog.setContentView(R.layout.dialog_navigation_app_selection);
        dialog.setTitle("");

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        Window window = dialog.getWindow();
        window.setAttributes(lp);
        window.setGravity(Gravity.CENTER);

        //adding dialog animation sliding up and down
        //dialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);

        dialog.setCanceledOnTouchOutside(true);
        dialog.setCancelable(true);
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation_Fade;

        mGoogleMap = (Button) dialog.findViewById(R.id.btn_google_map);
        mWaze = (Button) dialog.findViewById(R.id.btn_waze);


        if (!hasGoogleMap())
            mGoogleMap.setVisibility(GONE);

        if (!hasWaze())
            mWaze.setVisibility(GONE);


        //final String tmpAddress = "Silver Green Hotel, No.9 ANawYaHtar Road, Yangon";

        mGoogleMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                if (hasGoogleMap())
                    getGeocodingAndRoute("GMAP", address);
                else
                    gotoPlayStore("com.google.android.apps.maps");


            }
        });

        mWaze.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                if (hasWaze())
                    getGeocodingAndRoute("WAZE", address);
                else
                    gotoPlayStore("com.waze");
            }
        });

        dialog.show();
    }

    public boolean hasGoogleMap() {
        try {
            String gMap = "com.google.android.apps.maps";
            Drawable gMapIcon = getContext().getPackageManager().getApplicationIcon(gMap);

            // Assign icon
            //  mGoogleMap.setBackground(gMapIcon);
            return true;
        } catch (PackageManager.NameNotFoundException ne) {
            return false;
        }
    }

    public boolean hasWaze() {
        try {
            String waze = "com.waze";
            Drawable wazeIcon = getContext().getPackageManager().getApplicationIcon(waze);

            // Assign icon
            // mWaze.setBackground(wazeIcon);
            return true;
        } catch (PackageManager.NameNotFoundException ne) {
            return false;
        }
    }

    private void getGeocodingAndRoute(String provider, String address) {
        GeocodingLocation locationAddress = new GeocodingLocation();

        GeocoderHandler geocoderHandler = new GeocoderHandler();
        geocoderHandler.provider = provider;

        locationAddress.getAddressFromLocation(address,
                getContext(), geocoderHandler);

    }

    public void showRouteOnGoogleMap(String address) {
        String uri = "http://maps.google.com/maps?saddr=" +
                App.location.getLatitude() + "," +
                App.location.getLongitude() + "&daddr=" +
                address;

        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
        intent.setPackage("com.google.android.apps.maps");
        startActivity(intent);
    }


    public void showRouteOnWaze(String address, Location location) {
//        String uri = "waze://?ll=" + lat + "," + lng +
//                "&navigate=yes";

        String uri = "https://waze.com/ul?q=" + address + "&ll=" +
                location.getLatitude() + "," +
                location.getLongitude() +
                "&navigate=yes";

        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
        intent.setPackage("com.waze");
        startActivity(intent);
    }

    public void gotoPlayStore(String appPackageName) {
        try {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
        } catch (android.content.ActivityNotFoundException anfe) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
        }
    }


    @Subscribe(sticky = false)
    public void onEvent(Action action) {


        if (action.getAction().equalsIgnoreCase("UNASSIGN") && action.getJobNo().equalsIgnoreCase(App.jobList.get(index).getJobNo())) {
            getActivity().finish();
        }

        if (action.getAction().equalsIgnoreCase("REFRESH") && action.getJobNo().equalsIgnoreCase(job.getJobNo()))
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    showRefreshDialog();
                    Log.e("Msg Received", "REFRESH");
                }
            });

    }


    private void showRefreshDialog() {

        AlertDialog dialog = new AlertDialog.Builder(getContext())
                .setTitle(R.string.AppName)
                .setMessage("Details for this job may have changed.\nClick OK to refresh.")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        callJobDetail();
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .create();


        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }


    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        if (dialog != null && dialog.isShowing()) {
            Rect displayRectangle = new Rect();
            Window window = getActivity().getWindow();
            window.getDecorView().getWindowVisibleDisplayFrame(displayRectangle);

            WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
            lp.copyFrom(dialog.getWindow().getAttributes());
            lp.width = (int) (displayRectangle.width() * 1f);

            // Checks the orientation of the screen
            if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {

            } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {

            }

            dialog.getWindow().setAttributes(lp);
        }
    }

    private void callJobDetail() {
        Call<JobRes> call = RestClient.COACH().getApiService().GetJobDetails(
                job.getJobNo()
        );

        call.enqueue(new Callback<JobRes>() {
            @Override
            public void onResponse(Call<JobRes> call, Response<JobRes> response) {
                if (response.body().getResponsemessage().equalsIgnoreCase("Success") && response.body().getJobdetails() != null) {
                    job = response.body().getJobdetails();

                    displayJobDetail();

                    changeCurrentStatusButton(job.getJobStatus());
                }

                if (response.body().getResponsemessage().equalsIgnoreCase("BAD TOKEN")) {
                    RestClient.refreshToken("GET_JOB_DETAIL");
                }
            }

            @Override
            public void onFailure(Call<JobRes> call, Throwable t) {

            }
        });
    }

    private class GeocoderHandler extends Handler {
        Double lat, lng;
        String address = "";
        public String provider = "";
        Location location = new Location("MyProvider");

        @Override
        public void handleMessage(Message message) {

            switch (message.what) {
                case 1:
                    Bundle bundle = message.getData();
                    address = bundle.getString("address");

                    String[] loc = address.split(":");

                    address = loc[1].split("\n")[0];
                    loc = loc[2].split("\n");

                    location.setLatitude(Double.valueOf(loc[1]));
                    location.setLongitude(Double.valueOf(loc[2]));

                    if (provider.equalsIgnoreCase("WAZE"))
                        showRouteOnWaze(address, location);

                    if (provider.equalsIgnoreCase("GMAP"))
                        showRouteOnGoogleMap(address);

                    Log.e("LOC : ", loc[1].toString());

                    break;
                default:
                    location = null;
            }

        }

    }


    // Signature Related ==================================================
    public void saveSignature(Bitmap photo, String callBack) {
        try {

//            String filename = job.getJobNo() + "_sig_"
//                    + Util.convertLongDateToString(System.currentTimeMillis(), "MMddyyyy_hhss")
//                    + ".jpg";

            String filename = job.getJobNo() +  App.CONST_SIGNATURE_FILE_NAME;


             photo = getResizedBitmap(photo, 400);

//            // CALL THIS METHOD TO GET THE URI FROM THE BITMAP
//            Uri tempUri = getImageUri(getContext(), photo);
//
//            // CALL THIS METHOD TO GET THE ACTUAL PATH
//            File finalFile = new File(getRealPathFromURI(getContext(), tempUri));
//
//            InputStream inputStream = getContext().getContentResolver().openInputStream(tempUri);

            InputStream inputStream = bitmap2InputStream(photo);

            FtpHelper.uploadTask async = new FtpHelper.uploadTask(getActivity(), inputStream);
            async.execute(App.FTP_URL, App.FTP_USER, App.FTP_PASSWORD, App.FTP_DIR, filename, job.getJobNo(), "SIGNATURE", callBack);   //Passing arguments to AsyncThread


        } catch (Exception e) {
            Log.e("FTP Error : ", e.getLocalizedMessage().toString());
        }
    }


    public static InputStream bitmap2InputStream(Bitmap bm) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        InputStream is = new ByteArrayInputStream(baos.toByteArray());
        return is;
    }/*from   ww w  .ja v a2 s .co  m*/


    public Bitmap getResizedBitmap(Bitmap image, int maxSize) {
        int width = image.getWidth();
        int height = image.getHeight();

        float bitmapRatio = (float) width / (float) height;
        if (bitmapRatio > 1) {
            width = maxSize;
            height = (int) (width / bitmapRatio);
        } else {
            height = maxSize;
            width = (int) (height * bitmapRatio);
        }
        return Bitmap.createScaledBitmap(image, width, height, true);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_EXTERNAL_STORAGE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length <= 0
                        || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(getContext(), "Cannot write images to external storage", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    public File getAlbumStorageDir(String albumName) {
        // Get the directory for the user's public pictures directory.
        File file = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), albumName);
        if (!file.mkdirs()) {
            Log.e("SignaturePad", "Directory not created");
        }
        return file;
    }

    public void saveBitmapToJPG(Bitmap bitmap, File photo) throws IOException {
        Bitmap newBitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(newBitmap);
        canvas.drawColor(Color.WHITE);
        canvas.drawBitmap(bitmap, 0, 0, null);
        OutputStream stream = new FileOutputStream(photo);
        newBitmap.compress(Bitmap.CompressFormat.JPEG, 80, stream);
        stream.close();
    }

    public boolean addJpgSignatureToGallery(Bitmap signature) {
        boolean result = false;
        try {
            File photo = new File(getAlbumStorageDir("SignaturePad"), String.format("Signature_%d.jpg", System.currentTimeMillis()));
            saveBitmapToJPG(signature, photo);
            scanMediaFile(photo);
            result = true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    private void scanMediaFile(File photo) {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        Uri contentUri = Uri.fromFile(photo);
        mediaScanIntent.setData(contentUri);
        getContext().sendBroadcast(mediaScanIntent);
    }

    public boolean addSvgSignatureToGallery(String signatureSvg) {
        boolean result = false;
        try {
            File svgFile = new File(getAlbumStorageDir("SignaturePad"), String.format("Signature_%d.svg", System.currentTimeMillis()));
            OutputStream stream = new FileOutputStream(svgFile);
            OutputStreamWriter writer = new OutputStreamWriter(stream);
            writer.write(signatureSvg);
            writer.close();
            stream.flush();
            stream.close();
            scanMediaFile(svgFile);
            result = true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }


    public static void verifyStoragePermissions(Activity activity) {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }
    }


    private void playNextBeep() {
        toneGen.startTone(ToneGenerator.TONE_CDMA_ALERT_INCALL_LITE, 100);
        toneGen.startTone(ToneGenerator.TONE_CDMA_ABBR_ALERT, 20);
    }

    private void playBackBeep() {
        toneGen.startTone(ToneGenerator.TONE_CDMA_ABBR_ALERT, 100);
        toneGen.startTone(ToneGenerator.TONE_CDMA_ALERT_INCALL_LITE, 20);
    }


    public static boolean setListViewHeightBasedOnItems(ListView listView) {

        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter != null) {

            int numberOfItems = listAdapter.getCount();

            // Get total height of all items.
            int totalItemsHeight = 0;
            for (int itemPos = 0; itemPos < numberOfItems; itemPos++) {
                View item = listAdapter.getView(itemPos, null, listView);
                item.measure(0, 0);
                totalItemsHeight += item.getMeasuredHeight();
            }

            // Get total height of all item dividers.
            int totalDividersHeight = listView.getDividerHeight() *
                    (numberOfItems - 1);

            // Set list height.
            ViewGroup.LayoutParams params = listView.getLayoutParams();
            params.height = totalItemsHeight + totalDividersHeight;
            listView.setLayoutParams(params);
            listView.requestLayout();

            return true;

        } else {
            return false;
        }

    }

    @Subscribe(sticky = true)
    public void onEvent(String action) {
        ;

        switch (action.toUpperCase()) {
            case "SIGNATURE_UPLOAD_FAILED":
                // Error in signature uploading.
                AlertDialog sigDialog = new AlertDialog.Builder(getContext())
                        .setTitle(R.string.AppName)
                        .setMessage("Error in signature uploading.")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                done.setText("DONE");
                            }
                        })
                        .create();

                sigDialog.show();
                break;

            case "SHOW_UPLOAD_FAILED":
            case "NOSHOW_UPLOAD_FAILED":
                // Error in Photo uploading.
                AlertDialog photoDialog = new AlertDialog.Builder(getContext())
                        .setTitle(R.string.AppName)
                        .setMessage("Error in photo uploading.")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                // done.setText("DONE");
                            }
                        })
                        .create();

                photoDialog.show();
                break;
        }

        if (action.equalsIgnoreCase("GET_JOB_DETAIL")) {
            callJobDetail();
        }

        if (action.indexOf("ACTION") > -1) {
            updateJobStatus(action.replace("ACTION_", ""));
        }

        if (action.equalsIgnoreCase("UPDATE_SHOW_PASSENGER")) {
            callUpdateShowPassenger();

            // validate photo already exit.
//            if (passengerPhoto.getDrawable() == null) {
//                AlertDialog dialog = new AlertDialog.Builder(getContext())
//                        .setTitle(R.string.AppName)
//                        .setMessage("Please take a photo before submit.")
//                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
//                                dialog.dismiss();
//                            }
//                        })
//                        .create();
//
//                dialog.setCancelable(false);
//                dialog.setCanceledOnTouchOutside(false);
//                dialog.show();
//
//            }else{
//                 callUpdateShowPassenger();
//            }


        }

        if (action.equalsIgnoreCase("UPDATE_NO_SHOW")) {
            callUpdateNoShowPassenger();
        }

        if (action.equalsIgnoreCase("COMPLETE_JOB")) {
            callCompletedJob();
        }


//        if (action.equalsIgnoreCase("GET_JOB_DETAIL")) {
//            callJobDetail();
//        }
    }
}

