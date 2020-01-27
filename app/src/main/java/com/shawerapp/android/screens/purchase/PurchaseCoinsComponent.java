package com.shawerapp.android.screens.purchase;

import com.shawerapp.android.base.FragmentScope;
import com.shawerapp.android.screens.container.ContainerComponent;
import dagger.Component;

@FragmentScope
@Component(modules = PurchaseCoinsModule.class, dependencies = ContainerComponent.class)
public interface PurchaseCoinsComponent {
  void inject(PurchaseCoinsFragment fragment);
}
