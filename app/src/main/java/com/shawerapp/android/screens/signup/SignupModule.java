package com.shawerapp.android.screens.signup;

import com.shawerapp.android.base.ActivityScope;
import com.shawerapp.android.base.BaseActivity;

import dagger.Module;
import dagger.Provides;

@Module
public class SignupModule {

    private BaseActivity mActivity;

    private SignupContract.View mView;

    public SignupModule(BaseActivity activity, SignupContract.View view) {
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
    public SignupContract.View providesView() {
        return mView;
    }

    @ActivityScope
    @Provides
    public SignupContract.ViewModel providesViewModel(SignupViewModel viewModel) {
        return viewModel;
    }
}
