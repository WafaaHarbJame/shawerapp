package com.shawerapp.android.screens.viewrequest;

import com.google.auto.value.AutoValue;
import com.shawerapp.android.autovalue.PracticeRequest;
import com.shawerapp.android.base.BaseFragment;
import com.shawerapp.android.base.BaseKey;

@AutoValue
public abstract class ViewCaseRequestKey extends BaseKey {

    public abstract PracticeRequest practiceRequest();

    @Override
    protected BaseFragment createFragment() {
        return ViewCaseRequestFragment.newInstance(practiceRequest());
    }

    public static ViewCaseRequestKey create(PracticeRequest practiceRequest) {
        return builder()
                .practiceRequest(practiceRequest)
                .build();
    }

    public static Builder builder() {
        return new AutoValue_ViewCaseRequestKey.Builder();
    }

    @AutoValue.Builder
    public abstract static class Builder {
        public abstract Builder practiceRequest(PracticeRequest practiceRequest);

        public abstract ViewCaseRequestKey build();
    }
}
