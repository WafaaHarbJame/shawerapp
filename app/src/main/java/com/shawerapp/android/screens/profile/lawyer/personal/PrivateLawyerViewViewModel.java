package com.shawerapp.android.screens.profile.lawyer.personal;

import android.content.Intent;
import android.os.Handler;
import androidx.core.content.res.ResourcesCompat;
import android.text.SpannableStringBuilder;
import android.view.ViewGroup;

import com.google.firebase.messaging.FirebaseMessaging;
import com.shawerapp.android.R;
import com.shawerapp.android.autovalue.LawyerUser;
import com.shawerapp.android.autovalue.MemoEvent;
import com.shawerapp.android.autovalue.Question;
import com.shawerapp.android.autovalue.QuestionEvent;
import com.shawerapp.android.backend.base.AuthFramework;
import com.shawerapp.android.backend.base.RealTimeDataFramework;
import com.shawerapp.android.base.BaseFragment;
import com.shawerapp.android.screens.container.ContainerContract;
import com.shawerapp.android.screens.login.LoginActivity;
import com.shawerapp.android.screens.profile.lawyer.edit.LawyerEditKey;
import com.shawerapp.android.screens.questiondetails.QuestionDetailsKey;
import com.shawerapp.android.utils.AnimationUtils;
import com.shawerapp.android.utils.CommonUtils;
import com.shawerapp.android.utils.LocaleHelper;
import com.shawerapp.android.utils.LoginUtil;
import com.trello.rxlifecycle2.android.FragmentEvent;

import java.text.DecimalFormat;

import javax.inject.Inject;

import io.reactivex.functions.Consumer;
import io.reactivex.processors.BehaviorProcessor;

public class PrivateLawyerViewViewModel implements PrivateLawyerViewContract.ViewModel {

    private BaseFragment mFragment;

    private PrivateLawyerViewContract.View mView;

    @Inject
    ContainerContract.View mContainerView;

    @Inject
    ContainerContract.ViewModel mContainerViewModel;

    @Inject
    RealTimeDataFramework mRTDataFramework;

    @Inject
    AuthFramework mAuthFramework;

    @Inject
    LoginUtil mLoginUtil;

    private BehaviorProcessor<MemoEvent> mMemoProcessor = BehaviorProcessor.create();

    private BehaviorProcessor<QuestionEvent> mQuestionProcessor = BehaviorProcessor.create();

    private boolean mIsAddMemoShown;

    private boolean mIsAddMemoClicked;

    @Inject
    public PrivateLawyerViewViewModel(BaseFragment fragment, PrivateLawyerViewContract.View view) {
        mFragment = fragment;
        mView = view;
    }

    @Override
    public void onViewCreated() {
        mView.initBindings();
        mContainerView.selectTab(R.id.tabProfile);

        mMemoProcessor
                .serialize()
                .compose(mFragment.bindUntilEvent(FragmentEvent.DESTROY_VIEW))
                .subscribe(memoEventConsumer(), mContainerViewModel.catchErrorThrowable());

        mQuestionProcessor
                .serialize()
                .compose(mFragment.bindUntilEvent(FragmentEvent.DESTROY_VIEW))
                .filter(questionEvent -> !questionEvent.question().status().equalsIgnoreCase(Question.Status.CLOSED))
                .subscribe(questionEventConsumer(), mContainerViewModel.catchErrorThrowable());
    }

    private Consumer<? super MemoEvent> memoEventConsumer() {
        return memoEvent -> {
            switch (memoEvent.type()) {
                case RealTimeDataFramework.EVENT_ADDED:
                    mView.addMemo(memoEvent.memo());
                    break;
                case RealTimeDataFramework.EVENT_UPDATED:
                    mView.updateMemo(memoEvent.memo());
                    break;
                case RealTimeDataFramework.EVENT_REMOVED:
                    mView.removeMemo(memoEvent.memo());
                    break;
            }
        };
    }

    private Consumer<? super QuestionEvent> questionEventConsumer() {
        return questionEvent -> {
            switch (questionEvent.type()) {
                case RealTimeDataFramework.EVENT_ADDED:
                    mView.addQuestion(questionEvent.question());
                    break;
                case RealTimeDataFramework.EVENT_UPDATED:
                    mView.updateQuestion(questionEvent.question());
                    break;
                case RealTimeDataFramework.EVENT_REMOVED:
                    mView.removeQuestion(questionEvent.question());
                    break;
            }
        };
    }

    @Override
    public void onAfterEnterAnimation() {
        mRTDataFramework.fetchUser(mLoginUtil.getUserID())
                .doOnSubscribe(disposable -> mContainerView.showLoadingIndicator())
                .doFinally(() -> mContainerView.hideLoadingIndicator())
                .compose(mFragment.bindUntilEvent(FragmentEvent.DESTROY_VIEW))
                .subscribe(user -> {
                    if (user instanceof LawyerUser) {
                        LawyerUser individualUser = (LawyerUser) user;
                        setupProfileForLawyerUsers(individualUser);
                    }
                });
    }

    @Override
    public void onGlobalLayoutChange(ViewGroup fragmentView) {
        if (!CommonUtils.isKeyboardShown(fragmentView)) {
            if (mIsAddMemoShown && !mIsAddMemoClicked) {
                mView.hideMemoInput();
                mIsAddMemoShown = false;
            }
        }
    }

    @Override
    public void setupProfileForLawyerUsers(LawyerUser lawyerUser) {
        DecimalFormat decimalFormat = new DecimalFormat("#,###,###");

        mView.setUserImage(lawyerUser.imageUrl());
        mView.setUsername(lawyerUser.username());
        if (lawyerUser.questionsReceived() != null) {
            mView.setQuestionsReceived(decimalFormat.format(lawyerUser.questionsReceived()));
        }

        if (lawyerUser.answersSent() != null) {
            mView.setAnswersSent(decimalFormat.format(lawyerUser.answersSent()));
        }

        if (lawyerUser.likes() != null) {
            mView.setLikes(decimalFormat.format(lawyerUser.likes().size()));
        }

        if (lawyerUser.coins() != null) {
            mView.setCoins(decimalFormat.format(lawyerUser.coins()));
        }

        if (CommonUtils.isNotEmpty(lawyerUser.yearsOfExperience())) {
            mView.setYearsOfExperience(lawyerUser.yearsOfExperience());
        }

        if (CommonUtils.isNotEmpty(lawyerUser.fullName())) {
            mView.setLawyerFullName(lawyerUser.fullName());
        } else {
            SpannableStringBuilder nameHint = new SpannableStringBuilder(mFragment.getString(R.string.hint_full_name));
            mView.setLawyerFullName(nameHint);
        }

        if (CommonUtils.isNotEmpty(lawyerUser.profileBio())) {
            mView.setProfileBio(lawyerUser.profileBio());
        } else {
            SpannableStringBuilder profileBioHint = new SpannableStringBuilder(mFragment.getString(R.string.hint_add_profile_bio));
            mView.setProfileBio(profileBioHint);
        }

        mRTDataFramework.retrieveMemos()
                .compose(mFragment.bindUntilEvent(FragmentEvent.DESTROY_VIEW))
                .subscribe(mMemoProcessor::onNext, mContainerViewModel.catchErrorThrowable());

        mRTDataFramework.retrieveQuestionsReceived()
                .compose(mFragment.bindUntilEvent(FragmentEvent.DESTROY_VIEW))
                .subscribe(mQuestionProcessor::onNext, mContainerViewModel.catchErrorThrowable());
    }

    @Override
    public void onAddMemoClicked() {
        mIsAddMemoShown = true;
        mIsAddMemoClicked = true;
        mView.showMemoInput();

        new Handler().postDelayed(
                () -> mIsAddMemoClicked = false, 1000);
    }

    @Override
    public void onPostButtonClicked(CharSequence memoContent) {
        mRTDataFramework.addMemo(memoContent)
                .compose(mFragment.bindUntilEvent(FragmentEvent.DESTROY_VIEW))
                .doOnSubscribe(subscription -> mContainerView.showLoadingIndicator())
                .doFinally(() -> mContainerView.hideLoadingIndicator())
                .subscribe(
                        () -> {
                            mContainerView.showMessage(mFragment.getString(R.string.success_memo));
                            mView.hideMemoInput();
                            mIsAddMemoShown = false;
                        },
                        mContainerViewModel.catchErrorThrowable());
    }

    @Override
    public boolean onQuestionAssignedItemClicked(Question question) {
        mContainerViewModel
                .goTo(QuestionDetailsKey.create(question))
                .subscribe(mContainerViewModel.navigationObserver());
        return false;
    }

    @Override
    public void onLeftArrowClicked() {
        if (LocaleHelper.getLanguage(mFragment.getContext()).equalsIgnoreCase("ar")) {
            mView.scrollHorizontalQuestions(mFragment.getResources().getDimensionPixelOffset(R.dimen.size_150));
        } else {
            mView.scrollHorizontalQuestions(mFragment.getResources().getDimensionPixelOffset(R.dimen.size_negative_150));
        }
    }

    @Override
    public void onRightArrowClicked() {
        if (LocaleHelper.getLanguage(mFragment.getContext()).equalsIgnoreCase("ar")) {
            mView.scrollHorizontalQuestions(mFragment.getResources().getDimensionPixelOffset(R.dimen.size_negative_150));
        } else {
            mView.scrollHorizontalQuestions(mFragment.getResources().getDimensionPixelOffset(R.dimen.size_150));
        }
    }

    @Override
    public void onBackButtonClicked() {

    }

    @Override
    public void setupToolbar() {
        mContainerView.clearToolbarSubtitle();
        mContainerView.clearToolbarTitle();
        mContainerView.setLeftToolbarButtonImageResource(R.drawable.icon_sign_out);
        mContainerView.setRightToolbarButtonImageResource(R.drawable.icon_edit_profile);
    }

    @Override
    public void onLeftToolbarButtonClicked() {
        mContainerView.showConfirmationMessage(
                mFragment.getString(R.string.logout_confirm),
                mFragment.getString(R.string.yes),
                mFragment.getString(R.string.no),
                () -> mAuthFramework
                        .logout()
                        .doOnSubscribe(disposable -> mContainerView.showLoadingIndicator())
                        .doFinally(() -> mContainerView.hideLoadingIndicator())
                        .subscribe(() -> {
                            FirebaseMessaging.getInstance().unsubscribeFromTopic("individual");
                            FirebaseMessaging.getInstance().unsubscribeFromTopic("lawyer");
                            Intent intent = new Intent(mFragment.getContext(), LoginActivity.class);
                            mFragment.getContext().startActivity(intent);
                            mFragment.getActivity().finish();
                            AnimationUtils.overridePendingTransition(mFragment.getActivity(), AnimationUtils.ANIM_STYLE.SLIDE_IN_FROM_LEFT);
                        }));
    }

    @Override
    public void onRightToolbarButtonClicked() {
        mContainerViewModel.goTo(LawyerEditKey.create())
                .subscribe(mContainerViewModel.navigationObserver());
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
