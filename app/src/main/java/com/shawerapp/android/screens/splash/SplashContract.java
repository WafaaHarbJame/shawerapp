package com.shawerapp.android.screens.splash;

import com.shawerapp.android.base.ActivityLifecycle;

public interface SplashContract {

    interface View extends ActivityLifecycle.View {

        void initBindings();

        void startSplashAnimation();

        void showMessage(String message);
    }

    interface ViewModel extends ActivityLifecycle.ViewModel {

        void onSplashAnimationEnd();

        void showContainerActivity(String type);
    }
}
