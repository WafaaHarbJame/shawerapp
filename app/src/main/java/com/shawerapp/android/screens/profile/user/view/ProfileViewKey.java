package com.shawerapp.android.screens.profile.user.view;

import com.google.auto.value.AutoValue;
import com.shawerapp.android.base.BaseFragment;
import com.shawerapp.android.base.BaseKey;

@AutoValue
public abstract class ProfileViewKey extends BaseKey {

    public abstract String type();

    @Override
    protected BaseFragment createFragment() {
        return ProfileViewFragment.newInstance(type());
    }

    public static ProfileViewKey create(String type) {
        return builder()
                .type(type)
                .build();
    }

    public static Builder builder() {
        return new AutoValue_ProfileViewKey.Builder();
    }

    @AutoValue.Builder
    public abstract static class Builder {
        public abstract Builder type(String type);

        public abstract ProfileViewKey build();
    }
}
