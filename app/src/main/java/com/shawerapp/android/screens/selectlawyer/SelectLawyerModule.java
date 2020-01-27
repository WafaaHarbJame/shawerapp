package com.shawerapp.android.screens.selectlawyer;

import com.shawerapp.android.base.BaseFragment;
import com.shawerapp.android.base.FragmentScope;
import dagger.Module;
import dagger.Provides;

@Module
public class SelectLawyerModule {

  private BaseFragment mFragment;

  private SelectLawyerContract.View mView;

  public SelectLawyerModule(BaseFragment fragment, SelectLawyerContract.View view) {
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
  public SelectLawyerContract.View providesView() {
    return mView;
  }

  @FragmentScope
  @Provides
  public SelectLawyerContract.ViewModel providesViewModel(
      SelectLawyerViewModel viewModel) {
    return viewModel;
  }
}
