package com.shawerapp.android.screens.viewrequest;

import com.shawerapp.android.base.FragmentScope;
import com.shawerapp.android.screens.container.ContainerComponent;
import dagger.Component;

@FragmentScope
@Component(modules = ViewCaseRequestModule.class, dependencies = ContainerComponent.class)
public interface ViewCaseRequestComponent {
  void inject(ViewCaseRequestFragment fragment);
}
