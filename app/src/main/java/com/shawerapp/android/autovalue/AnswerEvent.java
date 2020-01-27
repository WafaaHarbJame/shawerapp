package com.shawerapp.android.autovalue;

import com.google.auto.value.AutoValue;

@AutoValue
public abstract class AnswerEvent {

    public abstract int type();

    public abstract Answer answer();

    public static AnswerEvent create(int type, Answer answer) {
        return builder()
                .type(type)
                .answer(answer)
                .build();
    }

    public static Builder builder() {
        return new AutoValue_AnswerEvent.Builder();
    }

    @AutoValue.Builder
    public abstract static class Builder {
        public abstract Builder type(int type);

        public abstract Builder answer(Answer answer);

        public abstract AnswerEvent build();
    }
}
