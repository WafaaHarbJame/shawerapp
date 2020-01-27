package com.shawerapp.android.screens.profile.lawyer.edit;

import com.shawerapp.android.base.FragmentScope;
import com.shawerapp.android.screens.container.ContainerComponent;
import dagger.Component;

@FragmentScope
@Component(modules = LawyerEditModule.class, dependencies = ContainerComponent.class)
public interface LawyerEditComponent {
    void inject(LawyerEditFragment fragment);
}
