package com.shawerapp.android.screens.questiondetails;

import com.google.auto.value.AutoValue;
import com.shawerapp.android.autovalue.Question;
import com.shawerapp.android.base.BaseFragment;
import com.shawerapp.android.base.BaseKey;

@AutoValue
public abstract class QuestionDetailsKey extends BaseKey {

    public abstract Question question();

    @Override
    protected BaseFragment createFragment() {
        return QuestionDetailsFragment.newInstance(question());
    }

    public static QuestionDetailsKey create(Question question) {
        return builder()
                .question(question)
                .build();
    }

    public static Builder builder() {
        return new AutoValue_QuestionDetailsKey.Builder();
    }

    @AutoValue.Builder
    public abstract static class Builder {
        public abstract Builder question(Question question);

        public abstract QuestionDetailsKey build();
    }
}
