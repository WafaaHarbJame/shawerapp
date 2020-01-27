package com.shawerapp.android.screens.profile.user.view;

import com.shawerapp.android.base.FragmentScope;
import com.shawerapp.android.screens.container.ContainerComponent;

import dagger.Component;

@FragmentScope
@Component(modules = ProfileViewModule.class, dependencies = ContainerComponent.class)
public interface ProfileViewComponent {
    void inject(ProfileViewFragment fragment);
}
