package com.shawerapp.android.screens.profile.lawyer.personal;

import com.shawerapp.android.base.FragmentScope;
import com.shawerapp.android.screens.container.ContainerComponent;
import dagger.Component;

@FragmentScope
@Component(modules = PrivateLawyerViewModule.class, dependencies = ContainerComponent.class)
public interface PrivateLawyerViewComponent {
  void inject(PrivateLawyerViewFragment fragment);
}
