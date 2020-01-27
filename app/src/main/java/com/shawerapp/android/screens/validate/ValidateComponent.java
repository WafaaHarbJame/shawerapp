package com.shawerapp.android.screens.validate;

import com.shawerapp.android.base.ActivityScope;

import dagger.Subcomponent;

@ActivityScope
@Subcomponent(modules = ValidateModule.class)
public interface ValidateComponent {
    void inject(ValidateActivity activity);
}
