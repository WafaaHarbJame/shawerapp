package com.shawerapp.android.screens.onboarding;

import android.content.Intent;
import android.os.Bundle;

import com.shawerapp.android.base.BaseActivity;
import com.shawerapp.android.screens.login.LoginActivity;
import com.shawerapp.android.screens.signup.SignupActivity;
import com.shawerapp.android.utils.AnimationUtils;

import javax.inject.Inject;

public class OnboardingViewModel implements OnboardingContract.ViewModel {

    private BaseActivity mActivity;

    private OnboardingContract.View mView;

    @Inject
    public OnboardingViewModel(BaseActivity activity, OnboardingContract.View view) {
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
    public void onRegisterButtonClicked() {
        Intent intent = new Intent(mActivity, SignupActivity.class);
        mActivity.startActivity(intent);
        AnimationUtils.overridePendingTransition(mActivity, AnimationUtils.ANIM_STYLE.SLIDE_IN_FROM_RIGHT);
    }

    @Override
    public void onLoginButtonClicked() {
        Intent intent = new Intent(mActivity, LoginActivity.class);
        mActivity.startActivity(intent);
        AnimationUtils.overridePendingTransition(mActivity, AnimationUtils.ANIM_STYLE.SLIDE_IN_FROM_RIGHT);
    }

    @Override
    public void onResume() {

    }

    @Override
    public void onDestroy() {

    }
}
