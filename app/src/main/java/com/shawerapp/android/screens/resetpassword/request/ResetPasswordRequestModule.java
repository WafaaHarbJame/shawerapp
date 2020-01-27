package com.shawerapp.android.screens.resetpassword.request;

import com.shawerapp.android.base.ActivityScope;
import com.shawerapp.android.base.BaseActivity;

import dagger.Module;
import dagger.Provides;

@Module
public class ResetPasswordRequestModule {
    
    private BaseActivity mActivity;
    
    private ResetPasswordRequestContract.View mView;

    public ResetPasswordRequestModule(BaseActivity activity, ResetPasswordRequestContract.View view) {
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
    public ResetPasswordRequestContract.View providesView() {
        return mView;
    }

    @ActivityScope
    @Provides
    public ResetPasswordRequestContract.ViewModel providesViewModel(ResetPasswordRequestViewModel viewModel) {
        return viewModel;
    }
}
