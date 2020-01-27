package com.shawerapp.android.screens.newrequest.step1;

import com.shawerapp.android.base.FragmentScope;
import com.shawerapp.android.screens.container.ContainerComponent;
import dagger.Component;

@FragmentScope
@Component(modules = SelectRequestFieldModule.class, dependencies = ContainerComponent.class)
public interface SelectRequestFieldComponent {
  void inject(SelectRequestFieldFragment fragment);
}
