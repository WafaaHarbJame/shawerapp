package com.shawerapp.android.screens.splash;

import com.shawerapp.android.base.ActivityScope;
import com.shawerapp.android.base.BaseActivity;

import dagger.Module;
import dagger.Provides;

@Module
public class SplashModule {

    private BaseActivity mActivity;

    private SplashContract.View mView;

    public SplashModule(BaseActivity activity, SplashContract.View view) {
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
    public SplashContract.View providesView() {
        return mView;
    }

    @ActivityScope
    @Provides
    public SplashContract.ViewModel providesViewModel(SplashViewModel viewModel) {
        return viewModel;
    }
}
