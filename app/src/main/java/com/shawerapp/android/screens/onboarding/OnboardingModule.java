package com.shawerapp.android.screens.onboarding;

import com.shawerapp.android.base.ActivityScope;
import com.shawerapp.android.base.BaseActivity;

import dagger.Module;
import dagger.Provides;

@Module
public class OnboardingModule {

    private BaseActivity mActivity;

    private OnboardingContract.View mView;

    public OnboardingModule(BaseActivity activity, OnboardingContract.View view) {
        mActivity = activity;
        mView = view;
    }

    @ActivityScope
    @Provides
    public BaseActivity providesActivity() {
        return mActivity;
    }

    @ActivityScope
    @Provides
    public OnboardingContract.View providesView() {
        return mView;
    }

    @ActivityScope
    @Provides
    public OnboardingContract.ViewModel providesViewModel(OnboardingViewModel viewModel) {
        return viewModel;
    }
}
