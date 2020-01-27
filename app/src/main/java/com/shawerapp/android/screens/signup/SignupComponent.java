package com.shawerapp.android.screens.signup;

import com.shawerapp.android.base.ActivityScope;

import dagger.Subcomponent;

@ActivityScope
@Subcomponent(modules = SignupModule.class)
public interface SignupComponent {
    void inject(SignupActivity activity);
}
