package com.shawerapp.android.screens.requestlist;

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
import eu.davidea.flexibleadapter.Payload;
import eu.davidea.flexibleadapter.items.IFlexible;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Action;
import timber.log.Timber;

import com.jakewharton.rxbinding2.view.RxView;
import com.jakewharton.rxbinding2.widget.RxTextView;
import com.shawerapp.android.R;
import com.shawerapp.android.adapter.PracticeRequestAdapter;
import com.shawerapp.android.adapter.item.PracticeRequestFlexible;
import com.shawerapp.android.base.BaseFragment;
import com.shawerapp.android.base.FragmentLifecycle;
import com.shawerapp.android.autovalue.PracticeRequest;
import com.shawerapp.android.screens.container.ContainerActivity;
import com.shawerapp.android.screens.container.ContainerContract;
import com.shawerapp.android.utils.CommonUtils;
import com.trello.rxlifecycle2.android.FragmentEvent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

public class RequestListFragment extends BaseFragment
        implements RequestListContract.View {

    public static RequestListFragment newInstance() {
        return new RequestListFragment();
    }

    @Inject
    RequestListContract.ViewModel mViewModel;

    @Inject
    ContainerContract.ViewModel mContainerViewModel;

    @BindView(R.id.recyclerView)
    RecyclerView mPracticeRequestList;

    @BindView(R.id.searchView)
    EditText mSearchView;

    @BindView(R.id.filterImageView)
    ImageButton mFilterImageView;

    @BindView(R.id.btn_request_practice)
    Button btnRequestPractice;

    @BindView(R.id.empty_practice)
    TextView emptyPractice;


    private PracticeRequestAdapter mPracticeRequestAdapter;

    @Override
    protected FragmentLifecycle.ViewModel getViewModel() {
        return mViewModel;
    }

    @Override
    public void onAttach(Context context) {
        DaggerRequestListComponent.builder()
                .containerComponent(((ContainerActivity) context).getContainerComponent())
                .requestListModule(new RequestListModule(this, this))
                .build()
                .inject(this);
        super.onAttach(context);
    }

    @Override
    public void initBindings() {
        mPracticeRequestAdapter = new PracticeRequestAdapter(new ArrayList<>(), this);
        mPracticeRequestAdapter.addListener(this);
        mPracticeRequestAdapter.setNotifyMoveOfFilteredItems(true);
        mPracticeRequestList.setItemAnimator(new DefaultItemAnimator());
        mPracticeRequestList.setLayoutManager(new LinearLayoutManager(getContext()));
        mPracticeRequestList.setAdapter(mPracticeRequestAdapter);

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

        RxView.clicks(btnRequestPractice)
                .compose(bindUntilEvent(FragmentEvent.DESTROY_VIEW))
                .throttleFirst(1, TimeUnit.SECONDS)
                .subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(o -> mViewModel.onRightToolbarButtonClicked());

    }

    @Override
    public void addItem(PracticeRequest question) {
        PracticeRequestFlexible item = new PracticeRequestFlexible(question);
        if (!mPracticeRequestAdapter.contains(item)) {
            mPracticeRequestAdapter.addItem(item);
        }
        if (emptyPractice.getVisibility() == View.VISIBLE) {
            emptyPractice.setVisibility(View.GONE);
            mPracticeRequestList.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void updateItem(PracticeRequest question) {
        PracticeRequestFlexible item = new PracticeRequestFlexible(question);
        if (mPracticeRequestAdapter.contains(item)) {
            mPracticeRequestAdapter.updateItem(item, Payload.CHANGE);
        }
    }

    @Override
    public void removeItem(PracticeRequest question) {
        PracticeRequestFlexible item = new PracticeRequestFlexible(question);
        int position = mPracticeRequestAdapter.getGlobalPositionOf(item);
        if (position != -1) {
            mPracticeRequestAdapter.removeItem(position);
        }
    }

    @Override
    public void filterList(String keyword) {
        mPracticeRequestAdapter.setFilter(keyword.toString());
        mPracticeRequestAdapter.filterItems();
    }

    @Override
    public void clearFilters() {
        mPracticeRequestAdapter.setFilter(null);
        mPracticeRequestAdapter.filterItems();
    }

    @Override
    public void sortList(Comparator<IFlexible> comparator) {
        try {
            ((Action) () -> {
                List<PracticeRequestFlexible> currentItemsInList = new ArrayList<>(mPracticeRequestAdapter.getCurrentItems());
                Collections.sort(currentItemsInList, comparator);
                mPracticeRequestAdapter.updateDataSet(currentItemsInList, true);
            }).run();
        } catch (Exception e) {
            Timber.e(CommonUtils.getExceptionString(e));
        }
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_request_shawer, container, false);
        mUnbinder = ButterKnife.bind(this, view);

        return view;
    }

    @Override
    public boolean onItemClick(View view, int position) {
        final PracticeRequestFlexible item = mPracticeRequestAdapter.getItem(position);
        if (item != null) {
            mViewModel.onRequestClicked(item.getPracticeRequest());
        }

        return false;
    }

    @Override
    public void onResume() {
        super.onResume();
        mContainerViewModel.hideRightToolbarButton();
    }
}
