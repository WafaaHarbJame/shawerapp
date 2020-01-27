package com.shawerapp.android.screens.newanswer;

import android.content.Intent;
import android.text.Editable;

import com.shawerapp.android.autovalue.LawyerFile;
import com.shawerapp.android.base.FragmentLifecycle;

import java.io.File;
import java.util.Iterator;
import java.util.List;

import io.reactivex.Maybe;
import io.reactivex.functions.Action;

public final class ComposeAnswerContract {

    interface View extends FragmentLifecycle.View {
        void initBindings();

        void setPrimaryInstruction(int primaryInstructionRes);

        void setSecondaryInstructionText(String text);

        void changeViewPagerPage(int page);

        void updateRecordProgress(String progress);

        void updateReviewProgress(String progress);

        void updateSelectedFiles(Iterator<String> selectedFilesPaths);

        void updateSelectedFiles_(Iterator<LawyerFile> selectedFilesPaths);

        void updateWordCount(int length);

        void updateRecordButtonDrawable(int resId);

        void updatePlayButtonDrawable(int resId);

        void showPlayButton();

        void showStopButton();

        void setCoinsToCollect(long coinsRequired);

        void showSuccessDialog(String successMessage, Action onConfirm);

        void addLawyerFile(LawyerFile lawyerFile);

        void updateLawyerFile(LawyerFile lawyerFile);

        void removeLawyerFile(LawyerFile lawyerFile);

        boolean isSelected(LawyerFile lawyerFile);

        void toggleSelection(LawyerFile lawyerFile);

        int getDisplayedScreen();

        void showComposeAnswer();

        void showSelectFiles(boolean isForward);

        void showViewFile(File downloadedFile);

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

        void onPlayButtonClicked();

        void startPlaying();

        void onStopPlayButtonClicked();

        void stopPlaying();

        void pauseRecord();

        void addAnswer(Maybe<String> audioFileUpload, Maybe<List<String>> attachmentFileUpload, Maybe<String> questionDescription);

        void onAttachButtonClicked();

        void onViewButtonClicked(LawyerFile item);

        boolean onItemClicked(LawyerFile item);

        void onActivityResult(int requestCode, int resultCode, Intent data);

        void onAnswerEditTextChange(Editable editable);

        void onCloseCaseClicked();

        void onOpenForDetailsClicked();

        void onOpenForFeedbackClicked();

        void showPayScreen();

        void onSubmitComposition();
    }
}
