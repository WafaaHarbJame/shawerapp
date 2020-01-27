package com.shawerapp.android.screens.login;

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

public class LoginActivity extends BaseActivity implements LoginContract.View {

    @Inject
    LoginContract.ViewModel mViewModel;

    @BindView(R.id.bottomButtons)
    View mBottomButtons;

    @BindView(R.id.login)
    ViewGroup mFragmentView;

    @BindView(R.id.btnForgetPassword)
    View mBtnForgotPassword;

    @BindView(R.id.btnNew)
    View mBtnNew;

    @BindView(R.id.username)
    EditText mUsername;

    @BindView(R.id.password)
    EditText mPassword;

    @BindView(R.id.btnLogin)
    View mBtnLogin;

    private IndeterminateTransparentProgressDialog mIndeterminateTransparentProgressDialog;

    @Override
    public ActivityLifecycle.ViewModel getViewModel() {
        return mViewModel;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        ((ApplicationModel) getApplication()).getAppComponent()
                .plus(new LoginModule(this, this))
                .inject(this);

        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

        super.onCreate(savedInstanceState);
    }

    @Override
    public void initBindings() {
        RxView.globalLayouts(mFragmentView)
                .compose(bindUntilEvent(ActivityEvent.DESTROY))
                .subscribe(o -> {
                    if (CommonUtils.isKeyboardShown(mFragmentView)) {
                        mBottomButtons.setVisibility(View.GONE);
                    } else {
                        mBottomButtons.setVisibility(View.VISIBLE);
                    }
                });

        subscribeErrorTextWatcher(mUsername);
        subscribeErrorTextWatcher(mPassword);

        RxView.clicks(mBtnForgotPassword)
                .throttleFirst(1, TimeUnit.SECONDS)
                .compose(bindUntilEvent(ActivityEvent.DESTROY))
                .subscribe(o -> mViewModel.onForgotPasswordClicked());

        RxView.clicks(mBtnNew)
                .throttleFirst(1, TimeUnit.SECONDS)
                .compose(bindUntilEvent(ActivityEvent.DESTROY))
                .subscribe(o -> mViewModel.onNewButtonClicked());

        RxView.clicks(mBtnLogin)
                .throttleFirst(1, TimeUnit.SECONDS)
                .compose(bindUntilEvent(ActivityEvent.DESTROY))
                .subscribe(o -> mViewModel.onLoginButtonClicked());
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
    public String getUsername() {
        if (CommonUtils.isNotEmpty(mUsername.getText())) {
            return mUsername.getText().toString();
        } else {
            mUsername.setBackgroundResource(R.drawable.input_field_bg_error);
            return null;
        }
    }

    @Override
    public String getPassword() {
        if (CommonUtils.isNotEmpty(mPassword.getText())) {
            return mPassword.getText().toString();
        } else {
            mPassword.setBackgroundResource(R.drawable.input_field_bg_error);
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
}
