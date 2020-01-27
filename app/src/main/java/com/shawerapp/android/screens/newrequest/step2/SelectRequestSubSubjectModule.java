package com.shawerapp.android.screens.newrequest.step2;

import com.shawerapp.android.base.BaseFragment;
import com.shawerapp.android.base.FragmentScope;
import dagger.Module;
import dagger.Provides;

@Module
public class SelectRequestSubSubjectModule {

  private BaseFragment mFragment;

  private SelectRequestSubSubjectContract.View mView;

  public SelectRequestSubSubjectModule(BaseFragment fragment, SelectRequestSubSubjectContract.View view) {
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
  public SelectRequestSubSubjectContract.View providesView() {
    return mView;
  }

  @FragmentScope
  @Provides
  public SelectRequestSubSubjectContract.ViewModel providesViewModel(
      SelectRequestSubSubjectViewModel viewModel) {
    return viewModel;
  }
}
