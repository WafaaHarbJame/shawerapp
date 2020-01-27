package com.shawerapp.android.screens.questionlist;

import com.shawerapp.android.base.FragmentScope;
import com.shawerapp.android.screens.container.ContainerComponent;
import dagger.Component;

@FragmentScope
@Component(modules = QuestionListModule.class, dependencies = ContainerComponent.class)
public interface QuestionListComponent {
  void inject(QuestionListFragment fragment);
}
