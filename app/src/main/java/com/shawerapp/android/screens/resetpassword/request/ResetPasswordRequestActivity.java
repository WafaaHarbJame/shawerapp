package com.shawerapp.android.screens.resetpassword.request;

import android.app.AlertDialog;
import android.os.Bundle;
import androidx.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.jakewharton.rxbinding2.view.RxView;
import com.jakewharton.rxbinding2.widget.RxTextView;
import com.shawerapp.android.R;
import com.shawerapp.android.application.ApplicationModel;
import com.shawerapp.android.base.ActivityLifecycle;
import com.shawerapp.android.base.BaseActivity;
import com.shawerapp.android.custom.views.IndeterminateTransparentProgressDialog;
import com.shawerapp.android.utils.CommonUtils;
import com.trello.rxlifecycle2.android.ActivityEvent;

import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ResetPasswordRequestActivity extends BaseActivity implements ResetPasswordRequestContract.View {

    @Inject
    ResetPasswordRequestContract.ViewModel mViewModel;

    @BindView(R.id.forgotPassword)
    ViewGroup mActivityView;

    @BindView(R.id.btnLoginToAccount)
    View mBtnLoginToAccount;

    @BindView(R.id.btnNew)
    View mBtnNew;

    @BindView(R.id.btnResetPassword)
    View mBtnResetPassword;

    @BindView(R.id.emailAddress)
    EditText mEmailAddress;

    @BindView(R.id.bottomButtons)
    ViewGroup mBottomButtons;

    private IndeterminateTransparentProgressDialog mIndeterminateTransparentProgressDialog;

    @Override
    public ActivityLifecycle.ViewModel getViewModel() {
        return mViewModel;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        ((ApplicationModel) getApplication()).getAppComponent()
                .plus(new ResetPasswordRequestModule(this, this))
                .inject(this);

        setContentView(R.layout.activity_reset_password_request);
        ButterKnife.bind(this);

        super.onCreate(savedInstanceState);
    }

    @Override
    public void initBindings() {
        RxView.globalLayouts(mActivityView)
                .compose(bindUntilEvent(ActivityEvent.DESTROY))
                .subscribe(o -> mViewModel.onGlobalLayoutChange(mActivityView));

        subscribeErrorTextWatcher(mEmailAddress);

        RxView.clicks(mBtnLoginToAccount)
                .throttleFirst(1, TimeUnit.SECONDS)
                .compose(bindUntilEvent(ActivityEvent.DESTROY))
                .subscribe(o -> mViewModel.onLoginToAccountClicked());

        RxView.clicks(mBtnNew)
                .throttleFirst(1, TimeUnit.SECONDS)
                .compose(bindUntilEvent(ActivityEvent.DESTROY))
                .subscribe(o -> mViewModel.onNewButtonClicked());

        RxView.clicks(mBtnResetPassword)
                .throttleFirst(1, TimeUnit.SECONDS)
                .compose(bindUntilEvent(ActivityEvent.DESTROY))
                .subscribe(o -> mViewModel.onResetPasswordClicked());
    }

    @Override
    public void subscribeErrorTextWatcher(EditText editText) {
        RxTextView.afterTextChangeEvents(editText)
                .compose(bindUntilEvent(ActivityEvent.DESTROY))
                .subscribe(textChangeEvent -> {
                    if (CommonUtils.isNotEmpty(textChangeEvent.editable())) {
                        editText.setBackgroundResource(R.drawable.input_field_bg);
                    }
                });
    }

    @Override
    public String getEmailAddress() {
        if (CommonUtils.isNotEmpty(mEmailAddress.getText())) {
            return mEmailAddress.getText().toString();
        } else {
            mEmailAddress.setBackgroundResource(R.drawable.input_field_bg_error);
            return null;
        }
    }

    @Override
    public void showMessage(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.DialogTheme);
        builder.setMessage(message);
        builder.setPositiveButton(R.string.ok_caps, (dialog, which) -> dialog.dismiss());
        builder.show();
    }

    @Override
    public void showLoadingIndicator() {
        if (mIndeterminateTransparentProgressDialog == null ||
                !mIndeterminateTransparentProgressDialog.isShowing()) {
            runOnUiThread(() -> mIndeterminateTransparentProgressDialog = IndeterminateTransparentProgressDialog.show(this, true, false));
        }
    }

    @Override
    public void hideLoadingIndicator() {
        if (mIndeterminateTransparentProgressDialog != null || mIndeterminateTransparentProgressDialog.isShowing()) {
            mIndeterminateTransparentProgressDialog.dismiss();
        }
    }

    @Override
    public void hideBottomButtons() {
        mBottomButtons.setVisibility(View.GONE);
    }

    @Override
    public void showBottomButtons() {
        mBottomButtons.setVisibility(View.VISIBLE);
    }
}
