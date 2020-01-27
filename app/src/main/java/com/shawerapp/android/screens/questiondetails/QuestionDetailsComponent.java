package com.shawerapp.android.screens.questiondetails;

import com.shawerapp.android.base.FragmentScope;
import com.shawerapp.android.screens.container.ContainerComponent;
import dagger.Component;

@FragmentScope
@Component(modules = QuestionDetailsModule.class, dependencies = ContainerComponent.class)
public interface QuestionDetailsComponent {
  void inject(QuestionDetailsFragment fragment);
}
