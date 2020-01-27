package com.shawerapp.android.screens.activate;

import android.content.Intent;
import android.os.Bundle;

import com.shawerapp.android.base.BaseActivity;
import com.shawerapp.android.screens.login.LoginActivity;
import com.shawerapp.android.screens.validate.ValidateActivity;
import com.shawerapp.android.utils.AnimationUtils;

import javax.inject.Inject;

public class ActivateViewModel implements ActivateContract.ViewModel {

    private BaseActivity mActivity;

    private ActivateContract.View mView;

    @Inject
    public ActivateViewModel(BaseActivity activity, ActivateContract.View view) {
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
    public void onOKButtonClicked() {
        Intent intent = new Intent(mActivity, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
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
