package com.shawerapp.android.screens.questionlist;

import com.shawerapp.android.base.BaseFragment;
import com.shawerapp.android.base.FragmentScope;
import dagger.Module;
import dagger.Provides;

@Module
public class QuestionListModule {

  private BaseFragment mFragment;
  private QuestionListContract.View mView;

  public QuestionListModule(BaseFragment fragment, QuestionListContract.View view) {
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
  public QuestionListContract.View providesView() {
    return mView;
  }

  @FragmentScope
  @Provides
  public QuestionListContract.ViewModel providesViewModel(
      QuestionListViewModel viewModel) {
    return viewModel;
  }
}
