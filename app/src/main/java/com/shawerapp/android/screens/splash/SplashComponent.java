package com.shawerapp.android.screens.splash;

import com.shawerapp.android.base.ActivityScope;

import dagger.Subcomponent;

@ActivityScope
@Subcomponent(modules = SplashModule.class)
public interface SplashComponent {
    void inject(SplashActivity activity);
}
