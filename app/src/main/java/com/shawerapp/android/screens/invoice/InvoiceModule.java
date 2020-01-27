package com.shawerapp.android.screens.invoice;

import com.shawerapp.android.base.BaseFragment;
import com.shawerapp.android.base.FragmentScope;

import dagger.Module;
import dagger.Provides;

@Module
public class InvoiceModule {

  private BaseFragment mFragment;
  private InvoiceContract.View mView;

  public InvoiceModule(BaseFragment fragment, InvoiceContract.View view) {
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
  public InvoiceContract.View providesView() {
    return mView;
  }

  @FragmentScope
  @Provides
  public InvoiceContract.ViewModel providesViewModel(
          InvoiceViewModel viewModel) {
    return viewModel;
  }
}
