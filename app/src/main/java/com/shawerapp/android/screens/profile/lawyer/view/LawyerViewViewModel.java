package com.shawerapp.android.screens.profile.lawyer.view;

import android.text.SpannableStringBuilder;

import com.google.android.gms.common.internal.service.Common;
import com.shawerapp.android.R;
import com.shawerapp.android.autovalue.LawyerUser;
import com.shawerapp.android.autovalue.LawyerUserEvent;
import com.shawerapp.android.autovalue.MemoEvent;
import com.shawerapp.android.autovalue.Question;
import com.shawerapp.android.autovalue.QuestionEvent;
import com.shawerapp.android.backend.base.RealTimeDataFramework;
import com.shawerapp.android.base.BaseFragment;
import com.shawerapp.android.screens.composer.ComposerKey;
import com.shawerapp.android.screens.container.ContainerContract;
import com.shawerapp.android.screens.profile.user.view.ProfileViewContract;
import com.shawerapp.android.utils.CommonUtils;
import com.shawerapp.android.utils.LocaleHelper;
import com.shawerapp.android.utils.LoginUtil;
import com.trello.rxlifecycle2.android.FragmentEvent;

import java.text.DecimalFormat;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import io.reactivex.Maybe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.processors.BehaviorProcessor;
import io.reactivex.schedulers.Schedulers;

import static com.shawerapp.android.screens.profile.lawyer.view.LawyerViewFragment.ARG_LAWYER;

public class LawyerViewViewModel implements LawyerViewContract.ViewModel {

    private BaseFragment mFragment;

    private LawyerViewContract.View mView;

    @Inject
    ContainerContract.View mContainerView;

    @Inject
    ContainerContract.ViewModel mContainerViewModel;

    @Inject
    RealTimeDataFramework mRTDataFramework;

    @Inject
    LoginUtil mLoginUtil;

    private BehaviorProcessor<MemoEvent> mMemoProcessor = BehaviorProcessor.create();

    private BehaviorProcessor<QuestionEvent> mQuestionProcessor = BehaviorProcessor.create();

    private LawyerUser mLawyerUser;

    @Inject
    public LawyerViewViewModel(BaseFragment fragment,
                               LawyerViewContract.View view) {
        mFragment = fragment;
        mView = view;

        mLawyerUser = fragment.getArguments().getParcelable(ARG_LAWYER);
    }

    @Override
    public void onViewCreated() {
        mView.initBindings();

        mMemoProcessor
                .serialize()
                .compose(mFragment.bindUntilEvent(FragmentEvent.DESTROY_VIEW))
                .subscribe(memoEventConsumer(), mContainerViewModel.catchErrorThrowable());

        mQuestionProcessor
                .serialize()
                .compose(mFragment.bindUntilEvent(FragmentEvent.DESTROY_VIEW))
                .filter(questionEvent -> !questionEvent.question().status().equalsIgnoreCase(Question.Status.CLOSED))
                .distinct(questionEvent -> questionEvent.question().subSubjectUid())
                .subscribe(questionEventConsumer(), mContainerViewModel.catchErrorThrowable());

        setupProfileForLawyerUsers(mLawyerUser);
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

        if (CommonUtils.isNotEmpty(lawyerUser.yearsOfExperience())) {
            mView.setYearsOfExperience(lawyerUser.yearsOfExperience());
        }

        if (CommonUtils.isNotEmpty(lawyerUser.fullName())) {
            mView.setLawyerFullName(lawyerUser.fullName());
        }

        if (CommonUtils.isNotEmpty(lawyerUser.profileBio())) {
            mView.setProfileBio(lawyerUser.profileBio());
        }

        if (CommonUtils.isNotEmpty(lawyerUser.presence())) {
            mView.setOnlineStatus(lawyerUser.presence().equals("online"));
        } else {
            mView.setOnlineStatus(false);
        }

        mRTDataFramework.observeUser(lawyerUser.uid())
                .compose(mFragment.bindUntilEvent(FragmentEvent.DESTROY_VIEW))
                .subscribe(o -> {
                    if (o instanceof LawyerUser) {
                        LawyerUser updatedProfile = (LawyerUser) o;

                        if (CommonUtils.isNotEmpty(updatedProfile.presence())) {
                            mView.setOnlineStatus(updatedProfile.presence().equals("online"));
                        } else {
                            mView.setOnlineStatus(false);
                        }
                    }
                });

        mRTDataFramework.retrieveMemosOfLawyer(mLawyerUser)
                .compose(mFragment.bindUntilEvent(FragmentEvent.DESTROY_VIEW))
                .subscribe(mMemoProcessor::onNext, mContainerViewModel.catchErrorThrowable());

        mRTDataFramework.retrieveQuestionsReceivedOfLawyer(mLawyerUser)
                .compose(mFragment.bindUntilEvent(FragmentEvent.DESTROY_VIEW))
                .subscribe(mQuestionProcessor::onNext, mContainerViewModel.catchErrorThrowable());
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
    public void onSubSubjectClicked(Question question) {
        Maybe.zip(
                mRTDataFramework.fetchField(question.fieldUid()),
                mRTDataFramework.fetchSubSubject(question.subSubjectUid()),
                (field, subSubject) -> ComposerKey.builder()
                        .requestType(ComposerKey.QUESTION)
                        .selectedField(field)
                        .selectedSubSubject(subSubject)
                        .selectedLawyerUser(mLawyerUser)
                        .build())
                .compose(mFragment.bindUntilEvent(FragmentEvent.DESTROY_VIEW))
                .doOnSubscribe(subscription -> mContainerView.showLoadingIndicator())
                .flatMapCompletable(composerKey -> mContainerViewModel.goTo(composerKey))
                .doFinally(() -> mContainerView.hideLoadingIndicator())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(mContainerViewModel.navigationObserver());
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

        boolean isFavorited = false;
        if (mLawyerUser.favoritedBy() != null) {
            isFavorited = mLawyerUser.favoritedBy().containsKey(mLoginUtil.getUserID()) &&
                    mLawyerUser.favoritedBy().get(mLoginUtil.getUserID());
        }
        if (!isFavorited) {
            mView.setFavoriteImage(R.drawable.icon_menu_favorites_unset);
            mContainerView.setRightToolbarButtonImageResource(R.drawable.icon_menu_favorites_unset);
        } else {
            mView.setFavoriteImage(R.drawable.icon_menu_favorites_set);
            mContainerView.setRightToolbarButtonImageResource(R.drawable.icon_menu_favorites_set);
        }
    }

    @Override
    public void onLeftToolbarButtonClicked() {
        onBackButtonClicked();
    }

    @Override
    public void onRightToolbarButtonClicked() {
        mRTDataFramework.fetchUser(mLawyerUser.uid())
                .compose(mFragment.bindUntilEvent(FragmentEvent.DESTROY_VIEW))
                .doOnSubscribe(subscription -> mContainerView.showLoadingIndicator())
                .flatMapMaybe(o -> {
                    LawyerUser lawyerUser = (LawyerUser) o;

                    boolean isFavorited = false;
                    if (lawyerUser.favoritedBy() != null) {
                        isFavorited = lawyerUser.favoritedBy().containsKey(mLoginUtil.getUserID()) &&
                                lawyerUser.favoritedBy().get(mLoginUtil.getUserID());
                    }

                    boolean finalIsFavorited = !isFavorited;
                    return mRTDataFramework
                            .favoriteLawyer(lawyerUser, !isFavorited)
                            .compose(mFragment.bindUntilEvent(FragmentEvent.DESTROY_VIEW))
                            .doOnSuccess(lawyerUser1 -> {
                                if (!finalIsFavorited) {
                                    mView.setFavoriteImage(R.drawable.icon_menu_favorites_unset);
                                    mContainerView.setRightToolbarButtonImageResource(R.drawable.icon_menu_favorites_unset);
                                } else {
                                    mView.setFavoriteImage(R.drawable.icon_menu_favorites_set);
                                    mContainerView.setRightToolbarButtonImageResource(R.drawable.icon_menu_favorites_set);
                                }
                            });
                })
                .doFinally(() -> mContainerView.hideLoadingIndicator())
                .subscribe();
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
