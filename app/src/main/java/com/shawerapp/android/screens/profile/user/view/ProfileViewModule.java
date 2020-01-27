package com.shawerapp.android.screens.profile.user.view;

import com.shawerapp.android.base.BaseFragment;
import com.shawerapp.android.base.FragmentScope;

import dagger.Module;
import dagger.Provides;

@Module
public class ProfileViewModule {

    private BaseFragment mFragment;

    private ProfileViewContract.View mView;

    public ProfileViewModule(BaseFragment fragment, ProfileViewContract.View view) {
        mFragment = fragment;
        mView = view;
    }

    @FragmentScope
    @Provides
    public BaseFragment providesFragment() {
        return mFragment;
    }

    @FragmentScope
    @Provides
    public ProfileViewContract.View providesView() {
        return mView;
    }

    @FragmentScope
    @Provides
    public ProfileViewContract.ViewModel providesViewModel(ProfileViewViewModel viewModel) {
        return viewModel;
    }
}
