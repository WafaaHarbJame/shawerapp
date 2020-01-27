package com.shawerapp.android.screens.composer;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AlertDialog;
import androidx.collection.ArraySet;

import com.esafirm.imagepicker.features.ImagePicker;
import com.esafirm.imagepicker.model.Image;
import com.github.piasy.rxandroidaudio.AudioRecorder;
import com.github.piasy.rxandroidaudio.PlayConfig;
import com.github.piasy.rxandroidaudio.RxAudioPlayer;
import com.shawerapp.android.R;
import com.shawerapp.android.autovalue.Answer;
import com.shawerapp.android.autovalue.CommercialUser;
import com.shawerapp.android.autovalue.Field;
import com.shawerapp.android.autovalue.IndividualUser;
import com.shawerapp.android.autovalue.Invoice;
import com.shawerapp.android.autovalue.Invoice_;
import com.shawerapp.android.autovalue.LawyerUser;
import com.shawerapp.android.autovalue.Question;
import com.shawerapp.android.autovalue.SubSubject;
import com.shawerapp.android.backend.base.FileFramework;
import com.shawerapp.android.backend.base.RealTimeDataFramework;
import com.shawerapp.android.base.BaseFragment;
import com.shawerapp.android.screens.answerlist.AnswerListKey;
import com.shawerapp.android.screens.container.ContainerContract;
import com.shawerapp.android.screens.invoice.InvoiceKey;
import com.shawerapp.android.screens.payment.PaymentContract;
import com.shawerapp.android.screens.payment.PaymentFragment;
import com.shawerapp.android.screens.payment.PaymentKey;
import com.shawerapp.android.screens.purchase.PurchaseCoinsKey;
import com.shawerapp.android.screens.requestlist.RequestListKey;
import com.shawerapp.android.utils.CommonUtils;
import com.shawerapp.android.utils.LocaleHelper;
import com.shawerapp.android.utils.LoginUtil;
import com.tbruyelle.rxpermissions2.RxPermissions;
import com.trello.rxlifecycle2.android.FragmentEvent;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import javax.inject.Inject;

import droidninja.filepicker.FilePickerBuilder;
import droidninja.filepicker.FilePickerConst;
import io.reactivex.Flowable;
import io.reactivex.Maybe;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.internal.functions.Functions;
import io.reactivex.schedulers.Schedulers;
import solid.functions.Action1;
import solid.stream.Stream;
import timber.log.Timber;

import static com.shawerapp.android.screens.composer.ComposerFragment.ARG_QUESTION_TO_RESPOND_TO;
import static com.shawerapp.android.screens.composer.ComposerFragment.ARG_REQUEST_TYPE;
import static com.shawerapp.android.screens.composer.ComposerFragment.ARG_SELECTED_FIELD;
import static com.shawerapp.android.screens.composer.ComposerFragment.ARG_SELECTED_LAWYER;
import static com.shawerapp.android.screens.composer.ComposerFragment.ARG_SELECTED_SUBSUBJECT;
import static com.shawerapp.android.screens.composer.ComposerFragment.REQUEST_ATTACH;
import static com.shawerapp.android.screens.composer.ComposerKey.DETAILS;
import static com.shawerapp.android.screens.composer.ComposerKey.FEEDBACK;
import static com.shawerapp.android.screens.composer.ComposerKey.PRACTICE;
import static com.shawerapp.android.screens.composer.ComposerKey.QUESTION;

public final class ComposerViewModel implements ComposerContract.ViewModel, Serializable {

    static Boolean paid = false;

    String transAction = "";

    PaymentContract.View mmView;

    PaymentFragment paymentFragment;
    PaymentContract.ViewModel paymentViewModel;

    private static final int RC_CAMERA = 3000;

    int recordTime = 900;

    private static final int ATTACHMENT_CAMERA = 0;

    private static final int ATTACHMENT_PICTURE_GALLERY = 1;

    private static final int ATTACHMENT_DOCUMMENT = 2;

    private static final int COMPOSE = 0;

    private static final int REVIEW = 1;

    private static final int PAY = 2;

    private BaseFragment mFragment;

    private ComposerContract.View mView;

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

    private int mRequestType;

    private Field mSelectedField;

    private SubSubject mSelectedSubSubject;

    private LawyerUser mSelectedLawyer;

    private AudioRecorder mAudioRecorder;

    private RxPermissions mPermissions;

    private Disposable mRecordDisposable;

    private File mRecordedAudioFile;

    private RxAudioPlayer mRxAudioPlayer;

    private ArraySet<String> mSelectedFilesPaths = new ArraySet<>();

    private CharSequence mComposition;

    private boolean mIsRecordPlaying;

    private boolean mIsRecording;

    private long mPracticeRequestCost;

    private Question mQuestionToRespondTo;

    private String mSelectedStatus;

    private Handler mReviewProgressHandler;

    private Runnable mReviewProgressRunnable;

    private Handler mRecordProgressHandler;

    private Runnable mRecordProgressRunnable;

    private Queue<File> mAudioFiles = new LinkedList<>();

    private Handler mVisualizerHandler;

    private Runnable mVisualizerRunnable;

    @Inject
    public ComposerViewModel(BaseFragment fragment, ComposerContract.View view) {
        mFragment = fragment;
        mView = view;

        Bundle args = fragment.getArguments();
        mRequestType = args.getInt(ARG_REQUEST_TYPE);
        mSelectedField = args.getParcelable(ARG_SELECTED_FIELD);
        mSelectedSubSubject = args.getParcelable(ARG_SELECTED_SUBSUBJECT);
        mSelectedLawyer = args.getParcelable(ARG_SELECTED_LAWYER);
        mQuestionToRespondTo = args.getParcelable(ARG_QUESTION_TO_RESPOND_TO);

        mPermissions = new RxPermissions(mFragment.getActivity());
        mAudioRecorder = AudioRecorder.getInstance();
        mAudioRecorder.setOnErrorListener(error -> {
            Timber.e("AudioRecorder Error Code: %d", error);
        });
        mRxAudioPlayer = RxAudioPlayer.getInstance();
//        mContainerView.ShowRightToolbarButton();
    }

    @Override
    public void onViewCreated() {
        try {
            checkAudioPermissions(() -> mView.initBindings());
        } catch (Exception e) {
            Timber.e(CommonUtils.getExceptionString(e));
            mContainerView.showMessage(e.getMessage());
        }

        if (mRequestType == QUESTION) {
            mView.setPrimaryInstruction(R.string.label_compose_question_primary_instruction);
        } else if (mRequestType == PRACTICE) {
            mView.setPrimaryInstruction(R.string.label_compose_request_primary_instruction);

            mRTDataFramework.retrievePracticeRequestCost()
                    .compose(mFragment.bindUntilEvent(FragmentEvent.DESTROY_VIEW))
                    .doOnSubscribe(disposable -> mContainerView.showLoadingIndicator())
                    .doFinally(() -> mContainerView.hideLoadingIndicator())
                    .subscribe(practiceRequestCost -> {

//                        mPracticeRequestCost = practiceRequestCost;
                        mPracticeRequestCost = 20;
                    });
        } else if (mRequestType == DETAILS || mRequestType == FEEDBACK) {
            mView.setPrimaryInstruction(R.string.label_compose_detail_feedback_primary_instruction);
        }
    }

    @Override
    public void onAfterEnterAnimation() {

    }

    @Override
    public void onBackButtonClicked() {
        String composeTitle = "";
        String confirmTitle = "";
        String composeText = "";
        String confirmText = "";

        if (mRequestType == QUESTION) {
            if (LocaleHelper.getLanguage(mFragment.getContext()).equalsIgnoreCase(LocaleHelper.ARABIC)) {
                composeTitle = mSelectedSubSubject.ar_subSubjectName();
            } else {
                composeTitle = mSelectedSubSubject.subSubjectName();
            }
            confirmTitle = mFragment.getString(R.string.new_case);
            composeText = mSelectedLawyer.fullName();
            confirmText = mFragment.getString(R.string.confirm_your_question);
        } else if (mRequestType == PRACTICE) {
            if (LocaleHelper.getLanguage(mFragment.getContext()).equalsIgnoreCase(LocaleHelper.ARABIC)) {
                composeTitle = mSelectedSubSubject.ar_subSubjectName();
            } else {
                composeTitle = mSelectedSubSubject.subSubjectName();
            }
            confirmTitle = mFragment.getString(R.string.new_practice_request).toUpperCase();
            composeText = mFragment.getString(R.string.new_practice_request);
            confirmText = mFragment.getString(R.string.confirm_your_practice_request);
        } else if (mRequestType == DETAILS || mRequestType == FEEDBACK) {
            if (LocaleHelper.getLanguage(mFragment.getContext()).equalsIgnoreCase(LocaleHelper.ARABIC)) {
                composeTitle = mQuestionToRespondTo.ar_subSubjectName();
            } else {
                composeTitle = mQuestionToRespondTo.subSubjectName();
            }
            confirmTitle = mFragment.getString(R.string.open_case);
            composeText = mQuestionToRespondTo.assignedLawyerName();
            confirmText = mFragment.getString(R.string.confirm_your_respond);
        }

        if (currentPage == COMPOSE) {
            mContainerViewModel.goBack().subscribe(mContainerViewModel.navigationObserver());
        } else if (currentPage == REVIEW) {
            mView.setSecondaryInstructionText(mFragment.getString(R.string.label_compose_question_sub_description_1));
            mContainerView.setToolbarTitle(composeTitle);
            mContainerView.setToolbarSubTitle(composeText);
            mContainerView.setRightToolbarButtonImageResource(R.drawable.ic_icon_navigation_right);
            currentPage = COMPOSE;
            mView.changeViewPagerPage(COMPOSE);
        } else if (currentPage == PAY) {
            mContainerView.setToolbarTitle(confirmTitle);
            mContainerView.setToolbarSubTitle(confirmText);
            mContainerView.setRightToolbarButtonImageResource(R.drawable.ic_icon_navigation_right);
            currentPage = REVIEW;
            mView.changeViewPagerPage(REVIEW);
        }
    }

    @Override
    public void setupToolbar() {
        if (mRequestType == QUESTION) {
            if (LocaleHelper.getLanguage(mFragment.getContext()).equalsIgnoreCase(LocaleHelper.ARABIC)) {
                mContainerView.setToolbarTitle(mSelectedSubSubject.ar_subSubjectName());
            } else {
                mContainerView.setToolbarTitle(mSelectedSubSubject.subSubjectName());
            }
            mContainerView.setToolbarSubTitle(mSelectedLawyer.fullName());
        } else if (mRequestType == PRACTICE) {
            if (LocaleHelper.getLanguage(mFragment.getContext()).equalsIgnoreCase(LocaleHelper.ARABIC)) {
                mContainerView.setToolbarTitle(mSelectedSubSubject.ar_subSubjectName());
            } else {
                mContainerView.setToolbarTitle(mSelectedSubSubject.subSubjectName());
            }
            mContainerView.setToolbarSubTitle(mFragment.getString(R.string.new_practice_request));
        } else if (mRequestType == DETAILS || mRequestType == FEEDBACK) {
            if (LocaleHelper.getLanguage(mFragment.getContext()).equalsIgnoreCase(LocaleHelper.ARABIC)) {
                mContainerView.setToolbarTitle(mQuestionToRespondTo.ar_subSubjectName());
            } else {
                mContainerView.setToolbarTitle(mQuestionToRespondTo.subSubjectName());
            }
        }
    }

    @Override
    public void onLeftToolbarButtonClicked() {
        onBackButtonClicked();
    }

    @Override
    public void onRightToolbarButtonClicked() {
        String confirmTitle = "";
        String confirmText = "";
        String paymentTitle = "";
        String paymentText = "";

        if (mRequestType == QUESTION) {
            confirmText = mFragment.getString(R.string.confirm_your_question);
            paymentText = mFragment.getString(R.string.pay_for_your_question);
            confirmTitle = mFragment.getString(R.string.new_case);
            paymentTitle = mFragment.getString(R.string.new_case);
        } else if (mRequestType == PRACTICE) {
            confirmText = mFragment.getString(R.string.confirm_your_practice_request);
            paymentText = mFragment.getString(R.string.pay_for_your_practice_request);
            confirmTitle = mFragment.getString(R.string.new_practice_request);
            paymentTitle = mFragment.getString(R.string.new_practice_request);
        } else if (mRequestType == DETAILS || mRequestType == FEEDBACK) {
            confirmText = mFragment.getString(R.string.confirm_your_respond);
            paymentText = mFragment.getString(R.string.pay_your_respond);
            confirmTitle = mFragment.getString(R.string.open_case);
            paymentTitle = mFragment.getString(R.string.open_case);
        }

        if (currentPage == COMPOSE) {
            if (CommonUtils.isNotEmpty(mComposition) || (mRecordedAudioFile != null)) {
                currentPage = REVIEW;
                mView.changeViewPagerPage(REVIEW);
                mView.setSecondaryInstructionText(mFragment.getString(R.string.label_compose_question_sub_description_2));
                mContainerView.setToolbarTitle(confirmTitle);
                mContainerView.setToolbarSubTitle(confirmText);
                if (mIsRecording){
                    mAudioRecorder.stopRecord();
                }
            } else {
                mContainerView.showMessage(mFragment.getString(R.string.invalid_input), mFragment.getString(R.string.error_compose), true);
            }
        } else if (currentPage == REVIEW) {
            if (mRequestType == QUESTION) {
                if (mLoginUtil.getUserRole().equals(IndividualUser.ROLE_VALUE)) {
                    mView.setCoinsRequired(mSelectedLawyer.individualFees().get(mSelectedSubSubject.uid()));
                } else if (mLoginUtil.getUserRole().equals(CommercialUser.ROLE_VALUE)) {
                    mView.setCoinsRequired(mSelectedLawyer.commercialFees().get(mSelectedSubSubject.uid()));
                }
            } else if (mRequestType == PRACTICE) {
                mView.setCoinsRequired(mPracticeRequestCost);
                mView.setLabelVisibility(View.VISIBLE);

            } else if (mRequestType == DETAILS || mRequestType == FEEDBACK) {
                mView.setCoinsRequired(0L);
            }
            mView.setSecondaryInstructionText(mFragment.getString(R.string.label_compose_question_sub_description_3));
            mContainerView.setToolbarTitle(paymentTitle);
            mContainerView.setToolbarSubTitle(paymentText);
            mContainerView.setRightToolbarButtonImageResource(-1);
            currentPage = PAY;
            mView.changeViewPagerPage(PAY);
        }

        CommonUtils.hideKeyBoard(mFragment.getActivity());
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

        ContextWrapper cw = new ContextWrapper(mFragment.getContext());

        AudioManager m = (AudioManager) cw.getSystemService(Context.AUDIO_SERVICE);
        String pRate = m.getProperty(AudioManager.PROPERTY_OUTPUT_SAMPLE_RATE);
        int nativeSampleRate = Integer.parseInt(pRate);

        int bufferSize = AudioRecord.getMinBufferSize(
                nativeSampleRate,
                AudioFormat.CHANNEL_IN_MONO,
                AudioFormat.ENCODING_PCM_16BIT
        );

        mRecordDisposable = Observable
                .fromCallable(() -> {
                    File mediaDir = cw.getFilesDir();

                    String fileName = mediaDir.getAbsolutePath()
                            + File.separator + System.nanoTime() + ".mp4";

                    mRecordedAudioFile = new File(fileName);


                    Timber.e("to prepare record");

                    return mAudioRecorder.prepareRecord(
                            MediaRecorder.AudioSource.MIC,
                            MediaRecorder.OutputFormat.MPEG_4,
                            MediaRecorder.AudioEncoder.AAC,
                            nativeSampleRate,
                            bufferSize,
                            mRecordedAudioFile);
                })
                .flatMap(b -> {
                    Timber.e("prepareRecord success");
                    Timber.e("to play audio_record_ready: " + R.raw.audio_record_ready);
                    return mRxAudioPlayer.play(
                            PlayConfig.res(mFragment.getContext().getApplicationContext(), R.raw.audio_record_ready)
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
                                mVisualizerHandler.postDelayed(this, 40);
                            }
                        }
                    };
                    mVisualizerHandler.post(mVisualizerRunnable);

                    mRecordProgressHandler = new Handler();
                    mRecordProgressRunnable = () -> {
                        if (mAudioRecorder != null) {
                            Timber.e("progress %d", mAudioRecorder.progress());
                            if (mLoginUtil.getUserRole().equals(IndividualUser.ROLE_VALUE)) {
                                if (mAudioRecorder.progress() >= (15*60)) {
                                    mAudioRecorder.stopRecord();
                                }
                                recordTime -= 1;
                                mView.updateRecordProgress(RecordSecToTime(recordTime));
                            } else {
                                mView.updateRecordProgress(secToTime(mAudioRecorder.progress()));
                            }
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

    public String RecordSecToTime(int sec) {
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
    public void onPlayButtonClicked(boolean isConfirm) {
        try {
            checkAudioPermissions(() -> {
                if (!mIsRecording && !mIsRecordPlaying) {
                    mIsRecordPlaying = true;
                    startPlaying(isConfirm);
                }
            });
        } catch (Exception e) {
            Timber.e(CommonUtils.getExceptionString(e));
            mContainerView.showMessage(e.getMessage());
        }
    }

    @Override
    public void startPlaying(boolean isConfirm) {
        File recordedAudioFile = new File(mRecordedAudioFile.getAbsolutePath());
        if (recordedAudioFile != null) {
            mRxAudioPlayer
                    .play(PlayConfig
                            .file(recordedAudioFile)
                            .streamType(AudioManager.STREAM_MUSIC)
                            .build())
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .doOnError(throwable -> mRxAudioPlayer.getMediaPlayer().release())
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
        } else {
            mContainerView.showMessage("Audio file error", true);
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

    @Override
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

    @Override
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
    public void openFilePicker() {
        List<String> selectedFiles = new ArrayList<>();
        Stream.stream(mSelectedFilesPaths.toArray())
                .forEach((Action1<Object>) value -> selectedFiles.add((String) value));

        FilePickerBuilder.Companion.getInstance().setMaxCount(5)
                .setSelectedFiles(Collections.list(Collections.enumeration(mSelectedFilesPaths)))
                .setActivityTheme(R.style.AppTheme)
                .pickFile(mFragment, ComposerFragment.REQUEST_ATTACH);
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

    @Override
    public void onCompositionEditTextChange(Editable editable) {
        mComposition = editable;
        mView.updateWordCount(editable.length());
    }

    @SuppressLint("CheckResult")
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

        Maybe<String> composition = Maybe.just("");
        if (CommonUtils.isNotEmpty(mComposition)) {
            composition = Maybe.just(mComposition.toString());
        }

        mContainerViewModel.hideRightToolbarButton();

        final List<String>[] attachmentsFiles = new List[]{new ArrayList<>()};

        attachmentFileUpload.map(authResult -> attachmentsFiles[0].addAll(authResult));

        if (mRequestType == QUESTION) {
            if (paid) {
                addNewQuestion(audioFileUpload, attachmentFileUpload, composition, mmView, paymentFragment);
                this.setPaidStatus(false);
            } else {
                mContainerViewModel
                        .goTo(PaymentKey.builder()
                                .requestType(mRequestType)
                                .selectedField(mSelectedField)
                                .selectedSubSubject(mSelectedSubSubject)
                                .selectedLawyerUser(mSelectedLawyer)
                                .questionDescription(String.valueOf(composition))
                                .attachmentFileUpload(attachmentsFiles[0])
                                .audioFileUpload(String.valueOf(audioFileUpload))
                                .mRecordedAudioFile(mRecordedAudioFile)
                                .mComposition(mComposition)
                                .mComposerViewModel(this)
                                .build())
                        .subscribe(mContainerViewModel.navigationObserver());
            }
        } else if (mRequestType == PRACTICE) {
            if (paid) {
                addNewPracticeRequest(audioFileUpload, attachmentFileUpload, composition, mmView, paymentFragment);
                this.setPaidStatus(false);
            } else {
                mContainerViewModel
                        .goTo(PaymentKey.builder()
                                .requestType(mRequestType)
                                .selectedField(mSelectedField)
                                .selectedSubSubject(mSelectedSubSubject)
                                .selectedLawyerUser(mSelectedLawyer)
                                .questionDescription(String.valueOf(composition))
                                .attachmentFileUpload(attachmentsFiles[0])
                                .audioFileUpload(String.valueOf(audioFileUpload))
                                .mRecordedAudioFile(mRecordedAudioFile)
                                .mComposition(mComposition)
                                .mComposerViewModel(this)
                                .build())
                        .subscribe(mContainerViewModel.navigationObserver());
            }
//            addNewPracticeRequest(audioFileUpload, attachmentFileUpload, composition);
        } else if (mRequestType == DETAILS) {
            mSelectedStatus = Question.Status.HAS_MORE_DETAILS;
            addAnswer(audioFileUpload, attachmentFileUpload, composition);
        } else if (mRequestType == FEEDBACK) {
            mSelectedStatus = Question.Status.HAS_FEEDBACK;
            addAnswer(audioFileUpload, attachmentFileUpload, composition);
        }
    }

    @SuppressLint("CheckResult")
    @Override
    public void addNewQuestion(Maybe<String> audioFileUpload, Maybe<List<String>> attachmentFileUpload, Maybe<String> questionDescription) {
        long questionServiceFee;
        if (mLoginUtil.getUserRole().equals(IndividualUser.ROLE_VALUE)) {
            questionServiceFee = mSelectedLawyer.individualFees().get(mSelectedSubSubject.uid());
        } else if (mLoginUtil.getUserRole().equals(CommercialUser.ROLE_VALUE)) {
            questionServiceFee = mSelectedLawyer.commercialFees().get(mSelectedSubSubject.uid());
        } else {
            questionServiceFee = 0L;
        }

        mRTDataFramework
                .retrieveUserCoins()
                .doOnSubscribe(disposable -> mContainerView.showLoadingIndicator())
                .flatMap(Maybe::just)
                .flatMap(currentCoins -> Maybe.zip(audioFileUpload, attachmentFileUpload, questionDescription,
                        (recordedAudioUrl, attachmentUrls, description) -> Answer.builder()
                                .audioRecordingUrl(recordedAudioUrl)
                                .fileAttachments(attachmentUrls)
                                .questionDescription(description)
                                .build()))
                .flatMapCompletable(answer -> mRTDataFramework
                        .addQuestion(
                                mSelectedField,
                                mSelectedSubSubject,
                                mSelectedLawyer,
                                answer,
                                "" + System.currentTimeMillis())
                        .compose(mFragment.bindUntilEvent(FragmentEvent.DESTROY)))
                .doFinally(mContainerView::hideLoadingIndicator)
                .subscribe(
                        () -> mView.showSuccessDialog(
                                mFragment.getString(R.string.success_new_question, questionServiceFee),
                                () -> mContainerViewModel
                                        .newTop(AnswerListKey.create())
                                        .subscribe(mContainerViewModel.navigationObserver())),
                        throwable -> {
                            if (throwable.getMessage().equalsIgnoreCase(mFragment.getString(R.string.error_not_enough_coins))) {
                                showNotEnoughCoinsPopup();
                            } else {
                                mContainerViewModel.catchErrorThrowable().accept(throwable);
                            }
                        });
    }

    @SuppressLint("CheckResult")
    public void addInvoice(String type, PaymentFragment paymentFragment, PaymentContract.View mmView) {

        long questionServiceFee;
        if (mLoginUtil.getUserRole().equals(IndividualUser.ROLE_VALUE)) {
            questionServiceFee = mSelectedLawyer.individualFees().get(mSelectedSubSubject.uid());
        } else if (mLoginUtil.getUserRole().equals(CommercialUser.ROLE_VALUE)) {
            questionServiceFee = mSelectedLawyer.commercialFees().get(mSelectedSubSubject.uid());
        } else {
            questionServiceFee = 0L;
        }

        Invoice invoice_ = Invoice.builder()
                .UserUid(mLoginUtil.getUserID())
                .collection("invoices")
                .LawyerUid(mSelectedLawyer.uid())
                .orderDate(new Date())
                .orderRequestNumber(transAction)
                .orderSubTotal(String.valueOf(questionServiceFee))
                .orderType(type)
                .orderTypePrice(String.valueOf(questionServiceFee))
                .orderVat("0.0%")
                .orderVatPrice("0")
                .paid("true")
                .build();

        mmView.showSuccessDialog(
                paymentFragment.getString(R.string.Paymentprocessedsuccessfully),
                () -> mContainerViewModel
                        .newTop(InvoiceKey.builder()
                                .invoice(invoice_)
                                .build())
                        .doFinally(mContainerView::hideLoadingIndicator)
                        .subscribe(mContainerViewModel.navigationObserver()));


        mRTDataFramework.addInvoice(invoice_)
                .compose(mFragment.bindUntilEvent(FragmentEvent.DESTROY_VIEW))
                .doFinally(mContainerView::hideLoadingIndicator)
                .subscribe();

        ;
    }

    public void add_Invoice(String type, PaymentFragment paymentFragment, PaymentContract.View mmView) {
        long questionServiceFee = 20L;

        Invoice invoice_ = Invoice.builder()
                .UserUid(mLoginUtil.getUserID())
                .collection("invoices")
                .orderDate(new Date())
                .orderRequestNumber(transAction)
                .orderSubTotal(String.valueOf(questionServiceFee))
                .orderType(type)
                .orderTypePrice(String.valueOf(questionServiceFee))
                .orderVat("0.0%")
                .orderVatPrice("0")
                .paid("true")
                .build();

        mmView.showSuccessDialog(
                paymentFragment.getString(R.string.Paymentprocessedsuccessfully),
                () -> mContainerViewModel
                        .newTop(InvoiceKey.builder()
                                .invoice(invoice_)
                                .build())
                        .subscribe(mContainerViewModel.navigationObserver()));

        mRTDataFramework.addInvoice(invoice_)
                .compose(mFragment.bindUntilEvent(FragmentEvent.DESTROY_VIEW))
                .doFinally(mContainerView::hideLoadingIndicator)
                .subscribe();

        ;
    }

    @SuppressLint("CheckResult")
    @Override
    public void addNewQuestion(Maybe<String> audioFileUpload, Maybe<List<String>> attachmentFileUpload, Maybe<String> questionDescription, PaymentContract.View mmView, PaymentFragment paymentFragment) {
        long questionServiceFee;
        if (mLoginUtil.getUserRole().equals(IndividualUser.ROLE_VALUE)) {
            questionServiceFee = mSelectedLawyer.individualFees().get(mSelectedSubSubject.uid());
        } else if (mLoginUtil.getUserRole().equals(CommercialUser.ROLE_VALUE)) {
            questionServiceFee = mSelectedLawyer.commercialFees().get(mSelectedSubSubject.uid());
        } else {
            questionServiceFee = 0L;
        }

        mRTDataFramework
                .retrieveUserCoins()
                .doOnSubscribe(disposable -> mContainerView.showLoadingIndicator())
                .flatMap(Maybe::just)
                .flatMap(currentCoins -> Maybe.zip(audioFileUpload, attachmentFileUpload, questionDescription,
                        (recordedAudioUrl, attachmentUrls, description) -> Answer.builder()
                                .audioRecordingUrl(recordedAudioUrl)
                                .fileAttachments(attachmentUrls)
                                .questionDescription(description)
                                .build()))
                .flatMapCompletable(answer -> mRTDataFramework
                        .addQuestion(
                                mSelectedField,
                                mSelectedSubSubject,
                                mSelectedLawyer,
                                answer,
                                transAction)
                        .compose(paymentFragment.bindUntilEvent(FragmentEvent.DESTROY)))
                .doFinally(mContainerView::hideLoadingIndicator)
                .subscribe(
                        () -> addInvoice("Esteshara Fee", paymentFragment, mmView),
                        throwable -> {
                            if (throwable.getMessage().equalsIgnoreCase(paymentFragment.getString(R.string.error_not_enough_coins))) {
                                showNotEnoughCoinsPopup();
                            } else {
                                mContainerViewModel.catchErrorThrowable().accept(throwable);
                            }
                        }
                        );

    }

    @Override
    public void addNewPracticeRequest(Maybe<String> audioFileUpload, Maybe<List<String>> attachmentFileUpload, Maybe<String> questionDescription) {
        mRTDataFramework
                .retrieveUserCoins()
                .doOnSubscribe(disposable -> mContainerView.showLoadingIndicator())
                .flatMap(currentCoins -> {
//                    if (currentCoins < mPracticeRequestCost) {
//                        return Maybe.error(new Throwable(mFragment.getString(R.string.error_not_enough_coins)));
//                    } else {
//                        return Maybe.just(currentCoins);
//                    }
                    return Maybe.just(currentCoins);
                })
                .flatMap(currentCoins -> Maybe.zip(audioFileUpload, attachmentFileUpload, questionDescription,
                        (recordedAudioUrl, attachmentUrls, description) -> Answer.builder()
                                .audioRecordingUrl(recordedAudioUrl)
                                .fileAttachments(attachmentUrls)
                                .questionDescription(description)
                                .build()))
                .flatMapCompletable(answer -> mRTDataFramework
                        .addPracticeRequest(
                                mSelectedField,
                                mSelectedSubSubject,
                                answer.audioRecordingUrl(),
                                answer.fileAttachments(),
                                answer.questionDescription(),
                                mPracticeRequestCost)
                        .compose(mFragment.bindUntilEvent(FragmentEvent.DESTROY)))
                .doFinally(mContainerView::hideLoadingIndicator)
                .subscribe(
                        () -> mView.showSuccessDialog(
                                mFragment.getString(R.string.Paymentprocessedsuccessfully),
                                () -> mContainerViewModel
                                        .newTop(RequestListKey.create())
                                        .subscribe(mContainerViewModel.navigationObserver())),
                        throwable -> {
                            if (throwable.getMessage().equalsIgnoreCase(mFragment.getString(R.string.error_not_enough_coins))) {
                                showNotEnoughCoinsPopup();
                            } else {
                                mContainerViewModel.catchErrorThrowable().accept(throwable);
                            }
                        });
    }

    public void addNewPracticeRequest(Maybe<String> audioFileUpload, Maybe<List<String>> attachmentFileUpload, Maybe<String> questionDescription, PaymentContract.View mmView, PaymentFragment paymentFragment) {
        mRTDataFramework
                .retrieveUserCoins()
                .doOnSubscribe(disposable -> mContainerView.showLoadingIndicator())
                .flatMap(currentCoins -> {
//                    if (currentCoins < mPracticeRequestCost) {
//                        return Maybe.error(new Throwable(mFragment.getString(R.string.error_not_enough_coins)));
//                    } else {
//                        return Maybe.just(currentCoins);
//                    }
                    return Maybe.just(currentCoins);
                })
                .flatMap(currentCoins -> Maybe.zip(audioFileUpload, attachmentFileUpload, questionDescription,
                        (recordedAudioUrl, attachmentUrls, description) -> Answer.builder()
                                .audioRecordingUrl(recordedAudioUrl)
                                .fileAttachments(attachmentUrls)
                                .questionDescription(description)
                                .build()))
                .flatMapCompletable(answer -> mRTDataFramework
                        .addPracticeRequest(
                                mSelectedField,
                                mSelectedSubSubject,
                                answer.audioRecordingUrl(),
                                answer.fileAttachments(),
                                answer.questionDescription(),
                                mPracticeRequestCost)
                        .compose(paymentFragment.bindUntilEvent(FragmentEvent.DESTROY)))
                .doFinally(mContainerView::hideLoadingIndicator)
                .subscribe(
                        () -> add_Invoice("Coordinate fees with lawyer office", paymentFragment, mmView),
                        throwable -> {
                            if (throwable.getMessage().equalsIgnoreCase(paymentFragment.getString(R.string.error_not_enough_coins))) {
                                showNotEnoughCoinsPopup();
                            } else {
                                mContainerViewModel.catchErrorThrowable().accept(throwable);
                            }
                        });
    }

    @Override
    public void addAnswer(Maybe<String> audioFileUpload, Maybe<List<String>> attachmentFileUpload, Maybe<String> questionDescription) {
        Maybe.zip(audioFileUpload, attachmentFileUpload, questionDescription,
                (recordedAudioUrl, attachmentUrls, description) -> Answer.builder()
                        .audioRecordingUrl(recordedAudioUrl)
                        .fileAttachments(attachmentUrls)
                        .questionDescription(description)
                        .answerFor(mQuestionToRespondTo.status())
                        .build())
                .doOnSubscribe(disposable -> mContainerView.showLoadingIndicator())
                .flatMapCompletable(answer -> mRTDataFramework
                        .updateQuestionStatus(mQuestionToRespondTo, mSelectedStatus)
                        .andThen(mRTDataFramework
                                .addAnswer(mQuestionToRespondTo, answer)
                                .compose(mFragment.bindUntilEvent(FragmentEvent.DESTROY))))
                .doFinally(mContainerView::hideLoadingIndicator)
                .subscribe(
                        () -> mView.showSuccessDialog(
                                mFragment.getString(R.string.success_respond),
                                () -> mContainerViewModel
                                        .newTop(AnswerListKey.create())
                                        .subscribe(mContainerViewModel.navigationObserver())),
                        mContainerViewModel.catchErrorThrowable());
    }

    @Override
    public void showNotEnoughCoinsPopup() {
        AlertDialog.Builder builder = new AlertDialog.Builder(mFragment.getContext(), R.style.DialogTheme);
        builder.setMessage(mFragment.getString(R.string.message_not_enough_coins));
        builder.setPositiveButton(mFragment.getString(R.string.yes), (dialog, which) -> mContainerViewModel
                .goTo(PurchaseCoinsKey.create())
                .subscribe(mContainerViewModel.navigationObserver()));
        builder.setCancelable(true);
        builder.show();
    }

    @Override
    public Boolean getPaidStatus() {
        return paid;
    }

    @Override
    public void setPaidStatus(Boolean paid_, PaymentContract.View mmView_, PaymentFragment paymentFragment_, PaymentContract.ViewModel paymentViewModel_, String transAction_) {
        mmView = mmView_;
        paymentFragment = paymentFragment_;
        paymentViewModel = paymentViewModel_;
        paid = paid_;
        transAction = transAction_;
    }

    @Override
    public void setPaidStatus(Boolean paid_) {
        paid = paid_;
    }

    @Override
    public void onStart() {
        mContainerView.setLeftToolbarTextResource(R.string.back);
        mContainerView.setRightToolbarTextResource(R.string.next);
    }

    @Override
    public void onStop() {
        mContainerView.hideLeftText();
        mContainerView.hideRightText();
        mContainerView.hideRightToolbarButton();
    }

    @Override
    public void onDetach() {
        mContainerView.hideRightText();
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
    }
}
