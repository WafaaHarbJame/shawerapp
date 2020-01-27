package com.shawerapp.android.screens.requestlist;

import androidx.appcompat.app.AlertDialog;
import android.widget.ListView;

import com.shawerapp.android.R;
import com.shawerapp.android.adapter.item.PracticeRequestFlexible;
import com.shawerapp.android.autovalue.PracticeRequestEvent;
import com.shawerapp.android.autovalue.PracticeRequest;
import com.shawerapp.android.backend.base.RealTimeDataFramework;
import com.shawerapp.android.base.BaseFragment;
import com.shawerapp.android.screens.composer.ComposerKey;
import com.shawerapp.android.screens.container.ContainerContract;
import com.shawerapp.android.screens.selectfield.SelectFieldKey;
import com.shawerapp.android.screens.viewrequest.ViewCaseRequestKey;
import com.shawerapp.android.utils.CommonUtils;
import com.shawerapp.android.utils.LocaleHelper;
import com.shawerapp.android.utils.TooltipUtils;
import com.trello.rxlifecycle2.android.FragmentEvent;

import java.util.Comparator;
import java.util.Objects;

import javax.inject.Inject;

import eu.davidea.flexibleadapter.items.IFlexible;
import io.reactivex.functions.Consumer;
import io.reactivex.processors.BehaviorProcessor;
import it.sephiroth.android.library.tooltip.Tooltip;

public class RequestListViewModel implements RequestListContract.ViewModel {

    private static final int FILTER_SUBSUBJECT_ASCENDING = 0;

    private static final int FILTER_SUBSUBJECT_DESCENDING = 1;

    private static final int FILTER_STATUS = 2;

    private BaseFragment mFragment;

    private RequestListContract.View mView;

    @Inject
    ContainerContract.View mContainerView;

    @Inject
    ContainerContract.ViewModel mContainerViewModel;

    @Inject
    RealTimeDataFramework mRTDataFramework;

    private BehaviorProcessor<PracticeRequestEvent> mPracticeRequestProcessor = BehaviorProcessor.create();

    private int mSelectedFilter = FILTER_STATUS;

    @Inject
    public RequestListViewModel(BaseFragment fragment,
                                RequestListContract.View view) {
        mFragment = fragment;
        mView = view;
    }

    @Override
    public void onViewCreated() {
        mView.initBindings();

        mPracticeRequestProcessor
                .serialize()
                .compose(mFragment.bindUntilEvent(FragmentEvent.DESTROY_VIEW))
                .subscribe(practiceRequestEventConsumer(), mContainerViewModel.catchErrorThrowable());

        mContainerViewModel.hideRightToolbarButton();
        mContainerViewModel.hideRightToolbarButton();
        mContainerView.hideRightToolbarButton();
    }

    private Consumer<? super PracticeRequestEvent> practiceRequestEventConsumer() {
        return practiceRequestEvent -> {
            switch (practiceRequestEvent.type()) {
                case RealTimeDataFramework.EVENT_ADDED:
                    mView.addItem(practiceRequestEvent.practiceRequest());
                    break;
                case RealTimeDataFramework.EVENT_UPDATED:
                    mView.updateItem(practiceRequestEvent.practiceRequest());
                    break;
                case RealTimeDataFramework.EVENT_REMOVED:
                    mView.removeItem(practiceRequestEvent.practiceRequest());
                    break;
            }
        };
    }

    @Override
    public void onAfterEnterAnimation() {
        mRTDataFramework.retrievePracticeRequestsSent()
                .compose(mFragment.bindUntilEvent(FragmentEvent.DESTROY_VIEW))
                .subscribe(mPracticeRequestProcessor::onNext, mContainerViewModel.catchErrorThrowable());

//        TooltipUtils
//                .show(mFragment.getContext(),
//                        TooltipUtils.SHOT_REQUEST_LAWYER,
//                        mContainerView.getRightToolbarButton(),
//                        Tooltip.Gravity.LEFT,
//                        mFragment.getString(R.string.tooltip_request_lawyer))
//                .subscribe(isConsumed -> {
//                    if (isConsumed) {
//                        TooltipUtils.setShot(mFragment.getContext(), TooltipUtils.SHOT_REQUEST_LAWYER);
//                    }
//                });
    }

    @Override
    public void onBackButtonClicked() {

    }

    @Override
    public void setupToolbar() {
        mContainerView.clearToolbarSubtitle();
        mContainerView.setToolbarTitle(mFragment.getString(R.string.label_title_requesting_shawer));
        mContainerView.setLeftToolbarButtonImageResource(-1);
        mContainerView.setRightToolbarButtonImageResource(R.drawable.icon_new_practice_requests);
    }

    @Override
    public void onLeftToolbarButtonClicked() {

    }

    @Override
    public void onRightToolbarButtonClicked() {
        mContainerViewModel.goTo(SelectFieldKey.create(ComposerKey.PRACTICE))
                .subscribe(mContainerViewModel.navigationObserver());
    }

    @Override
    public void onRequestClicked(PracticeRequest practiceRequest) {
        mContainerViewModel.goTo(ViewCaseRequestKey.create(practiceRequest))
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
    public void onFilterButtonClicked() {
        AlertDialog.Builder builder = new AlertDialog.Builder(mFragment.getContext(), R.style.DialogTheme);
        builder.setTitle(R.string.sort_by);
        builder.setSingleChoiceItems(new CharSequence[]{
                        mFragment.getString(R.string.subsubject_name_ascending),
                        mFragment.getString(R.string.subsubject_name_descending),
                        mFragment.getString(R.string.status)},
                mSelectedFilter, null);
        builder.setPositiveButton(mFragment.getString(R.string.ok_caps), (dialog, which) -> {
            AlertDialog dialog1 = (AlertDialog) dialog;
            ListView listView = dialog1.getListView();
            int selectedPosition = listView.getCheckedItemPosition();
            mSelectedFilter = selectedPosition;
            mView.sortList(getComparator(selectedPosition));
        });
        builder.show();
    }

    @Override
    public Comparator<IFlexible> getComparator(int filter) {
        switch (filter) {
            case FILTER_SUBSUBJECT_ASCENDING:
                return (o1, o2) -> {
                    PracticeRequestFlexible item1 = (PracticeRequestFlexible) o1;
                    PracticeRequestFlexible item2 = (PracticeRequestFlexible) o2;

                    PracticeRequest practiceRequest1 = item1.getPracticeRequest();
                    PracticeRequest practiceRequest2 = item2.getPracticeRequest();

                    if (LocaleHelper.getLanguage(mFragment.getContext()).equalsIgnoreCase(LocaleHelper.ARABIC)) {
                        return Objects.requireNonNull(practiceRequest1.ar_subSubjectName()).compareToIgnoreCase(practiceRequest2.ar_subSubjectName());
                    } else {
                        return Objects.requireNonNull(practiceRequest1.subSubjectName()).compareToIgnoreCase(practiceRequest2.subSubjectName());
                    }
                };
            case FILTER_SUBSUBJECT_DESCENDING:
                return (o1, o2) -> {
                    PracticeRequestFlexible item1 = (PracticeRequestFlexible) o1;
                    PracticeRequestFlexible item2 = (PracticeRequestFlexible) o2;

                    PracticeRequest practiceRequest1 = item1.getPracticeRequest();
                    PracticeRequest practiceRequest2 = item2.getPracticeRequest();

                    if (LocaleHelper.getLanguage(mFragment.getContext()).equalsIgnoreCase(LocaleHelper.ARABIC)) {
                        return Objects.requireNonNull(practiceRequest2.ar_subSubjectName()).compareToIgnoreCase(practiceRequest1.ar_subSubjectName());
                    } else {
                        return Objects.requireNonNull(practiceRequest2.subSubjectName()).compareToIgnoreCase(practiceRequest1.subSubjectName());
                    }
                };
            default:
                return (o1, o2) -> {
                    PracticeRequestFlexible item1 = (PracticeRequestFlexible) o1;
                    PracticeRequestFlexible item2 = (PracticeRequestFlexible) o2;

                    PracticeRequest practiceRequest1 = item1.getPracticeRequest();
                    PracticeRequest practiceRequest2 = item2.getPracticeRequest();

                    return Objects.requireNonNull(practiceRequest1.status()).compareToIgnoreCase(practiceRequest2.status());
                };
        }
    }

    @Override
    public int getSelectedFilter() {
        return mSelectedFilter;
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
