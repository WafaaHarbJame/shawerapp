package com.shawerapp.android.screens.selectfield;

import com.shawerapp.android.base.FragmentScope;
import com.shawerapp.android.screens.container.ContainerComponent;
import dagger.Component;

@FragmentScope
@Component(modules = SelectFieldModule.class, dependencies = ContainerComponent.class)
public interface SelectFieldComponent {
  void inject(SelectFieldFragment fragment);
}
