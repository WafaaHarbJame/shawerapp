package com.shawerapp.android.screens.composer;

import com.shawerapp.android.base.FragmentScope;
import com.shawerapp.android.screens.container.ContainerComponent;
import dagger.Component;

@FragmentScope
@Component(modules = ComposerModule.class, dependencies = ContainerComponent.class)
public interface ComposerComponent {
  void inject(ComposerFragment fragment);
}
