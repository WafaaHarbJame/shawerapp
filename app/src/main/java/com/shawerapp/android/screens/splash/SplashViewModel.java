package com.shawerapp.android.screens.splash;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import com.shawerapp.android.R;
import com.shawerapp.android.autovalue.CommercialUser;
import com.shawerapp.android.autovalue.IndividualUser;
import com.shawerapp.android.autovalue.LawyerUser;
import com.shawerapp.android.backend.base.AuthFramework;
import com.shawerapp.android.backend.base.RealTimeDataFramework;
import com.shawerapp.android.backend.base.RestFramework;
import com.shawerapp.android.base.BaseActivity;
import com.shawerapp.android.screens.container.ContainerActivity;
import com.shawerapp.android.screens.onboarding.OnboardingActivity;
import com.shawerapp.android.utils.AnimationUtils;
import com.shawerapp.android.utils.CommonUtils;
import com.shawerapp.android.utils.LoginUtil;
import com.trello.rxlifecycle2.android.ActivityEvent;

import javax.inject.Inject;

import timber.log.Timber;

public class SplashViewModel implements SplashContract.ViewModel {

    private BaseActivity mActivity;

    private SplashContract.View mView;

    @Inject
    RealTimeDataFramework mRTDataFramework;

    @Inject
    AuthFramework mAuthFramework;

    @Inject
    LoginUtil mLoginUtil;

    @Inject
    public SplashViewModel(BaseActivity activity, SplashContract.View view) {
        mActivity = activity;
        mView = view;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        mView.initBindings();
        mView.startSplashAnimation();
    }

    @SuppressLint("CheckResult")
    @Override
    public void onSplashAnimationEnd() {
        mAuthFramework.getAuthenticationStatus()
                .compose(mActivity.bindUntilEvent(ActivityEvent.DESTROY))
                .subscribe(
                        uid -> mRTDataFramework
                                .fetchUser(uid)
                                .compose(mActivity.bindUntilEvent(ActivityEvent.DESTROY))
                                .subscribe(
                                        user -> {
                                            if (user instanceof IndividualUser && ((IndividualUser) user).status().equalsIgnoreCase(IndividualUser.Status.ACTIVATED)) {
                                                showContainerActivity(ContainerActivity.TYPE_INDIVIDUAL);
                                            } else if (user instanceof CommercialUser && ((CommercialUser) user).status().equalsIgnoreCase(CommercialUser.Status.ACTIVATED)) {
                                                showContainerActivity(ContainerActivity.TYPE_COMMERCIAL);
                                            } else if (user instanceof LawyerUser && ((LawyerUser) user).status().equalsIgnoreCase(LawyerUser.Status.ACTIVATED)) {
                                                showContainerActivity(ContainerActivity.TYPE_LAWYER);
                                            } else {
                                                mAuthFramework
                                                        .logout()
                                                        .subscribe(() -> mView.showMessage(mActivity.getString(R.string.message_activation_error)));
                                            }
                                        }),
                        throwable -> {
                            if (throwable.getMessage().equalsIgnoreCase(mActivity.getApplicationContext().getString(R.string.error_no_user_logged_in)) ||
                                    throwable.getMessage().equalsIgnoreCase(mActivity.getApplicationContext().getString(R.string.error_email_not_verified))) {
                                mAuthFramework
                                        .logout()
                                        .subscribe(() -> {
                                            Intent intent = new Intent(mActivity, OnboardingActivity.class);
                                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                            mActivity.finish();
                                            mActivity.startActivity(intent);
                                            AnimationUtils.overridePendingTransition(mActivity, AnimationUtils.ANIM_STYLE.FADE);
                                        });
                            } else {
                                Timber.e(CommonUtils.getExceptionString(throwable));
                                mView.showMessage(throwable.getMessage());
                            }
                        });
    }

    @Override
    public void showContainerActivity(String type) {
        Intent intent = new Intent(mActivity, ContainerActivity.class);
        intent.putExtra(ContainerActivity.EXTRA_TYPE, type);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        mActivity.finish();
        mActivity.startActivity(intent);
        AnimationUtils.overridePendingTransition(mActivity, AnimationUtils.ANIM_STYLE.FADE);
    }

    @Override
    public void onBackPressed() {
        mActivity.finish();
    }

    @Override
    public void onResume() {

    }

    @Override
    public void onDestroy() {

    }
}
