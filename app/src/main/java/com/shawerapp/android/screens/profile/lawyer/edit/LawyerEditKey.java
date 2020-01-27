package com.shawerapp.android.screens.profile.lawyer.edit;

import com.google.auto.value.AutoValue;
import com.shawerapp.android.base.BaseFragment;
import com.shawerapp.android.base.BaseKey;

@AutoValue
public abstract class LawyerEditKey extends BaseKey {

    public static LawyerEditKey create() {
        return new AutoValue_LawyerEditKey();
    }

    @Override
    protected BaseFragment createFragment() {
        return LawyerEditFragment.newInstance();
    }
}
