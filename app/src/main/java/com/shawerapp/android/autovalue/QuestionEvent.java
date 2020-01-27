package com.shawerapp.android.autovalue;

import android.os.Parcelable;

import com.google.auto.value.AutoValue;

@AutoValue
public abstract class QuestionEvent implements Parcelable {

    public abstract int type();

    public abstract Question question();

    public static QuestionEvent create(int type, Question question) {
        return builder()
                .type(type)
                .question(question)
                .build();
    }

    public static Builder builder() {
        return new AutoValue_QuestionEvent.Builder();
    }

    @AutoValue.Builder
    public abstract static class Builder {
        public abstract Builder type(int type);

        public abstract Builder question(Question question);

        public abstract QuestionEvent build();
    }
}
