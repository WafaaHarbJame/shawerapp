package com.shawerapp.android.screens.payment;

import com.shawerapp.android.base.BaseFragment;
import com.shawerapp.android.base.FragmentScope;

import dagger.Module;
import dagger.Provides;

@Module
public class PaymentModule {

  private BaseFragment mFragment;
  private PaymentContract.View mView;

  public PaymentModule(BaseFragment fragment, PaymentContract.View view) {
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
  public PaymentContract.View providesView() {
    return mView;
  }

  @FragmentScope
  @Provides
  public PaymentContract.ViewModel providesViewModel(
          PaymentViewModel viewModel) {
    return viewModel;
  }
}
