package com.shawerapp.android.screens.newanswer;

import android.Manifest;
import android.app.Activity;
import android.content.ContextWrapper;
import android.content.Intent;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AlertDialog;
import androidx.collection.ArraySet;

import android.text.Editable;
import android.util.Log;

import com.esafirm.imagepicker.features.ImagePicker;
import com.esafirm.imagepicker.model.Image;
import com.github.piasy.rxandroidaudio.AudioRecorder;
import com.github.piasy.rxandroidaudio.PlayConfig;
import com.github.piasy.rxandroidaudio.RxAudioPlayer;
import com.shawerapp.android.R;
import com.shawerapp.android.autovalue.Answer;
import com.shawerapp.android.autovalue.LawyerFile;
import com.shawerapp.android.autovalue.LawyerFileEvent;
import com.shawerapp.android.autovalue.Question;
import com.shawerapp.android.backend.base.FileFramework;
import com.shawerapp.android.backend.base.RealTimeDataFramework;
import com.shawerapp.android.base.BaseFragment;
import com.shawerapp.android.screens.composer.ComposerFragment;
import com.shawerapp.android.screens.container.ContainerContract;
import com.shawerapp.android.screens.questionlist.QuestionListKey;
import com.shawerapp.android.utils.CommonUtils;
import com.shawerapp.android.utils.LocaleHelper;
import com.shawerapp.android.utils.LoginUtil;
import com.tbruyelle.rxpermissions2.RxPermissions;
import com.trello.rxlifecycle2.android.FragmentEvent;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import javax.inject.Inject;

import droidninja.filepicker.FilePickerConst;
import io.reactivex.Flowable;
import io.reactivex.Maybe;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.internal.functions.Functions;
import io.reactivex.processors.BehaviorProcessor;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

import static com.shawerapp.android.screens.composer.ComposerFragment.REQUEST_ATTACH;
import static com.shawerapp.android.screens.newanswer.ComposeAnswerFragment.ARG_QUESTION_TO_ANSWER;
import static com.shawerapp.android.screens.newanswer.ComposeAnswerFragment.COMPOSE_ANSWER;
import static com.shawerapp.android.screens.newanswer.ComposeAnswerFragment.SELECT_FILE;
import static com.shawerapp.android.screens.newanswer.ComposeAnswerFragment.VIEW_FILE;

public final class ComposeAnswerViewModel implements ComposeAnswerContract.ViewModel {

    public static int step = 0;

    private static final int ATTACHMENT_CAMERA = 0;

    private static final int ATTACHMENT_PICTURE_GALLERY = 1;

    private static final int COMPOSE = 0;

    private static final int REVIEW = 1;

    private static final int STATUS = 2;

    private static final int PAY = 3;

    private BaseFragment mFragment;

    private ComposeAnswerContract.View mView;

    @Inject
    ContainerContract.View mContainerView;

    @Inject
    ContainerContract.ViewModel mContainerViewModel;

    @Inject
    LoginUtil mLoginUtil;

    @Inject
    RealTimeDataFramework mRTDataFramework;

    @Inject
    FileFramework mFileFramework;

    private int currentPage = 0;

    private AudioRecorder mAudioRecorder;

    private RxPermissions mPermissions;

    private Disposable mRecordDisposable;

    private File mRecordedAudioFile;

    private RxAudioPlayer mRxAudioPlayer;

    private ArraySet<LawyerFile> mSelectedLawyerFiles = new ArraySet<>();

    private CharSequence mAnswerDescription;

    private boolean mIsRecordPlaying;

    private boolean mIsRecording;

    private long mPracticeRequestCost;

    private Question mQuestionToAnswer;

    private String mSelectedStatus;

    private BehaviorProcessor<LawyerFileEvent> mLawyerFileProcessor = BehaviorProcessor.create();

    private Handler mReviewProgressHandler;

    private Runnable mReviewProgressRunnable;

    private Handler mRecordProgressHandler;

    private Runnable mRecordProgressRunnable;

    private Queue<File> mAudioFiles = new LinkedList<>();

    private Handler mVisualizerHandler;

    private Runnable mVisualizerRunnable;

    @Inject
    public ComposeAnswerViewModel(BaseFragment fragment, ComposeAnswerContract.View view) {
        mFragment = fragment;
        mView = view;

        Bundle args = fragment.getArguments();
        mQuestionToAnswer = args.getParcelable(ARG_QUESTION_TO_ANSWER);

        mAudioRecorder = AudioRecorder.getInstance();
        mPermissions = new RxPermissions(mFragment.getActivity());
        mRxAudioPlayer = RxAudioPlayer.getInstance();
    }

    @Override
    public void onViewCreated() {
        try {
            checkAudioPermissions(() -> mView.initBindings());
        } catch (Exception e) {
            Timber.e(CommonUtils.getExceptionString(e));
            mContainerView.showMessage(e.getMessage());
        }

        mView.setPrimaryInstruction(R.string.compose_answer_primary_instruction);

        mLawyerFileProcessor
                .serialize()
                .compose(mFragment.bindUntilEvent(FragmentEvent.DESTROY_VIEW))
                .subscribe(fileEvent -> {
                    switch (fileEvent.type()) {
                        case RealTimeDataFramework.EVENT_ADDED:
                            mView.addLawyerFile(fileEvent.lawyerFile());
                            break;
                        case RealTimeDataFramework.EVENT_UPDATED:
                            mView.updateLawyerFile(fileEvent.lawyerFile());
                            break;
                        case RealTimeDataFramework.EVENT_REMOVED:
                            mView.removeLawyerFile(fileEvent.lawyerFile());
                            break;
                    }
                });

        mContainerView.ShowRightToolbarButton();
    }

    @Override
    public void onAfterEnterAnimation() {
        mRTDataFramework.retrieveLawyerFiles()
                .compose(mFragment.bindUntilEvent(FragmentEvent.DESTROY_VIEW))
                .subscribe(mLawyerFileProcessor::onNext);
    }

    @Override
    public void onBackButtonClicked() {
        if (mView.getDisplayedScreen() == COMPOSE_ANSWER) {
            if (currentPage == COMPOSE) {
                mContainerViewModel.goBack().subscribe(mContainerViewModel.navigationObserver());
                ComposeAnswerFragment.status = 0;
            } else if (currentPage == REVIEW) {
                if (LocaleHelper.getLanguage(mFragment.getContext()).equalsIgnoreCase(LocaleHelper.ARABIC)) {
                    mContainerView.setToolbarTitle(mQuestionToAnswer.ar_subSubjectName());
                } else {
                    mContainerView.setToolbarTitle(mQuestionToAnswer.subSubjectName());
                }
                mContainerView.setToolbarSubTitle(mQuestionToAnswer.askerUsername());
                mContainerView.setRightToolbarButtonImageResource(R.drawable.ic_icon_navigation_right);
                currentPage = COMPOSE;
                --step;
                --ComposeAnswerFragment.status;
                mView.changeViewPagerPage(COMPOSE);
            } else if (currentPage == STATUS) {
                mContainerView.setToolbarTitle(mFragment.getString(R.string.new_case));
                mContainerView.setToolbarSubTitle(mFragment.getString(R.string.confirm_your_answer));
                mContainerView.setRightToolbarButtonImageResource(R.drawable.ic_icon_navigation_right);
                currentPage = REVIEW;
                mView.changeViewPagerPage(REVIEW);
                ++step;
                ++ComposeAnswerFragment.status;
            } else if (currentPage == PAY) {
                mContainerView.setToolbarTitle(mFragment.getString(R.string.new_case));
                mContainerView.setToolbarSubTitle(mFragment.getString(R.string.set_case_status));
                mContainerView.setRightToolbarButtonImageResource(-1);
                currentPage = STATUS;
                mView.changeViewPagerPage(STATUS);
            }
        } else if (mView.getDisplayedScreen() == SELECT_FILE) {
            if (LocaleHelper.getLanguage(mFragment.getContext()).equalsIgnoreCase(LocaleHelper.ARABIC)) {
                mContainerView.setToolbarTitle(mQuestionToAnswer.ar_subSubjectName());
            } else {
                mContainerView.setToolbarTitle(mQuestionToAnswer.subSubjectName());
            }
            mContainerView.setToolbarSubTitle(mQuestionToAnswer.askerUsername());
            mContainerView.setRightToolbarButtonImageResource(R.drawable.ic_icon_navigation_right);
            mView.showComposeAnswer();
        } else if (mView.getDisplayedScreen() == VIEW_FILE) {
            if (LocaleHelper.getLanguage(mFragment.getContext()).equalsIgnoreCase(LocaleHelper.ARABIC)) {
                mContainerView.setToolbarTitle(mQuestionToAnswer.ar_subSubjectName());
            } else {
                mContainerView.setToolbarTitle(mQuestionToAnswer.subSubjectName());
            }
            mContainerView.setToolbarSubTitle(mFragment.getString(R.string.select_a_file));
            mContainerView.setRightToolbarButtonImageResource(-1);
            mView.showSelectFiles(false);
        }
    }

    @Override
    public void setupToolbar() {
        if (LocaleHelper.getLanguage(mFragment.getContext()).equalsIgnoreCase(LocaleHelper.ARABIC)) {
            mContainerView.setToolbarTitle(mQuestionToAnswer.ar_subSubjectName());
        } else {
            mContainerView.setToolbarTitle(mQuestionToAnswer.subSubjectName());
        }
        mContainerView.setToolbarSubTitle(mQuestionToAnswer.askerUsername());
    }

    @Override
    public void onLeftToolbarButtonClicked() {
        onBackButtonClicked();
    }

    @Override
    public void onRightToolbarButtonClicked() {
        if (currentPage == COMPOSE) {
            if (CommonUtils.isNotEmpty(mAnswerDescription) || (mRecordedAudioFile != null)) {
                mContainerView.setToolbarTitle(mFragment.getString(R.string.new_case));
                mContainerView.setToolbarSubTitle(mFragment.getString(R.string.confirm_your_answer));
                mContainerView.setRightToolbarButtonImageResource(R.drawable.ic_icon_navigation_right);
                mView.setSecondaryInstructionText(mFragment.getString(R.string.label_compose_question_sub_description_2));
                currentPage = REVIEW;
                mView.changeViewPagerPage(REVIEW);
            } else {
                mContainerView.showMessage(mFragment.getString(R.string.invalid_input), mFragment.getString(R.string.error_compose), true);
            }
        } else if (currentPage == REVIEW) {
            mView.setCoinsToCollect(mQuestionToAnswer.serviceFee());
            mContainerView.setToolbarTitle(mFragment.getString(R.string.new_case));
            mContainerView.setToolbarSubTitle(mFragment.getString(R.string.set_case_status));
            mContainerView.setRightToolbarButtonImageResource(-1);
            mView.setSecondaryInstructionText(mFragment.getString(R.string.label_compose_lawyer_answer_sub_description_3));
            currentPage = STATUS;
            mView.changeViewPagerPage(STATUS);
        }
    }

    @Override
    public void onRecordButtonClicked() {
        try {
            checkAudioPermissions(() -> {
                if (!mIsRecordPlaying) {
                    if (mIsRecording) {
                        stopRecording();
                    } else {
                        mView.updateRecordButtonDrawable(R.drawable.icon_stop_record);
                        startRecording();
                    }
                }
            });
        } catch (Exception e) {
            Timber.e(CommonUtils.getExceptionString(e));
            mContainerView.showMessage(e.getMessage());
        }
    }

    @Override
    public void onRecordButtonClicked1() {
        try {
            checkAudioPermissions(() -> {
                if (!mIsRecordPlaying) {
                    if (mIsRecording) {
                        stopRecording();
                    }
                }
            });
        } catch (Exception e) {
            Timber.e(CommonUtils.getExceptionString(e));
            mContainerView.showMessage(e.getMessage());
        }
    }

    @Override
    public void checkAudioPermissions(Action permissionGranted) throws Exception {
        boolean isPermissionsGranted = mPermissions.isGranted(Manifest.permission.WRITE_EXTERNAL_STORAGE) && mPermissions.isGranted(Manifest.permission.READ_EXTERNAL_STORAGE) && mPermissions.isGranted(Manifest.permission.RECORD_AUDIO);
        if (!isPermissionsGranted) {
            mPermissions
                    .request(Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.READ_EXTERNAL_STORAGE,
                            Manifest.permission.RECORD_AUDIO)
                    .subscribe(granted -> {
                        if (granted) {
                            permissionGranted.run();
                        } else {
                            mContainerView.showMessage(mFragment.getString(R.string.error_audio_permission), true);
                        }
                    }, Throwable::printStackTrace);
        } else {
            permissionGranted.run();
        }
    }

    @Override
    public void startRecording() {
        mIsRecording = true;

        int bufferSize = AudioRecord.getMinBufferSize(
                44100,
                AudioFormat.CHANNEL_IN_MONO,
                AudioFormat.ENCODING_PCM_16BIT
        );

        mRecordDisposable = Observable
                .fromCallable(() -> {
                    ContextWrapper cw = new ContextWrapper(mFragment.getContext());
                    File mediaDir = cw.getFilesDir();

                    String fileName = mediaDir.getAbsolutePath() +
                            File.separator + System.nanoTime() + ".wav";

                    mRecordedAudioFile = new File(fileName);

                    Timber.e("to prepare record");
                    return mAudioRecorder.prepareRecord(
                            MediaRecorder.AudioSource.MIC,
                            MediaRecorder.OutputFormat.MPEG_4,
                            MediaRecorder.AudioEncoder.AAC,
                            44100,
                            bufferSize,
                            mRecordedAudioFile);
                })
                .flatMap(b -> {
                    Timber.e("prepareRecord success");
                    Timber.e("to play audio_record_ready: " + R.raw.audio_record_ready);
                    return mRxAudioPlayer.play(
                            PlayConfig.res(mFragment.getContext(), R.raw.audio_record_ready)
                                    .build());
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnComplete(() -> {
                    Timber.e("audio_record_ready play finished");
                    mAudioRecorder.startRecord();

                    mVisualizerHandler = new Handler();
                    mVisualizerRunnable = new Runnable() {
                        @Override
                        public void run() {
                            if (mIsRecording) {
                                int amplitude = mAudioRecorder.getMaxAmplitude();
                                mView.updateAudioRecordVisualizer(amplitude);
                                // update in 40 milliseconds
                                mVisualizerHandler.postDelayed(this, 40);
                            }
                        }
                    };
                    mVisualizerHandler.post(mVisualizerRunnable);

                    mRecordProgressHandler = new Handler();
                    mRecordProgressRunnable = () -> {
                        if (mAudioRecorder != null) {
                            Timber.e("progress %d", mAudioRecorder.progress());
                            mView.updateRecordProgress(secToTime(mAudioRecorder.progress()));
                            mRecordProgressHandler.postDelayed(mRecordProgressRunnable, 1000);
                        }
                    };
                    mRecordProgressHandler.postDelayed(mRecordProgressRunnable, 1000);
                })
                .doOnNext(b -> Timber.d("startRecord success"))
                .subscribe(
                        Functions.emptyConsumer(),
                        mContainerViewModel.catchErrorThrowable());
    }

    @Override
    public void stopRecording() {
        if (mRecordDisposable != null && !mRecordDisposable.isDisposed()) {
            mRecordDisposable.dispose();
        }
        if (mVisualizerHandler != null && mVisualizerHandler != null) {
            mVisualizerHandler.removeCallbacks(mRecordProgressRunnable);
        }
        if (mRecordProgressHandler != null && mRecordProgressRunnable != null) {
            mRecordProgressHandler.removeCallbacks(mRecordProgressRunnable);
        }
        Observable
                .fromCallable(() -> {
                    int seconds = mAudioRecorder.stopRecord();
                    Timber.d("stopRecord: " + seconds);
                    mAudioFiles.offer(mRecordedAudioFile);
                    return true;
                })
                .compose(mFragment.bindToLifecycle())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(added -> {
                    if (added) {
                        mView.updateRecordButtonDrawable(R.drawable.icon_record);
                        /*mView.showPlayButton();
                        mView.showStopButton();*/
                        mIsRecording = false;
                    }
                }, Throwable::printStackTrace);
    }

    @Override
    public String secToTime(int sec) {
        int seconds = sec % 60;
        int minutes = sec / 60;
        if (minutes >= 60) {
            int hours = minutes / 60;
            minutes %= 60;
            if (hours >= 24) {
                int days = hours / 24;
                return String.format("%d days %02d:%02d:%02d", days, hours % 24, minutes, seconds);
            }
            return String.format("%02d:%02d:%02d", hours, minutes, seconds);
        }
        return String.format("00:%02d:%02d", minutes, seconds);
    }

    @Override
    public void onPlayButtonClicked() {
        try {
            checkAudioPermissions(() -> {
                if (!mIsRecording && !mIsRecordPlaying) {
                    mIsRecordPlaying = true;
                    startPlaying();
                }
            });
        } catch (Exception e) {
            Timber.e(CommonUtils.getExceptionString(e));
            mContainerView.showMessage(e.getMessage());
        }
    }

    @Override
    public void startPlaying() {
        if (mRecordedAudioFile != null) {
            mRxAudioPlayer
                    .play(PlayConfig
                            .file(mRecordedAudioFile)
                            .streamType(AudioManager.STREAM_MUSIC)
                            .build())
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                            aBoolean -> {
                                mView.startPlaybackVisualizer(mRxAudioPlayer.getMediaPlayer().getAudioSessionId());

                                mReviewProgressHandler = new Handler();
                                mReviewProgressRunnable = () -> {
                                    if (mRxAudioPlayer.getMediaPlayer() != null) {
                                        mView.updateReviewProgress(secToTime(mRxAudioPlayer.getMediaPlayer().getCurrentPosition() / 1000));
                                    }
                                    mReviewProgressHandler.postDelayed(mReviewProgressRunnable, 1000);
                                };
                                mReviewProgressHandler.postDelayed(mReviewProgressRunnable, 1000);
                            },
                            mContainerViewModel.catchErrorThrowable(),
                            () -> {
                                mReviewProgressHandler.removeCallbacks(mReviewProgressRunnable);
                                mView.stopPlaybackVisualizer();
                                mView.updateReviewProgress("00:00:00");
                                mIsRecordPlaying = false;
                            });
        }
    }

    @Override
    public void onStopPlayButtonClicked() {
        try {
            checkAudioPermissions(this::stopPlaying);
        } catch (Exception e) {
            Timber.e(CommonUtils.getExceptionString(e));
            mContainerView.showMessage(e.getMessage());
        }
    }

    @Override
    public void stopPlaying() {
        if (!mIsRecording && mIsRecordPlaying) {
            mRxAudioPlayer.stopPlay();
            if (mReviewProgressHandler != null && mReviewProgressRunnable != null) {
                mReviewProgressHandler.removeCallbacks(mReviewProgressRunnable);
                mView.stopPlaybackVisualizer();
                mView.updateReviewProgress("00:00:00");
            }
            mIsRecordPlaying = false;
        }
    }

    @Override
    public void pauseRecord() {
        if (mRecordedAudioFile != null) {
            mRxAudioPlayer.pause();
        }
    }

//    @Override
//    public void onAttachButtonClicked() {
//        mContainerView.setRightToolbarButtonImageResource(-1);
//        mView.showSelectFiles(true);
//    }

    @Override
    public void onAttachButtonClicked() {
        boolean isPermissionsGranted = mPermissions.isGranted(Manifest.permission.WRITE_EXTERNAL_STORAGE) && mPermissions.isGranted(Manifest.permission.RECORD_AUDIO) && mPermissions.isGranted(Manifest.permission.CAMERA);
        if (!isPermissionsGranted) {
            mPermissions
                    .request(Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.READ_EXTERNAL_STORAGE,
                            Manifest.permission.CAMERA)
                    .subscribe(granted -> {
                        if (granted) {
                            openAttachmentMenu();
                        } else {
                            mContainerView.showMessage(mFragment.getString(R.string.error_audio_permission), true);
                        }
                    }, Throwable::printStackTrace);
        } else {
            openAttachmentMenu();
        }
    }

    public void openAttachmentMenu() {
        AlertDialog.Builder builder = new AlertDialog.Builder(mFragment.getContext(), R.style.DialogTheme);
        builder.setTitle(R.string.title_select_attachments);
        builder.setSingleChoiceItems(new String[]{
                        mFragment.getString(R.string.attachment_camera),
                        mFragment.getString(R.string.attachment_picture_gallery)},
                -1, null);
//                mFragment.getString(R.string.attachment_documents)
        builder.setPositiveButton(R.string.ok_caps, (dialog, whichButton) -> {
            dialog.dismiss();
            int selectedPosition = ((AlertDialog) dialog).getListView().getCheckedItemPosition();
            switch (selectedPosition) {
                case ATTACHMENT_CAMERA:
//                    openCameraPicker();
                    ImagePicker.cameraOnly().start(mFragment);
                    break;
                case ATTACHMENT_PICTURE_GALLERY:
                    try {
                        openImagePicker();
//                        openFilePicker();
                    } catch (Exception ex) {
                        Log.e("Error", ex.getLocalizedMessage());
                    }
                    break;
//                case ATTACHMENT_DOCUMMENT:
////                    openFilePicker();
//                    break;
            }
        });
        builder.show();
    }

    public void openImagePicker() {
        try {
//            FilePickerBuilder.Companion.getInstance().setMaxCount(5)
//                    .setSelectedFiles(Collections.list(Collections.enumeration(mSelectedFilesPaths)))
//                    .setActivityTheme(R.style.AppTheme)
//                    .enableImagePicker(true)
//                    .enableVideoPicker(false)
//                    .enableCameraSupport(true)
//                    .pickPhoto(mFragment, ComposerFragment.REQUEST_ATTACH);
            ImagePicker.create(mFragment) // Activity or Fragment
                    .multi() // multi mode (default mode)
                    .limit(5) // max images can be selected (99 by default)
                    .showCamera(false)
                    .theme(R.style.AppTheme)
                    .start();
        } catch (Exception ex) {
            Log.e("Error", ex.getLocalizedMessage());
        }

    }

    @Override
    public void onViewButtonClicked(LawyerFile item) {
        mFileFramework.downloadFile(item.downloadUrl())
                .compose(mFragment.bindUntilEvent(FragmentEvent.DESTROY_VIEW))
                .doOnSubscribe(subscription -> mContainerView.showLoadingIndicator())
                .doFinally(() -> mContainerView.hideLoadingIndicator())
                .subscribe(
                        Functions.emptyConsumer(),
                        mContainerViewModel.catchErrorThrowable(),
                        () -> {
                            if (mFileFramework.isDownloaded(item.downloadUrl()) && !mFileFramework.isDownloading(item.downloadUrl())) {
                                mContainerView.setToolbarTitle(item.fileName().toUpperCase());
                                if (LocaleHelper.getLanguage(mFragment.getContext()).equalsIgnoreCase(LocaleHelper.ARABIC)) {
                                    mContainerView.setToolbarSubTitle(mQuestionToAnswer.ar_subSubjectName());
                                } else {
                                    mContainerView.setToolbarSubTitle(mQuestionToAnswer.subSubjectName());
                                }
                                mContainerView.setRightToolbarButtonImageResource(-1);
                                mView.showViewFile(mFileFramework.getDownloadedFile(item.downloadUrl()));
                            }
                        });
    }

    @Override
    public boolean onItemClicked(LawyerFile item) {
        mView.toggleSelection(item);
        if (mView.isSelected(item)) {
            mSelectedLawyerFiles.add(item);
//            mView.updateSelectedFiles(mSelectedLawyerFiles.iterator());
            mView.updateSelectedFiles(mSelectedFilesPaths.iterator());

        }
        return false;
    }

    @Override
    public void onAnswerEditTextChange(Editable editable) {
        mAnswerDescription = editable;
        mView.updateWordCount(editable.length());
    }

    @Override
    public void onCloseCaseClicked() {
        mSelectedStatus = Question.Status.CLOSED;
        if (mQuestionToAnswer.status().equalsIgnoreCase(Question.Status.PENDING_ANSWER)) {
            showPayScreen();
        } else {
            onSubmitComposition();
        }
    }

    @Override
    public void onOpenForDetailsClicked() {
        mSelectedStatus = Question.Status.OPEN_FOR_MORE_DETAILS;
        if (mQuestionToAnswer.status().equalsIgnoreCase(Question.Status.PENDING_ANSWER)) {
            showPayScreen();
        } else {
            onSubmitComposition();
        }
    }

    @Override
    public void onOpenForFeedbackClicked() {
        mSelectedStatus = Question.Status.OPEN_FOR_FEEDBACK;
        if (mQuestionToAnswer.status().equalsIgnoreCase(Question.Status.PENDING_ANSWER)) {
            showPayScreen();
        } else {
            onSubmitComposition();
        }
    }

    @Override
    public void showPayScreen() {
        mView.setCoinsToCollect(mQuestionToAnswer.serviceFee());
        mContainerView.setToolbarTitle(mFragment.getString(R.string.new_case));
        mContainerView.setToolbarSubTitle(mFragment.getString(R.string.get_paid_for_your_answer));
        mContainerView.setRightToolbarButtonImageResource(-1);
        mView.setSecondaryInstructionText(mFragment.getString(R.string.label_compose_lawyer_answer_sub_description_4));
        currentPage = PAY;
        mView.changeViewPagerPage(PAY);
    }

    @Override
    public void onSubmitComposition() {
        Maybe<String> audioFileUpload = Maybe.just("");
        if (mRecordedAudioFile != null) {
            audioFileUpload = mFileFramework.uploadAnswerAttachment(Uri.fromFile(mRecordedAudioFile))
                    .compose(mFragment.bindUntilEvent(FragmentEvent.DESTROY_VIEW));
        }

        Maybe<List<String>> attachmentFileUpload = Maybe.just(new ArrayList<>());
        if (mSelectedFilesPaths != null) {
            attachmentFileUpload = Flowable.fromIterable(mSelectedFilesPaths)
                    .flatMapMaybe(filePath -> mFileFramework.uploadAnswerAttachment(Uri.fromFile(new File(filePath))))
                    .toList()
                    .toMaybe()
                    .compose(mFragment.bindUntilEvent(FragmentEvent.DESTROY_VIEW));
        }

//        Maybe<List<String>> attachmentFileUpload = Maybe.just(new ArrayList<>());
//        if (mSelectedLawyerFiles != null) {
//            attachmentFileUpload = Flowable.fromIterable(mSelectedLawyerFiles)
//                    .map(LawyerFile::downloadUrl)
//                    .toList()
//                    .toMaybe()
//                    .compose(mFragment.bindUntilEvent(FragmentEvent.DESTROY_VIEW));
//        }

        Maybe<String> answerDescription = Maybe.just("");
        if (CommonUtils.isNotEmpty(mAnswerDescription)) {
            answerDescription = Maybe.just(mAnswerDescription.toString());
        }

        addAnswer(audioFileUpload, attachmentFileUpload, answerDescription);
    }

    @Override
    public void addAnswer(Maybe<String> audioFileUpload, Maybe<List<String>> attachmentFileUpload, Maybe<String> questionDescription) {
        Maybe.zip(audioFileUpload, attachmentFileUpload, questionDescription,
                (recordedAudioUrl, attachmentUrls, description) -> Answer.builder()
                        .audioRecordingUrl(recordedAudioUrl)
                        .fileAttachments(attachmentUrls)
                        .questionDescription(description)
                        .answerFor(mQuestionToAnswer.status())
                        .build())
                .doOnSubscribe(disposable -> mContainerView.showLoadingIndicator())
                .flatMapCompletable(answer -> mRTDataFramework
                        .updateQuestionStatus(mQuestionToAnswer, mSelectedStatus)
                        .andThen(mRTDataFramework
                                .addAnswer(mQuestionToAnswer, answer)
                                .compose(mFragment.bindUntilEvent(FragmentEvent.DESTROY))))
                .doFinally(mContainerView::hideLoadingIndicator)
                .subscribe(
                        () -> {
                            if (mQuestionToAnswer.status().equalsIgnoreCase(Question.Status.PENDING_ANSWER)) {
                                mView.showSuccessDialog(
                                        mFragment.getString(R.string.success_lawyer_answer, mQuestionToAnswer.serviceFee()),
                                        () -> mContainerViewModel
                                                .newTop(QuestionListKey.create())
                                                .subscribe(mContainerViewModel.navigationObserver()));
                            } else {
                                mContainerViewModel
                                        .newTop(QuestionListKey.create())
                                        .subscribe(mContainerViewModel.navigationObserver());
                            }
                        },
                        mContainerViewModel.catchErrorThrowable());
    }

    @Override
    public void onStart() {
        mContainerView.setLeftToolbarTextResource(R.string.back);
        mContainerView.setRightToolbarTextResource(R.string.next);
//        mContainerView.ShowRightToolbarButton();
    }

    @Override
    public void onStop() {
        mContainerView.hideLeftText();
        mContainerView.hideRightText();
        mContainerView.hideRightText_();
        mContainerView.hideRightToolbarButton();
    }

    @Override
    public void onDetach() {

    }

    @Override
    public void onDestroy() {
        if (mVisualizerHandler != null && mVisualizerHandler != null) {
            mVisualizerHandler.removeCallbacks(mRecordProgressRunnable);
        }
        if (mRecordProgressHandler != null && mRecordProgressRunnable != null) {
            mRecordProgressHandler.removeCallbacks(mRecordProgressRunnable);
        }
        if (mRxAudioPlayer != null) {
            mRxAudioPlayer.stopPlay();
        }

        mContainerView.hideLeftText();
        mContainerView.hideRightText();
        mContainerView.hideRightText_();
        mContainerView.hideRightToolbarButton();

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_ATTACH:
                if (resultCode == Activity.RESULT_OK && data != null) {
//                    mSelectedFilesPaths.clear();
                    if (data.getStringArrayListExtra(FilePickerConst.KEY_SELECTED_DOCS) != null) {
                        mSelectedFilesPaths.addAll(data.getStringArrayListExtra(FilePickerConst.KEY_SELECTED_DOCS));
                        mView.updateSelectedFiles(mSelectedFilesPaths.iterator());
                    }
                    if (data.getStringArrayListExtra(FilePickerConst.KEY_SELECTED_MEDIA) != null) {
                        mSelectedFilesPaths.addAll(data.getStringArrayListExtra(FilePickerConst.KEY_SELECTED_MEDIA));
                        mView.updateSelectedFiles(mSelectedFilesPaths.iterator());
                    }
                }
                break;
        }

        if (ImagePicker.shouldHandle(requestCode, resultCode, data)) {
            List<Image> images = ImagePicker.getImages(data);
            for (int i = 0; i< images.size(); i++) {
                mSelectedFilesPaths.add(images.get(i).getPath());
            }
            mView.updateSelectedFiles(mSelectedFilesPaths.iterator());
        }
    }

    private ArraySet<String> mSelectedFilesPaths = new ArraySet<>();


}
