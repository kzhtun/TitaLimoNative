package com.info121.nativelimo.services;


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

import android.os.PowerManager;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;


import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.RemoteMessage;

import com.info121.nativelimo.R;
import com.info121.nativelimo.App;
import com.info121.nativelimo.activities.DialogActivity;

import com.info121.nativelimo.activities.NotifyActivity;
import com.info121.nativelimo.activities.SplashActivity;
import com.info121.nativelimo.models.Action;

import org.greenrobot.eventbus.EventBus;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.Random;


public class FirebaseMessagingService extends com.google.firebase.messaging.FirebaseMessagingService {
    private static final String TAG = "FirebaseMessagingService";
    private static final String EXTRA_MESSAGE = "extra.message";
    public final String text = "";


    String OLD_CH = "";
    String NEW_CH = "";



    public static String getToken(Context context) {
        return context.getSharedPreferences("_", MODE_PRIVATE).getString("fb", "empty");
    }

    @Override
    public void onNewToken(@NonNull String token) {
        super.onNewToken(token);

        App.FCM_TOKEN = token;
        Log.e("FCM Token : " , token);
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        Log.e("Action ", remoteMessage.getData().get("action"));

        if (remoteMessage.getData() != null) {

            App.BadgeCount = (remoteMessage.getData().get("action").equalsIgnoreCase("Unassign")) ? App.BadgeCount - 1 : App.BadgeCount + 1;

            showNotification(remoteMessage.getData());

            if (remoteMessage.getData().get("action").equalsIgnoreCase("Assign") ||
                        remoteMessage.getData().get("action").equalsIgnoreCase("Reassign") ||
                        remoteMessage.getData().get("action").equalsIgnoreCase("Refresh")) {

                if (isUrgentJob(remoteMessage.getData())) {
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
                }
            }

            EventBus.getDefault().post(new Action(remoteMessage.getData().get("action"),
                    remoteMessage.getData().get("jobno")
            ));

            EventBus.getDefault().postSticky("UPDATE_JOB_COUNT");
        }

        super.onMessageReceived(remoteMessage);

    }

    private void showNotification(Map<String, String> payloadData){ //} String title, String body) {
        OLD_CH = App.getOldChannelId();
        NEW_CH = App.getNewChannelId();


        String title = payloadData.get("title");
        String body = payloadData.get("message");


        if(body.isEmpty()) return;

        Intent intent = new Intent(this, SplashActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);


        Bundle bundle = new Bundle();
        bundle.putString("ACTION", payloadData.get("action"));
        bundle.putString("JOB_NO", payloadData.get("jobno"));
        bundle.putString("JOB_TYPE", payloadData.get("jobtype"));

        intent.putExtras(bundle);
        App.intents.add(intent);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 , intent,
                PendingIntent.FLAG_MUTABLE);

        // notification.setSound(Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + getPackageName() + "/raw/kalimba"));


        Uri soundUri = App.getNotificationSoundUri();
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, NEW_CH)
                .setSmallIcon(R.mipmap.my_limo_launcher)
                .setContentTitle(title)
                .setContentText(body)
                .setAutoCancel(true)
                .setNumber(App.BadgeCount)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(body))
                .setSound(soundUri)
                //  .setSound(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + getPackageName() + "/raw/kalimba"))
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


                //it will delete existing channel if it exists
//                if (mNotificationManager.getNotificationChannel(OLD_CH) != null) {
//                    mNotificationManager.deleteNotificationChannel(OLD_CH);
//                }

                // Creating Channel
                NotificationChannel notificationChannel = new NotificationChannel(NEW_CH, NEW_CH, NotificationManager.IMPORTANCE_HIGH);

                notificationChannel.enableLights(true);
                notificationChannel.enableVibration(true);
                notificationChannel.setSound(soundUri, audioAttributes);
                mNotificationManager.createNotificationChannel(notificationChannel);
            }
        }


        int  notiIndex =  (new Random()).nextInt();

      //  mNotificationManager.notify(Integer.parseInt(payloadData.get("jobno")), notificationBuilder.build());
        mNotificationManager.notify(notiIndex, notificationBuilder.build());

        wakelock(5000);
    }


    private void wakelock(int delay) {

        // If the app is in the background, then we display it automatically displayNotification(notification, null);
        // Turn on the screen for notification
        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        boolean isScreenOn = pm.isScreenOn();

        if (!isScreenOn) {
            try {
                PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP | PowerManager.ON_AFTER_RELEASE, "com.mdcandroid:MyLock");
                wl.acquire(delay);

                PowerManager.WakeLock wl_cpu = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "com.mdcandroid:MyCpuLock");
                wl_cpu.acquire(delay);

                wl.release();
                wl_cpu.release();
            } catch (Exception e) {
                Log.e("Wake Lock : ", e.getLocalizedMessage());
            }
        }
    }


    public void showDialog(String jobNo, String name, String phone, String displayMessage) {
        OLD_CH = App.getOldChannelIdP();
        NEW_CH = App.getNewChannelIdP();

        Intent intent = new Intent(this, DialogActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|
                Intent.FLAG_ACTIVITY_SINGLE_TOP);

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
                .setNumber(App.BadgeCount)
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


                //it will delete existing channel if it exists
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


        Log.e("Noti", "Notify Job");

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




    //    Intent intent = new Intent(this, NotifyActivity.class);
        Intent intent = new Intent(this, NotifyActivity.class);
      //  intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK  | Intent.FLAG_ACTIVITY_NEW_TASK);

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
                .setNumber(App.BadgeCount)
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
         //startService(intent);

        if (App.notiActivityIsShowing) {
            App.intents.add(intent);
            Log.e("Noti", "Append");
        }else {
            startActivity(intent);
            Log.e("Noti", "New Task");
        }

    }

    private boolean isUrgentJob(Map<String, String> jobData) {

        String jobDate =  jobData.get("jobdate");
        String jobTime =  jobData.get("pickuptime");

        String action =  jobData.get("action");

        SimpleDateFormat simpleDateFormat;

        // update job date format is 01/01/2023
        if (action.equalsIgnoreCase("Refresh"))
            simpleDateFormat  = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss");
        else
            simpleDateFormat  = new SimpleDateFormat("dd/MMM/yyyy hh:mm:ss");

        try {
            Date jobDateTime = simpleDateFormat.parse(jobDate + " " + jobTime + ":00");
            Date currentDateTime = new Date(); // simpleDateFormat.parse("13/10/2013 20:35:55");

            Log.e("Job Date : " , jobDateTime.toString());
            Log.e("Current Date : " , currentDateTime.toString());
            long timeDiff =  jobDateTime.getTime() - currentDateTime.getTime();

            // 3600000 , 1 HR
            // 1800000 , 30 MIN
            long h1 = 3600000;
            long m30 = 1800000;

            return timeDiff <= m30 && timeDiff >= 0;

        } catch (ParseException e) {
            e.printStackTrace();
            return false;
        }
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
