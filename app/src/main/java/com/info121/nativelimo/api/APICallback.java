package com.info121.nativelimo.api;

import android.util.Log;

import org.greenrobot.eventbus.EventBus;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public abstract class APICallback<T> implements Callback<T> {
    private static final String TAG = APIClient.class.getSimpleName();

    public void onResponse(Call<T> call, Response<T> response) {
        if (response.body() != null) {
            EventBus.getDefault().post(response.body());
            Log.e(TAG, "Retrofit call success response [ " + response.body().toString() + " ] ");
        }

    }


    public void onFailure(Call<T> call, Throwable t) {
        EventBus.getDefault().post(t);
        Log.e(TAG, "Retrofit call fail : " + t.getMessage());
    }

}
