package com.shawerapp.android.screens.viewanswer;

import com.shawerapp.android.base.BaseFragment;
import com.shawerapp.android.base.FragmentScope;
import dagger.Module;
import dagger.Provides;

@Module
public class ViewAnswerModule {

  private BaseFragment mFragment;
  private ViewAnswerContract.View mView;

  public ViewAnswerModule(BaseFragment fragment, ViewAnswerContract.View view) {
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
  public ViewAnswerContract.View providesView() {
    return mView;
  }

  @FragmentScope
  @Provides
  public ViewAnswerContract.ViewModel providesViewModel(
      ViewAnswerViewModel viewModel) {
    return viewModel;
  }
}
