package com.shawerapp.android.autovalue;

import android.os.Parcelable;

import com.google.auto.value.AutoValue;

@AutoValue
public abstract class MemoEvent implements Parcelable {

    public abstract int type();

    public abstract Memo memo();

    public static MemoEvent create(int type, Memo memo) {
        return builder()
                .type(type)
                .memo(memo)
                .build();
    }

    public static Builder builder() {
        return new AutoValue_MemoEvent.Builder();
    }

    @AutoValue.Builder
    public abstract static class Builder {
        public abstract Builder type(int type);

        public abstract Builder memo(Memo memo);

        public abstract MemoEvent build();
    }
}
