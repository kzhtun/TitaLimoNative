package com.info121.nativelimo.services;


import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.media.AudioAttributes;
import android.net.Uri;

import android.os.Build;
import android.os.Bundle;

import android.os.Handler;
import android.os.Looper;
import android.os.PowerManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;


import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

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
import java.util.Objects;
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
        Log.e("FCM Token : ", token);
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        remoteMessage.getData();

        if (remoteMessage.getNotification() != null) {
            showNotificationForeground(  remoteMessage.getData().get("title"),
                    remoteMessage.getData().get("message"));
        }

        App.BadgeCount = (remoteMessage.getData().get("action").equalsIgnoreCase("Unassign")) ? App.BadgeCount - 1 : App.BadgeCount;
        App.BadgeCount = (remoteMessage.getData().get("action").equalsIgnoreCase("Assign")) ? App.BadgeCount +1 : App.BadgeCount;
        wakelock(300);

        showFullScreenNotificationForeground(
                remoteMessage.getData().get("IsUrgent"),
                remoteMessage.getData().get("title"),
                remoteMessage.getData().get("message"),
                remoteMessage.getData().get("action"),
                remoteMessage.getData().get("jobno"),
                remoteMessage.getData().get("jobtype"),
                remoteMessage.getData().get("jobdate"),
                remoteMessage.getData().get("pickuptime"),
                remoteMessage.getData().get("pickuppoint"),
                remoteMessage.getData().get("alightpoint"),
                remoteMessage.getData().get("clientname"),
                remoteMessage.getData().get("vehicletype"),
                remoteMessage.getData().get("driver"));



        if(!remoteMessage.getData().get("action").equalsIgnoreCase("Assign")){
            EventBus.getDefault().post(new Action(remoteMessage.getData().get("action"),
                    remoteMessage.getData().get("jobno")
            ));

        }

       // EventBus.getDefault().postSticky("UPDATE_JOB_COUNT");

        super.onMessageReceived(remoteMessage);

    }



    public void showNotificationForeground(String title, String message){
        Intent serviceIntent = new Intent(this, ForegroundNotificationService.class);
        serviceIntent.putExtra("TITLE", title);
        serviceIntent.putExtra("MESSAGE", message);

        ContextCompat.startForegroundService(this, serviceIntent);
    }

    public void showFullScreenNotificationForeground(String isUrgent, String title, String message,  String action, String jobNo, String jobType, String jobDate, String jobTime, String pickup, String dropoff, String clientName, String vehicleType, String driver){
        Intent serviceIntent = new Intent(this, ForegroundNotificationService.class);
        serviceIntent.putExtra("ISURGENT", isUrgent);
        serviceIntent.putExtra("TITLE", title);
        serviceIntent.putExtra("MESSAGE", message);
        serviceIntent.putExtra("ACTION", action);

        serviceIntent.putExtra("JOB_NO", jobNo);
        serviceIntent.putExtra("CLIENT_NAME", clientName);
        serviceIntent.putExtra("JOB_NO", jobNo);
        serviceIntent.putExtra("JOB_TYPE", jobType);
        serviceIntent.putExtra("JOB_DATE", jobDate);
        serviceIntent.putExtra("JOB_TIME", jobTime);
        serviceIntent.putExtra("PICKUP", pickup);
        serviceIntent.putExtra("DROPOFF", dropoff);
        serviceIntent.putExtra("CUST_NAME", clientName);
        serviceIntent.putExtra("VEHICLE_TYPE", vehicleType);
        serviceIntent.putExtra("DRIVER", driver);

        ContextCompat.startForegroundService(this, serviceIntent);
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

                PowerManager.WakeLock wl_cpu = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "com.mdcandroid:MyLock");
                wl_cpu.acquire(delay);

                wl.release();
                wl_cpu.release();
            } catch (Exception e) {
                Log.e("Wake Lock : ", e.getLocalizedMessage());
            }


        }


    }

//
//    private void showNotification(Map<String, String> payloadData) { //} String title, String body) {
//
//        Log.e(TAG, "normal notification triggered");
//
//
//        String title = payloadData.get("title");
//        String body = payloadData.get("message");
//
//        if (body.isEmpty()) return;
//
//        Intent intent = null;
//        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
//            intent = new Intent(this, SplashActivity.class);
//        }
//        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
//
//        Bundle bundle = new Bundle();
//        bundle.putString("ACTION", payloadData.get("action"));
//        bundle.putString("JOB_NO", payloadData.get("jobno"));
//        bundle.putString("JOB_TYPE", payloadData.get("jobtype"));
//
//        intent.putExtras(bundle);
//        App.intents.add(intent);
//
//        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent,
//                PendingIntent.FLAG_MUTABLE);
//
//        Uri soundUri = App.getNotificationSoundUri();
//
//        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, App.N_CHANNEL)
//                .setSmallIcon(R.mipmap.my_limo_launcher)
//                .setContentTitle(title)
//                .setContentText(body)
//                .setAutoCancel(true)
//                .setNumber(App.BadgeCount)
//                .setStyle(new NotificationCompat.BigTextStyle().bigText(body))
//                .setContentIntent(pendingIntent)
//                .setPriority(NotificationCompat.PRIORITY_HIGH)
//                .setDefaults(NotificationCompat.DEFAULT_ALL)
//                .setCategory(NotificationCompat.CATEGORY_MESSAGE)
//                .setSound(soundUri)
//                .setFullScreenIntent(pendingIntent, true)
//                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC);
//
//        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//
//        int notiIndex = (new Random()).nextInt();
//
//        //  mNotificationManager.notify(Integer.parseInt(payloadData.get("jobno")), notificationBuilder.build());
//        mNotificationManager.notify(notiIndex, notificationBuilder.build());
//
//        wakelock(5000);
//
//
//        // add more extras as needed
//        // ContextCompat.startForegroundService(this, );
////
////        // Create high importance channel for Android O+
////        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
////            NotificationChannel channel = new NotificationChannel(
////                    App.N_CHANNEL,
////                    "Job Notifications",
////                    NotificationManager.IMPORTANCE_HIGH
////            );
////            channel.setDescription("Important job notifications");
////            channel.enableLights(true);
////            channel.enableVibration(true);
////            if (soundUri != null) {
////                AudioAttributes audioAttributes = new AudioAttributes.Builder()
////                        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
////                        .setUsage(AudioAttributes.USAGE_NOTIFICATION_COMMUNICATION_INSTANT)
////                        .build();
////                channel.setSound(soundUri, audioAttributes);
////            }
////            mNotificationManager.createNotificationChannel(channel);
////        }
//    }


//
//    private void showNotificationV1(Map<String, String> payloadData) {
//        Log.e(TAG, "Normal notification triggered. Payload: " + payloadData.toString());
//
//        String title = payloadData.get("title");
//        String body = payloadData.get("message");
//
//        if (body == null || body.isEmpty()) {
//            Log.e(TAG, "Notification body is empty, not showing notification.");
//            return;
//        }
//
//        // Ensure App.N_CHANNEL is created with IMPORTANCE_HIGH (as shown in App.java)
//        String channelId = App.N_CHANNEL;
//
//        Intent intent = new Intent(this, SplashActivity.class);
//        // It's generally better to initialize the intent directly rather than conditionally
//        // If SplashActivity is always the target.
//        // if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
//        //     intent = new Intent(this, SplashActivity.class);
//        // } else {
//        //     intent = new Intent(this, SplashActivity.class); // Or your target for older versions
//        // }
//        // if (intent == null) { // Should not happen if initialized above
//        //    Log.e(TAG, "Intent is null, cannot create PendingIntent.");
//        //    return;
//        // }
//
//
//        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
//
//        Bundle bundle = new Bundle();
//        bundle.putString("ACTION", payloadData.get("action"));
//        bundle.putString("JOB_NO", payloadData.get("jobno"));
//        bundle.putString("JOB_TYPE", payloadData.get("jobtype"));
//        intent.putExtras(bundle);
//        // App.intents.add(intent); // Be cautious with static lists of Intents, can lead to memory leaks. Consider if this is necessary.
//
//        PendingIntent pendingIntent = PendingIntent.getActivity(this,
//                new Random().nextInt(), // Use a unique request code for each PendingIntent if they differ
//                intent,
//                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE); // Recommended flags
//
//        Uri soundUri = App.getNotificationSoundUri(); // From your App class
//
//        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, channelId)
//                .setSmallIcon(R.mipmap.my_limo_launcher) // Ensure this icon is valid
//                .setContentTitle(title)
//                .setContentText(body)
//                .setNumber(App.BadgeCount) // Ensure BadgeCount is managed correctly
//                .setStyle(new NotificationCompat.BigTextStyle().bigText(body))
//                .setContentIntent(pendingIntent)
//                .setPriority(NotificationCompat.PRIORITY_HIGH) // Still good for pre-Oreo
//                .setDefaults(NotificationCompat.DEFAULT_ALL) // This will use default sound/vibrate if channel/soundUri is not set, or add to it
//                .setCategory(NotificationCompat.CATEGORY_MESSAGE) // Good for messaging type notifications
//                .setFullScreenIntent(pendingIntent, true) // Crucial for heads-up, especially on lock screen
//                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC); // Shows content on lock screen
//
//        if (soundUri != null) {
//            notificationBuilder.setSound(soundUri);
//        } else {
//            // If soundUri is null, and you want default sound,
//            // setDefaults(NotificationCompat.DEFAULT_SOUND | NotificationCompat.DEFAULT_VIBRATE)
//            // or rely on channel settings.
//            // DEFAULT_ALL includes sound, lights, vibrate.
//        }
//
//
//        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//
//        if (mNotificationManager == null) {
//            Log.e(TAG, "NotificationManager is null.");
//            return;
//        }
//
//        // Create the channel if it doesn't exist (though ideally done in Application class)
//        // This is a fallback, but primary creation should be in App.java
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            NotificationChannel channel = mNotificationManager.getNotificationChannel(channelId);
//            if (channel == null) {
//                Log.w(TAG, "Notification channel " + channelId + " not found. Creating with high importance.");
//                channel = new NotificationChannel(
//                        App.N_CHANNEL,
//                        App.N_CHANNEL_NAME, // Use the name defined in App
//                        NotificationManager.IMPORTANCE_HIGH
//                );
//                channel.setDescription("Important job updates and alerts.");
//                channel.enableVibration(true);
//                // Set other channel properties if needed, consistent with App.java
//                if (soundUri != null) {
//                    AudioAttributes audioAttributes = new AudioAttributes.Builder()
//                            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
//                            .setUsage(AudioAttributes.USAGE_NOTIFICATION_COMMUNICATION_INSTANT)
//                            .build();
//                    channel.setSound(soundUri, audioAttributes);
//                }
//                mNotificationManager.createNotificationChannel(channel);
//            } else if (channel.getImportance() < NotificationManager.IMPORTANCE_HIGH) {
//                // This is tricky. Channels once created with lower importance cannot have importance increased.
//                // The user would have to clear app data or uninstall/reinstall for a new higher importance channel.
//                // Best to get it right the first time in App.java.
//                Log.w(TAG, "Notification channel " + channelId + " exists but has low importance. Heads-up may not work as expected.");
//            }
//        }
//
//        int notiIndex = new Random().nextInt(); // Or a more meaningful ID if you need to update/cancel it
//        Log.d(TAG, "Notifying with ID: " + notiIndex);
//        mNotificationManager.notify(notiIndex, notificationBuilder.build());
//
//        wakelock(5000); // Consider if this is still needed, especially if using a foreground service.
//    }
//
////
////    public void showDialog(String jobNo, String name, String phone, String displayMessage) {
////        OLD_CH = App.getOldChannelIdP();
////        NEW_CH = App.getNewChannelIdP();
////
////        Intent intent = new Intent(this, DialogActivity.class);
////        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|
////                Intent.FLAG_ACTIVITY_SINGLE_TOP);
////
//////        PendingIntent pendingIntent = PendingIntent.getService(this, 0 /* Request code */, intent,
//////                PendingIntent.FLAG_ONE_SHOT);
////        PendingIntent pendingIntent = PendingIntent.getService(this, 0 /* Request code */, intent,
////                PendingIntent.FLAG_IMMUTABLE);
////
////        Uri soundUri = App.getProminentSoundUri();
////        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, NEW_CH)
////                .setSmallIcon(R.mipmap.my_limo_launcher)
////                .setContentTitle("Tita Limo")
////                .setContentText("A job has been alerted for your confirmation.")
////                .setAutoCancel(true)
////                .setNumber(App.BadgeCount)
////                .setSound(soundUri)
////                .setContentIntent(pendingIntent);
////
////
////        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
////        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
////            if (soundUri != null) {
////                // Changing Default mode of notification
////                notificationBuilder.setDefaults(Notification.DEFAULT_VIBRATE);
////                // Creating an Audio Attribute
////                AudioAttributes audioAttributes = new AudioAttributes.Builder()
////                        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
////                        .setUsage(AudioAttributes.USAGE_ALARM)
////                        .build();
////
////
////                //it will delete existing channel if it exists
////                if (mNotificationManager.getNotificationChannel(OLD_CH) != null) {
////                    mNotificationManager.deleteNotificationChannel(OLD_CH);
////                }
////
////                // Creating Channel
////                NotificationChannel notificationChannel = new NotificationChannel(NEW_CH, NEW_CH, NotificationManager.IMPORTANCE_HIGH);
////
////                notificationChannel.enableLights(true);
////                notificationChannel.enableVibration(true);
////                notificationChannel.setSound(soundUri, audioAttributes);
////                mNotificationManager.createNotificationChannel(notificationChannel);
////            }
////        }
////
////        mNotificationManager.notify(0, notificationBuilder.build());
////
////        //mNotificationManager.cancel(0);
////
////
////        Bundle bundle = new Bundle();
////
////        bundle.putString(ShowDialogService.JOB_NO, jobNo);
////        bundle.putString(ShowDialogService.NAME, name);
////        bundle.putString(ShowDialogService.PHONE, phone);
////        bundle.putString(ShowDialogService.MESSAGE, displayMessage);
////
////        intent.putExtras(bundle);
////        // startService(intent);
////
////        startActivity(intent);
////    }
//
//
//
//    public void showFullScreenNotification(String jobNo, String jobType, String jobDate, String jobTime, String pickup, String dropoff, String clientName, String vehicleType, String driver) {
//
//        // bundle
////        Bundle bundle = new Bundle();
////
////        bundle.putString("JOB_NO", jobNo);
////        bundle.putString("JOB_TYPE", jobType);
////        bundle.putString("JOB_DATE", jobDate);
////        bundle.putString("JOB_TIME", jobTime);
////        bundle.putString("PICKUP", pickup);
////        bundle.putString("DROPOFF", dropoff);
////        bundle.putString("CUST_NAME", clientName);
////        bundle.putString("VEHICLE_TYPE", vehicleType);
////        bundle.putString("DRIVER", driver);
//
//
////        Map<String, String> payloadData = null;
////        payloadData.put("title", "New Job Alert");
////        payloadData.put("message", "You have a new job assignment");
////        payloadData.put("action", "Assign");
////        payloadData.put("jobno", "12345");
////        payloadData.put("jobtype", "PICKUP");
//
////        showNotificationV1(payloadData);
//
//      //  Log.e(TAG, "urgent notification triggered" + payloadData);
//
//        // Start foreground service with job data
//        Intent serviceIntent = new Intent(this, NotificationOverlayService.class);
//        serviceIntent.putExtra("JOB_NO", jobNo);
//        serviceIntent.putExtra("CLIENT_NAME", clientName);
//        serviceIntent.putExtra("JOB_NO", jobNo);
//        serviceIntent.putExtra("JOB_TYPE", jobType);
//        serviceIntent.putExtra("JOB_DATE", jobDate);
//        serviceIntent.putExtra("JOB_TIME", jobTime);
//        serviceIntent.putExtra("PICKUP", pickup);
//        serviceIntent.putExtra("DROPOFF", dropoff);
//        serviceIntent.putExtra("CUST_NAME", clientName);
//        serviceIntent.putExtra("VEHICLE_TYPE", vehicleType);
//        serviceIntent.putExtra("DRIVER", driver);
//
//
//        // add more extras as needed
//        ContextCompat.startForegroundService(this, serviceIntent);
//
//
////        Intent intent = new Intent(this, NotifyActivity.class);
////        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
////
////       // intent.putExtras(bundle);
////        startActivity(intent);
//
//    }
//
//    public void showNotifyJob(String jobNo, String jobType, String jobDate, String jobTime, String pickup, String dropoff, String clientName, String vehicleType, String driver) {
//
//        Log.e("Urgent Noti", "Notify Job");
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
//        bundle.putString("VEHICLE_TYPE", vehicleType);
//        bundle.putString("DRIVER", driver);
//
//        // -----------------------------------
//        OLD_CH = App.getOldChannelIdP();
//        NEW_CH = App.getNewChannelIdP();
//
//        //    Intent intent = new Intent(this, NotifyActivity.class);
//        Intent intent = new Intent(this, NotifyActivity.class);
//        //  intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
//        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
//
////        PendingIntent pendingIntent = PendingIntent.getService(this, 0 /* Request code */, intent,
////                PendingIntent.FLAG_ONE_SHOT);
//
//        PendingIntent pendingIntent = PendingIntent.getService(this, 0 /* Request code */, intent,
//                PendingIntent.FLAG_IMMUTABLE);
//
//        Uri soundUri = App.getProminentSoundUri();
//        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, NEW_CH)
//                .setSmallIcon(R.mipmap.my_limo_launcher)
//                .setContentTitle("Tita Limo")
//                .setContentText("A job has been alerted for your confirmation.")
//                .setAutoCancel(true)
//                .setNumber(App.BadgeCount)
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
//
//        //mNotificationManager.cancel(0);
//
//        intent.putExtras(bundle);
//        //startService(intent);
//
//        if (App.notiActivityIsShowing) {
//            App.intents.add(intent);
//            Log.e("Noti", "Append");
//        } else {
//            startActivity(intent);
//            Log.e("Noti", "New Task");
//        }
//
//    }
//
//    private boolean isUrgentJob(Map<String, String> jobData) {
//
//        String jobDate = jobData.get("jobdate");
//        String jobTime = jobData.get("pickuptime");
//
//        String action = jobData.get("action");
//
//        SimpleDateFormat simpleDateFormat;
//
//        // update job date format is 01/01/2023
//        if (action.equalsIgnoreCase("Refresh"))
//            simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss");
//        else
//            simpleDateFormat = new SimpleDateFormat("dd/MMM/yyyy hh:mm:ss");
//
//        try {
//            Date jobDateTime = simpleDateFormat.parse(jobDate + " " + jobTime + ":00");
//            Date currentDateTime = new Date(); // simpleDateFormat.parse("13/10/2013 20:35:55");
//
//            Log.e("Job Date : ", jobDateTime.toString());
//            Log.e("Current Date : ", currentDateTime.toString());
//            long timeDiff = jobDateTime.getTime() - currentDateTime.getTime();
//
//            // 3600000 , 1 HR
//            // 1800000 , 30 MIN
//            long h1 = 3600000;
//            long m30 = 1800000;
//
//            return timeDiff <= m30 && timeDiff >= 0;
//
//        } catch (ParseException e) {
//            e.printStackTrace();
//            return false;
//        }
//    }

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

