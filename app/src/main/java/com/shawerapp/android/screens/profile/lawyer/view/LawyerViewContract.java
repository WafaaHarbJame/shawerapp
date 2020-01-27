package com.shawerapp.android.screens.profile.lawyer.view;

import com.shawerapp.android.autovalue.LawyerUser;
import com.shawerapp.android.autovalue.Memo;
import com.shawerapp.android.autovalue.Question;
import com.shawerapp.android.base.FragmentLifecycle;

public class LawyerViewContract {
    interface View extends FragmentLifecycle.View {

        void initBindings();

        void addMemo(Memo memo);

        void updateMemo(Memo memo);

        void removeMemo(Memo memo);

        void addQuestion(Question question);

        void updateQuestion(Question question);

        void removeQuestion(Question question);

        void setUserImage(String imageUrl);

        void setUsername(CharSequence username);

        void setLawyerFullName(CharSequence fullName);

        void setProfileBio(CharSequence profileBio);

        void setQuestionsReceived(String questionsReceived);

        void setAnswersSent(String answersSent);

        void setLikes(String likes);

        void setYearsOfExperience(String yearsOfExperience);

        void setOnlineStatus(boolean isOnline);

        void scrollHorizontalQuestions(int pixelOffset);

        void setFavoriteImage(int imgRes);
    }

    interface ViewModel extends FragmentLifecycle.ViewModel {

        void setupProfileForLawyerUsers(LawyerUser lawyerUser);

        void onLeftArrowClicked();

        void onRightArrowClicked();

        void onSubSubjectClicked(Question question);
    }
}
