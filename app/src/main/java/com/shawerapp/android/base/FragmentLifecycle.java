package com.shawerapp.android.base;

/**
 * Created by Ernest on 1/20/2017.
 */

public interface FragmentLifecycle {

    interface View {

    }

    interface ViewModel {

        void onViewCreated();

        void onAfterEnterAnimation();

        void onBackButtonClicked();

        void setupToolbar();

        void onLeftToolbarButtonClicked();

        void onRightToolbarButtonClicked();

        void onStart();

        void onStop();

        void onDetach();

        void onDestroy();
    }
}
