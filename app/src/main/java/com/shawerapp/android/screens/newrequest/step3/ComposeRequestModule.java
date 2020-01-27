package com.shawerapp.android.screens.newrequest.step3;

import com.shawerapp.android.base.BaseFragment;
import com.shawerapp.android.base.FragmentScope;
import dagger.Module;
import dagger.Provides;

@Module
public class ComposeRequestModule {

  private BaseFragment mFragment;
  private ComposeRequestContract.View mView;

  public ComposeRequestModule(BaseFragment fragment, ComposeRequestContract.View view) {
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
  public ComposeRequestContract.View providesView() {
    return mView;
  }

  @FragmentScope
  @Provides
  public ComposeRequestContract.ViewModel providesViewModel(
      ComposeRequestViewModel viewModel) {
    return viewModel;
  }
}
