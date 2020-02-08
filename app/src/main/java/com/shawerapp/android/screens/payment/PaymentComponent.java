package com.shawerapp.android.screens.payment;

import com.shawerapp.android.base.FragmentScope;
import com.shawerapp.android.screens.composer.ComposerViewModel;
import com.shawerapp.android.screens.container.ContainerComponent;

import dagger.Component;

@FragmentScope
@Component(modules = PaymentModule.class, dependencies = ContainerComponent.class)
public interface PaymentComponent {
  void inject(PaymentFragment fragment);
}
