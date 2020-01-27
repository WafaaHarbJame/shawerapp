package com.shawerapp.android.screens.newanswer;

import com.shawerapp.android.base.BaseFragment;
import com.shawerapp.android.base.FragmentScope;
import dagger.Module;
import dagger.Provides;

@Module
public class ComposeAnswerModule {

  private BaseFragment mFragment;
  private ComposeAnswerContract.View mView;

  public ComposeAnswerModule(BaseFragment fragment, ComposeAnswerContract.View view) {
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
  public ComposeAnswerContract.View providesView() {
    return mView;
  }

  @FragmentScope
  @Provides
  public ComposeAnswerContract.ViewModel providesViewModel(
      ComposeAnswerViewModel viewModel) {
    return viewModel;
  }
}
