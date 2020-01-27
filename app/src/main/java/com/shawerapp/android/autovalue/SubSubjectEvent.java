package com.shawerapp.android.autovalue;

import android.os.Parcelable;

import com.google.auto.value.AutoValue;

@AutoValue
public abstract class SubSubjectEvent implements Parcelable {

    public abstract int type();

    public abstract SubSubject subSubject();

    public static SubSubjectEvent create(int type, SubSubject subSubject) {
        return builder()
                .type(type)
                .subSubject(subSubject)
                .build();
    }

    public static SubSubjectEvent.Builder builder() {
        return new AutoValue_SubSubjectEvent.Builder();
    }

    @AutoValue.Builder
    public abstract static class Builder {
        public abstract SubSubjectEvent.Builder type(int type);

        public abstract SubSubjectEvent.Builder subSubject(SubSubject subSubject);

        public abstract SubSubjectEvent build();
    }
}
