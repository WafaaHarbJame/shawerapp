package com.shawerapp.android.screens.login;

import com.shawerapp.android.base.ActivityScope;

import dagger.Subcomponent;

@ActivityScope
@Subcomponent(modules = LoginModule.class)
public interface LoginComponent {
    void inject(LoginActivity activity);
}
