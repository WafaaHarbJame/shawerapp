package com.shawerapp.android.screens.selectlawyer;

import com.google.auto.value.AutoValue;
import com.shawerapp.android.autovalue.Field;
import com.shawerapp.android.base.BaseFragment;
import com.shawerapp.android.base.BaseKey;
import com.shawerapp.android.autovalue.SubSubject;

@AutoValue
public abstract class SelectLawyerKey extends BaseKey {

    public abstract int requestType();

    public abstract Field field();

    public abstract SubSubject subSubject();

    @Override
    protected BaseFragment createFragment() {
        return SelectLawyerFragment.newInstance(requestType(), field(), subSubject());
    }

    public static SelectLawyerKey create(int requestType, Field field, SubSubject subSubject) {
        return builder()
                .requestType(requestType)
                .field(field)
                .subSubject(subSubject)
                .build();
    }

    public static Builder builder() {
        return new AutoValue_SelectLawyerKey.Builder();
    }

    @AutoValue.Builder
    public abstract static class Builder {
        public abstract Builder subSubject(SubSubject subSubject);

        public abstract Builder field(Field field);

        public abstract Builder requestType(int requestType);

        public abstract SelectLawyerKey build();
    }
}
