package com.shawerapp.android.screens.selectsubsubject;

import com.shawerapp.android.base.BaseFragment;
import com.shawerapp.android.base.FragmentScope;
import dagger.Module;
import dagger.Provides;

@Module
public class SelectSubSubjectModule {

  private BaseFragment mFragment;

  private SelectSubSubjectContract.View mView;

  public SelectSubSubjectModule(BaseFragment fragment, SelectSubSubjectContract.View view) {
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
  public SelectSubSubjectContract.View providesView() {
    return mView;
  }

  @FragmentScope
  @Provides
  public SelectSubSubjectContract.ViewModel providesViewModel(
      SelectSubSubjectViewModel viewModel) {
    return viewModel;
  }
}
