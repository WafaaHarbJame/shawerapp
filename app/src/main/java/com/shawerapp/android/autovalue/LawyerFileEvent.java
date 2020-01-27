package com.shawerapp.android.autovalue;

import android.os.Parcelable;

import com.google.auto.value.AutoValue;

@AutoValue
public abstract class LawyerFileEvent implements Parcelable {

    public abstract int type();

    public abstract LawyerFile lawyerFile();

    public static LawyerFileEvent create(int type, LawyerFile lawyerFile) {
        return builder()
                .type(type)
                .lawyerFile(lawyerFile)
                .build();
    }

    public static Builder builder() {
        return new AutoValue_LawyerFileEvent.Builder();
    }

    @AutoValue.Builder
    public abstract static class Builder {
        public abstract Builder type(int type);

        public abstract Builder lawyerFile(LawyerFile lawyerFile);

        public abstract LawyerFileEvent build();
    }
}
