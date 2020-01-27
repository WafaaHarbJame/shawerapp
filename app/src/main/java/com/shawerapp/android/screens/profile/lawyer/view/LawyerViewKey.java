package com.shawerapp.android.screens.profile.lawyer.view;

import com.google.auto.value.AutoValue;
import com.shawerapp.android.autovalue.LawyerUser;
import com.shawerapp.android.base.BaseFragment;
import com.shawerapp.android.base.BaseKey;

@AutoValue
public abstract class LawyerViewKey extends BaseKey {

    public abstract LawyerUser lawyerUser();

    @Override
    protected BaseFragment createFragment() {
        return LawyerViewFragment.newInstance(lawyerUser());
    }

    public static LawyerViewKey create(LawyerUser lawyerUser) {
        return builder()
                .lawyerUser(lawyerUser)
                .build();
    }

    public static Builder builder() {
        return new AutoValue_LawyerViewKey.Builder();
    }

    @AutoValue.Builder
    public abstract static class Builder {
        public abstract Builder lawyerUser(LawyerUser lawyerUser);

        public abstract LawyerViewKey build();
    }
}
