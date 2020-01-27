package com.shawerapp.android.backend.firebase;

import android.app.Application;
import android.content.Context;

import com.shawerapp.android.R;
import com.shawerapp.android.backend.base.AuthFramework;
import com.shawerapp.android.backend.base.ConnectivityUtils;
import com.shawerapp.android.utils.CommonUtils;
import com.shawerapp.android.utils.LocaleHelper;
import com.shawerapp.android.utils.LoginUtil;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import durdinapps.rxfirebase2.RxFirebaseAuth;
import durdinapps.rxfirebase2.RxFirebaseUser;
import io.reactivex.Completable;
import io.reactivex.Maybe;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Function;
import io.reactivex.internal.operators.completable.CompletableCreate;
import io.reactivex.internal.operators.completable.CompletableError;
import io.reactivex.internal.operators.maybe.MaybeCreate;
import io.reactivex.internal.operators.maybe.MaybeError;
import io.reactivex.internal.operators.maybe.MaybeJust;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

/**
 * Created by john.ernest on 05/10/2017.
 */

public class FirebaseAuthFrameworkImpl implements AuthFramework {

    private Context mContext;

    private FirebaseAuth mFirebaseAuth;

    private LoginUtil mLoginUtil;

    private ConnectivityUtils mUtils;

    private PhoneAuthProvider.ForceResendingToken mForceResendingToken;

    @Inject
    public FirebaseAuthFrameworkImpl(Application application, LoginUtil loginUtil, ConnectivityUtils connectivityUtils) {
        mContext = application.getApplicationContext();
        mLoginUtil = loginUtil;
        mUtils = connectivityUtils;

        mFirebaseAuth = FirebaseAuth.getInstance();
    }

    @Override
    public boolean isLoggedIn() {
        return mFirebaseAuth.getCurrentUser() != null;
    }

    @Override
    public Observable<String> getAuthenticationStatus() {
        return mUtils.withNetworkCheck(RxFirebaseAuth
                .observeAuthState(mFirebaseAuth))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .concatMap((firebaseAuth) -> {
                    if (firebaseAuth.getCurrentUser() != null) {
                        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                        mLoginUtil.setUserID(firebaseUser.getUid());
                        mLoginUtil.setUserLoggedIn(true);

                        if (firebaseUser.isEmailVerified()) {
                            return Observable.just(firebaseUser.getUid());
                        } else {
                            firebaseUser.sendEmailVerification();
                            if (LocaleHelper.getLanguage(mContext).equals("ar")) {
                                return Observable.error(new Throwable("خطأ: يجب أن تقوم بتوثيق حساب بريدك الإلكتروني قبل أن تقوم بإستخدام التطبيق. رسالة التوثيق الإلكترونية قد تستغرق ما يقارب 30 دقيقة."));
                            } else {
                                return Observable.error(new Throwable("You need to verfiy your email before you can use the app. Verfication email may take up to 30 mintues."));
    //                            return Observable.error(new Throwable(mContext.getString(R.string.error_email_not_verified)));
                            }
                        }
                    } else {
                        return Observable.error(new Throwable(mContext.getString(R.string.error_no_user_logged_in)));
                    }
                });
    }

    @Override
    public Completable updateEmail(String newEmail) {
        return mUtils.withNetworkCheck(RxFirebaseUser
                .updateEmail(mFirebaseAuth.getCurrentUser(), newEmail))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public Completable updatePassword(String newPassword) {
        return mUtils.withNetworkCheck(RxFirebaseUser
                .updatePassword(mFirebaseAuth.getCurrentUser(), newPassword))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public Maybe<String> createUserWithEmailAndPassword(String email, String password) {
        return mUtils.withNetworkCheck(RxFirebaseAuth
                .createUserWithEmailAndPassword(mFirebaseAuth, email, password)
                .flatMap(authResult -> RxFirebaseUser.sendEmailVerification(authResult.getUser())
                        .toSingle(() -> authResult.getUser().getUid())
                        .toMaybe()))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public Maybe<String> signInWithEmailAndPassword(String email, String password) {
        return mUtils.withNetworkCheck(RxFirebaseAuth
                .signInWithEmailAndPassword(mFirebaseAuth, email.toLowerCase(), password)
                .flatMap(authResult -> {
                    FirebaseUser firebaseUser = authResult.getUser();
                    mLoginUtil.setUserID(firebaseUser.getUid());
                    mLoginUtil.setUserLoggedIn(true);

                    System.out.println(" " + email.toLowerCase() + " isEmailVerified => " +  firebaseUser.isEmailVerified());
                    if (firebaseUser.isEmailVerified()) {
                        return Maybe.just(firebaseUser.getUid());
                    } else {
                        firebaseUser.sendEmailVerification();
                        if (LocaleHelper.getLanguage(mContext).equals("ar")) {
                            return Maybe.error(new Throwable("خطأ: يجب أن تقوم بتوثيق حساب بريدك الإلكتروني قبل أن تقوم بإستخدام التطبيق. رسالة التوثيق الإلكترونية قد تستغرق ما يقارب 30 دقيقة."));
                        } else {
                            return Maybe.error(new Throwable("You need to verfiy your email before you can use the app. Verfication email may take up to 30 mintues."));
//                            return Maybe.error(new Throwable(mContext.getString(R.string.error_email_not_verified)));
                        }
                    }
                }))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public Maybe<String> signInWithCustomToken(String token) {
        return mUtils.withNetworkCheck(RxFirebaseAuth
                .signInWithCustomToken(mFirebaseAuth, token)
                .map(authResult -> authResult.getUser().getUid()))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public Completable sendPasswordResetEmail(String email) {
        return mUtils.withNetworkCheck(RxFirebaseAuth
                .sendPasswordResetEmail(mFirebaseAuth, email)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread()));
    }

    @Override
    public Completable resendVerificationEmail() {
        if (mFirebaseAuth.getCurrentUser() != null) {
            return mUtils.withNetworkCheck(RxFirebaseUser
                    .sendEmailVerification(mFirebaseAuth.getCurrentUser()))
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread());
        } else {
            return new CompletableError(new Throwable(mContext.getString(R.string.error_no_user_logged_in)));
        }
    }

    @Override
    public Completable reAuthenticate(String email, String password) {
        AuthCredential credential = EmailAuthProvider
                .getCredential(email, password);

        return RxFirebaseUser.reAuthenticate(mFirebaseAuth.getCurrentUser(), credential)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public Maybe<Boolean> isEmailUsed(String email) {
        return mUtils.withNetworkCheck(RxFirebaseAuth
                .fetchProvidersForEmail(mFirebaseAuth, email))
                .map(providerQueryResult -> {
                    Timber.e(providerQueryResult.getProviders().toString());
                    return true;
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public Completable logout() {
        return new CompletableCreate(
                emitter -> {
                    mLoginUtil.clear();
                    mFirebaseAuth.signOut();
                    emitter.onComplete();
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }
}
