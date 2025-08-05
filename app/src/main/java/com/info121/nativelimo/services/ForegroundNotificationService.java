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
import android.os.IBinder;

import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import com.info121.nativelimo.App;
import com.info121.nativelimo.R;
import com.info121.nativelimo.activities.NotifyActivity;

import java.util.Random;

public class ForegroundNotificationService extends Service {

    private static final String CHANNEL_ID = "job_alerts";

    @Override
    public void onCreate() {
        super.onCreate();
       // createNotificationChannel();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String title = "";
        String message = "";
        String isUrgent = "";
        String action = "";

        if (intent != null && intent.getExtras() != null) {

            title = intent.getStringExtra("TITLE");
            message = intent.getStringExtra("MESSAGE");
            isUrgent = intent.getStringExtra("ISURGENT");
            action = intent.getStringExtra("ACTION");

            String jobNo = intent.getStringExtra("JOB_NO");
            String jobType = intent.getStringExtra("JOB_TYPE");
            String jobDate = intent.getStringExtra("JOB_DATE");
            String jobTime = intent.getStringExtra("JOB_TIME");
            String pickup = intent.getStringExtra("PICKUP");
            String dropoff = intent.getStringExtra("DROPOFF");
            String clientName = intent.getStringExtra("CUST_NAME");
            String vehicleType = intent.getStringExtra("VEHICLE_TYPE");
            String driver = intent.getStringExtra("DRIVER");



            if(isUrgent != null &&
                isUrgent.equalsIgnoreCase("Y") &&
                (action.equalsIgnoreCase("Assign") || action.equalsIgnoreCase("Reassign") || action.equalsIgnoreCase("Refresh"))
                    ){
                Intent serviceIntent = new Intent(this, NotifyActivity.class);
                //serviceIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                serviceIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
                        Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS |
                        Intent.FLAG_ACTIVITY_NO_HISTORY );

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

                if(jobNo != null && !jobNo.isEmpty()){
                    startActivity(serviceIntent);
                }
            }
        }


        Intent contentIntent = new Intent(this, ForegroundNotificationService.class);
        contentIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
                Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS |
                Intent.FLAG_ACTIVITY_SINGLE_TOP);

        PendingIntent pendingIntent = PendingIntent.getActivity(this,
                new Random().nextInt(), // Use a unique request code for each PendingIntent if they differ
                contentIntent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        Uri soundUri = App.getNotificationSoundUri();
        // normal
        Notification notification = new NotificationCompat.Builder(this, App.N_CHANNEL)
                .setSmallIcon(R.mipmap.my_limo_launcher)
                .setContentTitle(title)
                .setContentText(message)
                .setAutoCancel(true)
                .setNumber(App.BadgeCount)
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(message)
                        .setBigContentTitle(title)
                        .setSummaryText("Job Alert"))
                .setContentIntent(pendingIntent)
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                .setDefaults(Notification.DEFAULT_ALL)
                .setSound(soundUri)
                .setFullScreenIntent(pendingIntent, isUrgent != null && isUrgent.equalsIgnoreCase("Y"))
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .build();


        startForeground(new Random().nextInt(), notification);

        return START_NOT_STICKY;


    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

}
