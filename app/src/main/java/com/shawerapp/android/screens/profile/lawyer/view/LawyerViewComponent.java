package com.shawerapp.android.screens.profile.lawyer.view;

import com.shawerapp.android.base.FragmentScope;
import com.shawerapp.android.screens.container.ContainerComponent;
import dagger.Component;

@FragmentScope
@Component(modules = LawyerViewModule.class, dependencies = ContainerComponent.class)
public interface LawyerViewComponent {
  void inject(LawyerViewFragment fragment);
}
