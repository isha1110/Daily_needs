package com.skinfotech.dailyneeds.retrofit;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Protocol;
import okhttp3.internal.Util;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitApi {

    private RetrofitApi() {
        /*
         * This is done just to satisfy the sonar
         * */
    }

    private static final String BASE_URL = "https://skinfotechies.xyz/demo/dailyneeds/api/";

    private static AppServices sAppServices = null;

    public static AppServices getAppServicesObject() {
        if (null == sAppServices) {
            HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
            interceptor.level(HttpLoggingInterceptor.Level.BODY);
            OkHttpClient client = new OkHttpClient.Builder().addInterceptor(interceptor).connectTimeout(30, TimeUnit.SECONDS)
                                                            .readTimeout(30, TimeUnit.SECONDS)
                                                            .protocols(Util.immutableListOf(Protocol.HTTP_1_1)).build();
            Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(getGSON()))
                .client(client)
                .build();
            sAppServices = retrofit.create(AppServices.class);
        }
        return sAppServices;
    }

    public static Gson gson = null;
    public static Gson getGSON(){
        if(gson!=null){
            return gson;
        }
        gson = new GsonBuilder().setLenient().create();
        return gson;
    }
}
