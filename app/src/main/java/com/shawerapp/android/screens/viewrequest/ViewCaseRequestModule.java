package com.shawerapp.android.screens.viewrequest;

import com.shawerapp.android.base.BaseFragment;
import com.shawerapp.android.base.FragmentScope;
import dagger.Module;
import dagger.Provides;

@Module
public class ViewCaseRequestModule {

  private BaseFragment mFragment;
  private ViewCaseRequestContract.View mView;

  public ViewCaseRequestModule(BaseFragment fragment, ViewCaseRequestContract.View view) {
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
  public ViewCaseRequestContract.View providesView() {
    return mView;
  }

  @FragmentScope
  @Provides
  public ViewCaseRequestContract.ViewModel providesViewModel(
      ViewCaseRequestViewModel viewModel) {
    return viewModel;
  }
}
