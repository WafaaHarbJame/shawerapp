package com.shawerapp.android.autovalue;

import android.os.Parcelable;

import com.google.auto.value.AutoValue;

@AutoValue
public abstract class PracticeRequestEvent implements Parcelable {

    public abstract int type();

    public abstract PracticeRequest practiceRequest();

    public static PracticeRequestEvent create(int type, PracticeRequest practiceRequest) {
        return builder()
                .type(type)
                .practiceRequest(practiceRequest)
                .build();
    }

    public static Builder builder() {
        return new AutoValue_PracticeRequestEvent.Builder();
    }

    @AutoValue.Builder
    public abstract static class Builder {
        public abstract Builder type(int type);

        public abstract Builder practiceRequest(PracticeRequest practiceRequest);

        public abstract PracticeRequestEvent build();
    }
}
