package com.shawerapp.android.backend.retrofit;

import android.app.Application;
import android.content.Context;

import com.google.gson.GsonBuilder;
import com.shawerapp.android.application.ApplicationModel;
import com.shawerapp.android.autovalue.AvailabilityCheckResponse;
import com.shawerapp.android.autovalue.annotation.GetEmailResponse;
import com.shawerapp.android.backend.base.ConnectivityUtils;
import com.shawerapp.android.backend.base.RestFramework;
import com.shawerapp.android.utils.CommonUtils;
import com.shawerapp.android.utils.LoginUtil;

import javax.inject.Inject;

import io.reactivex.Completable;
import io.reactivex.Maybe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import timber.log.Timber;

public class RetrofitRestFrameworkImpl implements RestFramework {

    //    private static final String BASE_URL = "http://admin.shawerapp.com/";
    private static final String BASE_URL = "https://shaweradmin.herokuapp.com/";

    private APIServices mAPIServices;

    private Context mContext;

    @Inject
    ConnectivityUtils mUtils;

    @Inject
    LoginUtil mLoginUtil;

    @Inject
    public RetrofitRestFrameworkImpl(Application application) {
        mContext = application;

        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor(message ->
                Timber.tag(ApplicationModel.LOG_TAG)
                        .e(message));
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(interceptor)
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory
                        .create(new GsonBuilder()
                                .registerTypeAdapterFactory(AutoValueGsonFactory.create())
                                .create()))
                .client(client)
                .build();

        mAPIServices = retrofit.create(APIServices.class);
    }

    @Override
    public Maybe<Boolean> checkUsernameAvailability(String username) {
        return mUtils.withNetworkCheck(mAPIServices
                .checkUsernameAvailability(username))
                .flatMap(response -> {
                    if (response.code() == AvailabilityCheckResponse.SUCCESS) {
                        return Maybe.just(response.available());
                    } else {
                        return Maybe.error(new Throwable(response.message()));
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public Maybe<Boolean> checkEmailAvailability(String email) {
        return mUtils.withNetworkCheck(mAPIServices
                .checkEmailAvailability(email))
                .flatMap(response -> {
                    if (response.code() == AvailabilityCheckResponse.SUCCESS) {
                        return Maybe.just(response.available());
                    } else {
                        return Maybe.error(new Throwable(response.message()));
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public Maybe<String> getUserEmail(String username) {
        return mUtils.withNetworkCheck(mAPIServices
                .getUserEmail(username))
                .flatMap(response -> {
                    if (response.code() == GetEmailResponse.SUCCESS) {
                        if (CommonUtils.isNotEmpty(response.email())) {
                            return Maybe.just(response.email());
                        } else {
                            return Maybe.error(new Throwable(response.message()));
                        }
                    } else {
                        return Maybe.error(new Throwable(response.message()));
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

}

