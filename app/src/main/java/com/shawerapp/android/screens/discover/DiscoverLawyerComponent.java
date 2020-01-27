package com.shawerapp.android.screens.discover;

import com.shawerapp.android.base.FragmentScope;
import com.shawerapp.android.screens.container.ContainerComponent;
import dagger.Component;

@FragmentScope
@Component(modules = DiscoverLawyerModule.class, dependencies = ContainerComponent.class)
public interface DiscoverLawyerComponent {
  void inject(DiscoverLawyerFragment fragment);
}
