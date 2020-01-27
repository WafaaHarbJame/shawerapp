package com.shawerapp.android.screens.tutorial;

import com.shawerapp.android.base.FragmentScope;
import com.shawerapp.android.screens.container.ContainerComponent;
import dagger.Component;

@FragmentScope
@Component(modules = TutorialModule.class, dependencies = ContainerComponent.class)
public interface TutorialComponent {
  void inject(TutorialFragment fragment);
}
