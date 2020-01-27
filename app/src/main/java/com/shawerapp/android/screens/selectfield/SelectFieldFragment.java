package com.shawerapp.android.screens.selectfield;

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

import butterknife.BindView;
import butterknife.ButterKnife;
import eu.davidea.flexibleadapter.FlexibleAdapter;
import eu.davidea.flexibleadapter.Payload;

import com.jakewharton.rxbinding2.widget.RxTextView;
import com.shawerapp.android.R;
import com.shawerapp.android.adapter.FieldAdapter;
import com.shawerapp.android.adapter.item.FieldFlexible;
import com.shawerapp.android.autovalue.Field;
import com.shawerapp.android.base.BaseFragment;
import com.shawerapp.android.base.FragmentLifecycle;
import com.shawerapp.android.screens.container.ContainerActivity;
import com.trello.rxlifecycle2.android.FragmentEvent;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

public final class SelectFieldFragment extends BaseFragment implements SelectFieldContract.View, FlexibleAdapter.OnItemClickListener {

    public static final String ARG_REQUEST_TYPE = "requestType";

    public static SelectFieldFragment newInstance(int requestType) {

        Bundle args = new Bundle();
        args.putInt(ARG_REQUEST_TYPE, requestType);
        SelectFieldFragment fragment = new SelectFieldFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Inject
    SelectFieldContract.ViewModel mViewModel;

    @BindView(R.id.recyclerView)
    RecyclerView mFieldList;

    @BindView(R.id.searchView)
    EditText mSearchView;

    private FieldAdapter mFieldAdapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_select_field, container, false);
        mUnbinder = ButterKnife.bind(this, view);

        return view;
    }

    @Override
    public void onAttach(Context context) {
        DaggerSelectFieldComponent.builder()
                .containerComponent(((ContainerActivity) context).getContainerComponent())
                .selectFieldModule(new SelectFieldModule(this, this))
                .build()
                .inject(this);
        super.onAttach(context);
    }

    @Override
    protected FragmentLifecycle.ViewModel getViewModel() {
        return mViewModel;
    }

    @Override
    public void initBindings() {
        mFieldAdapter = new FieldAdapter(new ArrayList<>(), this);
        mFieldAdapter.setNotifyMoveOfFilteredItems(true);
        mFieldList.setItemAnimator(new DefaultItemAnimator());
        mFieldList.setLayoutManager(new LinearLayoutManager(getContext()));
        mFieldList.setAdapter(mFieldAdapter);

        RxTextView.afterTextChangeEvents(mSearchView)
                .compose(bindUntilEvent(FragmentEvent.DESTROY_VIEW))
                .throttleLast(700, TimeUnit.MILLISECONDS)
                .subscribe(textChangeEvent -> mViewModel.onSearchTextChanged(textChangeEvent.editable().toString()));
    }

    @Override
    public void addItem(Field field) {
        FieldFlexible item = new FieldFlexible(field);
        if (!mFieldAdapter.contains(item)) {
            mFieldAdapter.addItem(item);
        }
    }

    @Override
    public void updateItem(Field field) {
        FieldFlexible item = new FieldFlexible(field);
        if (mFieldAdapter.contains(item)) {
            mFieldAdapter.updateItem(item, Payload.CHANGE);
        }
    }

    @Override
    public void removeItem(Field field) {
        FieldFlexible item = new FieldFlexible(field);
        int position = mFieldAdapter.getGlobalPositionOf(item);
        if (position != -1) {
            mFieldAdapter.removeItem(position);
        }
    }

    @Override
    public void filterList(String keyword) {
        mFieldAdapter.setFilter(keyword.toString());
        mFieldAdapter.filterItems();
    }

    @Override
    public void clearFilters() {
        mFieldAdapter.setFilter(null);
        mFieldAdapter.filterItems();
    }

    @Override
    public boolean onItemClick(View view, int position) {
        final FieldFlexible item = mFieldAdapter.getItem(position);
        if (item != null) {
            mViewModel.onFieldClicked(item.getField());
        }

        return false;
    }
}
