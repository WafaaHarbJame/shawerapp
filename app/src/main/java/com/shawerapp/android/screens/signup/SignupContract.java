package com.shawerapp.android.screens.signup;

import android.text.Editable;
import android.view.ViewGroup;
import android.widget.EditText;

import com.shawerapp.android.base.ActivityLifecycle;

import io.reactivex.functions.Consumer;

public interface SignupContract {

    interface View extends ActivityLifecycle.View {

        void initBindings();

        void subscribeErrorTextWatcher(EditText editText);

        void clearErrors();

        void updateTabs(android.view.View view);

        void beginDelayTransition();

        String getUsername();

        String getEmailAddress();

        void setEmailAddressHint(String hint);

        String getPassword();

        String getCompanyName();

        void toggleCompanyName(boolean show);

        String getPhoneNumber();

        void togglePhoneNumber(boolean show);

        void showMessage(String message);

        void showLoadingIndicator();

        void hideLoadingIndicator();

        void toggleLoadingIndicatorUsername(boolean isShow);

        void toggleLoadingIndicatorEmail(boolean isShow);

        void toggleCheckUsername(boolean isShow);

        void toggleCheckEmail(boolean isShow);

        void setUsernameError(String message);

        void setEmailError(String message);

        boolean isTermsAndConditionsChecked();

        void setTermsAndConditionsText(CharSequence text);

        void hideBottomViews();

        void showBottomViews();
    }

    interface ViewModel extends ActivityLifecycle.ViewModel {

        void onGlobalLayoutChanged(ViewGroup activityView);

        void onTabClicked(android.view.View view);

        void checkUsernameAvailability(CharSequence username);

        void checkEmailAvailability(Editable email);

        void onSignupButtonClicked();

        Consumer<? super Throwable> catchErrorThrowable();

        void showActivateActivity();
    }
}
