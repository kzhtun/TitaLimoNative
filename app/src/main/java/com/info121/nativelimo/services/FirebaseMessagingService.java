package com.info121.nativelimo.services;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.AudioAttributes;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;
import android.view.View;

import com.google.firebase.messaging.RemoteMessage;
import com.info121.nativelimo.R;
import com.info121.nativelimo.App;
import com.info121.nativelimo.activities.DialogActivity;
import com.info121.nativelimo.activities.JobOverviewActivity;
import com.info121.nativelimo.activities.LoginActivity;
import com.info121.nativelimo.activities.MainActivity;
import com.info121.nativelimo.activities.NotifyActivity;
import com.info121.nativelimo.models.Action;

import org.greenrobot.eventbus.EventBus;

import java.util.Map;


public class FirebaseMessagingService extends com.google.firebase.messaging.FirebaseMessagingService {
    private static final String TAG = "FirebaseMessagingService";
    private static final String EXTRA_MESSAGE = "extra.message";
    public final String text = "";


    String OLD_CH = "";
    String NEW_CH = "";

    private void showNotification(Map<String, String> payloadData){ //} String title, String body) {
        OLD_CH = App.getOldChannelId();
        NEW_CH = App.getNewChannelId();


        String title = payloadData.get("title");
        String body = payloadData.get("message");

        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|
                Intent.FLAG_ACTIVITY_SINGLE_TOP);

//        remoteMessage.getData().get("jobno"),
//        remoteMessage.getData().get("jobtype"),
//                remoteMessage.getData().get("jobdate"),
//                remoteMessage.getData().get("pickuptime"),
//                remoteMessage.getData().get("pickuppoint"),
//                remoteMessage.getData().get("alightpoint"),
//                remoteMessage.getData().get("clientname"),
//                remoteMessage.getData().get("vehicletype"),
//                remoteMessage.getData().get("driver")

        Bundle bundle = new Bundle();
        bundle.putString("ACTION", payloadData.get("action"));
        bundle.putString("JOB_NO", payloadData.get("jobno"));
        bundle.putString("JOB_TYPE", payloadData.get("jobtype"));

        intent.putExtras(bundle);
        App.intents.add(intent);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 , intent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        Uri soundUri = App.getNotificationSoundUri();
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, NEW_CH)
                .setSmallIcon(R.mipmap.my_limo_launcher)
                .setContentTitle(title)
                .setContentText(body)
                .setAutoCancel(true)
                .setSound(soundUri)
                .setContentIntent(pendingIntent);


        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            if (soundUri != null) {
                // Changing Default mode of notification
                notificationBuilder.setDefaults(Notification.DEFAULT_VIBRATE);
                // Creating an Audio Attribute
                AudioAttributes audioAttributes = new AudioAttributes.Builder()
                        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                        .setUsage(AudioAttributes.USAGE_ALARM)
                        .build();


//                //it will delete existing channel if it exists
                if (mNotificationManager.getNotificationChannel(OLD_CH) != null) {
                    mNotificationManager.deleteNotificationChannel(OLD_CH);
                }

                // Creating Channel
                NotificationChannel notificationChannel = new NotificationChannel(NEW_CH, NEW_CH, NotificationManager.IMPORTANCE_HIGH);

                notificationChannel.enableLights(true);
                notificationChannel.enableVibration(true);
                notificationChannel.setSound(soundUri, audioAttributes);
                mNotificationManager.createNotificationChannel(notificationChannel);
            }
        }

        mNotificationManager.notify(0, notificationBuilder.build());
    }


    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        //        Key .title = title, _
//        Key .message = body, _
//        Key .action = action, _
//        Key .jobno = jobno, _
//        Key .jobtype = jobtype, _
//        Key .vehicletype = vehicletype, _
//        Key .jobdate = jobdate, _
//        Key .pickuptime = pickuptime, _
//        Key .endtime = endtime, _
//        Key .pickuppoint = pickuppoint, _
//        Key .alightpoint = alightpoint, _
//        Key .clientname = clientname, _
//        Key .phone = phone _

        if (remoteMessage.getData() != null) {
          if (remoteMessage.getData().get("action") == null) {
                showDialog(remoteMessage.getData().get("jobNo"),
                        remoteMessage.getData().get("Name"),
                        remoteMessage.getData().get("phone"),
                        remoteMessage.getData().get("displayMsg")
                );
            } else {

                if (remoteMessage.getData().get("action").equalsIgnoreCase("Assign") || remoteMessage.getData().get("action").equalsIgnoreCase("Reassign")) {
                    showNotifyJob(remoteMessage.getData().get("jobno"),
                            remoteMessage.getData().get("jobtype"),
                            remoteMessage.getData().get("jobdate"),
                            remoteMessage.getData().get("pickuptime"),
                            remoteMessage.getData().get("pickuppoint"),
                            remoteMessage.getData().get("alightpoint"),
                            remoteMessage.getData().get("clientname"),
                            remoteMessage.getData().get("vehicletype"),
                            remoteMessage.getData().get("driver")
                    );
                }else{
                   // showNotification(remoteMessage.getData().get("title"), remoteMessage.getData().get("message"));
                    showNotification(remoteMessage.getData());
                    //----------------------------------------------------------------------------------//
                    EventBus.getDefault().post(new Action(remoteMessage.getData().get("action"),
                            remoteMessage.getData().get("jobno")
                    ));

                    Log.e("FB Msg : " , remoteMessage.getData().toString());

                }

                EventBus.getDefault().postSticky("UPDATE_JOB_COUNT");
            }
        }

        super.onMessageReceived(remoteMessage);

    }


    public void showDialog(String jobNo, String name, String phone, String displayMessage) {
        OLD_CH = App.getOldChannelIdP();
        NEW_CH = App.getNewChannelIdP();

        Intent intent = new Intent(this, DialogActivity.class);
        //   intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

//        PendingIntent pendingIntent = PendingIntent.getService(this, 0 /* Request code */, intent,
//                PendingIntent.FLAG_ONE_SHOT);
        PendingIntent pendingIntent = PendingIntent.getService(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_IMMUTABLE);

        Uri soundUri = App.getProminentSoundUri();
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, NEW_CH)
                .setSmallIcon(R.mipmap.my_limo_launcher)
                .setContentTitle("Tita Limo")
                .setContentText("A job has been alerted for your confirmation.")
                .setAutoCancel(true)
                .setSound(soundUri)
                .setContentIntent(pendingIntent);


        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            if (soundUri != null) {
                // Changing Default mode of notification
                notificationBuilder.setDefaults(Notification.DEFAULT_VIBRATE);
                // Creating an Audio Attribute
                AudioAttributes audioAttributes = new AudioAttributes.Builder()
                        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                        .setUsage(AudioAttributes.USAGE_ALARM)
                        .build();


//                //it will delete existing channel if it exists
                if (mNotificationManager.getNotificationChannel(OLD_CH) != null) {
                    mNotificationManager.deleteNotificationChannel(OLD_CH);
                }

                // Creating Channel
                NotificationChannel notificationChannel = new NotificationChannel(NEW_CH, NEW_CH, NotificationManager.IMPORTANCE_HIGH);

                notificationChannel.enableLights(true);
                notificationChannel.enableVibration(true);
                notificationChannel.setSound(soundUri, audioAttributes);
                mNotificationManager.createNotificationChannel(notificationChannel);
            }
        }

        mNotificationManager.notify(0, notificationBuilder.build());

        //mNotificationManager.cancel(0);


        Bundle bundle = new Bundle();

        bundle.putString(ShowDialogService.JOB_NO, jobNo);
        bundle.putString(ShowDialogService.NAME, name);
        bundle.putString(ShowDialogService.PHONE, phone);
        bundle.putString(ShowDialogService.MESSAGE, displayMessage);

        intent.putExtras(bundle);
        // startService(intent);

        startActivity(intent);
    }

    public void showNotifyJob(String jobNo, String jobType, String jobDate, String jobTime, String pickup, String dropoff, String clientName, String vehicleType, String driver) {

        // bundle
        Bundle bundle = new Bundle();

        bundle.putString("JOB_NO", jobNo);
        bundle.putString("JOB_TYPE", jobType);
        bundle.putString("JOB_DATE", jobDate);
        bundle.putString("JOB_TIME", jobTime);
        bundle.putString("PICKUP", pickup);
        bundle.putString("DROPOFF", dropoff);
        bundle.putString("CUST_NAME", clientName);
        bundle.putString("VEHICLE_TYPE", vehicleType);
        bundle.putString("DRIVER", driver);


        // -----------------------------------
        OLD_CH = App.getOldChannelIdP();
        NEW_CH = App.getNewChannelIdP();

        Intent intent = new Intent(this, NotifyActivity.class);
        //   intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

//        PendingIntent pendingIntent = PendingIntent.getService(this, 0 /* Request code */, intent,
//                PendingIntent.FLAG_ONE_SHOT);

        PendingIntent pendingIntent = PendingIntent.getService(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_IMMUTABLE);

        Uri soundUri = App.getProminentSoundUri();
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, NEW_CH)
                .setSmallIcon(R.mipmap.my_limo_launcher)
                .setContentTitle("Tita Limo")
                .setContentText("A job has been alerted for your confirmation.")
                .setAutoCancel(true)
                .setSound(soundUri)
                .setContentIntent(pendingIntent);


        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            if (soundUri != null) {
                // Changing Default mode of notification
                notificationBuilder.setDefaults(Notification.DEFAULT_VIBRATE);
                // Creating an Audio Attribute
                AudioAttributes audioAttributes = new AudioAttributes.Builder()
                        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                        .setUsage(AudioAttributes.USAGE_ALARM)
                        .build();


//                //it will delete existing channel if it exists
                if (mNotificationManager.getNotificationChannel(OLD_CH) != null) {
                    mNotificationManager.deleteNotificationChannel(OLD_CH);
                }

                // Creating Channel
                NotificationChannel notificationChannel = new NotificationChannel(NEW_CH, NEW_CH, NotificationManager.IMPORTANCE_HIGH);

                notificationChannel.enableLights(true);
                notificationChannel.enableVibration(true);
                notificationChannel.setSound(soundUri, audioAttributes);
                mNotificationManager.createNotificationChannel(notificationChannel);
            }
        }

        mNotificationManager.notify(0, notificationBuilder.build());

        //mNotificationManager.cancel(0);

        intent.putExtras(bundle);
        // startService(intent);

        if (App.notiActivityIsShowing)
            App.intents.add(intent);
        else
            startActivity(intent);

    }

    @Override
    public void onDeletedMessages() {
        super.onDeletedMessages();
    }

    @Override
    public void onMessageSent(String s) {
        super.onMessageSent(s);
    }

    @Override
    public void onSendError(String s, Exception e) {
        super.onSendError(s, e);
    }


}
