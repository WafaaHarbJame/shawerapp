package com.shawerapp.android.screens.tutorial;

import com.shawerapp.android.base.BaseFragment;
import com.shawerapp.android.base.FragmentScope;
import dagger.Module;
import dagger.Provides;

@Module
public class TutorialModule {

  private BaseFragment mFragment;
  private TutorialContract.View mView;

  public TutorialModule(BaseFragment fragment, TutorialContract.View view) {
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
  public TutorialContract.View providesView() {
    return mView;
  }

  @FragmentScope
  @Provides
  public TutorialContract.ViewModel providesViewModel(
      TutorialViewModel viewModel) {
    return viewModel;
  }
}
