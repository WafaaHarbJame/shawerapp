package com.shawerapp.android.backend.retrofit;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public interface API_Client {

    String S_CHECK_INVOICE = "https://srstaging.stspayone.com/SmartRoutePaymentWeb/";

    static Retrofit getClient() {
        Gson gson = new GsonBuilder()
                .setLenient()
                .create();

        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();

//        httpClient.addInterceptor(chain -> {
//            Request original = chain.request();
//
//            Request request = null ;
//
//            request = original.newBuilder()
//                    .header("Accept", "application/json")
////                    .method(original.method(), original.body())
//                    .build();
//            return chain.proceed(request);
//
//        });

        OkHttpClient client = httpClient.build();

        return new Retrofit.Builder()
                .baseUrl(S_CHECK_INVOICE)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .client(client)
                .build();
    }

}
