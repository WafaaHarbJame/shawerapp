package com.shawerapp.android.screens.profile.lawyer.personal;

import com.shawerapp.android.base.BaseFragment;
import com.shawerapp.android.base.FragmentScope;
import dagger.Module;
import dagger.Provides;

@Module
public class PrivateLawyerViewModule {

  private BaseFragment mFragment;
  private PrivateLawyerViewContract.View mView;

  public PrivateLawyerViewModule(BaseFragment fragment, PrivateLawyerViewContract.View view) {
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
  public PrivateLawyerViewContract.View providesView() {
    return mView;
  }

  @FragmentScope
  @Provides
  public PrivateLawyerViewContract.ViewModel providesViewModel(
      PrivateLawyerViewViewModel viewModel) {
    return viewModel;
  }
}
