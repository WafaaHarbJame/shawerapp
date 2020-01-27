package com.shawerapp.android.screens.profile.user.edit;

import com.shawerapp.android.base.FragmentScope;
import com.shawerapp.android.screens.container.ContainerComponent;

import dagger.Component;

@FragmentScope
@Component(modules = ProfileEditModule.class, dependencies = ContainerComponent.class)
public interface ProfileEditComponent {
    void inject(ProfileEditFragment fragment);
}
