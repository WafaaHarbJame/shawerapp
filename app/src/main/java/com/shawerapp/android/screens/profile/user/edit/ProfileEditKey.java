package com.shawerapp.android.screens.profile.user.edit;

import com.google.auto.value.AutoValue;
import com.shawerapp.android.base.BaseFragment;
import com.shawerapp.android.base.BaseKey;

@AutoValue
public abstract class ProfileEditKey extends BaseKey {

    public abstract String type();

    @Override
    protected BaseFragment createFragment() {
        return ProfileEditFragment.newInstance(type());
    }

    public static ProfileEditKey create(String type) {
        return builder()
                .type(type)
                .build();
    }

    public static Builder builder() {
        return new AutoValue_ProfileEditKey.Builder();
    }

    @AutoValue.Builder
    public abstract static class Builder {
        public abstract Builder type(String type);

        public abstract ProfileEditKey build();
    }
}
