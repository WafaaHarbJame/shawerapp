package com.shawerapp.android.screens.selectsubsubject;

import com.google.auto.value.AutoValue;
import com.shawerapp.android.autovalue.Field;
import com.shawerapp.android.base.BaseFragment;
import com.shawerapp.android.base.BaseKey;

@AutoValue
public abstract class SelectSubSubjectKey extends BaseKey {

    public abstract Field selectedField();

    public abstract int requestType();

    @Override
    protected BaseFragment createFragment() {
        return SelectSubSubjectFragment.newInstance(requestType(), selectedField());
    }

    public static SelectSubSubjectKey create(int requestType, Field selectedField) {
        return builder()
                .selectedField(selectedField)
                .requestType(requestType)
                .build();
    }

    public static Builder builder() {
        return new AutoValue_SelectSubSubjectKey.Builder();
    }

    @AutoValue.Builder
    public abstract static class Builder {
        public abstract Builder selectedField(Field selectedField);

        public abstract Builder requestType(int requestType);

        public abstract SelectSubSubjectKey build();
    }
}
