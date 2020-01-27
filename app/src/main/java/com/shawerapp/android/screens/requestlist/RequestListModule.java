package com.shawerapp.android.screens.requestlist;

import com.shawerapp.android.base.BaseFragment;
import com.shawerapp.android.base.FragmentScope;
import dagger.Module;
import dagger.Provides;

@Module
public class RequestListModule {

  private BaseFragment mFragment;
  private RequestListContract.View mView;

  public RequestListModule(BaseFragment fragment, RequestListContract.View view) {
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
  public RequestListContract.View providesView() {
    return mView;
  }

  @FragmentScope
  @Provides
  public RequestListContract.ViewModel providesViewModel(
      RequestListViewModel viewModel) {
    return viewModel;
  }
}
