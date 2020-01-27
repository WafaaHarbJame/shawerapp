package com.shawerapp.android.screens.Invoices;

import com.shawerapp.android.base.FragmentScope;
import com.shawerapp.android.screens.container.ContainerComponent;

import dagger.Component;

@FragmentScope
@Component(modules = InvoicesModule.class, dependencies = ContainerComponent.class)
public interface InvoicesComponent {
  void inject(InvoicesFragment fragment);
}
