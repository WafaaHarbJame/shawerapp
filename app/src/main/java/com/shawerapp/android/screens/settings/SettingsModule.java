package com.shawerapp.android.screens.settings;

import com.shawerapp.android.base.BaseFragment;
import com.shawerapp.android.base.FragmentScope;
import dagger.Module;
import dagger.Provides;

@Module
public class SettingsModule {

  private BaseFragment mFragment;
  private SettingsContract.View mView;

  public SettingsModule(BaseFragment fragment, SettingsContract.View view) {
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
  public SettingsContract.View providesView() {
    return mView;
  }

  @FragmentScope
  @Provides
  public SettingsContract.ViewModel providesViewModel(SettingsViewModel viewModel) {
    return viewModel;
  }
}
