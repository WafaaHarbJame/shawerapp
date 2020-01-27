package com.shawerapp.android.backend.retrofit;

import android.app.Application;
import android.content.Context;

import com.google.gson.GsonBuilder;
import com.shawerapp.android.application.ApplicationModel;
import com.shawerapp.android.autovalue.AvailabilityCheckResponse;
import com.shawerapp.android.autovalue.annotation.GetEmailResponse;
import com.shawerapp.android.backend.base.ConnectivityUtils;
import com.shawerapp.android.backend.base.RestFramework;
import com.shawerapp.android.backend.base.RestFramework_;
import com.shawerapp.android.utils.CommonUtils;
import com.shawerapp.android.utils.LoginUtil;

import javax.inject.Inject;

import io.reactivex.Maybe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import timber.log.Timber;

public class RetrofitRestFrameworkImplCheckPayment implements RestFramework_ {

    //    private static final String BASE_URL = "http://admin.shawerapp.com/";
    private static final String BASE_URL = "https://shaweradmin.herokuapp.com/";
    private static final String S_CHECK_INVOICE = "https://srstaging.stspayone.com/SmartRoutePaymentWeb/";


    private APIServices mAPIServices;

    private Context mContext;

    @Inject
    ConnectivityUtils mUtils;

    @Inject
    LoginUtil mLoginUtil;

    @Inject
    public RetrofitRestFrameworkImplCheckPayment(Application application) {
        mContext = application;

        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor(message ->
                Timber.tag(ApplicationModel.LOG_TAG)
                        .e(message));
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(interceptor)
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(S_CHECK_INVOICE)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory
                        .create(new GsonBuilder()
                                .registerTypeAdapterFactory(AutoValueGsonFactory.create())
                                .create()))
                .client(client)
                .build();

        mAPIServices = retrofit.create(APIServices.class);
    }

//    @Override
//    public Maybe<String> getInvoiceStatus(String transActionID, String secureHash) {
//        return mUtils.withNetworkCheck(mAPIServices
//                .getInvoiceStatus("0100000205","2",transActionID,"2.0",secureHash))
//                .flatMap(response -> {
//                    if (response.contains("Response.GatewayStatusCode=0000")) {
////                        if (CommonUtils.isNotEmpty(response)) {
//                        return Maybe.just(response);
//                    } else {
//                        return Maybe.error(new Throwable(response));
//                    }
//                })
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread());
//    }
}
