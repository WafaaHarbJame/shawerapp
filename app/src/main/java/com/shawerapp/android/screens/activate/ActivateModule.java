package com.shawerapp.android.screens.activate;

import com.shawerapp.android.base.ActivityScope;
import com.shawerapp.android.base.BaseActivity;

import dagger.Module;
import dagger.Provides;

@Module
public class ActivateModule {

    private BaseActivity mActivity;

    private ActivateContract.View mView;

    public ActivateModule(BaseActivity activity, ActivateContract.View view) {
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
    public ActivateContract.View providesView() {
        return mView;
    }

    @ActivityScope
    @Provides
    public ActivateContract.ViewModel providesViewModel(ActivateViewModel viewModel) {
        return viewModel;
    }
}
