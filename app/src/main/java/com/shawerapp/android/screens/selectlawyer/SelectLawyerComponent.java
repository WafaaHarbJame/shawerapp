package com.shawerapp.android.screens.selectlawyer;

import com.shawerapp.android.base.FragmentScope;
import com.shawerapp.android.screens.container.ContainerComponent;
import dagger.Component;

@FragmentScope
@Component(modules = SelectLawyerModule.class, dependencies = ContainerComponent.class)
public interface SelectLawyerComponent {
  void inject(SelectLawyerFragment fragment);
}
