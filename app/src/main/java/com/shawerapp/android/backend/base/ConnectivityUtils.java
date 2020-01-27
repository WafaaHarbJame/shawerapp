package com.shawerapp.android.backend.base;

import android.app.Application;
import android.content.Context;

import  com.shawerapp.android.R;
import  com.shawerapp.android.utils.CommonUtils;
import com.google.firebase.FirebaseNetworkException;

import java.util.concurrent.Callable;

import javax.inject.Inject;

import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.Maybe;
import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.annotations.NonNull;

/**
 * Created by john.ernest on 10/10/2017.
 */

public class ConnectivityUtils {

    private Context mContext;

    @Inject
    public ConnectivityUtils(Application application) {
        mContext = application.getApplicationContext();
    }

    private Callable<? extends Throwable> asFirebaseException(Throwable throwable) {
        return () -> {
            if (throwable instanceof FirebaseNetworkException) {
                final String err = mContext.getString(R.string.error_no_connection);
                return new Throwable(err);
            }
            return throwable;
        };
    }

    public <T> Observable<T> withNetworkCheck(@NonNull Observable<T> continuation) {
        if (CommonUtils.hasConnection(mContext)) {
            return continuation.onErrorResumeNext(throwable -> {
                return Observable.error(asFirebaseException(throwable));
            });
        }
        final String err = mContext.getString(R.string.error_no_connection);
        return Observable.error(new Throwable(err));
    }

    public <T> Flowable<T> withNetworkCheck(@NonNull Flowable<T> continuation) {
        if (CommonUtils.hasConnection(mContext)) {
            return continuation.onErrorResumeNext(throwable -> {
                return Flowable.error(asFirebaseException(throwable));
            });
        }
        final String err = mContext.getString(R.string.error_no_connection);
        return Flowable.error(new Throwable(err));
    }

    public <T> Single<T> withNetworkCheck(@NonNull Single<T> continuation) {
        if (CommonUtils.hasConnection(mContext)) {
            return continuation.onErrorResumeNext(throwable -> Single.error(asFirebaseException(throwable)));
        }
        final String err = mContext.getString(R.string.error_no_connection);
        return Single.error(new Throwable(err));
    }

    public Completable withNetworkCheck(@NonNull Completable continuation) {
        if (CommonUtils.hasConnection(mContext)) {
            return continuation.onErrorResumeNext(throwable -> Completable.error(asFirebaseException(throwable)));
        }
        final String err = mContext.getString(R.string.error_no_connection);
        return Completable.error(new Throwable(err));
    }

    public <T> Maybe<T> withNetworkCheck(@NonNull Maybe<T> continuation) {
        if (CommonUtils.hasConnection(mContext)) {
            return continuation.onErrorResumeNext(throwable -> {
                return Maybe.error(asFirebaseException(throwable));
            });
        }
        final String err = mContext.getString(R.string.error_no_connection);
        return Maybe.error(new Throwable(err));
    }
}
