package com.shawerapp.android.autovalue;

import android.os.Parcelable;

import com.google.auto.value.AutoValue;

@AutoValue
public abstract class LawyerUserEvent implements Parcelable {

    public abstract int type();

    public abstract LawyerUser lawyerUser();

    public static LawyerUserEvent create(int type, LawyerUser lawyerUser) {
        return builder()
                .type(type)
                .lawyerUser(lawyerUser)
                .build();
    }

    public static Builder builder() {
        return new AutoValue_LawyerUserEvent.Builder();
    }

    @AutoValue.Builder
    public abstract static class Builder {
        public abstract Builder type(int type);

        public abstract Builder lawyerUser(LawyerUser lawyerUser);

        public abstract LawyerUserEvent build();
    }
}
