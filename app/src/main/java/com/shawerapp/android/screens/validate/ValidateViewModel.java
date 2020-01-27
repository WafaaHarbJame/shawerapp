package com.shawerapp.android.screens.validate;

import android.content.Intent;
import android.os.Bundle;

import com.shawerapp.android.base.BaseActivity;
import com.shawerapp.android.screens.login.LoginActivity;
import com.shawerapp.android.utils.AnimationUtils;

import javax.inject.Inject;

public class ValidateViewModel implements ValidateContract.ViewModel {

    private BaseActivity mActivity;

    private ValidateContract.View mView;

    @Inject
    public ValidateViewModel(BaseActivity activity, ValidateContract.View view) {
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
    public void onKeyboardButtonClicked(int buttonPosition) {
        CharSequence input = String.valueOf(buttonPosition + 1);

        if (buttonPosition == 9) {
            input = "0";
        }
        mView.appendPinInput(input);
    }

    @Override
    public void onDeleteButtonClicked() {
        mView.deleteInput();
    }

    @Override
    public void onActivateButtonClicked() {
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
