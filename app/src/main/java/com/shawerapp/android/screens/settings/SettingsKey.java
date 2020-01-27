package com.shawerapp.android.screens.settings;

import com.google.auto.value.AutoValue;
import com.shawerapp.android.base.BaseFragment;
import com.shawerapp.android.base.BaseKey;

@AutoValue
public abstract class SettingsKey extends BaseKey {

    public static SettingsKey create() {
        return new AutoValue_SettingsKey();
    }

    @Override
    protected BaseFragment createFragment() {
        return SettingsFragment.newInstance();
    }
}
