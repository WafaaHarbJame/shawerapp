package com.shawerapp.android.screens.Invoices;

import android.content.Context;

import com.shawerapp.android.base.BaseFragment;
import com.shawerapp.android.base.FragmentScope;
import com.shawerapp.android.screens.purchase.PurchaseCoinsContract;
import com.shawerapp.android.screens.purchase.PurchaseCoinsViewModel;

import dagger.Module;
import dagger.Provides;

@Module
public class InvoicesModule {

  private BaseFragment mFragment;
  private InvoicesContract.View mView;



  public InvoicesModule(BaseFragment fragment, InvoicesContract.View view) {
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
  public InvoicesContract.View providesView() {
    return mView;
  }

  @FragmentScope
  @Provides
  public InvoicesContract.ViewModel providesViewModel(
          InvoicesViewModel viewModel) {
    return viewModel;
  }
}
