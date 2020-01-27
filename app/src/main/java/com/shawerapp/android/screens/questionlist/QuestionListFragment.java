package com.shawerapp.android.screens.questionlist;

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
import com.shawerapp.android.adapter.item.QuestionAssignedVerticalFlexible;
import com.shawerapp.android.autovalue.Question;
import com.shawerapp.android.base.BaseFragment;
import com.shawerapp.android.base.FragmentLifecycle;
import com.shawerapp.android.screens.container.ContainerActivity;
import com.shawerapp.android.screens.container.ContainerContract;
import com.shawerapp.android.screens.profile.lawyer.personal.PrivateLawyerViewKey;
import com.shawerapp.android.screens.profile.user.view.ProfileViewKey;
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

import static com.shawerapp.android.screens.container.ContainerActivity.EXTRA_TYPE;
import static com.shawerapp.android.screens.container.ContainerActivity.TYPE_LAWYER;

public final class QuestionListFragment extends BaseFragment implements QuestionListContract.View {

    public static QuestionListFragment newInstance() {
        return new QuestionListFragment();
    }

    @Inject
    QuestionListContract.ViewModel mViewModel;

    @Inject
    ContainerContract.ViewModel mContainerViewModel;

    @BindView(R.id.recyclerView)
    RecyclerView mQuestionAssignedList;

    @BindView(R.id.searchView)
    EditText mSearchView;

    @BindView(R.id.filterImageView)
    ImageButton mFilterImageView;

    @BindView(R.id.btn_request_shawer)
    Button btnRequestShawer;

    @BindView(R.id.textView2)
    TextView textView2;

    @BindView(R.id.empty_answer)
    TextView empty_answer;

    private FlexibleAdapter<QuestionAssignedVerticalFlexible> mQuestionAssignedAdapter;

    @Override
    protected FragmentLifecycle.ViewModel getViewModel() {
        return mViewModel;
    }

    @Override
    public void onAttach(Context context) {
        DaggerQuestionListComponent.builder()
                .containerComponent(((ContainerActivity) context).getContainerComponent())
                .questionListModule(new QuestionListModule(this, this))
                .build()
                .inject(this);
        super.onAttach(context);
    }

    @Override
    public void initBindings() {
        mQuestionAssignedAdapter = new FlexibleAdapter<>(new ArrayList<>(), this);
        mQuestionAssignedAdapter.addListener(this);
        mQuestionAssignedAdapter.setNotifyMoveOfFilteredItems(true);
        mQuestionAssignedList.setItemAnimator(new DefaultItemAnimator());
        mQuestionAssignedList.setLayoutManager(new LinearLayoutManager(getContext()));
        mQuestionAssignedList.setAdapter(mQuestionAssignedAdapter);

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



        String mType = getActivity().getIntent().getStringExtra(EXTRA_TYPE);

        if (mType.equals(TYPE_LAWYER)) {
            btnRequestShawer.setVisibility(View.GONE);
            textView2.setText(R.string.list_of_consultations);
        } else {
            btnRequestShawer.setVisibility(View.VISIBLE);
            RxView.clicks(btnRequestShawer)
                    .compose(bindUntilEvent(FragmentEvent.DESTROY_VIEW))
                    .throttleFirst(1, TimeUnit.SECONDS)
                    .subscribeOn(AndroidSchedulers.mainThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(o -> mViewModel.onRightToolbarButtonClicked());
        }

    }

    @Override
    public void addItem(Question question) {
        QuestionAssignedVerticalFlexible item = new QuestionAssignedVerticalFlexible(question);
        if (!mQuestionAssignedAdapter.contains(item)) {
            mQuestionAssignedAdapter.addItem(item);
            mQuestionAssignedList.setVisibility(View.VISIBLE);
            empty_answer.setVisibility(View.GONE);
        }
    }

    @Override
    public void updateItem(Question question) {
        QuestionAssignedVerticalFlexible item = new QuestionAssignedVerticalFlexible(question);
        if (mQuestionAssignedAdapter.contains(item)) {
            mQuestionAssignedAdapter.updateItem(item, Payload.CHANGE);
        }
    }

    @Override
    public void removeItem(Question question) {
        QuestionAssignedVerticalFlexible item = new QuestionAssignedVerticalFlexible(question);
        int position = mQuestionAssignedAdapter.getGlobalPositionOf(item);
        if (position != -1) {
            mQuestionAssignedAdapter.removeItem(position);
        }
    }

    @Override
    public void filterList(String keyword) {
        mQuestionAssignedAdapter.setFilter(keyword.toString());
        mQuestionAssignedAdapter.filterItems();
    }

    @Override
    public void clearFilters() {
        mQuestionAssignedAdapter.setFilter(null);
        mQuestionAssignedAdapter.filterItems();
    }

    @Override
    public void sortList(Comparator<IFlexible> comparator) {
        try {
            ((Action) () -> {
                List<QuestionAssignedVerticalFlexible> currentItemsInList = new ArrayList<>(mQuestionAssignedAdapter.getCurrentItems());
                Collections.sort(currentItemsInList, comparator);
                mQuestionAssignedAdapter.updateDataSet(currentItemsInList, true);
            }).run();
        } catch (Exception e) {
            Timber.e(CommonUtils.getExceptionString(e));
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_default_list, container, false);
        mUnbinder = ButterKnife.bind(this, view);

        return view;
    }

    @Override
    public boolean onItemClick(View view, int position) {
        final QuestionAssignedVerticalFlexible item = mQuestionAssignedAdapter.getItem(position);
        if (item != null) {
            mViewModel.onTaskClicked(item.getQuestion());
        }

        return false;
    }

    @Override
    public void onResume() {
        super.onResume();
        mContainerViewModel.hideRightToolbarButton();
    }
}