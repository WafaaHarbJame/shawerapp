package com.shawerapp.android.screens.profile.lawyer.edit;

import com.shawerapp.android.base.BaseFragment;
import com.shawerapp.android.base.FragmentScope;
import dagger.Module;
import dagger.Provides;

@Module
public class LawyerEditModule {

    private BaseFragment mFragment;

    private LawyerEditContract.View mView;

    public LawyerEditModule(BaseFragment fragment, LawyerEditContract.View view) {
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
    public LawyerEditContract.View providesView() {
        return mView;
    }

    @FragmentScope
    @Provides
    public LawyerEditContract.ViewModel providesViewModel(LawyerEditViewModel viewModel) {
        return viewModel;
    }
}
