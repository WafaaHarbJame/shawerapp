package com.shawerapp.android.screens.profile.lawyer.view;

import com.shawerapp.android.base.BaseFragment;
import com.shawerapp.android.base.FragmentScope;
import dagger.Module;
import dagger.Provides;

@Module
public class LawyerViewModule {

  private BaseFragment mFragment;
  private LawyerViewContract.View mView;

  public LawyerViewModule(BaseFragment fragment, LawyerViewContract.View view) {
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
  public LawyerViewContract.View providesView() {
    return mView;
  }

  @FragmentScope
  @Provides
  public LawyerViewContract.ViewModel providesViewModel(
      LawyerViewViewModel viewModel) {
    return viewModel;
  }
}
