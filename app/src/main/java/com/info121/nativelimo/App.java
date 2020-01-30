package com.info121.nativelimo;

import android.app.Application;
import android.content.Context;
import android.location.Location;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.info121.nativelimo.models.Job;
import com.info121.nativelimo.utils.PrefDB;


import java.util.ArrayList;
import java.util.List;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;


public class App extends Application {
    public static String DEVICE_TYPE = "ANDROID";
    String TAG = "Application";

    // DEV
    public static String CONST_REST_API_URL = "http://alexisinfo121.noip.me:83/RestAPITitanium/MyLimoService.svc/";
    public static String CONST_PDF_URL = "http://alexisinfo121.noip.me:83/iops/uploads/";
    public static String CONST_PHOTO_URL = "http://alexisinfo121.noip.me/IOPS/images/mycoachpics/";


    // Playstore
//    public static String CONST_REST_API_URL = "http://103.7.10.47/RestApiTitanium/MyLimoService.svc/";
//    public static String CONST_PDF_URL = "http://103.7.10.47/iops/uploads/";
//    public static String CONST_PHOTO_URL = "http://103.7.10.47/iops/images/limopics";


    public static String CONST_USER_NAME = "USER_NAME";
    public static String CONST_ALREADY_LOGIN = "ALREADY_LOGIN";
    public static String CONST_NOTIFICATION_TONE = "NOTIFICATION_TONE";
    public static String CONST_PROMINENT_TONE = "PROMINENT_TONE";

    public static String CONST_DEVICE_ID = "DEVICE_ID";
    public static String CONST_TIMER_DELAY = "TIMER_DELAY";
    public static String CONST_REMEMBER_ME = "REMEMBER_ME";



    public static String userName = "user";
    public static String deviceID = "00000";
    public static String authToken = "00000";

    public static long timerDelay = 6000;
    public static Location location;
    public static String fullAddress = "";
    public static int gpsStatus = 0;

    public static Context targetContent;

    public static PrefDB prefDB = null;
    public static String FCM_TOKEN = "";

    public static Runnable mRunnable;
    public static final Handler mHandler = new Handler();


    public static Uri DEFAULT_SOUND_URI = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
    public static Uri NOTIFICATION_SOUND_URI = null;
    public static Uri PROMINENT_SOUND_URI = null;


    public static String test = "";


    public static List<Job> jobList = new ArrayList<>();


    // NEW SERVER
    public static final String FTP_URL = "103.7.10.47";
    public static final String FTP_USER = "info121";
    public static final String FTP_PASSWORD = "ja8mt988$$";
    public static String FTP_DIR = "limopics";

    public static final String[] SONG_PROJECTION = new String[]{
            MediaStore.Audio.Media._ID
            , MediaStore.Audio.Media.TITLE
            , MediaStore.Audio.Media.ARTIST
            , MediaStore.Audio.Albums.ALBUM
            , MediaStore.Audio.Media.DURATION
            , MediaStore.Audio.Media.TRACK
            , MediaStore.Audio.Media.ARTIST_ID
            , MediaStore.Audio.Media.ALBUM_ID
            , MediaStore.Audio.Media.DATA
            , MediaStore.Audio.Media.ALBUM_KEY
    };

    @Override
    public void onCreate() {
        super.onCreate();


        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/Lato-Regular.ttf")
                // .setFontAttrId(R.attr.fontPath)
                .build());


        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if (!task.isSuccessful()) {
                            Log.e(TAG, "getInstanceId failed", task.getException());
                            return;
                        }

                        // Get new Instance ID token
                        FCM_TOKEN = task.getResult().getToken();
                        Log.e("TOKEN : " , FCM_TOKEN);
                    }
                });





//        File f = new File(Environment.getExternalStorageDirectory(), PHOTO_FOLDER);
//
//        if (!f.exists()) {
//            f.mkdirs();
//        }

        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());



        // for notification tone
        prefDB = new PrefDB(getApplicationContext());

        if (prefDB.getString("OLD_CH_ID").length() == 0)
            prefDB.putString("OLD_CH_ID", "DEFAULT_OLD");

        if (prefDB.getString("NEW_CH_ID").length() == 0)
            prefDB.putString("NEW_CH_ID", "DEFAULT_NEW");

        if (prefDB.getString("OLD_CH_ID_P").length() == 0)
            prefDB.putString("OLD_CH_ID_P", "DEFAULT_OLD_P");

        if (prefDB.getString("NEW_CH_ID_P").length() == 0)
            prefDB.putString("NEW_CH_ID_P", "DEFAULT_NEW_P");

    }

    public static Uri getProminentSoundUri() {
        if (prefDB.getString(CONST_NOTIFICATION_TONE) == "")
            return App.DEFAULT_SOUND_URI;
        else
            return Uri.parse(prefDB.getString(CONST_PROMINENT_TONE));
    }

    public static Uri getNotificationSoundUri() {
        if (prefDB.getString(CONST_NOTIFICATION_TONE) == "")
            return App.DEFAULT_SOUND_URI;
        else
            return Uri.parse(prefDB.getString(CONST_NOTIFICATION_TONE));
    }


    public static String getNewChannelId() {
        return prefDB.getString("NEW_CH_ID");
    }

    public static String getOldChannelId() {
        return prefDB.getString("OLD_CH_ID");
    }

    public static String getNewChannelIdP() {
        return prefDB.getString("NEW_CH_ID_P");
    }

    public static String getOldChannelIdP() {
        return prefDB.getString("OLD_CH_ID_P");
    }


    @Override
    public void onTerminate() {
        super.onTerminate();

        mHandler.removeCallbacks(App.mRunnable);
        mRunnable = null;

    }
}
