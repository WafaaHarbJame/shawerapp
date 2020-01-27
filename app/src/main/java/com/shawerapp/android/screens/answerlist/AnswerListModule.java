package com.shawerapp.android.screens.answerlist;

import com.shawerapp.android.base.BaseFragment;
import com.shawerapp.android.base.FragmentScope;
import dagger.Module;
import dagger.Provides;

@Module
public class AnswerListModule {

  private BaseFragment mFragment;
  private AnswerListContract.View mView;

  public AnswerListModule(BaseFragment fragment, AnswerListContract.View view) {
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
  public AnswerListContract.View providesView() {
    return mView;
  }

  @FragmentScope
  @Provides
  public AnswerListContract.ViewModel providesViewModel(
      AnswerListViewModel viewModel) {
    return viewModel;
  }
}
