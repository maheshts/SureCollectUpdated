package com.loan.recovery.retrofit;

import android.content.Context;

import androidx.viewbinding.BuildConfig;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.loan.recovery.LoanApplication;
import com.loan.recovery.util.AppConstants;
import okhttp3.logging.HttpLoggingInterceptor;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by mjonnakuti on 15/03/2020.
 */

public class ApiClient {

    private static Retrofit retrofit = null;

    public static Retrofit getClient(Context context, int order) {
        Gson gson = new GsonBuilder()
                .setLenient()
                .create();

        OkHttpClient.Builder oktHttpClient = new OkHttpClient.Builder()
                .addInterceptor(new NetworkConnectionInterceptor(context));

//        OkHttpClient okHttpClient = new OkHttpClient.Builder()
//                .connectTimeout(6, TimeUnit.SECONDS)
//                .readTimeout(6, TimeUnit.SECONDS)
//                .writeTimeout(6, TimeUnit.SECONDS)
//                .build();

        OkHttpClient okHttpClient = UnsafeOkHttpClient.getUnsafeOkHttpClient();
      //  OkHttpClient.Builder client = new OkHttpClient.Builder();
        String url = AppConstants.BASE_URL1;
        if (order == 2)
            url = AppConstants.BASE_URL2;
        else if (order == 3)
            url = AppConstants.BASE_URL3;
        else if (order == 4)
            url = AppConstants.BASE_URL4;
        else if (order == 5)
            url = AppConstants.BASE_URL5;

        System.out.println("<=== URL===> " + url);
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(BuildConfig.DEBUG ? HttpLoggingInterceptor.Level.BODY : HttpLoggingInterceptor.Level.NONE);
        oktHttpClient.addInterceptor(loggingInterceptor);
        retrofit = new Retrofit.Builder()
                .baseUrl(url)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .client(okHttpClient)
                .build();
        return retrofit;
    }
}