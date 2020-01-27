package com.shawerapp.android.screens.activate;

import com.shawerapp.android.base.ActivityScope;

import dagger.Subcomponent;

@ActivityScope
@Subcomponent(modules = ActivateModule.class)
public interface ActivateComponent {
    void inject(ActivateActivity activity);
}
