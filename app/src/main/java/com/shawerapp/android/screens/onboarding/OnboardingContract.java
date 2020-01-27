package com.shawerapp.android.screens.onboarding;

import com.shawerapp.android.base.ActivityLifecycle;

public interface OnboardingContract {

    interface View extends ActivityLifecycle.View {

        void initBindings();
    }

    interface ViewModel extends ActivityLifecycle.ViewModel {

        void onRegisterButtonClicked();

        void onLoginButtonClicked();
    }
}
