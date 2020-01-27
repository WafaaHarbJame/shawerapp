package com.shawerapp.android.screens.resetpassword.request;

import android.view.ViewGroup;
import android.widget.EditText;

import com.shawerapp.android.base.ActivityLifecycle;

import io.reactivex.functions.Consumer;

public interface ResetPasswordRequestContract {

    interface View extends ActivityLifecycle.View {

        void initBindings();

        void subscribeErrorTextWatcher(EditText editText);

        String getEmailAddress();

        void showMessage(String message);

        void showLoadingIndicator();

        void hideLoadingIndicator();

        void hideBottomButtons();

        void showBottomButtons();
    }

    interface ViewModel extends ActivityLifecycle.ViewModel {

        void onGlobalLayoutChange(ViewGroup activityView);

        void onLoginToAccountClicked();

        void onNewButtonClicked();

        void onResetPasswordClicked();

        Consumer<? super Throwable> catchErrorThrowable();
    }
}
