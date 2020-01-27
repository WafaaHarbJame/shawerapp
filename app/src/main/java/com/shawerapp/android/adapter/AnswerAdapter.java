package com.shawerapp.android.adapter;

import android.content.Context;

import androidx.annotation.Nullable;

import com.github.piasy.rxandroidaudio.PlayConfig;
import com.github.piasy.rxandroidaudio.RxAudioPlayer;
import com.shawerapp.android.backend.base.FileFramework;
import com.shawerapp.android.utils.CommonUtils;

import java.util.List;

import javax.inject.Inject;

import eu.davidea.flexibleadapter.FlexibleAdapter;
import eu.davidea.flexibleadapter.items.IFlexible;
import io.reactivex.internal.functions.Functions;
import timber.log.Timber;

public class AnswerAdapter extends FlexibleAdapter<IFlexible> {

    private RxAudioPlayer mRxAudioPlayer;

    private String mAudioInPlay;

    private Context mContext;

    private FileFramework mFileFramework;

    @Inject
    public AnswerAdapter(@Nullable List<IFlexible> items, Context context, FileFramework fileFramework) {
        super(items);
        mContext = context;
        mFileFramework = fileFramework;
        mRxAudioPlayer = RxAudioPlayer.getInstance();
    }

    public RxAudioPlayer getAudioPlayer() {
        return mRxAudioPlayer;
    }

    public boolean isPlaying() {
        return mRxAudioPlayer.getMediaPlayer().isPlaying();
    }

    public String getAudioInPlay() {
        return mAudioInPlay;
    }

    public void playAudio(String audioRecordingUrl) {
        if (!isPlaying()) {
            mAudioInPlay = audioRecordingUrl;

            if (mFileFramework.isDownloaded(audioRecordingUrl)) {
                mRxAudioPlayer
                        .play(PlayConfig
                                .file(mFileFramework.getDownloadedFile(audioRecordingUrl))
                                .build())
                        .subscribe(
                                Functions.emptyConsumer(),
                                throwable -> Timber.e(CommonUtils.getExceptionString(throwable)),
                                () -> mAudioInPlay = null);
            }
        }
    }


    public boolean isDownloaded(String fileUrl) {
        return mFileFramework.isDownloaded(fileUrl);
    }

    public boolean isDownloading(String fileUrl) {
        return mFileFramework.isDownloading(fileUrl);
    }

    public void downloadFile(String fileUrl, FileFramework.DownloadProgressSubscriber subscriber) {
    }

    public void stopPlay() {
        mRxAudioPlayer.stopPlay();
    }
}
