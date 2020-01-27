package com.shawerapp.android.screens.discover;

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
import android.widget.EditText;
import android.widget.ImageButton;

import butterknife.BindView;
import butterknife.ButterKnife;

import com.jakewharton.rxbinding2.view.RxView;
import com.jakewharton.rxbinding2.widget.RxTextView;
import com.shawerapp.android.R;
import com.shawerapp.android.adapter.DiscoverLawyerAdapter;
import com.shawerapp.android.adapter.item.DiscoverLawyerFlexible;
import com.shawerapp.android.autovalue.LawyerUser;
import com.shawerapp.android.base.BaseFragment;
import com.shawerapp.android.base.FragmentLifecycle;
import com.shawerapp.android.model.Country;
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

public final class DiscoverLawyerFragment extends BaseFragment
        implements DiscoverLawyerContract.View, FlexibleAdapter.OnItemClickListener, View.OnClickListener {

    public static DiscoverLawyerFragment newInstance() {
        return new DiscoverLawyerFragment();
    }

    @Inject
    DiscoverLawyerContract.ViewModel mViewModel;

    @BindView(R.id.recyclerView)
    RecyclerView mLawyerList;

    @BindView(R.id.searchView)
    EditText mSearchView;

    @BindView(R.id.filterImageView)
    ImageButton mFilterImageView;

    @BindView(R.id.filterByCountryImageView)
    ImageButton mFilterByCountryImageView;

    private DiscoverLawyerAdapter mLawyersAdapter;

    @Override
    protected FragmentLifecycle.ViewModel getViewModel() {
        return mViewModel;
    }

    @Override
    public void onAttach(Context context) {
        DaggerDiscoverLawyerComponent.builder()
                .containerComponent(((ContainerActivity) context).getContainerComponent())
                .discoverLawyerModule(new DiscoverLawyerModule(this, this))
                .build()
                .inject(this);
        super.onAttach(context);
    }

    @Override
    public void initBindings() {
        mLawyersAdapter = new DiscoverLawyerAdapter(new ArrayList<>(), this);
        mLawyersAdapter.addListener(this);
        mLawyersAdapter.setNotifyMoveOfFilteredItems(true);
        mLawyerList.setItemAnimator(new DefaultItemAnimator());
        mLawyerList.setLayoutManager(new LinearLayoutManager(getContext()));
        mLawyerList.setAdapter(mLawyersAdapter);

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

        RxView.clicks(mFilterByCountryImageView)
                .compose(bindUntilEvent(FragmentEvent.DESTROY_VIEW))
                .throttleFirst(1, TimeUnit.SECONDS)
                .subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(o -> mViewModel.onFilterByCountryButtonClicked());
    }

    @Override
    public void addItem(LawyerUser lawyerUser, String userID) {
        DiscoverLawyerFlexible item = new DiscoverLawyerFlexible(lawyerUser, userID);
        if (!mLawyersAdapter.contains(item)) {
            mLawyersAdapter.addItem(mLawyersAdapter.calculatePositionFor(item, mViewModel.getComparator(mViewModel.getSelectedFilter())), item);
        }
    }

    @Override
    public void updateItem(LawyerUser lawyerUser, String userID) {
        DiscoverLawyerFlexible item = new DiscoverLawyerFlexible(lawyerUser, userID);
        if (mLawyersAdapter.contains(item)) {
            mLawyersAdapter.updateItem(item, Payload.CHANGE);
        }
    }

    @Override
    public void removeItem(LawyerUser lawyerUser, String userID) {
        DiscoverLawyerFlexible item = new DiscoverLawyerFlexible(lawyerUser, userID);
        int position = mLawyersAdapter.getGlobalPositionOf(item);
        if (position != -1) {
            mLawyersAdapter.removeItem(position);
        }
    }

    @Override
    public void filterList(String keyword) {
        mLawyersAdapter.setFilter(keyword.toString());
        mLawyersAdapter.filterItems();
    }

    @Override
    public void clearFilters() {
        mLawyersAdapter.setFilter(null);
        mLawyersAdapter.filterItems();
    }

    @Override
    public void sortList(Comparator<IFlexible> comparator) {
        try {
            ((Action) () -> {
                List<DiscoverLawyerFlexible> currentItemsInList = new ArrayList<>(mLawyersAdapter.getCurrentItems());
                Collections.sort(currentItemsInList, comparator);
                mLawyersAdapter.updateDataSet(currentItemsInList, true);
            }).run();
        } catch (Exception e) {
            Timber.e(CommonUtils.getExceptionString(e));
        }
    }

    @Override
    public void showFavorites() {
        mLawyersAdapter.setFilter(true);
        mLawyersAdapter.filterItems();
    }

    @Override
    public void filterListByCountry(Country countrySelected) {
        mLawyersAdapter.setFilter(countrySelected);
        mLawyersAdapter.filterItems();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_discover_lawyer, container, false);
        mUnbinder = ButterKnife.bind(this, view);

        return view;
    }

    @Override
    public boolean onItemClick(View view, int position) {
        final DiscoverLawyerFlexible item = mLawyersAdapter.getItem(position);
        if (item != null) {
            mViewModel.onLawyerClicked(item.getLawyerUser());
        }

        return false;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.favoriteIndicatorImageView:
                LawyerUser lawyerUser = ((DiscoverLawyerFlexible) v.getTag()).getLawyerUser();
                mViewModel.onLawyerFavorited(lawyerUser);
                break;
        }
    }
}