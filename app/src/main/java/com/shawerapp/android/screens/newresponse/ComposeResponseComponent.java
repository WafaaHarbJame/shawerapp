package com.shawerapp.android.screens.newresponse;

import com.shawerapp.android.base.FragmentScope;
import com.shawerapp.android.screens.container.ContainerComponent;
import dagger.Component;

@FragmentScope
@Component(modules = ComposeResponseModule.class, dependencies = ContainerComponent.class)
public interface ComposeResponseComponent {
  void inject(ComposeResponseFragment fragment);
}
