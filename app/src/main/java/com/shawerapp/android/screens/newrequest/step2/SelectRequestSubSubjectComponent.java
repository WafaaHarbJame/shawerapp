package com.shawerapp.android.screens.newrequest.step2;

import com.shawerapp.android.base.FragmentScope;
import com.shawerapp.android.screens.container.ContainerComponent;
import dagger.Component;

@FragmentScope
@Component(modules = SelectRequestSubSubjectModule.class, dependencies = ContainerComponent.class)
public interface SelectRequestSubSubjectComponent {
  void inject(SelectRequestSubSubjectFragment fragment);
}
