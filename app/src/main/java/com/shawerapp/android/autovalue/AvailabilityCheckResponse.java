package com.shawerapp.android.autovalue;

import android.os.Parcelable;

import com.google.auto.value.AutoValue;
import com.google.gson.Gson;
import com.google.gson.TypeAdapter;

import javax.annotation.Nullable;

@AutoValue
public abstract class AvailabilityCheckResponse implements Parcelable {

    public static final int SUCCESS = 200;

    public abstract int code();

    public abstract String message();

    public abstract boolean available();

    public static AvailabilityCheckResponse create(int code, String message, boolean available) {
        return builder()
                .code(code)
                .message(message)
                .available(available)
                .build();
    }

    public static TypeAdapter<AvailabilityCheckResponse> typeAdapter(Gson gson) {
        return new AutoValue_AvailabilityCheckResponse.GsonTypeAdapter(gson);
    }

    public static Builder builder() {
        return new AutoValue_AvailabilityCheckResponse.Builder();
    }

    @AutoValue.Builder
    public abstract static class Builder {
        public abstract Builder code(int code);

        public abstract Builder message(String message);

        public abstract Builder available(boolean available);

        public abstract AvailabilityCheckResponse build();
    }
}
