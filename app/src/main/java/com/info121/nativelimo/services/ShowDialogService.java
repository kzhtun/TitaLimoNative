package com.info121.nativelimo.services;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.Service;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.os.PowerManager;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.info121.nativelimo.R;
import com.info121.nativelimo.activities.DialogActivity;

import java.util.ArrayList;
import java.util.List;

public class ShowDialogService extends Service {
    public static final String JOB_NO = "JOB_NO";
    public static final String NAME = "NAME";
    public static final String PHONE = "PHONE";
    public static final String MESSAGE = "MESSAGE";


    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        createDialog(intent);
        stopSelf();


        return super.onStartCommand(intent, flags, startId);
    }

    private void createDialog(Intent intent) {

        final Dialog dialog = new Dialog(this, R.style.Theme_AppCompat);

        final Spinner mPhones;
        Button mDismiss, mCall, mConfirm;
        TextView mMessage;

        dialog.setContentView(R.layout.dialog_prominent);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        Window window = dialog.getWindow();

        dialog.setTitle("New Jobs");
        window.setAttributes(lp);
        window.setGravity(Gravity.CENTER);

        //adding dialog animation sliding up and down
        dialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);

        // disable home, back key
        dialog.setOnKeyListener(new Dialog.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialogs, int keyCode,
                                 KeyEvent event) {

                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    return true;
                }
                if (keyCode == KeyEvent.KEYCODE_HOME) {
                    return true;
                }
                return false;
            }
        });


        // show dialog
        dialog.show();


        mMessage = (TextView) dialog.findViewById(R.id.message);
        mPhones = (Spinner) dialog.findViewById(R.id.phone_spinner);
        mCall = (Button) dialog.findViewById(R.id.btn_call);
        mConfirm = (Button) dialog.findViewById(R.id.btn_confirm);
        mDismiss = (Button) dialog.findViewById(R.id.btn_remind_later);


        // Display Message
        Bundle bundle = intent.getExtras();

        final String phones = intent.getExtras().getString(PHONE);
        final String message = intent.getExtras().getString(MESSAGE);
        final String jobNo = intent.getExtras().getString(JOB_NO);


//        // for testing purpose only
//        String phones = "09965042219/095137664";
//        String message = "Hi askldfalk adsf asdf adsfj alsfaklsf klasdfj aldsf aldfs afsl";


        // Fill the data
        mMessage.setText(message);
        mPhones.setAdapter(fillPhoneNumbers(phones));


        mCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                phoneCall(mPhones.getSelectedItem().toString());
               // APIClient.ConfirmJob(jobNo);
            }
        });

        mDismiss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
               // APIClient.RemindLater(jobNo);
            }
        });

        mConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
              //  APIClient.ConfirmJob(jobNo);
            }
        });


        // Wake Screen
        @SuppressLint("InvalidWakeLockTag")
        PowerManager.WakeLock screenLock = ((PowerManager) getSystemService(POWER_SERVICE)).newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP, "TAG");
        screenLock.acquire();
        screenLock.release();
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

    public void phoneCall(String phoneNo) {

        Uri number = Uri.parse("tel:" + phoneNo);
        Intent callIntent = new Intent(Intent.ACTION_DIAL, number);
        callIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        try {
            startActivity(callIntent);
            Toast.makeText(getApplicationContext(), "Phone Call .... to  " + phoneNo, Toast.LENGTH_SHORT).show();
        }catch (android.content.ActivityNotFoundException ex){
            Toast.makeText(getApplicationContext(), ex.getMessage().toString(), Toast.LENGTH_LONG).show();
        }
    }

    private void createDialogActivity() {
        Intent dialogIntent = new Intent(this, DialogActivity.class);
        dialogIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(dialogIntent);

    }


}
