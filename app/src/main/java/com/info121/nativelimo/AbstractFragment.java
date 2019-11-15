package com.info121.nativelimo;

import android.support.v4.app.Fragment;
import android.util.Log;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by KZHTUN on 3/10/2016.
 */
public class AbstractFragment extends Fragment {
    final String TAG = AbstractActivity.class.getSimpleName();

    public AbstractFragment() {}

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
        Log.e(TAG, "EventBus Registered on Fragment ... " + this.getActivity().getLocalClassName());
    }

        @Override
        public void onStop() {
            super.onStop();
            EventBus.getDefault().unregister(this);
            Log.e(TAG, "EventBus Un-Registered on Fragment ... " + this.getActivity().getLocalClassName());
        }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
    }
}
