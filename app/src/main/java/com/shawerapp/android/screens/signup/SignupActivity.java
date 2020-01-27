package com.shawerapp.android.screens.signup;

import android.app.AlertDialog;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.widget.ContentLoadingProgressBar;
import android.text.method.LinkMovementMethod;
import android.util.Patterns;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import com.jakewharton.rxbinding2.view.RxView;
import com.jakewharton.rxbinding2.widget.RxTextView;
import com.shawerapp.android.R;
import com.shawerapp.android.application.ApplicationModel;
import com.shawerapp.android.base.ActivityLifecycle;
import com.shawerapp.android.base.BaseActivity;
import com.shawerapp.android.custom.views.IndeterminateTransparentProgressDialog;
import com.shawerapp.android.utils.CommonUtils;
import com.transitionseverywhere.TransitionManager;
import com.trello.rxlifecycle2.android.ActivityEvent;

import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.BindViews;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SignupActivity extends BaseActivity implements SignupContract.View {

    private static final ButterKnife.Action<? super TextView> TAB_SELECTED = new ButterKnife.Action<TextView>() {
        @Override
        public void apply(@NonNull TextView view, int index) {
            if (index == mSelectedTabIndex) {
                view.setTextColor(ContextCompat.getColor(view.getContext(), R.color.snow));
            } else {
                view.setTextColor(ContextCompat.getColorStateList(view.getContext(), R.color.signup_tabs_text_color));
            }
        }
    };

    private static final int INDIVIDUAL = 0;

    private static final int COMMERCIAL = 1;

    private static final int LAWYER = 2;

    private static int mSelectedTabIndex = INDIVIDUAL;

    @Inject
    SignupContract.ViewModel mViewModel;

    @BindView(R.id.signup)
    ViewGroup mActivityView;

    @BindViews({R.id.individualTab1, R.id.commercialTab1, R.id.lawyerTab1})
    List<TextView> mTabTextList1;

    @BindViews({R.id.individualTab2, R.id.commercialTab2, R.id.lawyerTab2})
    List<TextView> mTabTextList2;

    @BindView(R.id.btnSignUp)
    Button mBtnSignup;

    @BindView(R.id.username)
    EditText mUsername;

    @BindView(R.id.emailAddress)
    EditText mEmailAddress;

    @BindView(R.id.password)
    EditText mPassword;

    @BindView(R.id.companyName)
    EditText mCompanyName;

    @BindView(R.id.phoneNumber)
    EditText mPhoneNumber;

    @BindView(R.id.loadingIndicatorUsername)
    ContentLoadingProgressBar mLoadingIndicatorUsername;

    @BindView(R.id.loadingIndicatorEmail)
    ContentLoadingProgressBar mLoadingIndicatorEmail;

    @BindView(R.id.checkUsername)
    View mCheckUsername;

    @BindView(R.id.checkEmail)
    View mCheckEmail;

    @BindView(R.id.termsAndConditions)
    CheckBox mTermsAndConditions;

    @BindView(R.id.bottomViews)
    ViewGroup mBottomViews;

    private IndeterminateTransparentProgressDialog mIndeterminateTransparentProgressDialog;

    @Override
    public ActivityLifecycle.ViewModel getViewModel() {
        return mViewModel;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        ((ApplicationModel) getApplication()).getAppComponent()
                .plus(new SignupModule(this, this))
                .inject(this);

        setContentView(R.layout.activity_signup);
        ButterKnife.bind(this);

        super.onCreate(savedInstanceState);
    }

    @OnClick({R.id.individualTab, R.id.commercialTab, R.id.lawyerTab})
    public void onTabClicked(View view) {
        mViewModel.onTabClicked(view);
    }

    @Override
    public void initBindings() {
        RxView.globalLayouts(mActivityView)
                .compose(bindUntilEvent(ActivityEvent.DESTROY))
                .subscribe(o -> mViewModel.onGlobalLayoutChanged(mActivityView));

        mTermsAndConditions.setMovementMethod(new LinkMovementMethod());
        RxView.clicks(mBtnSignup)
                .throttleFirst(1, TimeUnit.SECONDS)
                .compose(bindUntilEvent(ActivityEvent.DESTROY))
                .subscribe(o -> mViewModel.onSignupButtonClicked());

        subscribeErrorTextWatcher(mUsername);
        subscribeErrorTextWatcher(mEmailAddress);
        subscribeErrorTextWatcher(mPassword);
        subscribeErrorTextWatcher(mPhoneNumber);
        subscribeErrorTextWatcher(mCompanyName);

        RxTextView.afterTextChangeEvents(mUsername)
                .compose(bindUntilEvent(ActivityEvent.DESTROY))
                .filter(textChangeEvent -> textChangeEvent.editable().length() > 0)
                .throttleLast(700, TimeUnit.MILLISECONDS)
                .subscribe(textChangeEvent -> {
                    if (CommonUtils.isNotEmpty(textChangeEvent.editable())) {
                        mViewModel.checkUsernameAvailability(textChangeEvent.editable());
                    }
                });

        RxTextView.afterTextChangeEvents(mEmailAddress)
                .compose(bindUntilEvent(ActivityEvent.DESTROY))
                .filter(textChangeEvent -> textChangeEvent.editable().length() > 0)
                .filter(textChangeEvent -> {
                    if (Patterns.EMAIL_ADDRESS.matcher(textChangeEvent.editable()).matches()) {
                        return true;
                    } else {
                        mEmailAddress.setError("Incorrect email format");
                        return false;
                    }
                })
                .throttleLast(700, TimeUnit.MILLISECONDS)
                .subscribe(textChangeEvent -> {
                    if (CommonUtils.isNotEmpty(textChangeEvent.editable())) {
                        mViewModel.checkEmailAvailability(textChangeEvent.editable());
                    }
                });
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
    public void clearErrors() {
        mUsername.setError(null);
        mUsername.setBackgroundResource(R.drawable.input_field_bg);
        mEmailAddress.setError(null);
        mEmailAddress.setBackgroundResource(R.drawable.input_field_bg);
        mPassword.setBackgroundResource(R.drawable.input_field_bg);
        mPhoneNumber.setBackgroundResource(R.drawable.input_field_bg);
        mCompanyName.setBackgroundResource(R.drawable.input_field_bg);
    }

    @Override
    public void updateTabs(View view) {
        switch (view.getId()) {
            case R.id.individualTab:
                mSelectedTabIndex = INDIVIDUAL;
                break;
            case R.id.commercialTab:
                mSelectedTabIndex = COMMERCIAL;
                break;
            case R.id.lawyerTab:
                mSelectedTabIndex = LAWYER;
                break;
        }
        ButterKnife.apply(mTabTextList1, TAB_SELECTED);
        ButterKnife.apply(mTabTextList2, TAB_SELECTED);
    }

    @Override
    public void beginDelayTransition() {
        TransitionManager.beginDelayedTransition(mActivityView);
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
    public String getEmailAddress() {
        if (CommonUtils.isNotEmpty(mEmailAddress.getText())) {
            return mEmailAddress.getText().toString();
        } else {
            mEmailAddress.setBackgroundResource(R.drawable.input_field_bg_error);
            return null;
        }
    }

    @Override
    public void setEmailAddressHint(String hint) {
        mEmailAddress.setHint(hint);
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
    public String getCompanyName() {
        if (CommonUtils.isNotEmpty(mCompanyName.getText())) {
            return mCompanyName.getText().toString();
        } else {
            mCompanyName.setBackgroundResource(R.drawable.input_field_bg_error);
            return null;
        }
    }

    @Override
    public void toggleCompanyName(boolean show) {
        if (show) {
            mCompanyName.setVisibility(View.VISIBLE);
        } else {
            mCompanyName.setVisibility(View.GONE);
        }
    }

    @Override
    public String getPhoneNumber() {
        if (CommonUtils.isNotEmpty(mPhoneNumber.getText())) {
            return mPhoneNumber.getText().toString();
        } else {
            mPhoneNumber.setBackgroundResource(R.drawable.input_field_bg_error);
            return null;
        }
    }

    @Override
    public void togglePhoneNumber(boolean show) {
        if (show) {
            mPhoneNumber.setVisibility(View.VISIBLE);
        } else {
            mPhoneNumber.setVisibility(View.GONE);
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
    public void toggleLoadingIndicatorUsername(boolean isShow) {
        mLoadingIndicatorUsername.setVisibility(isShow ? View.VISIBLE : View.GONE);
    }

    @Override
    public void toggleLoadingIndicatorEmail(boolean isShow) {
        mLoadingIndicatorEmail.setVisibility(isShow ? View.VISIBLE : View.GONE);
    }

    @Override
    public void toggleCheckUsername(boolean isShow) {
        mCheckUsername.setVisibility(isShow ? View.VISIBLE : View.GONE);
    }

    @Override
    public void toggleCheckEmail(boolean isShow) {
        mCheckEmail.setVisibility(isShow ? View.VISIBLE : View.GONE);
    }

    @Override
    public void setUsernameError(String message) {
        mUsername.setError(message);
    }

    @Override
    public void setEmailError(String message) {
        mEmailAddress.setError(message);
    }

    @Override
    public boolean isTermsAndConditionsChecked() {
        return mTermsAndConditions.isChecked();
    }

    @Override
    public void setTermsAndConditionsText(CharSequence text) {
        mTermsAndConditions.setText(text);
    }

    @Override
    public void hideBottomViews() {
        mBottomViews.setVisibility(View.GONE);
    }

    @Override
    public void showBottomViews() {
        mBottomViews.setVisibility(View.VISIBLE);
    }
}
