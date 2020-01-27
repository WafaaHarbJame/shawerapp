package com.shawerapp.android.screens.newrequest.step1;

import com.shawerapp.android.base.BaseFragment;
import com.shawerapp.android.base.FragmentScope;
import dagger.Module;
import dagger.Provides;

@Module
public class SelectRequestFieldModule {

  private BaseFragment mFragment;
  private SelectRequestFieldContract.View mView;

  public SelectRequestFieldModule(BaseFragment fragment, SelectRequestFieldContract.View view) {
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
  public SelectRequestFieldContract.View providesView() {
    return mView;
  }

  @FragmentScope
  @Provides
  public SelectRequestFieldContract.ViewModel providesViewModel(
      SelectRequestFieldViewModel viewModel) {
    return viewModel;
  }
}
