package com.info121.nativelimo.services;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.info121.nativelimo.App;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import io.nlopez.smartlocation.OnLocationUpdatedListener;
import io.nlopez.smartlocation.SmartLocation;
import io.nlopez.smartlocation.location.config.LocationAccuracy;
import io.nlopez.smartlocation.location.config.LocationParams;

/**
 * Created by KZHTUN on 7/26/2017.
 */

public class SmartLocationService extends Service implements OnLocationUpdatedListener {

    private Context mContext;
    private LocationParams mParams;
    private static final int LOCATION_PERMISSION_ID = 1001;

    private Location mLocation;

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = getBaseContext();
        startLocation();

        BroadcastReceiver mGpsSwitchStateReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().matches("android.location.PROVIDERS_CHANGED")) {
                    Log.e("GPS Status ... ", (isGpsEnabled()) ? "ON" : "OFF");
                }
            }
        };

        registerReceiver(mGpsSwitchStateReceiver, new IntentFilter(LocationManager.PROVIDERS_CHANGED_ACTION));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopLocation();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onLocationUpdated(Location location) {
        mLocation = location;
    }

    private void startLocation() {
        // Create location config
        mParams = new LocationParams.Builder()
                .setAccuracy(LocationAccuracy.HIGH)
                .setDistance(1)
                .setInterval(App.timerDelay)
                .build();

        SmartLocation.with(getApplicationContext()).location()
                .config(mParams)
                .start(this);

        updateToServer();
        startTimer();
        Log.e("Start Service ", "");

        // update location first time
    }

    private void stopLocation() {
        SmartLocation.with(getApplicationContext()).location()
                .stop();

        stopTimer();
        Log.e("Stop Service ", "");

    }

    private boolean isGpsEnabled() {
        final LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        return manager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }


    private void startTimer() {
        App.mRunnable = new Runnable() {
            @Override
            public void run() {
            //    Log.e("Timer ", "is Running");
                if (App.userName.length() > 0)
                    updateToServer();
                App.mHandler.postDelayed(this, App.timerDelay);
            }
        };

        App.mHandler.post(App.mRunnable);
    }

    private void stopTimer() {
        App.mHandler.removeCallbacks(App.mRunnable);
        App.mRunnable = null;
    }

    private void updateToServer() {
        if (mLocation != null) {

            Calendar c = Calendar.getInstance();
            SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy HH.mm");
            String formattedDate = df.format(c.getTime());

            Log.e("Location ... ", "lat : " + mLocation.getLatitude() + "lon : " + mLocation.getLongitude());
            Log.e("GPS Status ... ", String.valueOf((isGpsEnabled()) ? 1 : 0) + "  ");
            Log.e("Date Time ... ", formattedDate);


            App.gpsStatus = (isGpsEnabled()) ? 1 : 0;

            App.location = mLocation;
           // App.fullAddress = "Kyaw Thu 20St"; //getCompleteAddressString(mLocation);
            App.fullAddress = getCompleteAddressString(mLocation).replace(",", "#.#");

            Log.e("Address : " , App.fullAddress);

            // fake location
//            mLocation.setLatitude(16.0000);
//            mLocation.setLatitude(96.0000);

//            APIClient.UpdateDriverLocation(
//                    App.userName,
//                    String.valueOf(mLocation.getLatitude()),
//                    String.valueOf(mLocation.getLongitude()),
//                    (isGpsEnabled()) ? 1 : 0,
//                    getCompleteAddressString(mLocation)
//            );

            // call api to checkVersion
           // APIClient.CheckVersion(String.valueOf(Util.getVersionCode(mContext)));
        }
    }

    private String getCompleteAddressString(Location location) {
        String strAdd = "";
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
            if (addresses != null) {

                strAdd = addresses.get(0).getFeatureName();
                strAdd += ", " + addresses.get(0).getThoroughfare();
                strAdd += ", " + addresses.get(0).getLocality();
                strAdd += ", " + addresses.get(0).getCountryName();

            } else {
            }
        } catch (Exception e) {
            e.printStackTrace();

        }

       // strAdd = strAdd.replaceAll("\\+", "-");

        return strAdd.replaceAll("\\+", "-");
    }
}
