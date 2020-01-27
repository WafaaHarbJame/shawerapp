package com.shawerapp.android.screens.composer;

import com.shawerapp.android.base.BaseFragment;
import com.shawerapp.android.base.FragmentScope;
import dagger.Module;
import dagger.Provides;

@Module
public class ComposerModule {

  private BaseFragment mFragment;
  private ComposerContract.View mView;

  public ComposerModule(BaseFragment fragment, ComposerContract.View view) {
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
  public ComposerContract.View providesView() {
    return mView;
  }

  @FragmentScope
  @Provides
  public ComposerContract.ViewModel providesViewModel(
      ComposerViewModel viewModel) {
    return viewModel;
  }
}
