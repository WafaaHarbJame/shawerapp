package com.shawerapp.android.screens.newresponse;

import com.shawerapp.android.base.BaseFragment;
import com.shawerapp.android.base.FragmentScope;
import dagger.Module;
import dagger.Provides;

@Module
public class ComposeResponseModule {

  private BaseFragment mFragment;
  private ComposeResponseContract.View mView;

  public ComposeResponseModule(BaseFragment fragment, ComposeResponseContract.View view) {
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
  public ComposeResponseContract.View providesView() {
    return mView;
  }

  @FragmentScope
  @Provides
  public ComposeResponseContract.ViewModel providesViewModel(
      ComposeResponseViewModel viewModel) {
    return viewModel;
  }
}
