package com.info121.nativelimo.fragments;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.customtabs.CustomTabsIntent;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
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
import android.widget.TextView;
import android.widget.Toast;

import com.github.gcacace.signaturepad.views.SignaturePad;
import com.info121.nativelimo.R;
import com.info121.nativelimo.AbstractFragment;
import com.info121.nativelimo.App;
import com.info121.nativelimo.api.RestClient;
import com.info121.nativelimo.models.Action;
import com.info121.nativelimo.models.Job;
import com.info121.nativelimo.models.JobRes;
import com.info121.nativelimo.utils.FtpHelper;
import com.info121.nativelimo.utils.GeocodingLocation;
import com.info121.nativelimo.utils.Util;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.app.Activity.RESULT_OK;
import static android.view.View.GONE;
import static com.info121.nativelimo.utils.FtpHelper.getImageUri;
import static com.info121.nativelimo.utils.FtpHelper.getRealPathFromURI;


public class JobDetailFragment extends AbstractFragment {
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {Manifest.permission.WRITE_EXTERNAL_STORAGE};

    Job job;
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

    @BindView(R.id.eta)
    TextView mETA;

    @BindView(R.id.pickup)
    TextView mPickup;

    @BindView(R.id.dropoff)
    TextView mDropOff;

    @BindView(R.id.remarks)
    TextView mRemarks;

    @BindView(R.id.assign_layout)
    LinearLayout mAssignLayout;

    @BindView(R.id.update_layout)
    LinearLayout mUpdateLayout;

    @BindView(R.id.update_status)
    Button mUpdateStatus;

    @BindView(R.id.itinerary)
    Button mItinerary;

    @BindView(R.id.label_eta)
    TextView mLabelETA;

    @BindView(R.id.label_flight_no)
    TextView mLabelFlightNo;


    @BindView(R.id.phone_layout1)
    LinearLayout mPhoneLayout1;

    @BindView(R.id.phone_layout2)
    LinearLayout mPhoneLayout2;

    @BindView(R.id.mobile1)
    TextView mMobile1;

    @BindView(R.id.mobile2)
    TextView mMobile2;


    @BindView(R.id.layout_flight)
    LinearLayout mLayoutFlight;

    @BindView(R.id.hs_view)
    HorizontalScrollView mHSView;

    @BindView(R.id.hs_root_layout)
    LinearLayout mHSLayout;

    View rootView;


    TextView photoLabel, signatureLabel, clear, done;
    ImageView addPhoto, passengerPhoto;
    SignaturePad signaturePad;


    Boolean visible = false;

    public JobDetailFragment() {
        // Required empty public constructor
    }


    public static JobDetailFragment newInstance(Job job, String currentTab) {
        JobDetailFragment fragment = new JobDetailFragment();
        Bundle args = new Bundle();

        fragment.mCurrentTab = currentTab;
        fragment.job = job;
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_job_detail, container, false);

        ButterKnife.bind(this, rootView);

        if (job.getJobStatus().equalsIgnoreCase("JOB ASSIGNED")) {
            mAssignLayout.setVisibility(View.VISIBLE);
            mUpdateLayout.setVisibility(GONE);
        } else {
            mAssignLayout.setVisibility(GONE);
            mUpdateLayout.setVisibility(View.VISIBLE);
        }


        return rootView;
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

    @OnClick(R.id.update_status)
    public void updateStatusOnClick() {

        App.fullAddress = (App.fullAddress.isEmpty()) ? " " : App.fullAddress;

        dialog = new Dialog(getContext());

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        dialog.setContentView(R.layout.dialog_update_job);
        dialog.setTitle("");

        LinearLayout confirm = dialog.findViewById(R.id.confirm);
        LinearLayout otw = dialog.findViewById(R.id.otw);
        LinearLayout onSite = dialog.findViewById(R.id.on_site);
        LinearLayout pob = dialog.findViewById(R.id.pob);
        LinearLayout pns = dialog.findViewById(R.id.pns);
        LinearLayout cmpl = dialog.findViewById(R.id.cmpl);


        switch (job.getJobStatus().toUpperCase()) {
            case "CONFIRM":
                changeUpdateButtonBackground(otw, true);
                break;

            case "ON THE WAY":
                changeUpdateButtonBackground(onSite, true);
                break;

            case "ON SITE":
                changeUpdateButtonBackground(pob, true);
                break;

            case "PASSENGER ON BOARD":
                changeUpdateButtonBackground(cmpl, true);
                break;

//            case "PASSENGER NO SHOW":
//                changeUpdateButtonBackground(cnpl, true);
//                break;

//            case "COMPLETE":
//                changeUpdateButtonBackground(cnpl, true);
//                break;
        }

        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateJobStatus("Confirm");
                dialog.dismiss();
            }
        });

        otw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateJobStatus("On The Way");
                dialog.dismiss();
            }
        });

        onSite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateJobStatus("On Site");
                dialog.dismiss();
            }
        });

        pob.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                showPassengerOnBoardDialog();

            }
        });

        pns.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                showPassengerNoShowDialog();
            }
        });

        cmpl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                showCompleteDialog();
            }
        });

        dialog.show();

        // call update driver location when dialog dismiss
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                callUpdateDriverLocation();
            }
        });

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

        date.setText(job.getUsageDate());
        time.setText(job.getPickUpTime());
        location.setText(job.getLocation());

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
        addPhoto = dialog.findViewById(R.id.add_photo);
        passengerPhoto = dialog.findViewById(R.id.passenger_photo);
        TextView title = dialog.findViewById(R.id.title);
        TextView label = dialog.findViewById(R.id.photo_label);
        TextView remarks = dialog.findViewById(R.id.remarks);

        Button save = dialog.findViewById(R.id.save);

        title.setText("PASSENGER NO SHOW");
        label.setText("PHOTO");
        remarks.setText(job.getNoShowRemarks());

        // hide signature pad
        signatureLabel.setVisibility(GONE);
        signaturePad.setVisibility(GONE);
        clear.setVisibility(GONE);
        done.setVisibility(GONE);

        passengerPhoto.setImageResource(0);
        Picasso.get().load(App.CONST_PHOTO_URL + job.getNoShowPhoto())
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
        signaturePad = dialog.findViewById(R.id.signature_pad);
        addPhoto = dialog.findViewById(R.id.add_photo);
        passengerPhoto = dialog.findViewById(R.id.passenger_photo);
        TextView title = dialog.findViewById(R.id.title);
        TextView label = dialog.findViewById(R.id.photo_label);
        TextView remarks = dialog.findViewById(R.id.remarks);

        Button save = dialog.findViewById(R.id.save);

        title.setText("PASSENGER ON BOARD");
        label.setText("PASSENGER PHOTO");
        remarks.setText(job.getShowRemarks());

        passengerPhoto.setImageResource(0);

        Picasso.get().load(App.CONST_PHOTO_URL + job.getShowPhoto())
                .networkPolicy(NetworkPolicy.NO_CACHE)
                .memoryPolicy(MemoryPolicy.NO_CACHE)
                //    .placeholder(R.drawable.bv_logo_default).stableKey(id)
                .into(passengerPhoto);


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
            }
        });

        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bitmap signatureBitmap = signaturePad.getSignatureBitmap();
                saveSignature(signatureBitmap);
            }
        });


        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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

        photoLabel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                togglePlaceHolder(1);
            }
        });

        signatureLabel.setOnClickListener(new View.OnClickListener() {
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
            addPhoto.setVisibility(View.VISIBLE);
            passengerPhoto.setVisibility(View.VISIBLE);
            signaturePad.setVisibility(GONE);
            clear.setVisibility(View.GONE);
            done.setVisibility(View.GONE);

            //photoLabel.setLayoutParams(params);
            photoLabel.setTextColor(ContextCompat.getColor(getContext(), R.color.cell_label));
            signatureLabel.setTextColor(ContextCompat.getColor(getContext(), R.color.aluminum));
        } else {
            addPhoto.setVisibility(GONE);
            passengerPhoto.setVisibility(GONE);
            signaturePad.setVisibility(View.VISIBLE);
            clear.setVisibility(View.VISIBLE);
            done.setVisibility(View.VISIBLE);

            // signatureLabel.setLayoutParams(params);
            photoLabel.setTextColor(ContextCompat.getColor(getContext(), R.color.aluminum));
            signatureLabel.setTextColor(ContextCompat.getColor(getContext(), R.color.cell_label));
        }
    }

    private void callUpdateShowPassenger() {
        EditText remark = dialog.findViewById(R.id.remarks);
        Call<JobRes> call = RestClient.COACH().getApiService().UpdateShowConfirmJob(
                job.getJobNo(),
                App.fullAddress,
                Util.replaceEscapeChr(remark.getText().toString()),
                "Passenger On Board"
        );

        call.enqueue(new Callback<JobRes>() {
            @Override
            public void onResponse(Call<JobRes> call, Response<JobRes> response) {
                Toast.makeText(getContext(), "Passenger On Board Successful", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
                displayUpdateStatus("Passenger On Board");

                callJobDetail();
            }

            @Override
            public void onFailure(Call<JobRes> call, Throwable t) {
                dialog.dismiss();
            }
        });
    }

    private void callUpdateNoShowPassenger() {
        EditText remark = dialog.findViewById(R.id.remarks);
        Call<JobRes> call = RestClient.COACH().getApiService().UpdateNoShowConfirmJob(
                job.getJobNo(),
                App.fullAddress,
                Util.replaceEscapeChr(remark.getText().toString()),
                "Passenger No Show"
        );

        call.enqueue(new Callback<JobRes>() {
            @Override
            public void onResponse(Call<JobRes> call, Response<JobRes> response) {
                Toast.makeText(getContext(), "Passenger No Show Successful", Toast.LENGTH_SHORT).show();

                dialog.dismiss();
                getActivity().finish();
                EventBus.getDefault().postSticky("UPDATE_JOB_COUNT");

            }

            @Override
            public void onFailure(Call<JobRes> call, Throwable t) {
                dialog.dismiss();
            }
        });
    }

    private void callCompletedJob() {
        EditText remark = dialog.findViewById(R.id.remarks);
        Call<JobRes> call = RestClient.COACH().getApiService().UpdateCompletJob(
                job.getJobNo(),
                App.fullAddress,
                Util.replaceEscapeChr(remark.getText().toString()),
                "Completed"
        );

        call.enqueue(new Callback<JobRes>() {
            @Override
            public void onResponse(Call<JobRes> call, Response<JobRes> response) {
                Toast.makeText(getContext(), "Job Complete Successful", Toast.LENGTH_SHORT).show();

                dialog.dismiss();
                getActivity().finish();
                EventBus.getDefault().postSticky("UPDATE_JOB_COUNT");
            }

            @Override
            public void onFailure(Call<JobRes> call, Throwable t) {
                dialog.dismiss();
            }
        });
    }


    @OnClick(R.id.call1)
    public void phone1OnClick() {
        String phoneNo = phoneList.get(0);
        Uri number = Uri.parse("tel:" + phoneNo);
        Intent callIntent = new Intent(Intent.ACTION_DIAL, number);
        startActivity(callIntent);

        Toast.makeText(getContext(), "Phone Call .... to  " + phoneNo, Toast.LENGTH_SHORT).show();
    }

    @OnClick(R.id.sms1)
    public void sms1OnClick() {
        String phoneNo = phoneList.get(0);
        String msg = "";
        Uri number = Uri.parse("sms:" + phoneNo);
        Intent smsIntent = new Intent(Intent.ACTION_VIEW, number);
        startActivity(smsIntent);

        Toast.makeText(getContext(), "Send SMS .... to  " + phoneNo, Toast.LENGTH_SHORT).show();
    }


    @OnClick(R.id.call2)
    public void phone2OnClick() {
        String phoneNo = phoneList.get(1);
        Uri number = Uri.parse("tel:" + phoneNo);
        Intent callIntent = new Intent(Intent.ACTION_DIAL, number);
        startActivity(callIntent);

        Toast.makeText(getContext(), "Phone Call .... to  " + phoneNo, Toast.LENGTH_SHORT).show();
    }

    @OnClick(R.id.sms2)
    public void sms2OnClick() {
        String phoneNo = phoneList.get(1);
        String msg = "";
        Uri number = Uri.parse("sms:" + phoneNo);
        Intent smsIntent = new Intent(Intent.ACTION_VIEW, number);
        startActivity(smsIntent);

        Toast.makeText(getContext(), "Send SMS .... to  " + phoneNo, Toast.LENGTH_SHORT).show();
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            try {

                Bundle extras = data.getExtras();

                Bitmap photo = (Bitmap) data.getExtras().get("data");

                // display passenger photo
                ImageView passenger_photo = dialog.findViewById(R.id.passenger_photo);
                passenger_photo.setImageBitmap(photo);

                // CALL THIS METHOD TO GET THE URI FROM THE BITMAP

                Uri tempUri = getImageUri(getActivity().getApplicationContext(), photo);

                // CALL THIS METHOD TO GET THE ACTUAL PATH
                File finalFile = new File(getRealPathFromURI(getActivity().getApplicationContext(), tempUri));

                InputStream inputStream = getActivity().getContentResolver().openInputStream(tempUri);


                // FTP Uploading
                FtpHelper.uploadTask async = new FtpHelper.uploadTask(getContext(), inputStream);

                if (requestCode == REQUEST_SHOW_CAMERA)
                    async.execute(App.FTP_URL,
                            App.FTP_USER,
                            App.FTP_PASSWORD,
                            App.FTP_DIR,
                            job.getJobNo() + "_show.jpg",
                            job.getJobNo(),
                            "SHOW");   //Passing arguments to AsyncThread

                if (requestCode == REQUEST_NO_SHOW_CAMERA)
                    async.execute(App.FTP_URL,
                            App.FTP_USER,
                            App.FTP_PASSWORD,
                            App.FTP_DIR,
                            job.getJobNo() + "_no_show.jpg",
                            job.getJobNo(),
                            "NOSHOW");

            } catch (Exception e) {
                Log.e("Camera Error : ", e.getLocalizedMessage().toString());
            }
        }
    }


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


    private void changeUpdateButtonBackground(LinearLayout layout, boolean selected) {
        final int sdk = android.os.Build.VERSION.SDK_INT;

        if (sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
            layout.setBackgroundDrawable(ContextCompat.getDrawable(getContext(),
                    selected ? R.drawable.rounded_button_orange : R.drawable.rounded_button
            ));
        } else {
            layout.setBackground(ContextCompat.getDrawable(getContext(),
                    selected ? R.drawable.rounded_button_orange : R.drawable.rounded_button));
        }
    }

    @Override
    public void setMenuVisibility(boolean menuVisible) {
        super.setMenuVisibility(menuVisible);
        if (menuVisible) {

        }

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        displayJobDetail();



    }


    private void scrollActionButtons(final int buttonIndex){
        rootView.post(new Runnable() {
            @Override
            public void run() {
                Log.v("", "Left position of 12th child = " + mHSLayout.getChildAt(buttonIndex).getLeft());
                mHSView.smoothScrollTo(mHSLayout.getChildAt(buttonIndex).getLeft(), 0);
            }
        });
    }


    private void displayUpdateStatus(String status) {
        job.setJobStatus(status);
        displayJobDetail();
    }

    private void displayJobDetail() {

        Log.e("Detail Fragment", mCurrentTab);


        if (job.getCustomerTel().trim().length() > 0) {
            String p[] = job.getCustomerTel().trim().split("/");
            for (String s : p) {
                phoneList.add(s.trim());
            }
        }

        if (phoneList.size() == 0) {
            mPhoneLayout1.setVisibility(View.GONE);
            mPhoneLayout2.setVisibility(View.GONE);
        }

        if (phoneList.size() == 1) {
            mPhoneLayout1.setVisibility(View.VISIBLE);
            mPhoneLayout2.setVisibility(View.GONE);

            mMobile1.setText(phoneList.get(0));
        }

        if (phoneList.size() == 2) {
            mPhoneLayout1.setVisibility(View.VISIBLE);
            mPhoneLayout2.setVisibility(View.VISIBLE);

            mMobile1.setText(phoneList.get(0));
            mMobile2.setText(phoneList.get(1));
        }


        if (mCurrentTab.equalsIgnoreCase("HISTORY")) {

            mPhoneLayout1.setVisibility(GONE);
            mPhoneLayout2.setVisibility(GONE);

            mUpdateStatus.setVisibility(GONE);
        }


        // UAE
        if (job.getJobType().equalsIgnoreCase("UAE")) {
            mLabelFlightNo.setText("FILE NO");
            mFlightNo.setText(job.getFileNo());

            mLabelETA.setVisibility(GONE);
            mETA.setVisibility(GONE);
        } else {
            mLabelFlightNo.setText("FLIGHT NO");
            mFlightNo.setText(job.getFlight());

            mLabelETA.setVisibility(View.VISIBLE);
            mETA.setVisibility(View.VISIBLE);
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


        // Set Mobile Numbers


        //  mMobile.setText(job.getCustomerTel());


        mJobNo.setText(job.getJobNo());
        mJobType.setText(job.getJobType());
        mJobStatus.setText(job.getJobStatus());
        mDate.setText(job.getUsageDate());
        mTime.setText(job.getPickUpTime());
        mPassenger.setText(job.getCustomer());


        mETA.setText(job.getETA());
        mPickup.setText(job.getPickUp());
        mDropOff.setText(job.getDestination());
        mRemarks.setText(job.getRemarks());

        mItinerary.setVisibility((job.getFile1().

                isEmpty()) ? GONE : View.VISIBLE);
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

        Call<JobRes> call = RestClient.COACH().getApiService().UpdateJobStatus(
                job.getJobNo(),
                App.fullAddress,
                status

        );

        call.enqueue(new Callback<JobRes>() {
            @Override
            public void onResponse(Call<JobRes> call, Response<JobRes> response) {
                Log.e("Update Job Successful", response.toString());
                mAssignLayout.setVisibility(GONE);
                mUpdateLayout.setVisibility(View.VISIBLE);
                Toast.makeText(getContext(), "Update Successful", Toast.LENGTH_SHORT).show();

                displayUpdateStatus(status);

                EventBus.getDefault().postSticky("UPDATE_JOB_COUNT");

                if (status.equalsIgnoreCase("REJECTED"))
                    getActivity().finish();
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
//        // Toast.makeText(getContext(), action.getAction() + " " + action.getJobNo(), Toast.LENGTH_SHORT).show();
//        if (job.getJobNo().equals(action.getJobNo())) {
//
//            getActivity().runOnUiThread(new Runnable() {
//                public void run() {
//                    showCustomAlertDialog();
//                }
//            });
//        }
    }


    private void showCustomAlertDialog() {

        dialog = new Dialog(getContext());

        dialog.setContentView(R.layout.dialog_message);
        dialog.setTitle("");
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        TextView title = dialog.findViewById(R.id.title);
        Button ok = dialog.findViewById(R.id.ok);

        title.setText("MY COACH");

        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                getActivity().finish();
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


    private void callJobDetail() {
        Call<JobRes> call = RestClient.COACH().getApiService().GetJobDetails(
                job.getJobNo()
        );

        call.enqueue(new Callback<JobRes>() {
            @Override
            public void onResponse(Call<JobRes> call, Response<JobRes> response) {
                if (response.body().getJobdetails() != null)
                    job = response.body().getJobdetails();
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
    public void saveSignature(Bitmap photo) {
        try {


            photo = getResizedBitmap(photo, 400);

            // CALL THIS METHOD TO GET THE URI FROM THE BITMAP
            Uri tempUri = getImageUri(getContext(), photo);

            // CALL THIS METHOD TO GET THE ACTUAL PATH
            File finalFile = new File(getRealPathFromURI(getContext(), tempUri));

            InputStream inputStream = getContext().getContentResolver().openInputStream(tempUri);

            FtpHelper.uploadTask async = new FtpHelper.uploadTask(getActivity(), inputStream);
            async.execute(App.FTP_URL, App.FTP_USER, App.FTP_PASSWORD, App.FTP_DIR, job.getJobNo() + "_signature.jpg", job.getJobNo(), "SIGNATURE");   //Passing arguments to AsyncThread


        } catch (Exception e) {
            Log.e("FTP Error : ", e.getLocalizedMessage().toString());
        }
    }


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


}
