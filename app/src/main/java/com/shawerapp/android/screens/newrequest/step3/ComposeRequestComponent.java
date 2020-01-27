package com.shawerapp.android.screens.newrequest.step3;

import com.shawerapp.android.base.FragmentScope;
import com.shawerapp.android.screens.container.ContainerComponent;
import dagger.Component;

@FragmentScope
@Component(modules = ComposeRequestModule.class, dependencies = ContainerComponent.class)
public interface ComposeRequestComponent {
  void inject(ComposeRequestFragment fragment);
}
