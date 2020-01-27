package com.shawerapp.android.screens.login;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;
import com.shawerapp.android.MainActivity;
import com.shawerapp.android.R;
import com.shawerapp.android.autovalue.CommercialUser;
import com.shawerapp.android.autovalue.IndividualUser;
import com.shawerapp.android.autovalue.LawyerUser;
import com.shawerapp.android.backend.base.AuthFramework;
import com.shawerapp.android.backend.base.RealTimeDataFramework;
import com.shawerapp.android.backend.base.RestFramework;
import com.shawerapp.android.base.BaseActivity;
import com.shawerapp.android.screens.container.ContainerActivity;
import com.shawerapp.android.screens.loginbytype.LoginByTypeActivity;
import com.shawerapp.android.screens.onboarding.OnboardingActivity;
import com.shawerapp.android.screens.resetpassword.request.ResetPasswordRequestActivity;
import com.shawerapp.android.screens.signup.SignupActivity;
import com.shawerapp.android.utils.AnimationUtils;
import com.shawerapp.android.utils.CommonUtils;
import com.shawerapp.android.utils.LoginUtil;
import com.trello.rxlifecycle2.android.ActivityEvent;

import javax.inject.Inject;

import androidx.annotation.NonNull;
import io.reactivex.Maybe;
import io.reactivex.functions.Consumer;
import timber.log.Timber;

public class LoginViewModel implements LoginContract.ViewModel {

    private BaseActivity mActivity;

    private LoginContract.View mView;

    @Inject
    AuthFramework mAuthFramework;

    @Inject
    RealTimeDataFramework mRTDataFramework;

    @Inject
    RestFramework mRestFramework;

    @Inject
    LoginUtil mLogintUtil;

    @Inject
    public LoginViewModel(BaseActivity activity, LoginContract.View view) {
        mActivity = activity;
        mView = view;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        mView.initBindings();
    }

    @Override
    public void onBackPressed() {
        mActivity.finish();
        AnimationUtils.overridePendingTransition(mActivity, AnimationUtils.ANIM_STYLE.SLIDE_IN_FROM_LEFT);
    }

    @Override
    public void onForgotPasswordClicked() {
        Intent intent = new Intent(mActivity, ResetPasswordRequestActivity.class);
        mActivity.startActivity(intent);
        AnimationUtils.overridePendingTransition(mActivity, AnimationUtils.ANIM_STYLE.SLIDE_IN_FROM_RIGHT);
    }

    @Override
    public void onNewButtonClicked() {
        Intent intent = new Intent(mActivity, SignupActivity.class);
        mActivity.startActivity(intent);
        AnimationUtils.overridePendingTransition(mActivity, AnimationUtils.ANIM_STYLE.SLIDE_IN_FROM_RIGHT);
    }

    @Override
    public void onLoginButtonClicked() {
        String username = mView.getUsername();
        String password = mView.getPassword();

        Maybe<String> signInFlow;
        if (Patterns.EMAIL_ADDRESS.matcher(username).matches()) {
            signInFlow = mAuthFramework
                    .signInWithEmailAndPassword(username, password)
                    .compose(mActivity.bindUntilEvent(ActivityEvent.DESTROY))
                    .doOnSubscribe(disposable -> mView.showLoadingIndicator())
                    .flatMap(userUid -> mRTDataFramework.fetchUser(userUid)
                            .flatMapMaybe(user -> {
                                if (user instanceof IndividualUser &&
                                        ((IndividualUser) user).status().equalsIgnoreCase(IndividualUser.Status.ACTIVATED)) {
                                    mLogintUtil.setUserRole(IndividualUser.ROLE_VALUE);
                                    mLogintUtil.setUsername(((IndividualUser) user).username());
                                    FirebaseMessaging.getInstance().subscribeToTopic("individual")
                                            .addOnCompleteListener(task -> {
                                                System.out.println("Done => " + task.toString());
                                            });

                                    return Maybe.just(ContainerActivity.TYPE_INDIVIDUAL);
                                } else if (user instanceof CommercialUser &&
                                        ((CommercialUser) user).status().equalsIgnoreCase(CommercialUser.Status.ACTIVATED)) {
                                    mLogintUtil.setUsername(((CommercialUser) user).username());
                                    mLogintUtil.setUserRole(CommercialUser.ROLE_VALUE);
                                    return Maybe.just(ContainerActivity.TYPE_COMMERCIAL);
                                } else if (user instanceof LawyerUser &&
                                        ((LawyerUser) user).status().equalsIgnoreCase(LawyerUser.Status.ACTIVATED)) {
                                    mLogintUtil.setUserRole(LawyerUser.ROLE_VALUE);
                                    mLogintUtil.setUsername(((LawyerUser) user).username());
                                    FirebaseMessaging.getInstance().subscribeToTopic("lawyer")
                                            .addOnCompleteListener(task -> {

                                            });
                                    return Maybe.just(ContainerActivity.TYPE_LAWYER);
                                } else {
                                    return mAuthFramework.logout()
                                            .andThen(Maybe.error(new Throwable(mActivity.getString(R.string.message_activation_error))));
                                }
                            }))
                    .doFinally(mView::hideLoadingIndicator);
        } else {
            signInFlow = mRestFramework
                    .getUserEmail(username)
                    .compose(mActivity.bindUntilEvent(ActivityEvent.DESTROY))
                    .doOnSubscribe(disposable -> mView.showLoadingIndicator())
                    .flatMap(email -> mAuthFramework
                            .signInWithEmailAndPassword(email, password))
                    .flatMap(userUid -> mRTDataFramework.fetchUser(userUid)
                            .flatMapMaybe(user -> {
                                mLogintUtil.setUsername(username);

                                if (user instanceof IndividualUser &&
                                        ((IndividualUser) user).status().equalsIgnoreCase(IndividualUser.Status.ACTIVATED)) {
                                    mLogintUtil.setUserRole(IndividualUser.ROLE_VALUE);
                                    FirebaseMessaging.getInstance().subscribeToTopic("individual")
                                            .addOnCompleteListener(task -> {
                                                System.out.println("Done => " + task.toString());
                                            });
                                    return Maybe.just(ContainerActivity.TYPE_INDIVIDUAL);
                                } else if (user instanceof CommercialUser &&
                                        ((CommercialUser) user).status().equalsIgnoreCase(CommercialUser.Status.ACTIVATED)) {
                                    mLogintUtil.setUserRole(CommercialUser.ROLE_VALUE);
                                    return Maybe.just(ContainerActivity.TYPE_COMMERCIAL);
                                } else if (user instanceof LawyerUser &&
                                        ((LawyerUser) user).status().equalsIgnoreCase(LawyerUser.Status.ACTIVATED)) {
                                    FirebaseMessaging.getInstance().subscribeToTopic("lawyer")
                                            .addOnCompleteListener(task -> {
                                                System.out.println("Done => " + task.toString());
                                            });
                                    mLogintUtil.setUserRole(LawyerUser.ROLE_VALUE);
                                    return Maybe.just(ContainerActivity.TYPE_LAWYER);
                                } else {
                                    return mAuthFramework.logout()
                                            .andThen(Maybe.error(new Throwable(mActivity.getString(R.string.message_activation_error))));
                                }
                            }))
                    .doFinally(mView::hideLoadingIndicator);
        }

        signInFlow.subscribe(
                type -> {
                    Intent intent = new Intent(mActivity, ContainerActivity.class);
                    intent.putExtra(ContainerActivity.EXTRA_TYPE, type);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    mActivity.finish();
                    mActivity.startActivity(intent);
                    AnimationUtils.overridePendingTransition(mActivity, AnimationUtils.ANIM_STYLE.FADE);
                },
                throwable -> {
                    if (throwable.getMessage().equalsIgnoreCase(mActivity.getString(R.string.error_email_not_verified))) {
                        mAuthFramework
                                .logout()
                                .subscribe(() -> mView.showMessage(throwable.getMessage()));
                    } else {
                        Timber.e(CommonUtils.getExceptionString(throwable));
                        mView.showMessage(throwable.getMessage());
                    }
                });
    }

    @Override
    public Consumer<? super Throwable> catchErrorThrowable() {
        return throwable -> {
            Timber.e(CommonUtils.getExceptionString(throwable));
            mView.showMessage(throwable.getMessage());
        };
    }

    @Override
    public void onResume() {

    }

    @Override
    public void onDestroy() {

    }
}
