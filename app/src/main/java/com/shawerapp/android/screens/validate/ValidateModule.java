package com.shawerapp.android.screens.validate;

import com.shawerapp.android.base.ActivityScope;
import com.shawerapp.android.base.BaseActivity;

import dagger.Module;
import dagger.Provides;

@Module
public class ValidateModule {
    
    private BaseActivity mActivity;
    
    private ValidateContract.View mView;

    public ValidateModule(BaseActivity activity, ValidateContract.View view) {
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
    public ValidateContract.View providesView() {
        return mView;
    }

    @ActivityScope
    @Provides
    public ValidateContract.ViewModel providesViewModel(ValidateViewModel viewModel) {
        return viewModel;
    }
}
