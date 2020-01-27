package com.shawerapp.android.autovalue.annotation;

import android.os.Parcelable;

import com.google.auto.value.AutoValue;
import com.google.gson.Gson;
import com.google.gson.TypeAdapter;

import javax.annotation.Nullable;

@AutoValue
public abstract class GetEmailResponse implements Parcelable {

    public static final int SUCCESS = 200;

    public abstract int code();

    @Nullable
    public abstract String message();

    @Nullable
    public abstract String email();

    public static GetEmailResponse create(int code, String message, String email) {
        return builder()
                .code(code)
                .message(message)
                .email(email)
                .build();
    }

    public static TypeAdapter<GetEmailResponse> typeAdapter(Gson gson) {
        return new AutoValue_GetEmailResponse.GsonTypeAdapter(gson);
    }

    public static GetEmailResponse.Builder builder() {
        return new AutoValue_GetEmailResponse.Builder();
    }

    @AutoValue.Builder
    public abstract static class Builder {
        public abstract GetEmailResponse.Builder code(int code);

        public abstract GetEmailResponse.Builder message(String message);

        public abstract Builder email(String email);

        public abstract GetEmailResponse build();
    }
}
