package com.shawerapp.android.Payment;

import com.shawerapp.android.base.ActivityScope;
import com.shawerapp.android.screens.container.ContainerComponent;
import com.shawerapp.android.screens.payment.PaymentModule;
import com.shawerapp.android.screens.splash.SplashActivity;
import com.shawerapp.android.screens.splash.SplashModule;

import dagger.Component;
import dagger.Subcomponent;

@ActivityScope
@Subcomponent(modules = PaymentActivityModule.class)
public interface PaymentActivityComponent {
    void inject(PaymentActivity activity);
}
