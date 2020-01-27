package com.shawerapp.android.screens.requestlist;

import com.shawerapp.android.base.FragmentScope;
import com.shawerapp.android.screens.container.ContainerComponent;
import dagger.Component;

@FragmentScope
@Component(modules = RequestListModule.class, dependencies = ContainerComponent.class)
public interface RequestListComponent {
  void inject(RequestListFragment fragment);
}
