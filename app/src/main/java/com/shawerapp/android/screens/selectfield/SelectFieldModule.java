package com.shawerapp.android.screens.selectfield;

import com.shawerapp.android.base.BaseFragment;
import com.shawerapp.android.base.FragmentScope;
import dagger.Module;
import dagger.Provides;

@Module
public class SelectFieldModule {

  private BaseFragment mFragment;

  private SelectFieldContract.View mView;

  public SelectFieldModule(BaseFragment fragment, SelectFieldContract.View view) {
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
  public SelectFieldContract.View providesView() {
    return mView;
  }

  @FragmentScope
  @Provides
  public SelectFieldContract.ViewModel providesViewModel(
      SelectFieldViewModel viewModel) {
    return viewModel;
  }
}
