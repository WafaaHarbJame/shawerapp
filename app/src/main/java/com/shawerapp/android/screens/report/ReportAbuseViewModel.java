package com.shawerapp.android.screens.report;

import android.app.AlertDialog;
import androidx.core.util.Pair;

import com.shawerapp.android.R;
import com.shawerapp.android.autovalue.LawyerUser;
import com.shawerapp.android.autovalue.UserEvent;
import com.shawerapp.android.backend.base.RealTimeDataFramework;
import com.shawerapp.android.backend.base.RestFramework;
import com.shawerapp.android.base.BaseFragment;
import com.shawerapp.android.screens.container.ContainerContract;
import com.shawerapp.android.utils.CommonUtils;
import com.shawerapp.android.utils.LoginUtil;
import com.trello.rxlifecycle2.android.FragmentEvent;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.Flowable;
import io.reactivex.functions.Consumer;
import io.reactivex.processors.BehaviorProcessor;

import static com.shawerapp.android.backend.base.RealTimeDataFramework.EVENT_ADDED;
import static com.shawerapp.android.backend.base.RealTimeDataFramework.EVENT_REMOVED;
import static com.shawerapp.android.backend.base.RealTimeDataFramework.EVENT_UPDATED;

public class ReportAbuseViewModel implements ReportAbuseContract.ViewModel {

    private BaseFragment mFragment;

    private ReportAbuseContract.View mView;

    @Inject
    ContainerContract.View mContainerView;

    @Inject
    ContainerContract.ViewModel mContainerViewModel;

    @Inject
    LoginUtil mLoginUtil;

    @Inject
    RealTimeDataFramework mRTDataFramework;

    @Inject
    RestFramework mRestFramework;

    private BehaviorProcessor<UserEvent> mUserProcessor = BehaviorProcessor.create();

    @Inject
    public ReportAbuseViewModel(BaseFragment fragment, ReportAbuseContract.View view) {
        mFragment = fragment;
        mView = view;
    }

    @Override
    public void onViewCreated() {
        mView.initBindings(!mLoginUtil.getUserRole().equalsIgnoreCase(LawyerUser.ROLE_VALUE));

        mUserProcessor
                .serialize()
                .compose(mFragment.bindUntilEvent(FragmentEvent.DESTROY_VIEW))
                .subscribe(userEventConsumer(), mContainerViewModel.catchErrorThrowable());
    }

    private Consumer<? super UserEvent> userEventConsumer() {
        return userEvent -> {
            switch (userEvent.type()) {
                case EVENT_ADDED:
                    mView.addItem(userEvent.user(), mLoginUtil.getUserID());
                    break;
                case EVENT_UPDATED:
                    mView.updateItem(userEvent.user(), mLoginUtil.getUserID());
                    break;
                case EVENT_REMOVED:
                    mView.removeItem(userEvent.user(), mLoginUtil.getUserID());
                    break;
            }
        };
    }

    @Override
    public void onAfterEnterAnimation() {
        Flowable<UserEvent> retrieveUserList;
        if (mLoginUtil.getUserRole().equals(LawyerUser.ROLE_VALUE)) {
            retrieveUserList = Flowable.merge(
                    mRTDataFramework.retrieveIndividualUsers(),
                    mRTDataFramework.retrieveCommercialUsers());
        } else {
            retrieveUserList = mRTDataFramework.retrieveLawyerUsers();
        }

        retrieveUserList
                .compose(mFragment.bindUntilEvent(FragmentEvent.DESTROY_VIEW))
                .subscribe(mUserProcessor::onNext, mContainerViewModel.catchErrorThrowable());
    }

    @Override
    public void onBackButtonClicked() {
        mContainerViewModel.goBack()
                .subscribe(mContainerViewModel.navigationObserver());
    }

    @Override
    public void setupToolbar() {
        mContainerView.clearToolbarSubtitle();
        mContainerView.setToolbarTitle(mFragment.getString(R.string.label_report_title));
        mContainerView.setToolbarSubTitle(mFragment.getString(R.string.label_report_subtitle));
        mContainerView.setLeftToolbarButtonImageResource(R.drawable.icon_back);
        mContainerView.setRightToolbarButtonImageResource(R.drawable.icon_check_light);
    }

    @Override
    public void onLeftToolbarButtonClicked() {
        onBackButtonClicked();
    }

    @Override
    public void onRightToolbarButtonClicked() {
        List<Object> selectedUsers = mView.getSelectedUsers();

        mRTDataFramework
                .reportUsers(selectedUsers)
                .doOnSubscribe(subscription -> mContainerView.showLoadingIndicator())
                .doFinally(() -> mContainerView.hideLoadingIndicator())
                .subscribe(() -> {
                    AlertDialog.Builder builder = new AlertDialog.Builder(mFragment.getContext(), R.style.DialogTheme);
                    builder.setTitle(R.string.title_report_user);
                    builder.setMessage(R.string.message_report_user);
                    builder.setPositiveButton(mFragment.getString(R.string.ok_caps), (dialog, which) -> {
                        dialog.dismiss();
                        onBackButtonClicked();
                    });
                    builder.show();
                }, mContainerViewModel.catchErrorThrowable());
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
