package com.shawerapp.android.screens.report;

import com.shawerapp.android.base.BaseFragment;
import com.shawerapp.android.base.FragmentScope;
import dagger.Module;
import dagger.Provides;

@Module
public class ReportAbuseModule {

  private BaseFragment mFragment;

  private ReportAbuseContract.View mView;

  public ReportAbuseModule(BaseFragment fragment, ReportAbuseContract.View view) {
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
  public ReportAbuseContract.View providesView() {
    return mView;
  }

  @FragmentScope
  @Provides
  public ReportAbuseContract.ViewModel providesViewModel(
      ReportAbuseViewModel viewModel) {
    return viewModel;
  }
}
