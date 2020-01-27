package com.shawerapp.android.screens.signup;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ClickableSpan;
import android.view.View;
import android.view.ViewGroup;

import com.shawerapp.android.R;
import com.shawerapp.android.autovalue.CommercialUser;
import com.shawerapp.android.autovalue.IndividualUser;
import com.shawerapp.android.autovalue.LawyerUser;
import com.shawerapp.android.backend.base.AuthFramework;
import com.shawerapp.android.backend.base.RealTimeDataFramework;
import com.shawerapp.android.backend.base.RestFramework;
import com.shawerapp.android.base.BaseActivity;
import com.shawerapp.android.screens.activate.ActivateActivity;
import com.shawerapp.android.utils.AnimationUtils;
import com.shawerapp.android.utils.CommonUtils;
import com.trello.rxlifecycle2.android.ActivityEvent;

import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import io.reactivex.functions.Consumer;
import io.reactivex.processors.BehaviorProcessor;
import timber.log.Timber;

public class SignupViewModel implements SignupContract.ViewModel {

    private BaseActivity mActivity;

    private SignupContract.View mView;

    private BehaviorProcessor<View> mTabClickProcessor = BehaviorProcessor.create();

    private int selectedTabId = R.id.individualTab;

    @Inject
    AuthFramework mAuthFramework;

    @Inject
    RealTimeDataFramework mRTDataFramework;

    @Inject
    RestFramework mRestFramework;

    private BehaviorProcessor<String> mUsernameChangeProcessor = BehaviorProcessor.create();

    private BehaviorProcessor<String> mEmailChangeProcessor = BehaviorProcessor.create();

    private boolean mIsUsernameAvailable;

    private boolean mIsUsernameCheckInProgress;

    private boolean mIsEmailAvailable;

    private boolean mIsEmailCheckInProgress;

    @Inject
    public SignupViewModel(BaseActivity activity, SignupContract.View view) {
        mActivity = activity;
        mView = view;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        mView.initBindings();

        String agreementText = mActivity.getString(R.string.terms_and_privacy_policy_message);
        SpannableStringBuilder builder = new SpannableStringBuilder(agreementText);
        String termsText = mActivity.getString(R.string.terms_and_conditions);
        int termsStart = agreementText.indexOf(termsText);
        if (termsStart != -1) {
            int termsEnd = termsStart + termsText.length();
            builder.setSpan(new ClickableSpan() {
                @Override
                public void onClick(View widget) {
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    String termsLink = CommonUtils.isRTL() ? "http://www.shawerapp.com/TermsConditions_ar.html" : "http://www.shawerapp.com/TermsConditions.html";
                    intent.setData(Uri.parse(termsLink));
                    mActivity.startActivity(intent);
                }
            }, termsStart, termsEnd, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }

        String privacyText = mActivity.getString(R.string.privacy_policy);
        int privacyStart = agreementText.indexOf(privacyText);
        if (privacyStart != -1) {
            int privacyEnd = privacyStart + privacyText.length();
            builder.setSpan(new ClickableSpan() {
                @Override
                public void onClick(View widget) {
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    String privacyLink = CommonUtils.isRTL() ? "http://www.shawerapp.com/PrivacyPolicy_ar.html" : "http://www.shawerapp.com/PrivacyPolicy.html";
                    intent.setData(Uri.parse(privacyLink));
                    mActivity.startActivity(intent);
                }
            }, privacyStart, privacyEnd, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        mView.setTermsAndConditionsText(builder);

        mUsernameChangeProcessor
                .compose(mActivity.bindUntilEvent(ActivityEvent.DESTROY))
                .throttleLast(700, TimeUnit.MILLISECONDS)
                .subscribe(username -> mRestFramework
                        .checkUsernameAvailability(username)
                        .compose(mActivity.bindUntilEvent(ActivityEvent.DESTROY))
                        .doOnSubscribe(subscription -> {
                            mIsUsernameCheckInProgress = true;
                            mActivity.runOnUiThread(() -> {
                                mView.setUsernameError(null);
                                mView.toggleCheckUsername(false);
                                mView.toggleLoadingIndicatorUsername(true);
                            });
                        })
                        .doFinally(() -> {
                            mIsUsernameCheckInProgress = false;
                            mView.toggleLoadingIndicatorUsername(false);
                        })
                        .subscribe(isAvailable -> {
                            mIsUsernameAvailable = isAvailable;
                            if (mIsUsernameAvailable) {
                                mView.toggleCheckUsername(true);
                            } else {
                                mView.setUsernameError("Username is not available");
                            }
                        }));

        mEmailChangeProcessor
                .compose(mActivity.bindUntilEvent(ActivityEvent.DESTROY))
                .throttleLast(700, TimeUnit.MILLISECONDS)
                .subscribe(email -> mRestFramework
                        .checkEmailAvailability(email)
                        .compose(mActivity.bindUntilEvent(ActivityEvent.DESTROY))
                        .doOnSubscribe(subscription -> {
                            mIsEmailCheckInProgress = true;
                            mActivity.runOnUiThread(() -> {
                                mView.setEmailError(null);
                                mView.toggleCheckEmail(false);
                                mView.toggleLoadingIndicatorEmail(true);
                            });
                        })
                        .doFinally(() -> {
                            mIsEmailCheckInProgress = false;
                            mView.toggleLoadingIndicatorEmail(false);
                        })
                        .subscribe(isAvailable -> {
                            mIsEmailAvailable = isAvailable;
                            if (mIsEmailAvailable) {
                                mView.toggleCheckEmail(true);
                            } else {
                                mView.setEmailError("Email is not available");
                            }
                        }));

        mTabClickProcessor
                .throttleFirst(1, TimeUnit.SECONDS)
                .compose(mActivity.bindUntilEvent(ActivityEvent.DESTROY))
                .subscribe(view -> {
                    mView.clearErrors();

                    mView.beginDelayTransition();
                    mView.updateTabs(view);
                    selectedTabId = view.getId();
                    switch (view.getId()) {
                        case R.id.individualTab:
                            mView.setEmailAddressHint(mActivity.getString(R.string.hint_email_address));
                            mView.toggleCompanyName(false);
                            mView.togglePhoneNumber(false);
                            break;
                        case R.id.commercialTab:
                            mView.setEmailAddressHint(mActivity.getString(R.string.hint_work_email_address));
                            mView.toggleCompanyName(true);
                            mView.togglePhoneNumber(false);
                            break;
                        case R.id.lawyerTab:
                            mView.setEmailAddressHint(mActivity.getString(R.string.hint_email_address));
                            mView.toggleCompanyName(false);
                            mView.togglePhoneNumber(true);
                            break;
                    }
                });
    }

    @Override
    public void onGlobalLayoutChanged(ViewGroup activityView) {
        if (CommonUtils.isKeyboardShown(activityView)) {
            mView.hideBottomViews();
        } else {
            mView.showBottomViews();
        }
    }

    @Override
    public void onTabClicked(View view) {
        mTabClickProcessor.onNext(view);
    }

    @Override
    public void checkUsernameAvailability(CharSequence username) {
        mUsernameChangeProcessor.onNext(username.toString());
    }

    @Override
    public void checkEmailAvailability(Editable email) {
        mEmailChangeProcessor.onNext(email.toString());
    }

    @Override
    public void onSignupButtonClicked() {
        if (mIsEmailCheckInProgress || mIsUsernameCheckInProgress) {
            return;
        }

        String username = mView.getUsername();
        String email = mView.getEmailAddress();
        String password = mView.getPassword();
        String companyName = mView.getCompanyName();
        String phoneNumber = mView.getPhoneNumber();

        if (mIsUsernameAvailable && mIsEmailAvailable) {
            switch (selectedTabId) {
                case R.id.individualTab:
                    if (CommonUtils.isNotEmpty(username) && CommonUtils.isNotEmpty(email) && CommonUtils.isNotEmpty(password)) {
                        if (!mView.isTermsAndConditionsChecked()) {
                            mView.showMessage(mActivity.getString(R.string.terms_error));
                            return;
                        }

                        mAuthFramework
                                .createUserWithEmailAndPassword(email, password)
                                .compose(mActivity.bindUntilEvent(ActivityEvent.DESTROY))
                                .doOnSubscribe(disposable -> mView.showLoadingIndicator())
                                .flatMapSingle(uid -> mRTDataFramework
                                        .saveUser(IndividualUser.builder()
                                                .uid(uid)
                                                .role(IndividualUser.ROLE_VALUE)
                                                .username(username)
                                                .emailAddress(email)
                                                .status(IndividualUser.Status.ACTIVATED)
                                                .build()))
                                .flatMapCompletable(user -> mAuthFramework.logout())
                                .doFinally(() -> mView.hideLoadingIndicator())
                                .subscribe(this::showActivateActivity, catchErrorThrowable());
                    } else {
                        mView.showMessage(mActivity.getString(R.string.error_complete_fields));
                    }
                    break;
                case R.id.commercialTab:
                    if (CommonUtils.isNotEmpty(username) && CommonUtils.isNotEmpty(email) && CommonUtils.isNotEmpty(password)
                            && CommonUtils.isNotEmpty(companyName)) {
                        if (!mView.isTermsAndConditionsChecked()) {
                            mView.showMessage(mActivity.getString(R.string.terms_error));
                            return;
                        }

                        mAuthFramework
                                .createUserWithEmailAndPassword(email, password)
                                .compose(mActivity.bindUntilEvent(ActivityEvent.DESTROY))
                                .doOnSubscribe(disposable -> mView.showLoadingIndicator())
                                .flatMapSingle(uid -> mRTDataFramework
                                        .saveUser(CommercialUser.builder()
                                                .uid(uid)
                                                .role(CommercialUser.ROLE_VALUE)
                                                .username(username)
                                                .emailAddress(email)
                                                .companyName(companyName)
                                                .status(CommercialUser.Status.ACTIVATED)
                                                .build()))
                                .flatMapCompletable(user -> mAuthFramework.logout())
                                .doFinally(() -> mView.hideLoadingIndicator())
                                .subscribe(this::showActivateActivity, catchErrorThrowable());
                    } else {
                        mView.showMessage(mActivity.getString(R.string.error_complete_fields));
                    }
                    break;
                case R.id.lawyerTab:
                    if (CommonUtils.isNotEmpty(username) && CommonUtils.isNotEmpty(email) && CommonUtils.isNotEmpty(password)
                            && CommonUtils.isNotEmpty(phoneNumber)) {
                        if (!mView.isTermsAndConditionsChecked()) {
                            mView.showMessage(mActivity.getString(R.string.terms_error));
                            return;
                        }

                        mAuthFramework
                                .createUserWithEmailAndPassword(email, password)
                                .compose(mActivity.bindUntilEvent(ActivityEvent.DESTROY))
                                .doOnSubscribe(disposable -> mView.showLoadingIndicator())
                                .flatMapSingle(uid -> mRTDataFramework
                                        .saveUser(LawyerUser.builder()
                                                .uid(uid)
                                                .role(LawyerUser.ROLE_VALUE)
                                                .username(username)
                                                .emailAddress(email)
                                                .phoneNumber(phoneNumber)
                                                .status(LawyerUser.Status.PENDING)
                                                .build()))
                                .flatMapCompletable(user -> mAuthFramework.logout())
                                .doFinally(() -> mView.hideLoadingIndicator())
                                .subscribe(this::showActivateActivity, catchErrorThrowable());
                    } else {
                        mView.showMessage(mActivity.getString(R.string.error_complete_fields));
                    }
                    break;
            }
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
    public void showActivateActivity() {
        Intent intent = new Intent(mActivity, ActivateActivity.class);
        mActivity.startActivity(intent);
        AnimationUtils.overridePendingTransition(mActivity, AnimationUtils.ANIM_STYLE.SLIDE_IN_FROM_RIGHT);
    }

    @Override
    public void onBackPressed() {
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
