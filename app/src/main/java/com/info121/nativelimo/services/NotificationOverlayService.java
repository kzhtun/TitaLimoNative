package com.info121.nativelimo.services;


import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;

import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.AudioAttributes;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.info121.nativelimo.App;
import com.info121.nativelimo.R;
import com.info121.nativelimo.activities.HeadupActivity;
import com.info121.nativelimo.activities.NotifyActivity;

import java.util.Map;
import java.util.Random;

public class NotificationOverlayService extends Service {

    private static final String CHANNEL_ID = "job_alerts";

    @Override
    public void onCreate() {
        super.onCreate();
       // createNotificationChannel();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            NotificationChannel channel = new NotificationChannel(
//                    "overlay_channel_id",
//                    "Overlay Notification Channel",
//                    NotificationManager.IMPORTANCE_LOW
//            );
//            NotificationManager manager = getSystemService(NotificationManager.class);
//            if (manager != null) {
//                manager.createNotificationChannel(channel);
//            }
//        }
        PendingIntent pendingIntent = PendingIntent.getActivity(this,
                new Random().nextInt(), // Use a unique request code for each PendingIntent if they differ
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE); // Recommended flags

        Uri soundUri = App.getNotificationSoundUri(); // From your App class
        Notification notification = new NotificationCompat.Builder(this, App.N_CHANNEL)
                .setContentTitle("Titalimo background service is running.")
                .setContentText("Please do NOT close, it's required for the URGENT JOB NOTIFICATIONS to work properly.")
                .setSmallIcon(R.mipmap.mylimo_noti)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_CALL) // or CATEGORY_MESSAGE
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC) // <-- REQUIRED
                .setContentIntent(pendingIntent)
                .build();

        startForeground(new Random().nextInt(), notification);

        // Check if intent has extras before processing
        if (intent != null && intent.getExtras() != null && intent.hasExtra("JOB_NO")) {
            String jobNo = intent.getStringExtra("JOB_NO");
            String jobType = intent.getStringExtra("JOB_TYPE");
            String jobDate = intent.getStringExtra("JOB_DATE");
            String jobTime = intent.getStringExtra("JOB_TIME");
            String pickup = intent.getStringExtra("PICKUP");
            String dropoff = intent.getStringExtra("DROPOFF");
            String clientName = intent.getStringExtra("CUST_NAME");
            String vehicleType = intent.getStringExtra("VEHICLE_TYPE");
            String driver = intent.getStringExtra("DRIVER");

            // Start the activity directly
            Intent activityIntent = new Intent(this, NotifyActivity.class);
            activityIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            activityIntent.putExtra("JOB_NO", jobNo);
            activityIntent.putExtra("JOB_TYPE", jobType);
            activityIntent.putExtra("JOB_DATE", jobDate);
            activityIntent.putExtra("JOB_TIME", jobTime);
            activityIntent.putExtra("PICKUP", pickup);
            activityIntent.putExtra("DROPOFF", dropoff);
            activityIntent.putExtra("CUST_NAME", clientName);
            activityIntent.putExtra("VEHICLE_TYPE", vehicleType);
            activityIntent.putExtra("DRIVER", driver);

         //   startActivity(activityIntent);
        }

    //    stopSelf(); // Stop service immediately after showing notification
        return START_NOT_STICKY;
        //return START_STICKY;

    }

    private void showNotificationV1() {
       // Log.e(TAG, "Normal notification triggered. Payload: " + payloadData.toString());

        String title = "New Job Alert";
        String body = "you job is ready to be accepted";

        if (body == null || body.isEmpty()) {
            //Log.e(TAG, "Notification body is empty, not showing notification.");
            return;
        }

        // Ensure App.N_CHANNEL is created with IMPORTANCE_HIGH (as shown in App.java)
        String channelId = App.N_CHANNEL;

        Intent intent = new Intent(this, NotificationOverlayService.class);
        // It's generally better to initialize the intent directly rather than conditionally
        // If SplashActivity is always the target.
        // if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
        //     intent = new Intent(this, SplashActivity.class);
        // } else {
        //     intent = new Intent(this, SplashActivity.class); // Or your target for older versions
        // }
        // if (intent == null) { // Should not happen if initialized above
        //    Log.e(TAG, "Intent is null, cannot create PendingIntent.");
        //    return;
        // }


        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);

//        Bundle bundle = new Bundle();
//        bundle.putString("ACTION", payloadData.get("action"));
//        bundle.putString("JOB_NO", payloadData.get("jobno"));
//        bundle.putString("JOB_TYPE", payloadData.get("jobtype"));
//        intent.putExtras(bundle);
        // App.intents.add(intent); // Be cautious with static lists of Intents, can lead to memory leaks. Consider if this is necessary.

        PendingIntent pendingIntent = PendingIntent.getActivity(this,
                new Random().nextInt(), // Use a unique request code for each PendingIntent if they differ
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE); // Recommended flags

        Uri soundUri = App.getNotificationSoundUri(); // From your App class

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, channelId)
                .setSmallIcon(R.mipmap.my_limo_launcher) // Ensure this icon is valid
                .setContentTitle(title)
                .setContentText(body)
                .setNumber(App.BadgeCount) // Ensure BadgeCount is managed correctly
                .setStyle(new NotificationCompat.BigTextStyle().bigText(body))
                .setContentIntent(pendingIntent)
                .setPriority(NotificationCompat.PRIORITY_HIGH) // Still good for pre-Oreo
                .setDefaults(NotificationCompat.DEFAULT_ALL) // This will use default sound/vibrate if channel/soundUri is not set, or add to it
                .setCategory(NotificationCompat.CATEGORY_MESSAGE) // Good for messaging type notifications
                .setFullScreenIntent(pendingIntent, true) // Crucial for heads-up, especially on lock screen
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC); // Shows content on lock screen

        if (soundUri != null) {
            notificationBuilder.setSound(soundUri);
        } else {
            // If soundUri is null, and you want default sound,
            // setDefaults(NotificationCompat.DEFAULT_SOUND | NotificationCompat.DEFAULT_VIBRATE)
            // or rely on channel settings.
            // DEFAULT_ALL includes sound, lights, vibrate.
        }


        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        if (mNotificationManager == null) {
           // Log.e(TAG, "NotificationManager is null.");
            return;
        }

        // Create the channel if it doesn't exist (though ideally done in Application class)
        // This is a fallback, but primary creation should be in App.java
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = mNotificationManager.getNotificationChannel(channelId);
            if (channel == null) {
               // Log.w(TAG, "Notification channel " + channelId + " not found. Creating with high importance.");
                channel = new NotificationChannel(
                        App.N_CHANNEL,
                        App.N_CHANNEL_NAME, // Use the name defined in App
                        NotificationManager.IMPORTANCE_HIGH
                );
                channel.setDescription("Important job updates and alerts.");
                channel.enableVibration(true);
                // Set other channel properties if needed, consistent with App.java
                if (soundUri != null) {
                    AudioAttributes audioAttributes = new AudioAttributes.Builder()
                            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                            .setUsage(AudioAttributes.USAGE_NOTIFICATION_COMMUNICATION_INSTANT)
                            .build();
                    channel.setSound(soundUri, audioAttributes);
                }
                mNotificationManager.createNotificationChannel(channel);
            } else if (channel.getImportance() < NotificationManager.IMPORTANCE_HIGH) {
                // This is tricky. Channels once created with lower importance cannot have importance increased.
                // The user would have to clear app data or uninstall/reinstall for a new higher importance channel.
                // Best to get it right the first time in App.java.
             //   Log.w(TAG, "Notification channel " + channelId + " exists but has low importance. Heads-up may not work as expected.");
            }
        }

        int notiIndex = new Random().nextInt(); // Or a more meaningful ID if you need to update/cancel it
      // Log.d(TAG, "Notifying with ID: " + notiIndex);
        mNotificationManager.notify(notiIndex, notificationBuilder.build());

       // wakelock(5000); // Consider if this is still needed, especially if using a foreground service.
    }


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "Job Alert Notifications",
                    NotificationManager.IMPORTANCE_HIGH
            );
            channel.setDescription("Full-screen notifications for new jobs");
            NotificationManager manager = getSystemService(NotificationManager.class);
            if (manager != null) {
                manager.createNotificationChannel(channel);
            }
        }
    }
}
