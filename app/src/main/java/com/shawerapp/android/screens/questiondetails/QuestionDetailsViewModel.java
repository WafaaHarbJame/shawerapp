package com.shawerapp.android.screens.questiondetails;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Build;
import android.os.Handler;
import androidx.annotation.DrawableRes;
import androidx.annotation.IdRes;
import androidx.annotation.LayoutRes;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.core.widget.ContentLoadingProgressBar;
import android.text.SpannableStringBuilder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.github.piasy.rxandroidaudio.PlayConfig;
import com.github.piasy.rxandroidaudio.RxAudioPlayer;
import com.github.piasy.rxandroidaudio.StreamAudioPlayer;
import com.jakewharton.rxbinding2.view.RxView;
import com.shawerapp.android.R;
import com.shawerapp.android.autovalue.Answer;
import com.shawerapp.android.autovalue.AnswerEvent;
import com.shawerapp.android.autovalue.CommercialUser;
import com.shawerapp.android.autovalue.IndividualUser;
import com.shawerapp.android.autovalue.LawyerUser;
import com.shawerapp.android.autovalue.Question;
import com.shawerapp.android.backend.base.FileFramework;
import com.shawerapp.android.backend.base.RealTimeDataFramework;
import com.shawerapp.android.base.BaseFragment;
import com.shawerapp.android.custom.views.CustomLineBarVisualizer;
import com.shawerapp.android.screens.composer.ComposerKey;
import com.shawerapp.android.screens.container.ContainerContract;
import com.shawerapp.android.screens.newanswer.ComposeAnswerKey;
import com.shawerapp.android.utils.CommonUtils;
import com.shawerapp.android.utils.LocaleHelper;
import com.shawerapp.android.utils.LoginUtil;
import com.tbruyelle.rxpermissions2.RxPermissions;
import com.trello.rxlifecycle2.android.FragmentEvent;

import java.io.File;
import java.text.SimpleDateFormat;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.TimeZone;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.internal.functions.Functions;
import io.reactivex.processors.BehaviorProcessor;
import io.reactivex.schedulers.Schedulers;

import static com.shawerapp.android.screens.questiondetails.QuestionDetailsFragment.ARG_QUESTION;

public class QuestionDetailsViewModel implements QuestionDetailsContract.ViewModel {

    private BaseFragment mFragment;

    private QuestionDetailsContract.View mView;

    @Inject
    ContainerContract.View mContainerView;

    @Inject
    ContainerContract.ViewModel mContainerViewModel;

    @Inject
    RealTimeDataFramework mRTDataFramework;

    @Inject
    FileFramework mFileFramework;

    @Inject
    LoginUtil mLoginUtil;

    private Question mQuestion;

    private BehaviorProcessor<AnswerEvent> mAnswerEventProcessor = BehaviorProcessor.create();

    private RxAudioPlayer mRxAudioPlayer;

    private Answer mCurrentPlaying;

    private RxPermissions mPermissions;

    private Handler mPlaybackHandler;

    private Runnable mPlaybackRunnable;

    static final int BUFFER_SIZE = 2048;

    private byte[] mBuffer;

    private StreamAudioPlayer mStreamAudioPlayer;

    @Inject
    public QuestionDetailsViewModel(BaseFragment fragment, QuestionDetailsContract.View view) {
        mFragment = fragment;
        mView = view;

        mQuestion = fragment.getArguments().getParcelable(ARG_QUESTION);
        mPermissions = new RxPermissions(mFragment.getActivity());
        mRxAudioPlayer = RxAudioPlayer.getInstance();
        mStreamAudioPlayer = StreamAudioPlayer.getInstance();
        mStreamAudioPlayer.init();
    }

    @Override
    public void onViewCreated() {
        mView.initBindings();
        mContainerView.hideRightText_();
        mAnswerEventProcessor
                .serialize()
                .compose(mFragment.bindUntilEvent(FragmentEvent.DESTROY_VIEW))
                .subscribe(
                        answerEvent -> addAnswer(answerEvent.answer()),
                        mContainerViewModel.catchErrorThrowable());
    }

    @Override
    public void onAfterEnterAnimation() {
        if (mLoginUtil.getUserRole().equals(LawyerUser.ROLE_VALUE)) {
            mRTDataFramework
                    .retrieveAnswers(mQuestion)
                    .compose(mFragment.bindUntilEvent(FragmentEvent.DESTROY_VIEW))
                    .doOnSubscribe(disposable -> mContainerView.showLoadingIndicator())
                    .map(unsortedList -> {
                        List<AnswerEvent> sortedList = new ArrayList<>(unsortedList);
                        Collections.sort(sortedList, new Answer.Comparator());
                        return sortedList;
                    })
                    .flattenAsFlowable(answerEvents -> answerEvents)
                    .doOnNext(mAnswerEventProcessor::onNext)
                    .toList()
                    .flatMap(answerEvents -> mRTDataFramework
                            .fetchUser(mQuestion.askerUid())
                            .doOnSuccess(o -> {
                                if (o instanceof IndividualUser) {
                                    IndividualUser individualUser = (IndividualUser) o;
                                    mView.setUserImage(individualUser.imageUrl());
                                    mView.setUsername(individualUser.username());
                                    mView.setUserType(mFragment.getString(R.string.format_question_detail_user_type, mFragment.getString(R.string.individual).toUpperCase()));
                                } else if (o instanceof CommercialUser) {
                                    CommercialUser commercialUser = (CommercialUser) o;
                                    mView.setUserImage(commercialUser.imageUrl());
                                    mView.setUsername(commercialUser.username());
                                    mView.setUserType(mFragment.getString(R.string.format_question_detail_user_type, mFragment.getString(R.string.commercial).toUpperCase()));
                                }
                                if (LocaleHelper.getLanguage(mFragment.getContext()).equalsIgnoreCase(LocaleHelper.ARABIC)) {
                                    mView.setSubSubjectName(mQuestion.ar_subSubjectName());
                                } else {
                                    mView.setSubSubjectName(mQuestion.subSubjectName());
                                }
                                setUpStatus();
                            }))
                    .doFinally(() -> mContainerView.hideLoadingIndicator())
                    .subscribe(o -> mView.scrollToBottom());
        } else {
            mRTDataFramework
                    .retrieveAnswers(mQuestion)
                    .compose(mFragment.bindUntilEvent(FragmentEvent.DESTROY_VIEW))
                    .doOnSubscribe(disposable -> mContainerView.showLoadingIndicator())
                    .flattenAsFlowable(answerEvents -> answerEvents)
                    .doOnNext(mAnswerEventProcessor::onNext)
                    .toList()
                    .flatMap(answerEvents -> mRTDataFramework
                            .fetchUser(mQuestion.assignedLawyerUid())
                            .doOnSuccess(o -> {
                                if (o instanceof LawyerUser) {
                                    LawyerUser lawyerUser = (LawyerUser) o;
                                    mView.setUserImage(lawyerUser.imageUrl());
                                    mView.setUsername(CommonUtils.isNotEmpty(lawyerUser.fullName()) ? lawyerUser.fullName() : lawyerUser.username());
                                }
                                if (LocaleHelper.getLanguage(mFragment.getContext()).equalsIgnoreCase(LocaleHelper.ARABIC)) {
                                    mView.setSubSubjectName(mQuestion.ar_subSubjectName());
                                } else {
                                    mView.setSubSubjectName(mQuestion.subSubjectName());
                                }

                                setUpStatus();

                            }))
                    .doFinally(() -> mContainerView.hideLoadingIndicator())
                    .subscribe(o -> mView.scrollToBottom());
        }
    }

    @Override
    public void setUpStatus() {
        mContainerView.ShowRightToolbarButton();
        if (CommonUtils.isNotEmpty(mQuestion.status())) {
            SpannableStringBuilder builder = new SpannableStringBuilder("");
            if (mQuestion.status().equalsIgnoreCase(Question.Status.PENDING_ANSWER)) {
                if (mLoginUtil.getUserRole().equals(IndividualUser.ROLE_VALUE) || mLoginUtil.getUserRole().equals(CommercialUser.ROLE_VALUE)) {
                    builder.append(mFragment.getString(R.string.pending_message_client1, CommonUtils.isNotEmpty(mQuestion.assignedLawyerName()) ? mQuestion.assignedLawyerName() : mQuestion.assignedLawyerUsername()));
                    builder.append("\n");
                    builder.append(mFragment.getString(R.string.pending_message_client2));
                } else {
                    builder.append(mFragment.getString(R.string.pending_message_lawyer1));
                    builder.append("\n");
                    builder.append(mFragment.getString(R.string.pending_message_lawyer2));
                }
                mContainerView.setRightToolbarButtonImageResource(R.drawable.icon_plain_circle_light);
            } else if (mQuestion.status().equalsIgnoreCase(Question.Status.OPEN_FOR_FEEDBACK)) {
                if (mLoginUtil.getUserRole().equals(IndividualUser.ROLE_VALUE) || mLoginUtil.getUserRole().equals(CommercialUser.ROLE_VALUE)) {
                    builder.append(mFragment.getString(R.string.feedback_message_client1, CommonUtils.isNotEmpty(mQuestion.assignedLawyerName()) ? mQuestion.assignedLawyerName() : mQuestion.assignedLawyerUsername()));
                    builder.append("\n");
                    builder.append(mFragment.getString(R.string.feedback_message_client2));
                } else {
                    builder.append(mFragment.getString(R.string.feedback_message_lawyer1));
                    builder.append("\n");
                    builder.append(mFragment.getString(R.string.feedback_message_lawyer2));
                }
                mContainerView.setRightToolbarButtonImageResource(R.drawable.icon_feedback);
            } else if (mQuestion.status().equalsIgnoreCase(Question.Status.HAS_FEEDBACK)) {
                if (mLoginUtil.getUserRole().equals(IndividualUser.ROLE_VALUE) || mLoginUtil.getUserRole().equals(CommercialUser.ROLE_VALUE)) {
                    builder.append(mFragment.getString(R.string.has_feedback_message_client1, CommonUtils.isNotEmpty(mQuestion.assignedLawyerName()) ? mQuestion.assignedLawyerName() : mQuestion.assignedLawyerUsername()));
                    builder.append("\n");
                    builder.append(mFragment.getString(R.string.has_feedback_message_client2));
                } else {
                    builder.append(mFragment.getString(R.string.has_feedback_message_lawyer1));
                    builder.append("\n");
                    builder.append(mFragment.getString(R.string.has_feedback_message_lawyer2));
                }
                mContainerView.setRightToolbarButtonImageResource(R.drawable.icon_feedback);
            } else if (mQuestion.status().equalsIgnoreCase(Question.Status.OPEN_FOR_MORE_DETAILS)) {
                if (mLoginUtil.getUserRole().equals(IndividualUser.ROLE_VALUE) || mLoginUtil.getUserRole().equals(CommercialUser.ROLE_VALUE)) {
                    builder.append(mFragment.getString(R.string.details_message_client1, CommonUtils.isNotEmpty(mQuestion.assignedLawyerName()) ? mQuestion.assignedLawyerName() : mQuestion.assignedLawyerUsername()));
                    builder.append("\n");
                    builder.append(mFragment.getString(R.string.details_message_client2));
                } else {
                    builder.append(mFragment.getString(R.string.details_message_lawyer1));
                    builder.append("\n");
                    builder.append(mFragment.getString(R.string.details_message_lawyer2));
                }
                mContainerView.setRightToolbarButtonImageResource(R.drawable.icon_reply_with_details);
            } else if (mQuestion.status().equalsIgnoreCase(Question.Status.HAS_MORE_DETAILS)) {
                if (mLoginUtil.getUserRole().equals(IndividualUser.ROLE_VALUE) || mLoginUtil.getUserRole().equals(CommercialUser.ROLE_VALUE)) {
                    builder.append(mFragment.getString(R.string.has_details_message_client1, CommonUtils.isNotEmpty(mQuestion.assignedLawyerName()) ? mQuestion.assignedLawyerName() : mQuestion.assignedLawyerUsername()));
                    builder.append("\n");
                    builder.append(mFragment.getString(R.string.has_details_message_client2));
                } else {
                    builder.append(mFragment.getString(R.string.has_details_message_lawyer1));
                    builder.append("\n");
                    builder.append(mFragment.getString(R.string.has_details_message_lawyer2));
                }
                mContainerView.setRightToolbarButtonImageResource(R.drawable.icon_reply_with_details);
            } else if (mQuestion.status().equalsIgnoreCase(Question.Status.CLOSED)) {
                if (mLoginUtil.getUserRole().equals(IndividualUser.ROLE_VALUE) || mLoginUtil.getUserRole().equals(CommercialUser.ROLE_VALUE)) {
                    if (CommonUtils.isEmpty(mQuestion.answerFeedback())) {
                        builder.append(mFragment.getString(R.string.closed_message_client1, CommonUtils.isNotEmpty(mQuestion.assignedLawyerName()) ? mQuestion.assignedLawyerName() : mQuestion.assignedLawyerUsername()));
                        builder.append("\n");
                        builder.append(mFragment.getString(R.string.closed_message_client2));
                        mView.showRatingButtons();
                    } else {
                        builder.append(mFragment.getString(R.string.case_closed));
                        if (mQuestion.answerFeedback().equalsIgnoreCase(Question.Feedback.GOOD)) {
                            mView.showGoodRating();
                        } else {
                            mView.showBadRating();
                        }
                    }
                } else {
                    builder.append(mFragment.getString(R.string.case_closed));
                    builder.append("\n");
                    if (CommonUtils.isEmpty(mQuestion.answerFeedback())) {
                        builder.append(mFragment.getString(R.string.closed_message_lawyer_no_rating));
                    } else {
                        builder.append(mFragment.getString(R.string.closed_message_lawyer_has_rating, mQuestion.answerFeedback()));
                        if (mQuestion.answerFeedback().equalsIgnoreCase(Question.Feedback.GOOD)) {
                            mView.showGoodRating();
                        } else {
                            mView.showBadRating();
                        }
                    }
                }
                mContainerView.setRightToolbarButtonImageResource(R.drawable.icon_case_closed);
            }
            mView.setStatus(builder);
        }
    }

    @Override
    public void onBackButtonClicked() {
        mContainerViewModel.hideRightToolbarButton();
        mContainerView.hideRightToolbarButton();
        mContainerViewModel.goBack()
                .subscribe(mContainerViewModel.navigationObserver());
    }

    @Override
    public void setupToolbar() {
        mContainerView.clearToolbarSubtitle();
        mContainerView.clearToolbarTitle();
        mContainerView.setLeftToolbarButtonImageResource(R.drawable.icon_back);
        mContainerView.setRightToolbarButtonImageResource(-1);
    }

    @Override
    public void onLeftToolbarButtonClicked() {
        onBackButtonClicked();
    }

    @Override
    public void onRightToolbarButtonClicked() {
        mContainerViewModel.ShowRightToolbarButton();
        mContainerView.ShowRightToolbarButton();
        if (mLoginUtil.getUserRole().equalsIgnoreCase(IndividualUser.ROLE_VALUE) || mLoginUtil.getUserRole().equalsIgnoreCase(CommercialUser.ROLE_VALUE)) {
            int requestCode = ComposerKey.FEEDBACK;
            if (mQuestion.status().equalsIgnoreCase(Question.Status.OPEN_FOR_MORE_DETAILS)) {
                requestCode = ComposerKey.DETAILS;
            }

            if (mQuestion.status().equalsIgnoreCase(Question.Status.OPEN_FOR_FEEDBACK) ||
                    mQuestion.status().equalsIgnoreCase(Question.Status.OPEN_FOR_MORE_DETAILS)) {
                mContainerViewModel
                        .goTo(ComposerKey.builder()
                                .requestType(requestCode)
                                .questionToRepondTo(mQuestion)
                                .build())
                        .subscribe(mContainerViewModel.navigationObserver());
            }
        } else {
            if (mQuestion.status().equalsIgnoreCase(Question.Status.PENDING_ANSWER) ||
                    mQuestion.status().equalsIgnoreCase(Question.Status.HAS_FEEDBACK) ||
                    mQuestion.status().equalsIgnoreCase(Question.Status.HAS_MORE_DETAILS)) {
                mContainerViewModel
                        .goTo(ComposeAnswerKey.builder()
                                .questionToAnswer(mQuestion)
                                .build())
                        .subscribe(mContainerViewModel.navigationObserver());
            }
        }
    }

    @Override
    public void addAnswer(Answer answer) {
        View answerContainer = LayoutInflater.from(mFragment.getContext()).inflate(R.layout.item_answer, mView.getAnswersContainer(), false);
        TextView date = answerContainer.findViewById(R.id.date);
        SimpleDateFormat formatter = new SimpleDateFormat("EEEE, dd MMMM yyyy '@' hh:mm a");

        formatter.setTimeZone(TimeZone.getTimeZone("GMT"));
//        Calendar cal = Calendar.getInstance();
//        TimeZone tz = cal.getTimeZone();
//        System.out.println("Time Zone => " + tz.getDisplayName());
//        formatter.setTimeZone(tz);

        if (answer.dateSent() != null) {
            if (CommonUtils.isNotEmpty(answer.answerFor())) {
                if (answer.answerFor().equalsIgnoreCase(Question.Status.PENDING_ANSWER)) {
                    if (answer.senderRole().equalsIgnoreCase(IndividualUser.ROLE_VALUE) || answer.senderRole().equalsIgnoreCase(CommercialUser.ROLE_VALUE)) {
                        date.setText(mFragment.getString(R.string.format_question_date, formatter.format(answer.dateSent())));
                    } else {
                        date.setText(mFragment.getString(R.string.format_answer_date, formatter.format(answer.dateSent())));
                    }
                } else if (answer.answerFor().equalsIgnoreCase(Question.Status.OPEN_FOR_MORE_DETAILS)) {
                    date.setText(mFragment.getString(R.string.format_extra_details_date, formatter.format(answer.dateSent())));
                } else if (answer.answerFor().equalsIgnoreCase(Question.Status.HAS_MORE_DETAILS)) {
                    date.setText(mFragment.getString(R.string.format_answer_date, formatter.format(answer.dateSent())));
                } else if (answer.answerFor().equalsIgnoreCase(Question.Status.OPEN_FOR_FEEDBACK)) {
                    date.setText(mFragment.getString(R.string.format_respond_date, formatter.format(answer.dateSent())));
                } else if (answer.answerFor().equalsIgnoreCase(Question.Status.HAS_FEEDBACK)) {
                    date.setText(mFragment.getString(R.string.format_answer_date, formatter.format(answer.dateSent())));
                } else if (answer.answerFor().equalsIgnoreCase(Question.Status.CLOSED)) {
                    date.setText(mFragment.getString(R.string.format_answer_date, formatter.format(answer.dateSent())));
                }
            } else {
                if (answer.senderRole().equalsIgnoreCase(IndividualUser.ROLE_VALUE) || answer.senderRole().equalsIgnoreCase(CommercialUser.ROLE_VALUE)) {
                    date.setText(mFragment.getString(R.string.format_question_date, formatter.format(answer.dateSent())));
                } else {
                    date.setText(mFragment.getString(R.string.format_answer_date, formatter.format(answer.dateSent())));
                }
            }
        }

        if (answer.senderRole().equalsIgnoreCase(IndividualUser.ROLE_VALUE) || answer.senderRole().equalsIgnoreCase(CommercialUser.ROLE_VALUE)) {
            View outboundView = answerContainer.findViewById(R.id.outbound);

            View messageContainer = outboundView.findViewById(R.id.outboundMessageContainer);
            TextView message = outboundView.findViewById(R.id.outboundMessage);
            View audioContainer = outboundView.findViewById(R.id.outboundVoiceContainer);
            ImageButton btnAudioPlay = outboundView.findViewById(R.id.outboundPlayPauseImageView);
            ContentLoadingProgressBar audioDownload = outboundView.findViewById(R.id.outboundAudioDownloading);
            CustomLineBarVisualizer audioProgressBar = outboundView.findViewById(R.id.outboundVoiceProgressBar);
            audioProgressBar.setColor(ContextCompat.getColor(mFragment.getContext(), R.color.yankeesBlue));
            audioProgressBar.setDensity(45f);
            TextView durationTextView = outboundView.findViewById(R.id.outboundVoiceDurationTextView);
            int playDrawableRes = R.drawable.icon_voice_play_dark;
            int pauseDrawableRes = R.drawable.icon_voice_pause_dark;
            ViewGroup attachmentContainer = outboundView.findViewById(R.id.outboundAttachmentContainer);
            int attachmentLayoutRes = R.layout.item_outbound_question_attachment;
            int fileDownloadId = R.id.outboundFileDownloadProgress;
            int downloadImageId = R.id.downloadImageView;
            int showImageViewID = R.id.showImageView;
            int attachmentInfoId = R.id.attachmentInfoTextView;

            prepareAnswerView(answer, outboundView, messageContainer, message,
                    audioContainer, btnAudioPlay, audioDownload, audioProgressBar, durationTextView, playDrawableRes, pauseDrawableRes,
                    attachmentContainer, attachmentLayoutRes, fileDownloadId, showImageViewID, downloadImageId, attachmentInfoId);
        } else {
            View inboundView = answerContainer.findViewById(R.id.inbound);

            View messageContainer = inboundView.findViewById(R.id.inboundMessageContainer);
            TextView message = inboundView.findViewById(R.id.inboundMessage);
            View audioContainer = inboundView.findViewById(R.id.inboundVoiceContainer);
            ImageButton btnAudioPlay = inboundView.findViewById(R.id.inboundPlayPauseImageView);
            ContentLoadingProgressBar audioDownload = inboundView.findViewById(R.id.inboundAudioDownloading);
            CustomLineBarVisualizer audioProgressBar = inboundView.findViewById(R.id.inboundVoiceProgressBar);
            audioProgressBar.setColor(ContextCompat.getColor(mFragment.getContext(), R.color.wheat));
            audioProgressBar.setDensity(45f);
            TextView durationTextView = inboundView.findViewById(R.id.inboundVoiceDurationTextView);
            int playDrawableRes = R.drawable.icon_voice_play_light;
            int pauseDrawableRes = R.drawable.icon_voice_pause_light;
            ViewGroup attachmentContainer = inboundView.findViewById(R.id.inboundAttachmentContainer);
            int attachmentLayoutRes = R.layout.item_inbound_question_attachment;
            int fileDownloadId = R.id.inboundFileDownloadProgress;
            int downloadImageId = R.id.downloadImageView;
            int showImageViewID = R.id.showImageView;
            int attachmentInfoId = R.id.attachmentInfoTextView;

            prepareAnswerView(answer, inboundView, messageContainer, message,
                    audioContainer, btnAudioPlay, audioDownload, audioProgressBar, durationTextView, playDrawableRes, pauseDrawableRes,
                    attachmentContainer, attachmentLayoutRes, fileDownloadId, showImageViewID, downloadImageId, attachmentInfoId);
        }

        mView.getAnswersContainer().addView(answerContainer);
//        mContainerViewModel.hideRightToolbarButton();
//        mContainerView.hideRightText_();
//        mContainerView.hideRightToolbarTextButton();

    }

    @SuppressLint("CheckResult")
    @Override
    public void prepareAnswerView(Answer answer, View container, View messageContainer, TextView message,
                                  View audioContainer, ImageButton btnAudioPlay, ContentLoadingProgressBar audioDownload, CustomLineBarVisualizer audioProgressBar, TextView durationTextView, @DrawableRes int playImageRes, @DrawableRes int pauseImageRes,
                                  ViewGroup attachmentContainer, @LayoutRes int attachmentLayoutRes, @IdRes int fileDownloadId, @IdRes int showImageViewID, @IdRes int downloadImageId, @IdRes int attachmentInfoId) {

        container.setVisibility(View.VISIBLE);
        if (CommonUtils.isNotEmpty(answer.questionDescription())) {
            messageContainer.setVisibility(View.VISIBLE);
            message.setText(answer.questionDescription());
        }

        if (CommonUtils.isNotEmpty(answer.audioRecordingUrl())) {
            audioContainer.setVisibility(View.VISIBLE);

            RxView.clicks(btnAudioPlay)
                    .compose(mFragment.bindUntilEvent(FragmentEvent.DESTROY_VIEW))
                    .compose(mPermissions.ensure(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE))
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(isGranted -> {
                        if (isGranted) {
                            if (mFileFramework.isDownloaded(answer.audioRecordingUrl())) {
                                if (mCurrentPlaying == null) {
                                    mCurrentPlaying = answer;
                                    btnAudioPlay.setImageResource(pauseImageRes);

                                    File audioFile = mFileFramework.getDownloadedFile(answer.audioRecordingUrl());
                                    mRxAudioPlayer
                                            .play(PlayConfig
                                                    .file(audioFile)
                                                    .streamType(AudioManager.STREAM_MUSIC)
                                                    .build())
                                            .subscribeOn(Schedulers.io())
                                            .observeOn(AndroidSchedulers.mainThread())
                                            .subscribe(
                                                    aBoolean -> {
                                                        audioProgressBar.setPlayer(mRxAudioPlayer.getMediaPlayer().getAudioSessionId());

                                                        mPlaybackHandler = new Handler();
                                                        mPlaybackRunnable = () -> {
                                                            if (mRxAudioPlayer.getMediaPlayer() != null) {
                                                                durationTextView.setText(secToTime(mRxAudioPlayer.getMediaPlayer().getCurrentPosition() / 1000));
                                                            }
                                                            mPlaybackHandler.postDelayed(mPlaybackRunnable, 1000);
                                                        };
                                                        mPlaybackHandler.postDelayed(mPlaybackRunnable, 1000);
                                                    },
                                                    mContainerViewModel.catchErrorThrowable(),
                                                    () -> {
                                                        btnAudioPlay.setImageResource(playImageRes);
                                                        mPlaybackHandler.removeCallbacks(mPlaybackRunnable);
                                                        audioProgressBar.release();
                                                        durationTextView.setText("00:00:00");
                                                        mCurrentPlaying = null;
                                                    });
                                } else {
                                    if (mCurrentPlaying.equals(answer)) {
                                        mRxAudioPlayer.stopPlay();

                                        btnAudioPlay.setImageResource(playImageRes);
                                        mPlaybackHandler.removeCallbacks(mPlaybackRunnable);
                                        audioProgressBar.release();
                                        durationTextView.setText("00:00:00");
                                        mCurrentPlaying = null;
                                    }
                                }
                            } else {
                                if (!mFileFramework.isDownloading(answer.audioRecordingUrl())) {
                                    mFileFramework.downloadFile(answer.audioRecordingUrl())
                                            .doOnSubscribe(subscription -> audioDownload.setVisibility(View.VISIBLE))
                                            .doFinally(() -> audioDownload.setVisibility(View.GONE))
                                            .subscribe(Functions.emptyConsumer(), mContainerViewModel.catchErrorThrowable());
                                }
                            }
                        } else {
                            mContainerView.showMessage(mFragment.getString(R.string.error_permission), true);
                        }
                    });
        }

        if (answer.fileAttachments() != null && answer.fileAttachments().size() > 0) {
            attachmentContainer.setVisibility(View.VISIBLE);

            for (String attachmentUrl : Objects.requireNonNull(answer.fileAttachments())) {
                if (CommonUtils.isNotEmpty(attachmentUrl)) {
                    View attachmentView = LayoutInflater.from(mFragment.getContext()).inflate(attachmentLayoutRes, attachmentContainer, false);
                    ContentLoadingProgressBar fileDownloadProgress = attachmentView.findViewById(fileDownloadId);
                    ImageButton downloadImageView = attachmentView.findViewById(downloadImageId);
                    ImageButton showImageView = attachmentView.findViewById(showImageViewID);
                    TextView attachmentInfoTextView = attachmentView.findViewById(attachmentInfoId);

                    File attachmentFile = mFileFramework.getDownloadedFile(attachmentUrl);
                    if (attachmentFile != null) {
                        attachmentInfoTextView.setText(mFragment.getString(R.string.format_attachment_info, attachmentFile.getName(), mFileFramework.getFileSize(attachmentFile)));
                    }

                    RxView.clicks(downloadImageView)
                            .compose(mFragment.bindUntilEvent(FragmentEvent.DESTROY_VIEW))
                            .compose(mPermissions.ensure(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE))
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(isGranted -> {
                                if (isGranted) {
                                    if (!mFileFramework.isDownloaded(attachmentUrl) && !mFileFramework.isDownloading(attachmentUrl)) {
                                        mFileFramework.downloadFile(attachmentUrl)
                                                .doOnSubscribe(subscription -> fileDownloadProgress.setVisibility(View.VISIBLE))
                                                .doFinally(() -> fileDownloadProgress.setVisibility(View.GONE))
                                                .subscribe(progress -> {

                                                        }, mContainerViewModel.catchErrorThrowable(),
                                                        () -> {
                                                            File downloadedFile = mFileFramework.getDownloadedFile(attachmentUrl);
                                                            if (downloadedFile != null) {
                                                                attachmentInfoTextView.setText(mFragment.getString(R.string.format_attachment_info, downloadedFile.getName(), mFileFramework.getFileSize(downloadedFile)));
                                                            }
                                                        });
                                    }
                                } else {
                                    mContainerView.showMessage(mFragment.getString(R.string.error_permission), true);
                                }
                            });
                    RxView.clicks(showImageView)
                            .compose(mFragment.bindUntilEvent(FragmentEvent.DESTROY_VIEW))
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(isGranted -> mView.showImageView(attachmentUrl));
                    attachmentView.setOnClickListener(v -> {
                        if (mFileFramework.isDownloaded(attachmentUrl)) {
                            Intent intent = new Intent(Intent.ACTION_VIEW);
                            intent.setData(FileProvider
                                    .getUriForFile(
                                            mFragment.getContext(),
                                            mFragment.getContext().getApplicationContext().getPackageName() + ".com.shawerapp.android.provider",
                                            mFileFramework.getDownloadedFile(attachmentUrl)));
                            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                            Intent openFile = Intent.createChooser(intent, "Choose an application to open with:");
                            mFragment.startActivity(openFile);
                        }
                    });
                    attachmentContainer.addView(attachmentView);
                }
            }
        }
    }

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
    public void onLikeButtonClicked() {
        mRTDataFramework.updateQuestionFeedback(mQuestion, Question.Feedback.GOOD)
                .compose(mFragment.bindUntilEvent(FragmentEvent.DESTROY_VIEW))
                .doOnSubscribe(disposable -> mContainerView.showLoadingIndicator())
                .doFinally(() -> mContainerView.hideLoadingIndicator())
                .subscribe(() -> {
                    mView.setStatus(mFragment.getString(R.string.case_closed));
                    mView.showGoodRating();
                });
    }

    @Override
    public void onDislikeButtonClicked() {
        mRTDataFramework.updateQuestionFeedback(mQuestion, Question.Feedback.BAD)
                .compose(mFragment.bindUntilEvent(FragmentEvent.DESTROY_VIEW))
                .doOnSubscribe(disposable -> mContainerView.showLoadingIndicator())
                .doFinally(() -> mContainerView.hideLoadingIndicator())
                .subscribe(() -> {
                    mView.setStatus(mFragment.getString(R.string.case_closed));
                    mView.showBadRating();
                });
    }

    @Override
    public void onStart() {

    }

    @Override
    public void onStop() {

    }

    @Override
    public void onDetach() {

    }

    @Override
    public void onDestroy() {

    }
}
