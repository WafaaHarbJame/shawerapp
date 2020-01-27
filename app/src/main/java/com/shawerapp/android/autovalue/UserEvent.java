package com.shawerapp.android.autovalue;

import android.os.Parcelable;

import com.google.auto.value.AutoValue;

@AutoValue
public abstract class UserEvent
{

    public abstract int type();

    public abstract Object user();

    public static UserEvent create(int type, Object user) {
        return builder()
                .type(type)
                .user(user)
                .build();
    }

    public static Builder builder() {
        return new AutoValue_UserEvent.Builder();
    }

    @AutoValue.Builder
    public abstract static class Builder {
        public abstract Builder type(int type);

        public abstract Builder user(Object user);

        public abstract UserEvent build();
    }
}
