package com.shawerapp.android.screens.newanswer;

import com.shawerapp.android.base.FragmentScope;
import com.shawerapp.android.screens.container.ContainerComponent;
import dagger.Component;

@FragmentScope
@Component(modules = ComposeAnswerModule.class, dependencies = ContainerComponent.class)
public interface ComposeAnswerComponent {
  void inject(ComposeAnswerFragment fragment);
}
