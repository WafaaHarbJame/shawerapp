package com.shawerapp.android.screens.questionlist;

import androidx.appcompat.app.AlertDialog;
import android.widget.ListView;

import com.shawerapp.android.R;
import com.shawerapp.android.adapter.item.QuestionAssignedVerticalFlexible;
import com.shawerapp.android.autovalue.Question;
import com.shawerapp.android.autovalue.QuestionEvent;
import com.shawerapp.android.backend.base.RealTimeDataFramework;
import com.shawerapp.android.base.BaseFragment;
import com.shawerapp.android.screens.composer.ComposerKey;
import com.shawerapp.android.screens.container.ContainerContract;
import com.shawerapp.android.screens.questiondetails.QuestionDetailsKey;
import com.shawerapp.android.screens.selectfield.SelectFieldKey;
import com.shawerapp.android.utils.CommonUtils;
import com.shawerapp.android.utils.LocaleHelper;
import com.trello.rxlifecycle2.android.FragmentEvent;

import java.util.Comparator;
import java.util.Objects;

import javax.inject.Inject;

import eu.davidea.flexibleadapter.items.IFlexible;
import io.reactivex.functions.Consumer;
import io.reactivex.processors.BehaviorProcessor;

public class QuestionListViewModel implements QuestionListContract.ViewModel {

    private static final int FILTER_SUBSUBJECT_ASCENDING = 0;

    private static final int FILTER_SUBSUBJECT_DESCENDING = 1;

    private static final int FILTER_COST_DESCENDING = 2;

    private static final int FILTER_COST_ASCENDING = 3;

    private static final int FILTER_STATUS = 4;

    private BaseFragment mFragment;

    private QuestionListContract.View mView;

    @Inject
    ContainerContract.View mContainerView;

    @Inject
    ContainerContract.ViewModel mContainerViewModel;

    @Inject
    RealTimeDataFramework mRTDataFramework;

    private BehaviorProcessor<QuestionEvent> mQuestionProcessor = BehaviorProcessor.create();

    private int mSelectedFilter = FILTER_STATUS;

    @Inject
    public QuestionListViewModel(BaseFragment fragment,
                                 QuestionListContract.View view) {
        mFragment = fragment;
        mView = view;
    }

    @Override
    public void onViewCreated() {
        mView.initBindings();

        mQuestionProcessor
                .serialize()
                .compose(mFragment.bindUntilEvent(FragmentEvent.DESTROY_VIEW))
                .subscribe(questionEventConsumer(), mContainerViewModel.catchErrorThrowable());
    }

    private Consumer<? super QuestionEvent> questionEventConsumer() {
        return questionEvent -> {

                switch (questionEvent.type()) {
                    case RealTimeDataFramework.EVENT_ADDED:
                        mView.addItem(questionEvent.question());
                        break;
                    case RealTimeDataFramework.EVENT_UPDATED:
                        mView.updateItem(questionEvent.question());
                        break;
                    case RealTimeDataFramework.EVENT_REMOVED:
                        mView.removeItem(questionEvent.question());
                        break;
                }
        };
    }

    @Override
    public void onAfterEnterAnimation() {
        mRTDataFramework.retrieveQuestionsReceived()
                .compose(mFragment.bindUntilEvent(FragmentEvent.DESTROY_VIEW))
                .subscribe(mQuestionProcessor::onNext, mContainerViewModel.catchErrorThrowable());
    }

    @Override
    public void onBackButtonClicked() {

    }

    @Override
    public void setupToolbar() {
        mContainerView.clearToolbarSubtitle();
        mContainerView.setToolbarTitle(mFragment.getString(R.string.label_title_answering_shawer));
        mContainerView.setLeftToolbarButtonImageResource(-1);
        mContainerView.setRightToolbarButtonImageResource(-1);
    }

    @Override
    public void onLeftToolbarButtonClicked() {

    }

    @Override
    public void onRightToolbarButtonClicked() {
        mContainerViewModel.goTo(SelectFieldKey.create(ComposerKey.QUESTION))
                .subscribe(mContainerViewModel.navigationObserver());
    }

    @Override
    public void onTaskClicked(Question question) {
        mContainerViewModel.goTo(QuestionDetailsKey.create(question))
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
                        mFragment.getString(R.string.cost_descending),
                        mFragment.getString(R.string.cost_ascending),
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
                    QuestionAssignedVerticalFlexible item1 = (QuestionAssignedVerticalFlexible) o1;
                    QuestionAssignedVerticalFlexible item2 = (QuestionAssignedVerticalFlexible) o2;

                    Question question1 = item1.getQuestion();
                    Question question2 = item2.getQuestion();

                    if (LocaleHelper.getLanguage(mFragment.getContext()).equalsIgnoreCase(LocaleHelper.ARABIC)) {
                        return Objects.requireNonNull(question1.ar_subSubjectName()).compareToIgnoreCase(question2.ar_subSubjectName());
                    } else {
                        return Objects.requireNonNull(question1.subSubjectName()).compareToIgnoreCase(question2.subSubjectName());
                    }
                };
            case FILTER_SUBSUBJECT_DESCENDING:
                return (o1, o2) -> {
                    QuestionAssignedVerticalFlexible item1 = (QuestionAssignedVerticalFlexible) o1;
                    QuestionAssignedVerticalFlexible item2 = (QuestionAssignedVerticalFlexible) o2;

                    Question question1 = item1.getQuestion();
                    Question question2 = item2.getQuestion();

                    if (LocaleHelper.getLanguage(mFragment.getContext()).equalsIgnoreCase(LocaleHelper.ARABIC)) {
                        return Objects.requireNonNull(question2.ar_subSubjectName()).compareToIgnoreCase(question1.ar_subSubjectName());
                    } else {
                        return Objects.requireNonNull(question2.subSubjectName()).compareToIgnoreCase(question1.subSubjectName());
                    }
                };
            case FILTER_COST_DESCENDING:
                return (o1, o2) -> {
                    QuestionAssignedVerticalFlexible item1 = (QuestionAssignedVerticalFlexible) o1;
                    QuestionAssignedVerticalFlexible item2 = (QuestionAssignedVerticalFlexible) o2;

                    Question question1 = item1.getQuestion();
                    Question question2 = item2.getQuestion();

                    return Long.compare(Objects.requireNonNull(question2.serviceFee()), Objects.requireNonNull(question1.serviceFee()));
                };
            case FILTER_COST_ASCENDING:
                return (o1, o2) -> {
                    QuestionAssignedVerticalFlexible item1 = (QuestionAssignedVerticalFlexible) o1;
                    QuestionAssignedVerticalFlexible item2 = (QuestionAssignedVerticalFlexible) o2;

                    Question question1 = item1.getQuestion();
                    Question question2 = item2.getQuestion();

                    return Long.compare(Objects.requireNonNull(question1.serviceFee()), Objects.requireNonNull(question2.serviceFee()));
                };
            default:
                return (o1, o2) -> {
                    QuestionAssignedVerticalFlexible item1 = (QuestionAssignedVerticalFlexible) o1;
                    QuestionAssignedVerticalFlexible item2 = (QuestionAssignedVerticalFlexible) o2;

                    Question question1 = item1.getQuestion();
                    Question question2 = item2.getQuestion();

                    return Objects.requireNonNull(question1.status()).compareToIgnoreCase(question2.status());
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
