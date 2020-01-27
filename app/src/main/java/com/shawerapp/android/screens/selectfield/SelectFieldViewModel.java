package com.shawerapp.android.screens.selectfield;

import android.app.AlertDialog;

import com.shawerapp.android.R;
import com.shawerapp.android.autovalue.FieldEvent;
import com.shawerapp.android.backend.base.AuthFramework;
import com.shawerapp.android.backend.base.RealTimeDataFramework;
import com.shawerapp.android.base.BaseFragment;
import com.shawerapp.android.autovalue.Field;
import com.shawerapp.android.screens.container.ContainerContract;
import com.shawerapp.android.screens.selectsubsubject.SelectSubSubjectKey;
import com.shawerapp.android.utils.CommonUtils;
import com.shawerapp.android.utils.LoginUtil;
import com.trello.rxlifecycle2.android.FragmentEvent;

import javax.inject.Inject;

import io.reactivex.functions.Consumer;
import io.reactivex.processors.BehaviorProcessor;

import static com.shawerapp.android.screens.selectfield.SelectFieldFragment.ARG_REQUEST_TYPE;
import static com.shawerapp.android.screens.composer.ComposerKey.PRACTICE;
import static com.shawerapp.android.screens.composer.ComposerKey.QUESTION;

public final class SelectFieldViewModel implements SelectFieldContract.ViewModel {

    private BaseFragment mFragment;

    private SelectFieldContract.View mView;

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

    private BehaviorProcessor<FieldEvent> mFieldProcessor = BehaviorProcessor.create();

    private int mRequestType;

    @Inject
    public SelectFieldViewModel(BaseFragment fragment,
                                SelectFieldContract.View view) {
        mFragment = fragment;
        mView = view;
        mRequestType = fragment.getArguments().getInt(ARG_REQUEST_TYPE);
    }

    @Override
    public void onViewCreated() {
        mView.initBindings();

        mFieldProcessor.serialize()
                .compose(mFragment.bindUntilEvent(FragmentEvent.DESTROY_VIEW))
                .subscribe(fieldEventConsumer(), mContainerViewModel.catchErrorThrowable());
    }

    private Consumer<? super FieldEvent> fieldEventConsumer() {
        return fieldEvent -> {
            switch (fieldEvent.type()) {
                case RealTimeDataFramework.EVENT_ADDED:
                    mView.addItem(fieldEvent.field());
                    break;
                case RealTimeDataFramework.EVENT_UPDATED:
                    mView.updateItem(fieldEvent.field());
                    break;
                case RealTimeDataFramework.EVENT_REMOVED:
                    mView.removeItem(fieldEvent.field());
                    break;
            }
        };
    }

    @Override
    public void onAfterEnterAnimation() {
        mRTDataFramework.retrieveFields()
                .compose(mFragment.bindUntilEvent(FragmentEvent.DESTROY_VIEW))
                .subscribe(mFieldProcessor::onNext, mContainerViewModel.catchErrorThrowable());
    }

    @Override
    public void onBackButtonClicked() {
        AlertDialog.Builder builder = new AlertDialog.Builder(mFragment.getContext(), R.style.DialogTheme);
        builder.setTitle(R.string.title_confirm);
        builder.setMessage(R.string.message_exit_compose);
        builder.setPositiveButton(R.string.ok_caps, (dialog, which) -> {
            dialog.dismiss();
//            mContainerView.setRightToolbarButtonImageResource(R.string.empty);
            mContainerViewModel
                    .goBack()
                    .subscribe(mContainerViewModel.navigationObserver());
        });
        builder.setNegativeButton(R.string.cancel, (dialog, which) -> dialog.dismiss());
        builder.show();
    }

    @Override
    public void setupToolbar() {
        mContainerView.clearToolbarSubtitle();
        if (mRequestType == QUESTION) {
            mContainerView.setToolbarTitle(mFragment.getString(R.string.label_title_new_question));
        } else if (mRequestType == PRACTICE) {
            mContainerView.setToolbarTitle(mFragment.getString(R.string.label_title_new_request));
        }
        mContainerView.setToolbarSubTitle(mFragment.getString(R.string.label_subtitle_field_selection));
    }

    @Override
    public void onLeftToolbarButtonClicked() {
        onBackButtonClicked();
    }

    @Override
    public void onRightToolbarButtonClicked() {

    }

    @Override
    public void onFieldClicked(Field field) {
        mContainerViewModel.goTo(SelectSubSubjectKey.create(mRequestType, field))
                .subscribe(mContainerViewModel.navigationObserver());
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
