package com.shawerapp.android.screens.resetpassword.request;

import android.content.Intent;
import android.os.Bundle;
import android.view.ViewGroup;

import com.google.android.gms.common.internal.service.Common;
import com.shawerapp.android.R;
import com.shawerapp.android.backend.base.AuthFramework;
import com.shawerapp.android.base.BaseActivity;
import com.shawerapp.android.screens.container.ContainerActivity;
import com.shawerapp.android.screens.login.LoginActivity;
import com.shawerapp.android.screens.resetpassword.sent.ResetPasswordSentActivity;
import com.shawerapp.android.screens.signup.SignupActivity;
import com.shawerapp.android.utils.AnimationUtils;
import com.shawerapp.android.utils.CommonUtils;
import com.trello.rxlifecycle2.android.ActivityEvent;

import javax.inject.Inject;

import io.reactivex.functions.Consumer;
import timber.log.Timber;

public class ResetPasswordRequestViewModel implements ResetPasswordRequestContract.ViewModel {

    private BaseActivity mActivity;

    private ResetPasswordRequestContract.View mView;

    @Inject
    AuthFramework mAuthFramework;

    @Inject
    public ResetPasswordRequestViewModel(BaseActivity activity, ResetPasswordRequestContract.View view) {
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
    public void onGlobalLayoutChange(ViewGroup activityView) {
        if (CommonUtils.isKeyboardShown(activityView)) {
            mView.hideBottomButtons();
        } else {
            mView.showBottomButtons();
        }
    }

    @Override
    public void onLoginToAccountClicked() {
        Intent intent = new Intent(mActivity, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        mActivity.startActivity(intent);
        AnimationUtils.overridePendingTransition(mActivity, AnimationUtils.ANIM_STYLE.SLIDE_IN_FROM_LEFT);
    }

    @Override
    public void onNewButtonClicked() {
        Intent intent = new Intent(mActivity, SignupActivity.class);
        mActivity.startActivity(intent);
        AnimationUtils.overridePendingTransition(mActivity, AnimationUtils.ANIM_STYLE.SLIDE_IN_FROM_RIGHT);
    }

    @Override
    public void onResetPasswordClicked() {
        String email = mView.getEmailAddress();

        if (CommonUtils.isNotEmpty(email)) {
            mAuthFramework
                    .sendPasswordResetEmail(email)
                    .compose(mActivity.bindUntilEvent(ActivityEvent.DESTROY))
                    .doOnSubscribe(disposable -> mView.showLoadingIndicator())
                    .doFinally(mView::hideLoadingIndicator)
                    .subscribe(
                            () -> {
                                Intent intent = new Intent(mActivity, ResetPasswordSentActivity.class);
                                intent.putExtra(ResetPasswordSentActivity.EXTRA_EMAIL, email);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                mActivity.finish();
                                mActivity.startActivity(intent);
                                AnimationUtils.overridePendingTransition(mActivity, AnimationUtils.ANIM_STYLE.FADE);
                            },
                            catchErrorThrowable());
        } else {
            mView.showMessage(mActivity.getString(R.string.error_input_email_address));
        }
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
