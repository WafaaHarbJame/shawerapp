package com.shawerapp.android.screens.selectsubsubject;

import android.os.Bundle;

import com.shawerapp.android.R;
import com.shawerapp.android.autovalue.Field;
import com.shawerapp.android.autovalue.SubSubjectEvent;
import com.shawerapp.android.backend.base.RealTimeDataFramework;
import com.shawerapp.android.base.BaseFragment;
import com.shawerapp.android.autovalue.SubSubject;
import com.shawerapp.android.screens.container.ContainerContract;
import com.shawerapp.android.screens.selectlawyer.SelectLawyerKey;
import com.shawerapp.android.screens.composer.ComposerKey;
import com.shawerapp.android.utils.CommonUtils;
import com.shawerapp.android.utils.LocaleHelper;
import com.trello.rxlifecycle2.android.FragmentEvent;

import javax.inject.Inject;

import io.reactivex.functions.Consumer;
import io.reactivex.processors.BehaviorProcessor;

import static com.shawerapp.android.screens.composer.ComposerKey.QUESTION;
import static com.shawerapp.android.screens.selectsubsubject.SelectSubSubjectFragment.ARG_REQUEST_TYPE;
import static com.shawerapp.android.screens.selectsubsubject.SelectSubSubjectFragment.ARG_SELECTED_FIELD;

public final class SelectSubSubjectViewModel implements SelectSubSubjectContract.ViewModel {

    private BaseFragment mFragment;

    private SelectSubSubjectContract.View mView;

    @Inject
    ContainerContract.View mContainerView;

    @Inject
    ContainerContract.ViewModel mContainerViewModel;

    @Inject
    RealTimeDataFramework mRTDataFramework;

    private int mRequestType;

    private Field mSelectedField;

    private BehaviorProcessor<SubSubjectEvent> mSubSubjectProcessor = BehaviorProcessor.create();

    private Long mPracticeRequestCost;

    @Inject
    public SelectSubSubjectViewModel(BaseFragment fragment, SelectSubSubjectContract.View view) {
        mFragment = fragment;
        mView = view;

        Bundle args = fragment.getArguments();
        mRequestType = args.getInt(ARG_REQUEST_TYPE);
        mSelectedField = args.getParcelable(ARG_SELECTED_FIELD);
    }

    @Override
    public void onViewCreated() {
        mView.initBindings();

        mSubSubjectProcessor.serialize()
                .compose(mFragment.bindUntilEvent(FragmentEvent.DESTROY_VIEW))
                .subscribe(subSubjectEventConsumer(), mContainerViewModel.catchErrorThrowable());
    }

    private Consumer<? super SubSubjectEvent> subSubjectEventConsumer() {
        return subSubjectEvent -> {
            switch (subSubjectEvent.type()) {
                case RealTimeDataFramework.EVENT_ADDED:
                    mView.addItem(subSubjectEvent.subSubject(), mRequestType == QUESTION ? null : mPracticeRequestCost);
                    break;
                case RealTimeDataFramework.EVENT_UPDATED:
                    mView.updateItem(subSubjectEvent.subSubject(), mRequestType == QUESTION ? null : mPracticeRequestCost);
                    break;
                case RealTimeDataFramework.EVENT_REMOVED:
                    mView.removeItem(subSubjectEvent.subSubject(), mRequestType == QUESTION ? null : mPracticeRequestCost);
                    break;
            }
        };
    }

    @Override
    public void onAfterEnterAnimation() {
        mRTDataFramework.retrievePracticeRequestCost()
                .compose(mFragment.bindUntilEvent(FragmentEvent.DESTROY_VIEW))
                .doOnSubscribe(disposable -> mContainerView.showLoadingIndicator())
                .doFinally(() -> mContainerView.hideLoadingIndicator())
                .subscribe(practiceRequestCost -> {
                    mPracticeRequestCost = practiceRequestCost;

                    mRTDataFramework.retrieveSubSubjectForField(mSelectedField)
                            .compose(mFragment.bindUntilEvent(FragmentEvent.DESTROY_VIEW))
                            .subscribe(mSubSubjectProcessor::onNext, mContainerViewModel.catchErrorThrowable());
                });
    }

    @Override
    public void onBackButtonClicked() {
        mContainerViewModel.goBack()
                .subscribe(mContainerViewModel.navigationObserver());

    }

    @Override
    public void setupToolbar() {
        mContainerView.clearToolbarSubtitle();
        if (LocaleHelper.getLanguage(mFragment.getContext()).equalsIgnoreCase(LocaleHelper.ARABIC)) {
            mContainerView.setToolbarTitle(mSelectedField.ar_fieldName());
        } else {
            mContainerView.setToolbarTitle(mSelectedField.fieldName());
        }
        mContainerView.setToolbarSubTitle(mFragment.getString(R.string.label_subtitle_select_subsubject));
    }

    @Override
    public void onLeftToolbarButtonClicked() {
        onBackButtonClicked();
    }

    @Override
    public void onRightToolbarButtonClicked() {

    }

    @Override
    public boolean onSubSubjectClicked(SubSubject subSubject) {
        if (mRequestType == QUESTION) {
            mContainerViewModel.goTo(SelectLawyerKey.create(mRequestType, mSelectedField, subSubject))
                    .subscribe(mContainerViewModel.navigationObserver());
        } else {
            mContainerViewModel.goTo(ComposerKey.builder()
                    .requestType(mRequestType)
                    .selectedField(mSelectedField)
                    .selectedSubSubject(subSubject)
                    .selectedLawyerUser(null)
                    .build())
                    .subscribe(mContainerViewModel.navigationObserver());
        }
        return false;
    }

    @Override
    public void onSearchTextChanged(String keyword) {
        if (CommonUtils.isNotEmpty(keyword)) {
            mView.filterList(keyword);
        } else {
            mView.clearFilters();
        }
    }

    @Override
    public void onStart() {
        mContainerView.setLeftToolbarTextResource(R.string.back);
        mContainerView.setLeftToolbarButtonImageResource(-1);
        mContainerView.setRightToolbarButtonImageResource(-1);
    }

    @Override
    public void onStop() {
        mContainerView.hideLeftText();
    }

    @Override
    public void onDetach() {

    }

    @Override
    public void onDestroy() {

    }
}
