package com.shawerapp.android.screens.onboarding;

import com.shawerapp.android.base.ActivityScope;

import dagger.Subcomponent;

@ActivityScope
@Subcomponent(modules = OnboardingModule.class)
public interface OnboardingComponent {
    void inject(OnboardingActivity activity);
}
