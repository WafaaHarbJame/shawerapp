package com.shawerapp.android.screens.container;

import android.graphics.Point;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;

import com.shawerapp.android.base.ActivityLifecycle;
import com.shawerapp.android.base.BaseFragment;
import com.shawerapp.android.base.BaseKey;

import io.reactivex.Completable;
import io.reactivex.CompletableObserver;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;

/**
 * Created by john.ernest on 2/16/18.
 */

public interface ContainerContract {

    interface View extends ActivityLifecycle.View {

        ViewGroup getActivityView();

        void initBindings(String type);

        void selectTab(int tabId);

        void showMessage(String message, boolean isError);

        void showMessage(String title, String message, boolean isError);

        void showMessage(String message);

        void showConfirmationMessage(String message, String confirmText, String cancelText, Action onConfirm);

        void exitScreen();

        BaseFragment getCurrentFragmentInFrame();

        void showLoadingIndicator();

        void hideLoadingIndicator();

        void hideRightToolbarButton();

        void hideRightToolbarTextButton();

        void ShowRightToolbarButton();

        void ShowRight_ToolbarButton();

        void ShowRight__ToolbarButton();

        void setToolbarTitle(String title);

        void setToolbarSubTitle(String title);

        void clearToolbarTitle();

        void clearToolbarSubtitle();

        void setLeftToolbarButtonImageResource(int resId);

        void setRightToolbarButtonImageResource(int resId);

        void setLeftToolbarTextResource(int resId);

        void setRightToolbarTextResource(int resId);

        void hideLeftText();

        void hideRightText();

        void hideRightText_();

        android.view.View getRightToolbarButton();

        void hideTabs();

        void showTabs();

        ViewGroup getToolbar();
    }

    interface ViewModel extends ActivityLifecycle.ViewModel {

        void initializeBackstack(Bundle savedInstanceState);

        void onGlobalLayoutChange(ViewGroup activityView);

        void onLeftToolbarButtonClicked();

        void onRightToolbarButtonClicked();

        void hideRightToolbarButton();

        void ShowRightToolbarButton();

        void onTabClicked(int tabId);

        void selectTab(int tabId);

        Object onRetainCustomNonConfigurationInstance();

        Consumer<? super Throwable> catchErrorThrowable();

        Completable goTo(BaseKey key);

        Completable goBack();

        Completable backPress();

        Completable replace(BaseKey key);

        Completable newTop(BaseKey baseKey);

        CompletableObserver navigationObserver();

        void recreateActivity();
    }
}
