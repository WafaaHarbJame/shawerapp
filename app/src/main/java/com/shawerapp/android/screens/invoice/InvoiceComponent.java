package com.shawerapp.android.screens.invoice;

import com.shawerapp.android.base.FragmentScope;
import com.shawerapp.android.screens.container.ContainerComponent;

import dagger.Component;

@FragmentScope
@Component(modules = InvoiceModule.class, dependencies = ContainerComponent.class)
public interface InvoiceComponent {
  void inject(InvoiceFragment fragment);
}
