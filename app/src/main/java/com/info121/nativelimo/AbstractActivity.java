package com.info121.nativelimo;

import android.app.ActivityManager;
import android.content.Context;
import android.os.Build;

import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

/**
 * Created by KZHTUN on 3/8/2016.
 */
public class AbstractActivity extends AppCompatActivity {
    final String TAG = AbstractActivity.class.getSimpleName();

//    Toolbar mToolbar;
//    DrawerLayout mDrawerLayout;
//    NavigationView mNavigationView;

    @Override
    protected void onStart() {
        Log.e("Abstract", "On Start");
        checkApplicationState();
        super.onStart();

        // event bus register
        EventBus.getDefault().register(this);
        Log.e(TAG, "EventBus Registered on Activity ... " + this.getLocalClassName());
    }

    @Override
    protected void onStop() {
        Log.e("Abstract", "On Stop");
        checkApplicationState();

        // event bus unregister
        EventBus.getDefault().unregister(this);
        Log.e(TAG, "EventBus Un-Registered on Activity ... " + this.getLocalClassName());


        super.onStop();
    }

    @Override
    protected void onResume() {
        Log.e("Abstract", "On Resume");
        checkApplicationState();
        super.onResume();
    }

    @Override
    protected void onPause() {
        Log.e("Abstract", "On Pause");
        checkApplicationState();
        super.onPause();
    }

    @Override
    protected void attachBaseContext(Context newBase) {
       // super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));

        //Implement this for api 28 and below
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.Q){
            super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
        }
        //Or implement this for api 29 and above
        else {
            super.attachBaseContext(newBase);
        }
    }

    @Subscribe
    public void onEvent(Throwable t) {

    }

    public void checkApplicationState(){
        ActivityManager.RunningAppProcessInfo myProcess = new ActivityManager.RunningAppProcessInfo();
        ActivityManager.getMyMemoryState(myProcess);

        if(myProcess.importance != ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND){
            Log.e("App State ", "App is in Background");
            this.finishAffinity();
        }else{
            Log.e("App State  ", "App is Active");
        }
    }
}
