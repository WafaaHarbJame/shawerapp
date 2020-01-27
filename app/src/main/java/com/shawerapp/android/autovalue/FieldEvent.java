package com.shawerapp.android.autovalue;

import android.os.Parcelable;

import com.google.auto.value.AutoValue;

@AutoValue
public abstract class FieldEvent implements Parcelable {

    public abstract int type();

    public abstract Field field();

    public static FieldEvent create(int type, Field field) {
        return builder()
                .type(type)
                .field(field)
                .build();
    }

    public static Builder builder() {
        return new AutoValue_FieldEvent.Builder();
    }

    @AutoValue.Builder
    public abstract static class Builder {
        public abstract Builder type(int type);

        public abstract Builder field(Field field);

        public abstract FieldEvent build();
    }
}
