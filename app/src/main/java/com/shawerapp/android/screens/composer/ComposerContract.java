package com.shawerapp.android.screens.composer;

import android.content.Intent;
import android.text.Editable;

import com.shawerapp.android.base.FragmentLifecycle;
import com.shawerapp.android.screens.payment.PaymentContract;
import com.shawerapp.android.screens.payment.PaymentFragment;

import java.util.Iterator;
import java.util.List;

import io.reactivex.Maybe;
import io.reactivex.functions.Action;

public final class ComposerContract {

    interface View extends FragmentLifecycle.View {
        void initBindings();

        void setPrimaryInstruction(int primaryInstructionRes);

        void setSecondaryInstructionText(String text);

        void changeViewPagerPage(int page);

        void updateRecordProgress(String progress);

        void updateReviewProgress(String progress);

        void updateSelectedFiles(Iterator<String> selectedFilesPaths);

        void updateWordCount(int length);

        void updateRecordButtonDrawable(int resId);

        void updatePlayButtonDrawable(int resId);

        void showPlayButton();

        void showStopButton();

        void setCoinsRequired(long coinsRequired);

        void setLabelVisibility(int visibility);

        void showSuccessDialog(String successMessage, Action onConfirm);

        void updateAudioRecordVisualizer(int amplitude);

        void startPlaybackVisualizer(int audioSessionId);

        void stopPlaybackVisualizer();
    }

    interface ViewModel extends FragmentLifecycle.ViewModel {
        void onRecordButtonClicked();

        void onRecordButtonClicked1();

        void checkAudioPermissions(Action permissionGranted) throws Exception;

        void startRecording();

        void stopRecording();

        String secToTime(int sec);

        void onStopPlayButtonClicked();

        void stopPlaying();

        void onPlayButtonClicked(boolean isConfirm);

        void startPlaying(boolean isConfirm);

        void pauseRecord();

        void onAttachButtonClicked();

        void openAttachmentMenu();

        void openImagePicker();

        void openFilePicker();

        void onActivityResult(int requestCode, int resultCode, Intent data);

        void onCompositionEditTextChange(Editable editable);

        void onSubmitComposition();

        void addNewQuestion(Maybe<String> audioFileUpload, Maybe<List<String>> attachmentFileUpload, Maybe<String> questionDescription);

        void addNewQuestion(Maybe<String> audioFileUpload, Maybe<List<String>> attachmentFileUpload, Maybe<String> questionDescription, PaymentContract.View mmView, PaymentFragment paymentFragment);

        void addNewPracticeRequest(Maybe<String> audioFileUpload, Maybe<List<String>> attachmentFileUpload, Maybe<String> questionDescription);

        void addNewPracticeRequest(Maybe<String> audioFileUpload, Maybe<List<String>> attachmentFileUpload, Maybe<String> questionDescription, PaymentContract.View mmView, PaymentFragment paymentFragment);

        void addAnswer(Maybe<String> audioFileUpload, Maybe<List<String>> attachmentFileUpload, Maybe<String> questionDescription);

        void showNotEnoughCoinsPopup();

        Boolean getPaidStatus();

        void setPaidStatus(Boolean paid_, PaymentContract.View mmView_, PaymentFragment paymentFragment, PaymentContract.ViewModel paymentViewModel_, String transAction_);

        void setPaidStatus(Boolean paid_);
    }
}
