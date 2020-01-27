package com.shawerapp.android.screens.validate;

import com.shawerapp.android.base.ActivityLifecycle;

public interface ValidateContract {

    interface View extends ActivityLifecycle.View {

        void initBindings();

        void appendPinInput(CharSequence input);

        void deleteInput();
    }

    interface ViewModel extends ActivityLifecycle.ViewModel {

        void onKeyboardButtonClicked(int buttonPosition);

        void onDeleteButtonClicked();

        void onActivateButtonClicked();
    }
}
