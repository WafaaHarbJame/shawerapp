package com.shawerapp.android.screens.login;

import android.widget.EditText;

import com.shawerapp.android.base.ActivityLifecycle;

import io.reactivex.functions.Consumer;

public interface LoginContract {

    interface View extends ActivityLifecycle.View {
        void initBindings();

        void subscribeErrorTextWatcher(EditText editText);

        String getUsername();

        String getPassword();

        void showMessage(String message);

        void showLoadingIndicator();

        void hideLoadingIndicator();
    }

    interface ViewModel extends ActivityLifecycle.ViewModel {
        void onForgotPasswordClicked();

        void onNewButtonClicked();

        void onLoginButtonClicked();

        Consumer<? super Throwable> catchErrorThrowable();
    }
}
