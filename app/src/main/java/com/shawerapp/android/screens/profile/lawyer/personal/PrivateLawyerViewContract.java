package com.shawerapp.android.screens.profile.lawyer.personal;

import android.text.Editable;
import android.view.ViewGroup;

import com.shawerapp.android.autovalue.LawyerUser;
import com.shawerapp.android.autovalue.Question;
import com.shawerapp.android.base.FragmentLifecycle;
import com.shawerapp.android.autovalue.Memo;

public class PrivateLawyerViewContract {
    interface View extends FragmentLifecycle.View {

        void initBindings();

        void addMemo(Memo question);

        void updateMemo(Memo question);

        void removeMemo(Memo question);

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

        void setCoins(String coins);

        void setYearsOfExperience(String yearsOfExperience);

        void hideMemoInput();

        void showMemoInput();

        void scrollHorizontalQuestions(int pixelOffset);
    }

    interface ViewModel extends FragmentLifecycle.ViewModel {

        void onGlobalLayoutChange(ViewGroup fragmentView);

        void setupProfileForLawyerUsers(LawyerUser lawyerUser);

        void onAddMemoClicked();

        void onPostButtonClicked(CharSequence memoContent);

        boolean onQuestionAssignedItemClicked(Question question);

        void onLeftArrowClicked();

        void onRightArrowClicked();
    }
}
