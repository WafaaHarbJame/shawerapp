package com.shawerapp.android.screens.selectfield;

import com.google.auto.value.AutoValue;
import com.shawerapp.android.base.BaseFragment;
import com.shawerapp.android.base.BaseKey;

@AutoValue
public abstract class SelectFieldKey extends BaseKey {

    public abstract int requestType();

    @Override
    protected BaseFragment createFragment() {
        return SelectFieldFragment.newInstance(requestType());
    }

    public static SelectFieldKey create(int requestType) {
        return builder()
                .requestType(requestType)
                .build();
    }

    public static Builder builder() {
        return new AutoValue_SelectFieldKey.Builder();
    }

    @AutoValue.Builder
    public abstract static class Builder {
        public abstract Builder requestType(int requestType);

        public abstract SelectFieldKey build();
    }
}
