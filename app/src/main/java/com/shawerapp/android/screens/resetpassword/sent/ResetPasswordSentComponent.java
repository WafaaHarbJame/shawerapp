package com.shawerapp.android.screens.resetpassword.sent;

import com.shawerapp.android.base.ActivityScope;

import dagger.Subcomponent;

@ActivityScope
@Subcomponent(modules = ResetPasswordSentModule.class)
public interface ResetPasswordSentComponent {
    void inject(ResetPasswordSentActivity activity);
}
