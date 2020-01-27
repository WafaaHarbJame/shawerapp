package com.shawerapp.android.screens.resetpassword.sent;

import android.os.Bundle;
import androidx.annotation.Nullable;
import android.view.View;
import android.widget.TextView;

import com.jakewharton.rxbinding2.view.RxView;
import com.shawerapp.android.R;
import com.shawerapp.android.application.ApplicationModel;
import com.shawerapp.android.base.ActivityLifecycle;
import com.shawerapp.android.base.BaseActivity;
import com.trello.rxlifecycle2.android.ActivityEvent;

import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ResetPasswordSentActivity extends BaseActivity implements ResetPasswordSentContract.View {

    public static final String EXTRA_EMAIL = "email";

    @Inject
    ResetPasswordSentContract.ViewModel mViewModel;

    @BindView(R.id.btnLogin)
    View mBtnLogin;

    @BindView(R.id.emailAddress)
    TextView mEmailAddress;

    @Override
    public ActivityLifecycle.ViewModel getViewModel() {
        return mViewModel;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        ((ApplicationModel) getApplication()).getAppComponent()
                .plus(new ResetPasswordSentModule(this, this))
                .inject(this);

        setContentView(R.layout.activity_reset_password_sent);
        ButterKnife.bind(this);

        super.onCreate(savedInstanceState);
    }

    @Override
    public void initBindings() {
        RxView.clicks(mBtnLogin)
                .throttleFirst(1, TimeUnit.SECONDS)
                .compose(bindUntilEvent(ActivityEvent.DESTROY))
                .subscribe(o -> mViewModel.onLoginButtonClicked());
    }

    @Override
    public void setEmail(String email) {
        mEmailAddress.setText(email);
    }
}
