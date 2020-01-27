package com.shawerapp.android.screens.answerlist;

import android.content.Context;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;

import com.jakewharton.rxbinding2.view.RxView;
import com.jakewharton.rxbinding2.widget.RxTextView;
import com.shawerapp.android.R;
import com.shawerapp.android.adapter.SubSubjectCaseAdapter;
import com.shawerapp.android.adapter.item.QuestionFlexible;
import com.shawerapp.android.autovalue.Question;
import com.shawerapp.android.base.BaseFragment;
import com.shawerapp.android.base.FragmentLifecycle;
import com.shawerapp.android.screens.container.ContainerActivity;
import com.shawerapp.android.utils.CommonUtils;
import com.trello.rxlifecycle2.android.FragmentEvent;

import eu.davidea.flexibleadapter.FlexibleAdapter;
import eu.davidea.flexibleadapter.Payload;
import eu.davidea.flexibleadapter.items.IFlexible;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Action;
import timber.log.Timber;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

public final class AnswerListFragment extends BaseFragment
        implements AnswerListContract.View, FlexibleAdapter.OnItemClickListener {

    public static AnswerListFragment newInstance() {
        return new AnswerListFragment();
    }

    @Inject
    AnswerListContract.ViewModel mViewModel;

    @BindView(R.id.recyclerView)
    RecyclerView mQuestionList;

    @BindView(R.id.searchView)
    EditText mSearchView;

    @BindView(R.id.filterImageView)
    ImageButton mFilterImageView;

    @BindView(R.id.btn_request_shawer)
    Button btnRequestShawer;

    @BindView(R.id.empty_answer)
    TextView emptyAnswer;

    private FlexibleAdapter<QuestionFlexible> mQuestionAdapter;

    @Override
    protected FragmentLifecycle.ViewModel getViewModel() {
        return mViewModel;
    }

    @Override
    public void onAttach(Context context) {
        DaggerAnswerListComponent.builder()
                .containerComponent(((ContainerActivity) context).getContainerComponent())
                .answerListModule(new AnswerListModule(this, this))
                .build()
                .inject(this);
        super.onAttach(context);
    }

    @Override
    public void initBindings() {
        mQuestionAdapter = new SubSubjectCaseAdapter(new ArrayList<>(), this);
        mQuestionAdapter.addListener(this);
        mQuestionAdapter.setNotifyMoveOfFilteredItems(true);
        mQuestionList.setItemAnimator(new DefaultItemAnimator());
        mQuestionList.setLayoutManager(new LinearLayoutManager(getContext()));
        mQuestionList.setAdapter(mQuestionAdapter);

        RxTextView.afterTextChangeEvents(mSearchView)
                .compose(bindUntilEvent(FragmentEvent.DESTROY_VIEW))
                .throttleLast(700, TimeUnit.MILLISECONDS)
                .subscribe(textChangeEvent -> mViewModel.onSearchTextChanged(textChangeEvent.editable().toString()));

        RxView.clicks(mFilterImageView)
                .compose(bindUntilEvent(FragmentEvent.DESTROY_VIEW))
                .throttleFirst(1, TimeUnit.SECONDS)
                .subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(o -> mViewModel.onFilterButtonClicked());

        RxView.clicks(btnRequestShawer)
                .compose(bindUntilEvent(FragmentEvent.DESTROY_VIEW))
                .throttleFirst(1, TimeUnit.SECONDS)
                .subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(o -> mViewModel.onRightToolbarButtonClicked());

    }

    @Override
    public void addItem(Question question) {
        QuestionFlexible item = new QuestionFlexible(question);
        if (!mQuestionAdapter.contains(item)) {
            mQuestionAdapter.addItem(item);
        }

        if (emptyAnswer.getVisibility() == View.VISIBLE) {
            emptyAnswer.setVisibility(View.GONE);
            mQuestionList.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void updateItem(Question question) {
        QuestionFlexible item = new QuestionFlexible(question);
        if (mQuestionAdapter.contains(item)) {
            mQuestionAdapter.updateItem(item, Payload.CHANGE);
        }
    }

    @Override
    public void removeItem(Question question) {
        QuestionFlexible item = new QuestionFlexible(question);
        int position = mQuestionAdapter.getGlobalPositionOf(item);
        if (position != -1) {
            mQuestionAdapter.removeItem(position);
        }
    }

    @Override
    public void filterList(String keyword) {
        mQuestionAdapter.setFilter(keyword.toString());
        mQuestionAdapter.filterItems();
    }

    @Override
    public void clearFilters() {
        mQuestionAdapter.setFilter(null);
        mQuestionAdapter.filterItems();
    }

    @Override
    public void sortList(Comparator<IFlexible> comparator) {
        try {
            ((Action) () -> {
                List<QuestionFlexible> currentItemsInList = new ArrayList<>(mQuestionAdapter.getCurrentItems());
                Collections.sort(currentItemsInList, comparator);
                mQuestionAdapter.updateDataSet(currentItemsInList, true);
            }).run();
        } catch (Exception e) {
            Timber.e(CommonUtils.getExceptionString(e));
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_default_list, container, false);
        mUnbinder = ButterKnife.bind(this, view);

        return view;
    }

    @Override
    public boolean onItemClick(View view, int position) {
        final QuestionFlexible item = mQuestionAdapter.getItem(position);
        if (item != null) {
            mViewModel.onQuestionClicked(item.getQuestion());
        }

        return false;
    }
}