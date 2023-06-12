package com.info121.nativelimo;

import android.content.Context;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

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
        super.onStart();

        // event bus register
        EventBus.getDefault().register(this);
        Log.e(TAG, "EventBus Registered on Activity ... " + this.getLocalClassName());
    }

    @Override
    protected void onStop() {
        // event bus unregister
        EventBus.getDefault().unregister(this);
        Log.e(TAG, "EventBus Un-Registered on Activity ... " + this.getLocalClassName());

        super.onStop();
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
}
