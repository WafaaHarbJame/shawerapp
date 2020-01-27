package com.shawerapp.android.screens.resetpassword.sent;

import com.shawerapp.android.base.ActivityLifecycle;

public interface ResetPasswordSentContract {

    interface View extends ActivityLifecycle.View {

        void initBindings();

        void setEmail(String email);
    }

    interface ViewModel extends ActivityLifecycle.ViewModel {

        void onLoginButtonClicked();
    }
}
