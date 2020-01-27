package com.shawerapp.android.screens.composer;

import com.google.auto.value.AutoValue;
import com.shawerapp.android.autovalue.Field;
import com.shawerapp.android.autovalue.LawyerUser;
import com.shawerapp.android.autovalue.Question;
import com.shawerapp.android.base.BaseFragment;
import com.shawerapp.android.base.BaseKey;
import com.shawerapp.android.autovalue.SubSubject;

import javax.annotation.Nullable;

@AutoValue
public abstract class ComposerKey extends BaseKey {

    public static final int QUESTION = 1;

    public static final int PRACTICE = 2;

    public static final int DETAILS = 3;

    public static final int FEEDBACK = 4;

    public abstract int requestType();

    @Nullable
    public abstract Field selectedField();

    @Nullable
    public abstract SubSubject selectedSubSubject();

    @Nullable
    public abstract LawyerUser selectedLawyerUser();

    @Nullable
    public abstract Question questionToRepondTo();

    @Override
    protected BaseFragment createFragment() {
        return ComposerFragment.newInstance(requestType(), selectedField(), selectedSubSubject(), selectedLawyerUser(), questionToRepondTo());
    }

    public static ComposerKey create(int requestType, Field selectedField, SubSubject selectedSubSubject, LawyerUser selectedLawyerUser, Question questionToRepondTo) {
        return builder()
                .requestType(requestType)
                .selectedField(selectedField)
                .selectedSubSubject(selectedSubSubject)
                .selectedLawyerUser(selectedLawyerUser)
                .questionToRepondTo(questionToRepondTo)
                .build();
    }

    public static Builder builder() {
        return new AutoValue_ComposerKey.Builder();
    }

    @AutoValue.Builder
    public abstract static class Builder {
        public abstract Builder selectedLawyerUser(LawyerUser selectedLawyerUser);

        public abstract Builder selectedField(Field selectedField);

        public abstract Builder selectedSubSubject(SubSubject selectedSubSubject);

        public abstract Builder requestType(int requestType);

        public abstract Builder questionToRepondTo(Question questionToRepondTo);

        public abstract ComposerKey build();
    }
}
