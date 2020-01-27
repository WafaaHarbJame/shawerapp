package com.shawerapp.android.screens.questiondetails;

import androidx.annotation.DrawableRes;
import androidx.annotation.IdRes;
import androidx.annotation.LayoutRes;
import androidx.core.widget.ContentLoadingProgressBar;

import android.graphics.drawable.Drawable;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.shawerapp.android.autovalue.Answer;
import com.shawerapp.android.base.FragmentLifecycle;
import com.shawerapp.android.custom.views.CustomLineBarVisualizer;

public class QuestionDetailsContract {

    interface View extends FragmentLifecycle.View {
        void initBindings();

        ViewGroup getAnswersContainer();

        void setUserImage(String imageUrl);

        void setUsername(String username);

        void setUserType(String roleValue);

        void setSubSubjectName(String subSubjectName);

        void setStatus(CharSequence statusMessage);

        void showRatingButtons();

        void showImageView(String url);

        void hideImageView();

        void showGoodRating();

        void showBadRating();

        void scrollToBottom();
    }

    interface ViewModel extends FragmentLifecycle.ViewModel {

        void setUpStatus();

        void addAnswer(Answer answer);

        void prepareAnswerView(Answer answer, android.view.View answerContainer, android.view.View messageContainer, TextView message,
                               android.view.View audioContainer, ImageButton btnAudioPlay, ContentLoadingProgressBar audioDownload, CustomLineBarVisualizer audioProgressBar, TextView durationTextView, @DrawableRes int playImageRes, @DrawableRes int pauseImageRes,
                               ViewGroup attachmentContainer, @LayoutRes int attachmentLayoutRes, @IdRes int fileDownloadId, @IdRes int showImageViewID, @IdRes int downloadImageId, @IdRes int attachmentInfoId);

        void onLikeButtonClicked();

        void onDislikeButtonClicked();

    }
}
