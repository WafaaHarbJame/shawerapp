package com.shawerapp.android.screens.resetpassword.sent;

import com.shawerapp.android.base.ActivityScope;
import com.shawerapp.android.base.BaseActivity;

import dagger.Module;
import dagger.Provides;

@Module
public class ResetPasswordSentModule {

    private BaseActivity mActivity;

    private ResetPasswordSentContract.View mView;

    public ResetPasswordSentModule(BaseActivity activity, ResetPasswordSentContract.View view) {
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
    public ResetPasswordSentContract.View providesView() {
        return mView;
    }

    @ActivityScope
    @Provides
    public ResetPasswordSentContract.ViewModel providesViewModel(ResetPasswordSentViewModel viewModel) {
        return viewModel;
    }
}
