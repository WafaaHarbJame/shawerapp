package com.shawerapp.android.screens.discover;

import com.shawerapp.android.base.BaseFragment;
import com.shawerapp.android.base.FragmentScope;
import dagger.Module;
import dagger.Provides;

@Module
public class DiscoverLawyerModule {

  private BaseFragment mFragment;
  private DiscoverLawyerContract.View mView;

  public DiscoverLawyerModule(BaseFragment fragment, DiscoverLawyerContract.View view) {
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
  public DiscoverLawyerContract.View providesView() {
    return mView;
  }

  @FragmentScope
  @Provides
  public DiscoverLawyerContract.ViewModel providesViewModel(
      DiscoverLawyerViewModel viewModel) {
    return viewModel;
  }
}
