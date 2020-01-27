package com.shawerapp.android.screens.selectlawyer;

import android.os.Bundle;

import com.shawerapp.android.R;
import com.shawerapp.android.autovalue.Field;
import com.shawerapp.android.autovalue.LawyerUser;
import com.shawerapp.android.autovalue.LawyerUserEvent;
import com.shawerapp.android.backend.base.RealTimeDataFramework;
import com.shawerapp.android.base.BaseFragment;
import com.shawerapp.android.autovalue.SubSubject;
import com.shawerapp.android.screens.composer.ComposerKey;
import com.shawerapp.android.screens.container.ContainerContract;
import com.shawerapp.android.utils.CommonUtils;
import com.shawerapp.android.utils.LocaleHelper;
import com.shawerapp.android.utils.LoginUtil;
import com.trello.rxlifecycle2.android.FragmentEvent;

import javax.inject.Inject;

import io.reactivex.functions.Consumer;
import io.reactivex.processors.BehaviorProcessor;

import static com.shawerapp.android.screens.selectlawyer.SelectLawyerFragment.ARG_REQUEST_TYPE;
import static com.shawerapp.android.screens.selectlawyer.SelectLawyerFragment.ARG_SELECTED_FIELD;
import static com.shawerapp.android.screens.selectlawyer.SelectLawyerFragment.ARG_SELECTED_SUBSUBJECT;

public final class SelectLawyerViewModel implements SelectLawyerContract.ViewModel {

    private BaseFragment mFragment;

    private SelectLawyerContract.View mView;

    @Inject
    ContainerContract.View mContainerView;

    @Inject
    ContainerContract.ViewModel mContainerViewModel;

    @Inject
    RealTimeDataFramework mRTDataFramwork;

    @Inject
    LoginUtil mLoginUtil;

    private int mRequestType;

    private Field mSelectedField;

    private SubSubject mSelectedSubSubject;

    private BehaviorProcessor<LawyerUserEvent> mLawyerUserProcessor = BehaviorProcessor.create();

    @Inject
    public SelectLawyerViewModel(BaseFragment fragment, SelectLawyerContract.View view) {
        mFragment = fragment;
        mView = view;

        Bundle args = fragment.getArguments();
        mRequestType = args.getInt(ARG_REQUEST_TYPE);
        mSelectedField = args.getParcelable(ARG_SELECTED_FIELD);
        mSelectedSubSubject = args.getParcelable(ARG_SELECTED_SUBSUBJECT);
    }

    @Override
    public void onViewCreated() {
        mView.initBindings();

        mLawyerUserProcessor
                .serialize()
                .compose(mFragment.bindUntilEvent(FragmentEvent.DESTROY_VIEW))
                .subscribe(lawyserUserEventConsumer(), mContainerViewModel.catchErrorThrowable());
    }

    private Consumer<? super LawyerUserEvent> lawyserUserEventConsumer() {
        return lawyerUserEvent -> {
            switch (lawyerUserEvent.type()) {
                case RealTimeDataFramework.EVENT_ADDED:
                    mView.addItem(lawyerUserEvent.lawyerUser(), mLoginUtil.getUserRole(), mSelectedSubSubject.uid(), mLoginUtil.getUserID());
                    break;
                case RealTimeDataFramework.EVENT_UPDATED:
                    mView.updateItem(lawyerUserEvent.lawyerUser(), mLoginUtil.getUserRole(), mSelectedSubSubject.uid(), mLoginUtil.getUserID());
                    break;
                case RealTimeDataFramework.EVENT_REMOVED:
                    mView.removeItem(lawyerUserEvent.lawyerUser(), mLoginUtil.getUserRole(), mSelectedSubSubject.uid(), mLoginUtil.getUserID());
                    break;
            }
        };
    }

    @Override
    public void onAfterEnterAnimation() {
        mRTDataFramwork.retrieveLawyersForSubSubject(mSelectedSubSubject)
                .compose(mFragment.bindUntilEvent(FragmentEvent.DESTROY_VIEW))
                .subscribe(mLawyerUserProcessor::onNext, mContainerViewModel.catchErrorThrowable());
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
            mContainerView.setToolbarTitle(mSelectedSubSubject.ar_subSubjectName());
        } else {
            mContainerView.setToolbarTitle(mSelectedSubSubject.subSubjectName());
        }
        mContainerView.setToolbarSubTitle(mFragment.getString(R.string.label_subtitle_select_lawyer));
    }

    @Override
    public void onLeftToolbarButtonClicked() {
        onBackButtonClicked();
    }

    @Override
    public void onRightToolbarButtonClicked() {

    }

    @Override
    public boolean onLawyerClicked(LawyerUser lawyerUser) {
        mContainerViewModel.goTo(ComposerKey.builder()
                .requestType(mRequestType)
                .selectedField(mSelectedField)
                .selectedSubSubject(mSelectedSubSubject)
                .selectedLawyerUser(lawyerUser)
                .build())
                .subscribe(mContainerViewModel.navigationObserver());
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
