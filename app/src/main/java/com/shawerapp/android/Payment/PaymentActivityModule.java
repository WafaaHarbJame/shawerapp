package com.shawerapp.android.Payment;

import com.shawerapp.android.base.ActivityScope;
import com.shawerapp.android.base.BaseActivity;


import dagger.Module;
import dagger.Provides;

@Module
public class PaymentActivityModule {

    private BaseActivity mActivity;

    private PaymentActivityContract.View mView;


    public PaymentActivityModule(BaseActivity activity, PaymentActivityContract.View view) {
        mActivity = activity;
        mView = view;
    }

    @ActivityScope
    @Provides
    public BaseActivity providesActivity() {
        return mActivity;
    }

    @ActivityScope
    @Provides
    public PaymentActivityContract.View providesView() {
        return mView;
    }

    @ActivityScope
    @Provides
    public PaymentActivityContract.ViewModel providesViewModel(PaymentViewModel viewModel) {
        return viewModel;
    }
}
