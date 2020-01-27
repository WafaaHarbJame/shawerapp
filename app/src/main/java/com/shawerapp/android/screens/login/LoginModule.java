package com.shawerapp.android.screens.login;

import com.shawerapp.android.base.ActivityScope;
import com.shawerapp.android.base.BaseActivity;

import dagger.Module;
import dagger.Provides;

@Module
public class LoginModule {

    private BaseActivity mActivity;

    private LoginContract.View mView;

    public LoginModule(BaseActivity activity, LoginContract.View view) {
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
    public LoginContract.View providesView() {
        return mView;
    }

    @ActivityScope
    @Provides
    public LoginContract.ViewModel providesViewModel(LoginViewModel viewModel) {
        return viewModel;
    }
}
