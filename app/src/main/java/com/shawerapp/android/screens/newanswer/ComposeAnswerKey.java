package com.shawerapp.android.screens.newanswer;

import com.google.auto.value.AutoValue;
import com.shawerapp.android.autovalue.Question;
import com.shawerapp.android.base.BaseFragment;
import com.shawerapp.android.base.BaseKey;

@AutoValue
public abstract class ComposeAnswerKey extends BaseKey {

    public abstract Question questionToAnswer();

    @Override
    protected BaseFragment createFragment() {
        return ComposeAnswerFragment.newInstance(questionToAnswer());
    }

    public static ComposeAnswerKey create(Question questionToAnswer) {
        return builder()
                .questionToAnswer(questionToAnswer)
                .build();
    }

    public static Builder builder() {
        return new AutoValue_ComposeAnswerKey.Builder();
    }

    @AutoValue.Builder
    public abstract static class Builder {
        public abstract Builder questionToAnswer(Question questionToAnswer);

        public abstract ComposeAnswerKey build();
    }
}
