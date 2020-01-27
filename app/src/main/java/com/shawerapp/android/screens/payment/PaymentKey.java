package com.shawerapp.android.screens.payment;

import com.google.auto.value.AutoValue;
import com.shawerapp.android.autovalue.Field;
import com.shawerapp.android.autovalue.LawyerUser;
import com.shawerapp.android.autovalue.SubSubject;
import com.shawerapp.android.base.BaseFragment;
import com.shawerapp.android.base.BaseKey;
import com.shawerapp.android.screens.composer.ComposerViewModel;

import java.io.File;
import java.util.List;

import javax.annotation.Nullable;

import io.reactivex.Maybe;

@AutoValue public abstract class PaymentKey extends BaseKey {

//    public abstract Invoice invoice();

//    public static PaymentKey create() {
//        return new AutoValue_PaymentKey();
//    }

    @Override
    protected BaseFragment createFragment() {
        return PaymentFragment.newInstance(requestType(), selectedField(), selectedSubSubject(),
                selectedLawyerUser(), questionDescription(), audioFileUpload(), attachmentFileUpload(), mRecordedAudioFile(), mComposition(), mComposerViewModel());
    }

//    public static PaymentKey create(Invoice invoice) {
//        if (invoice != null) {
//            return builder()
//                    .invoice(invoice)
//                    .build();
//        } else {
//            return builder()
//                    .build();
//        }
//
//    }

//    public static Builder builder() { return new AutoValue_PaymentKey.Builder(); }
//
//    @AutoValue.Builder()
//    public abstract static class Builder {
//        public abstract PaymentKey.Builder invoice(Invoice invoice);
//
//        public abstract PaymentKey build();
//    }

    public static final int QUESTION = 1;

    public static final int PRACTICE = 2;

    public static final int DETAILS = 3;

    public static final int FEEDBACK = 4;

    public abstract int requestType();

    @Nullable
    public abstract Field selectedField();

    @Nullable
    public abstract File mRecordedAudioFile();

//    @Nullable
//    public abstract ArraySet<String> mSelectedFilesPaths();

    @Nullable
    public abstract CharSequence mComposition();


    @Nullable
    public abstract SubSubject selectedSubSubject();

    @Nullable
    public abstract LawyerUser selectedLawyerUser();

    @Nullable
    public abstract String questionDescription();

    @Nullable
    public abstract String audioFileUpload();

    @Nullable
    public abstract List<String> attachmentFileUpload();

    @Nullable
    public abstract ComposerViewModel mComposerViewModel();




    public static PaymentKey create(int requestType, Field selectedField, SubSubject selectedSubSubject,
                                    LawyerUser selectedLawyerUser,
                                    Maybe<String> questionDescription, Maybe<String> audioFileUpload,
                                    Maybe<List<String>> attachmentFileUpload, File mRecordedAudioFile,
                                    CharSequence mComposition, ComposerViewModel mComposerViewModel) {
        return builder()
                .requestType(requestType)
                .selectedField(selectedField)
                .selectedSubSubject(selectedSubSubject)
                .selectedLawyerUser(selectedLawyerUser)
                .questionDescription(String.valueOf(questionDescription))
                .audioFileUpload(String.valueOf(audioFileUpload))
                .attachmentFileUpload((List<String>) attachmentFileUpload)
                .mRecordedAudioFile(mRecordedAudioFile)
//                .mSelectedFilesPaths(mSelectedFilesPaths)
                .mComposerViewModel(mComposerViewModel)
                .mComposition(mComposition)
                .build();
    }

    public static PaymentKey.Builder builder() {
        return new AutoValue_PaymentKey.Builder();
    }

    @AutoValue.Builder
    public abstract static class Builder {
        public abstract PaymentKey.Builder selectedLawyerUser(LawyerUser selectedLawyerUser);

        public abstract PaymentKey.Builder selectedField(Field selectedField);

        public abstract PaymentKey.Builder selectedSubSubject(SubSubject selectedSubSubject);

        public abstract PaymentKey.Builder requestType(int requestType);

        public abstract PaymentKey.Builder questionDescription(String questionDescription);

        public abstract PaymentKey.Builder audioFileUpload(String audioFileUpload);

        public abstract PaymentKey.Builder mRecordedAudioFile(File mRecordedAudioFile);

//        public abstract PaymentKey.Builder mSelectedFilesPaths(ArraySet<String> mSelectedFilesPaths);

        public abstract PaymentKey.Builder mComposition(CharSequence mComposition);

        public abstract PaymentKey.Builder attachmentFileUpload(List<String> attachmentFileUpload);

        public abstract PaymentKey.Builder mComposerViewModel(ComposerViewModel mComposerViewModel);

        public abstract PaymentKey build();
    }

}

