package com.info121.nativelimo.services;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;
import android.content.pm.ServiceInfo;
import android.util.Log;

import com.info121.nativelimo.App;
import com.info121.nativelimo.R;
import androidx.core.app.NotificationCompat;

import java.util.Random;

public class ForegroundService extends Service {

    private static final String CHANNEL_ID = "ALERT_CHANNEL";

    @Override
    public void onCreate() {
        super.onCreate();
        // createNotificationChannel();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // check if the intent is null
        if (intent == null) {
            Log.e("ForegroundService", "Intent was null in onStartCommand. Stopping service.");
            stopSelf();// Or handle appropriately
            return START_NOT_STICKY;
        }

        // create a notification intent
        //createNotificationChannel();
        createOrRecreateNotificationChannel();

        PendingIntent pendingIntent = PendingIntent.getActivity(this,
                new Random().nextInt(), // Use a unique request code for each PendingIntent if they differ
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE); // Recommended flags

        Uri soundUri = App.getNotificationSoundUri(); // From your App class
        Notification notification = new NotificationCompat.Builder(this,CHANNEL_ID)
                .setContentTitle("Titalimo background service is running.")
                .setContentText("Please do NOT close, it's required for the URGENT JOB NOTIFICATIONS to work properly.")
                .setSmallIcon(R.mipmap.mylimo_noti)
                .setPriority(NotificationCompat.PRIORITY_MIN)
                .setCategory(NotificationCompat.CATEGORY_MESSAGE) // or CATEGORY_MESSAGE
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC) // <-- REQUIRED
                .setContentIntent(pendingIntent)
                .build();

        startForeground(new Random().nextInt(), notification);

        return START_STICKY;

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

    private void createOrRecreateNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManager manager = (NotificationManager) getSystemService(getApplicationContext().NOTIFICATION_SERVICE);

            if (manager == null) {
                // Handle the case where the manager couldn't be retrieved, though rare.
                return;
            }

            // Check if the channel already exists
            NotificationChannel existingChannel = manager.getNotificationChannel(CHANNEL_ID);
            if (existingChannel != null) {
                // If it exists, delete it first
                manager.deleteNotificationChannel(CHANNEL_ID);
            }

            // Now, create the new channel
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "Job Alert Notifications", // Consider making this a string resource
                    NotificationManager.IMPORTANCE_HIGH
            );
            channel.setDescription("Full-screen notifications for new jobs"); // Also consider string resource

            // Optional: Configure other channel properties if needed
            // channel.setSound(soundUri, audioAttributes);
            // channel.enableLights(true);
            // channel.setLightColor(Color.RED);
            // channel.enableVibration(true);
            // channel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
            // channel.setShowBadge(true); // Whether notifications posted to this channel can appear as application icon badges

            manager.createNotificationChannel(channel);
        }
    }
}
