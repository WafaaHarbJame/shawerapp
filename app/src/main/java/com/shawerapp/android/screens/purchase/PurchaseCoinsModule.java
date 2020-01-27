package com.shawerapp.android.screens.purchase;

import com.shawerapp.android.base.BaseFragment;
import com.shawerapp.android.base.FragmentScope;
import dagger.Module;
import dagger.Provides;

@Module
public class PurchaseCoinsModule {

  private BaseFragment mFragment;
  private PurchaseCoinsContract.View mView;

  public PurchaseCoinsModule(BaseFragment fragment, PurchaseCoinsContract.View view) {
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
  public PurchaseCoinsContract.View providesView() {
    return mView;
  }

  @FragmentScope
  @Provides
  public PurchaseCoinsContract.ViewModel providesViewModel(
      PurchaseCoinsViewModel viewModel) {
    return viewModel;
  }
}
