package com.shawerapp.android.screens.settings;

import com.shawerapp.android.base.FragmentScope;
import com.shawerapp.android.screens.container.ContainerComponent;
import dagger.Component;

@FragmentScope
@Component(modules = SettingsModule.class, dependencies = ContainerComponent.class)
public interface SettingsComponent {
  void inject(SettingsFragment fragment);
}
