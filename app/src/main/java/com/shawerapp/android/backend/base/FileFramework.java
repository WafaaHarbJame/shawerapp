package com.shawerapp.android.backend.base;

import android.net.Uri;

import java.io.File;

import io.reactivex.Flowable;
import io.reactivex.Maybe;

/**
 * Created by john.ernest on 24/10/2017.
 */

public interface FileFramework {
    interface DownloadProgressSubscriber {

        void onProgress(long progress);
        void onError(Throwable throwable);

        void onFinish();

    }
    Maybe<String> uploadProfileImage(Uri imageUri);

    Maybe<String> uploadAnswerAttachment(Uri attachmentUri);

    Flowable<Long> downloadFile(String fileUrl);

    boolean isDownloading(String fileUrl);

    boolean isDownloaded(String fileUrl);

    File getDownloadedFile(String fileUrl);

    String getFileSize(File attachmentFile);
}
