package com.shawerapp.android.screens.viewrequest;

import android.Manifest;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Handler;
import androidx.core.content.FileProvider;
import androidx.core.widget.ContentLoadingProgressBar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.github.piasy.rxandroidaudio.PlayConfig;
import com.github.piasy.rxandroidaudio.RxAudioPlayer;
import com.jakewharton.rxbinding2.view.RxView;
import com.shawerapp.android.R;
import com.shawerapp.android.autovalue.PracticeRequest;
import com.shawerapp.android.backend.base.FileFramework;
import com.shawerapp.android.backend.base.RealTimeDataFramework;
import com.shawerapp.android.base.BaseFragment;
import com.shawerapp.android.screens.container.ContainerContract;
import com.shawerapp.android.utils.CommonUtils;
import com.shawerapp.android.utils.LocaleHelper;
import com.shawerapp.android.utils.LoginUtil;
import com.tbruyelle.rxpermissions2.RxPermissions;
import com.trello.rxlifecycle2.android.FragmentEvent;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Objects;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.internal.functions.Functions;
import io.reactivex.schedulers.Schedulers;

import static com.shawerapp.android.screens.viewrequest.ViewCaseRequestFragment.ARG_PRACTICE_REQUEST;

public class ViewCaseRequestViewModel implements ViewCaseRequestContract.ViewModel {

    private BaseFragment mFragment;

    private ViewCaseRequestContract.View mView;

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

    private RxAudioPlayer mRxAudioPlayer;

    private PracticeRequest mCurrentPlaying;

    private PracticeRequest mPracticeRequest;

    private RxPermissions mPermissions;

    private Handler mPlaybackHandler;

    private Runnable mPlaybackRunnable;

    @Inject
    public ViewCaseRequestViewModel(BaseFragment fragment, ViewCaseRequestContract.View view) {
        mFragment = fragment;
        mView = view;

        mRxAudioPlayer = RxAudioPlayer.getInstance();
        mPermissions = new RxPermissions(mFragment.getActivity());
        mPracticeRequest = fragment.getArguments().getParcelable(ARG_PRACTICE_REQUEST);
    }

    @Override
    public void onViewCreated() {
        mView.initBindings();

        if (LocaleHelper.getLanguage(mFragment.getContext()).equalsIgnoreCase(LocaleHelper.ARABIC)) {
            mView.setSubSubjectName(mPracticeRequest.ar_subSubjectName());
        } else {
            mView.setSubSubjectName(mPracticeRequest.subSubjectName());
        }
        setupPracticeRequestDetails(mPracticeRequest);

        if (mPracticeRequest.status().equalsIgnoreCase(PracticeRequest.Status.FULFILLED)) {
            setupPracticeRequetsResponse(mPracticeRequest);
            mView.setStatus(mFragment.getString(R.string.request_fulfilled_status));
            mContainerView.setRightToolbarButtonImageResource(R.drawable.icon_plain_circle_light);
        } else {
            mView.setStatus(mFragment.getString(R.string.request_unfulfilled_status));
            mContainerView.setRightToolbarButtonImageResource(R.drawable.icon_case_closed);
        }
    }

    @Override
    public void setupPracticeRequestDetails(PracticeRequest practiceRequest) {
        mView.showOutboundContainer();
        SimpleDateFormat formatter = new SimpleDateFormat("EEEE dd MMMM yyyy '@' hh:mm a");
        mView.setRequestDate(mFragment.getString(R.string.format_question_date, formatter.format(practiceRequest.dateCreated())));
        if (CommonUtils.isNotEmpty(practiceRequest.questionDescription())) {
            mView.showOutboundMessage(practiceRequest.questionDescription());
        }

        if (CommonUtils.isNotEmpty(practiceRequest.audioRecordingUrl())) {
            mView.showOutboundAudio();
        }

        if (practiceRequest.fileAttachments() != null && practiceRequest.fileAttachments().size() > 0) {
            LinearLayout attachmentContainer = mView.getOutboundAttachmentContainer();
            attachmentContainer.setVisibility(View.VISIBLE);

            for (String attachmentUrl : Objects.requireNonNull(practiceRequest.fileAttachments())) {
                if (CommonUtils.isNotEmpty(attachmentUrl)) {
                    View attachmentView = LayoutInflater.from(mFragment.getContext()).inflate(R.layout.item_outbound_request_attachment, attachmentContainer, false);
                    ContentLoadingProgressBar fileDownloadProgress = attachmentView.findViewById(R.id.outboundFileDownloadProgress);
                    ImageButton downloadImageView = attachmentView.findViewById(R.id.downloadImageView);
                    TextView attachmentInfoTextView = attachmentView.findViewById(R.id.attachmentInfoTextView);

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

    @Override
    public void setupPracticeRequetsResponse(PracticeRequest practiceRequest) {
        mView.showInboundContainer();
        mView.setLawyerName(mFragment.getString(R.string.format_lawyer_name, practiceRequest.lawyerName()));
        mView.setLawyerContactNo(mFragment.getString(R.string.format_lawyer_contact, practiceRequest.lawyerContactNo()));
        mView.setLawyerAddress(mFragment.getString(R.string.format_lawyer_address, practiceRequest.lawyerAddress()));
        mView.setLawyerOfficeName(mFragment.getString(R.string.format_lawyer_office_name, practiceRequest.lawyerOfficeName()));
        mView.setShawerSpecialPromoCode(mFragment.getString(R.string.format_shawer_promo_code, practiceRequest.shawerSpecialPromoCode()));
    }

    @Override
    public void onOutboundPlayButtonClicked() {
        if (mFileFramework.isDownloaded(mPracticeRequest.audioRecordingUrl())) {
            if (mCurrentPlaying == null) {
                mCurrentPlaying = mPracticeRequest;
                mView.setOutboundPlayButtonResource(R.drawable.icon_voice_pause_dark);

                File audioFile = mFileFramework.getDownloadedFile(mPracticeRequest.audioRecordingUrl());
                mRxAudioPlayer
                        .play(PlayConfig
                                .file(audioFile)
                                .streamType(AudioManager.STREAM_MUSIC)
                                .build())
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                aBoolean -> {
                                    mView.startPlaybackVisualization(mRxAudioPlayer.getMediaPlayer().getAudioSessionId());

                                    mPlaybackHandler = new Handler();
                                    mPlaybackRunnable = () -> {
                                        if (mRxAudioPlayer.getMediaPlayer() != null) {
                                            mView.setOutboundProgress(secToTime(mRxAudioPlayer.getMediaPlayer().getCurrentPosition() / 1000));
                                        }
                                        mPlaybackHandler.postDelayed(mPlaybackRunnable, 1000);
                                    };
                                    mPlaybackHandler.postDelayed(mPlaybackRunnable, 1000);
                                },
                                mContainerViewModel.catchErrorThrowable(),
                                () -> {
                                    mView.setOutboundPlayButtonResource(R.drawable.icon_voice_play_dark);
                                    mPlaybackHandler.removeCallbacks(mPlaybackRunnable);
                                    mView.stopPlaybackVisualization();
                                    mView.setOutboundProgress("00:00:00");
                                    mCurrentPlaying = null;
                                });
            } else {
                if (mCurrentPlaying.equals(mPracticeRequest)) {
                    mRxAudioPlayer.stopPlay();

                    mView.setOutboundPlayButtonResource(R.drawable.icon_voice_play_dark);
                    mPlaybackHandler.removeCallbacks(mPlaybackRunnable);
                    mView.stopPlaybackVisualization();
                    mView.setOutboundProgress("00:00:00");
                    mCurrentPlaying = null;
                }
            }
        } else {
            if (!mFileFramework.isDownloading(mPracticeRequest.audioRecordingUrl())) {
                mFileFramework.downloadFile(mPracticeRequest.audioRecordingUrl())
                        .doOnSubscribe(subscription -> mView.showOutboundDownloading())
                        .doFinally(() -> mView.hideOutboundDownloading())
                        .subscribe(Functions.emptyConsumer(), mContainerViewModel.catchErrorThrowable());
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
    public void onAfterEnterAnimation() {

    }

    @Override
    public void onBackButtonClicked() {
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
