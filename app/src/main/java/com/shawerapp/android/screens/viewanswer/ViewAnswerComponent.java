package com.shawerapp.android.screens.viewanswer;

import com.shawerapp.android.base.FragmentScope;
import com.shawerapp.android.screens.container.ContainerComponent;
import dagger.Component;

@FragmentScope
@Component(modules = ViewAnswerModule.class, dependencies = ContainerComponent.class)
public interface ViewAnswerComponent {
  void inject(ViewAnswerFragment fragment);
}
