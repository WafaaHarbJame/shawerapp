package com.shawerapp.android.screens.answerlist;

import com.shawerapp.android.base.FragmentScope;
import com.shawerapp.android.screens.container.ContainerComponent;
import dagger.Component;

@FragmentScope
@Component(modules = AnswerListModule.class, dependencies = ContainerComponent.class)
public interface AnswerListComponent {
  void inject(AnswerListFragment fragment);
}
