package com.shawerapp.android.screens.container;

import com.shawerapp.android.base.ActivityScope;
import com.shawerapp.android.base.BaseActivity;

import dagger.Module;
import dagger.Provides;

/**
 * Created by john.ernest on 2/16/18.
 */
@Module
public class ContainerModule {

    private BaseActivity mActivity;

    private ContainerContract.View mView;

    public ContainerModule(BaseActivity activity, ContainerContract.View view) {
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
    public ContainerContract.View providesView() {
        return mView;
    }

    @ActivityScope
    @Provides
    public ContainerContract.ViewModel providesViewModel(ContainerViewModel viewModel) {
        return viewModel;
    }
}
