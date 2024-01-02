package com.info121.nativelimo.api;

import android.content.DialogInterface;


import androidx.appcompat.app.AlertDialog;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.info121.nativelimo.App;
import com.info121.nativelimo.R;
import com.info121.nativelimo.models.ObjectRes;
import com.info121.nativelimo.utils.Util;

import org.greenrobot.eventbus.EventBus;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;


/**
 * Created by KZHTUN on 2/16/2017.
 */

public class RestClient {
   // private static final String MAI_URL = "http://8mapi.mywebcheck.in/AIRLINEAPI/AWS.svc/";

    private static String AuthToken = "";
    private static RestClient instance = null;
    private static int callCount = 10;
    private APIService service;



    private RestClient() {

        Gson gson = new GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.UPPER_CAMEL_CASE).create();

        Retrofit retrofit = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(App.CONST_REST_API_URL)
                .client(new OkHttpClient().newBuilder()
                        .addInterceptor(new Interceptor() {
                            @Override
                            public Response intercept(Chain chain) throws IOException {
                                Request original = chain.request();
                                HttpUrl originalHttpUrl = original.url();

                                HttpUrl newUrl = originalHttpUrl.newBuilder()
                                        .build();


                                Request newRequest = original.newBuilder()
                                        .header("driver", App.userName)
                                        .header("token", App.authToken)
                                        .url(newUrl)
                                        .method(original.method(), original.body())
                                        .build();

                                return chain.proceed(newRequest);
                            }
                        })
                        .connectTimeout(60, TimeUnit.SECONDS)
                        .readTimeout(60, TimeUnit.SECONDS)
                        .build()
                ).build();

        service = retrofit.create(APIService.class);

    }

    public APIService getApiService() {
        return service;
    }

    public static RestClient COACH() {
        if (instance == null) {
            instance = new RestClient();
        }

//        instance.service.CheckSession().enqueue(new Callback<ObjectRes>() {
//            @Override
//            public void onResponse(Call<ObjectRes> call, retrofit2.Response<ObjectRes> response) {
//                if(response.body().getResponsemessage().equalsIgnoreCase("VALID")) {
//                    Log.e("RESTCLIENT ", "VALID ..... ");
//
//                }
//
//                if(response.body().getResponsemessage().equalsIgnoreCase("BAD TOKEN")) {
//                    Log.e("RESTCLIENT ", "BAD TOKEN ***** ");
//
//
//                }
//            }
//
//            @Override
//            public void onFailure(Call<ObjectRes> call, Throwable t) {
//
//            }
//        });


        return instance;
    }


    public static void refreshToken(final String action){
        instance.service.ValidateDriver(App.userName).enqueue(new Callback<ObjectRes>() {
            @Override
            public void onResponse(Call<ObjectRes> call, retrofit2.Response<ObjectRes> response) {
                if(response.body().getResponsemessage().equalsIgnoreCase("VALID")) {
                    App.authToken = response.body().getToken();

                    EventBus.getDefault().post(action);
                }
            }

            @Override
            public void onFailure(Call<ObjectRes> call, Throwable t) {

            }
        });
    }

    private static void showRefreshDialog() {

        AlertDialog dialog = new AlertDialog.Builder(App.targetContent)
                .setTitle(R.string.AppName)
                .setMessage("Session expired. \nWould you like to extend ?")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .create();



        dialog.show();
    }


    public static void Dismiss(){
        instance = null;
    }

}
