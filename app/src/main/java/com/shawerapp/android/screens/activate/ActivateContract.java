package com.shawerapp.android.screens.activate;

import com.shawerapp.android.base.ActivityLifecycle;

public interface ActivateContract {

    interface View extends ActivityLifecycle.View {

        void initBindings();
    }

    interface ViewModel extends ActivityLifecycle.ViewModel {

        void onOKButtonClicked();
    }
}
