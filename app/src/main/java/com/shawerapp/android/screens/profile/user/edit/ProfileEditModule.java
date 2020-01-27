package com.shawerapp.android.screens.profile.user.edit;

import com.shawerapp.android.base.BaseFragment;
import com.shawerapp.android.base.FragmentScope;

import dagger.Module;
import dagger.Provides;

@Module
public class ProfileEditModule {

    private BaseFragment mFragment;

    private ProfileEditContract.View mView;

    public ProfileEditModule(BaseFragment fragment, ProfileEditContract.View view) {
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
    public ProfileEditContract.View providesView() {
        return mView;
    }

    @FragmentScope
    @Provides
    public ProfileEditContract.ViewModel providesViewModel(ProfileEditViewModel viewModel) {
        return viewModel;
    }
}
