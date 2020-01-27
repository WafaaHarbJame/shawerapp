package com.shawerapp.android.screens.resetpassword.sent;

import android.content.Intent;
import android.os.Bundle;

import com.shawerapp.android.base.BaseActivity;
import com.shawerapp.android.screens.login.LoginActivity;
import com.shawerapp.android.utils.AnimationUtils;

import javax.inject.Inject;

import static com.shawerapp.android.screens.resetpassword.sent.ResetPasswordSentActivity.EXTRA_EMAIL;

public class ResetPasswordSentViewModel implements ResetPasswordSentContract.ViewModel {

    private BaseActivity mActivity;

    private ResetPasswordSentContract.View mView;

    private String mEmail;

    @Inject
    public ResetPasswordSentViewModel(BaseActivity activity, ResetPasswordSentContract.View view) {
        mActivity = activity;
        mView = view;

        mEmail = activity.getIntent().getExtras().getString(EXTRA_EMAIL);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        mView.initBindings();

        mView.setEmail(mEmail);
    }

    @Override
    public void onBackPressed() {
        onLoginButtonClicked();
    }

    @Override
    public void onLoginButtonClicked() {
        Intent intent = new Intent(mActivity, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        mActivity.startActivity(intent);
        mActivity.finish();
        AnimationUtils.overridePendingTransition(mActivity, AnimationUtils.ANIM_STYLE.SLIDE_IN_FROM_LEFT);
    }

    @Override
    public void onResume() {

    }

    @Override
    public void onDestroy() {

    }
}
