package com.shawerapp.android.screens.conversation;

import com.shawerapp.android.base.FragmentScope;
import com.shawerapp.android.screens.container.ContainerComponent;
import com.shawerapp.android.screens.discover.DiscoverLawyerFragment;
import dagger.Component;

@FragmentScope
@Component(modules = ConversationModule.class, dependencies = ContainerComponent.class)
public interface ConversationComponent {
  void inject(ConversationFragment fragment);
}
