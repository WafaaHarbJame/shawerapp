package com.shawerapp.android.screens.resetpassword.request;

import com.shawerapp.android.base.ActivityScope;

import dagger.Subcomponent;

@ActivityScope
@Subcomponent(modules = ResetPasswordRequestModule.class)
public interface ResetPasswordRequestComponent {
    void inject(ResetPasswordRequestActivity activity);
}
