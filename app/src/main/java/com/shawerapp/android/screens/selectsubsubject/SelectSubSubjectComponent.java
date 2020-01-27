package com.shawerapp.android.screens.selectsubsubject;

import com.shawerapp.android.base.FragmentScope;
import com.shawerapp.android.screens.container.ContainerComponent;
import dagger.Component;

@FragmentScope
@Component(modules = SelectSubSubjectModule.class, dependencies = ContainerComponent.class)
public interface SelectSubSubjectComponent {
  void inject(SelectSubSubjectFragment fragment);
}
