package com.shawerapp.android.screens.selectsubsubject;

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
import eu.davidea.flexibleadapter.Payload;

import com.jakewharton.rxbinding2.widget.RxTextView;
import com.shawerapp.android.R;
import com.shawerapp.android.adapter.SubSubjectAdapter;
import com.shawerapp.android.adapter.item.SubSubjectFlexible;
import com.shawerapp.android.autovalue.Field;
import com.shawerapp.android.base.BaseFragment;
import com.shawerapp.android.base.FragmentLifecycle;
import com.shawerapp.android.autovalue.SubSubject;
import com.shawerapp.android.screens.container.ContainerActivity;
import com.trello.rxlifecycle2.android.FragmentEvent;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

public final class SelectSubSubjectFragment extends BaseFragment implements SelectSubSubjectContract.View {

    public static final String ARG_REQUEST_TYPE = "requestType";

    public static final String ARG_SELECTED_FIELD = "selectedField";

    public static SelectSubSubjectFragment newInstance(int requestType, Field selectedField) {

        Bundle args = new Bundle();
        args.putInt(ARG_REQUEST_TYPE, requestType);
        args.putParcelable(ARG_SELECTED_FIELD, selectedField);
        SelectSubSubjectFragment fragment = new SelectSubSubjectFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Inject
    SelectSubSubjectContract.ViewModel mViewModel;

    @BindView(R.id.recyclerView)
    RecyclerView mSubSubjectList;

    @BindView(R.id.searchView)
    EditText mSearchView;

    private SubSubjectAdapter mSubSubjectAdapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_select_subsubject, container, false);
        mUnbinder = ButterKnife.bind(this, view);

        return view;
    }

    @Override
    public void onAttach(Context context) {
        DaggerSelectSubSubjectComponent.builder()
                .containerComponent(((ContainerActivity) context).getContainerComponent())
                .selectSubSubjectModule(new SelectSubSubjectModule(this, this))
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
        mSubSubjectAdapter = new SubSubjectAdapter(new ArrayList<>(), this);
        mSubSubjectAdapter.addListener(this);
        mSubSubjectAdapter.setNotifyMoveOfFilteredItems(true);
        mSubSubjectList.setItemAnimator(new DefaultItemAnimator());
        mSubSubjectList.setLayoutManager(new LinearLayoutManager(getContext()));
        mSubSubjectList.setAdapter(mSubSubjectAdapter);

        RxTextView.afterTextChangeEvents(mSearchView)
                .compose(bindUntilEvent(FragmentEvent.DESTROY_VIEW))
                .throttleLast(700, TimeUnit.MILLISECONDS)
                .subscribe(textChangeEvent -> mViewModel.onSearchTextChanged(textChangeEvent.editable().toString()));
    }

    @Override
    public void addItem(SubSubject subSubject, Long practiceRequestCost) {
        SubSubjectFlexible item = new SubSubjectFlexible(subSubject, practiceRequestCost);
        if (!mSubSubjectAdapter.contains(item)) {
            mSubSubjectAdapter.addItem(item);
        }
    }

    @Override
    public void updateItem(SubSubject subSubject, Long practiceRequestCost) {
        SubSubjectFlexible item = new SubSubjectFlexible(subSubject, practiceRequestCost);
        if (mSubSubjectAdapter.contains(item)) {
            mSubSubjectAdapter.updateItem(item, Payload.CHANGE);
        }
    }

    @Override
    public void removeItem(SubSubject subSubject, Long practiceRequestCost) {
        SubSubjectFlexible item = new SubSubjectFlexible(subSubject, practiceRequestCost);
        int position = mSubSubjectAdapter.getGlobalPositionOf(item);
        if (position != -1) {
            mSubSubjectAdapter.removeItem(position);
        }
    }

    @Override
    public void filterList(String keyword) {
        mSubSubjectAdapter.setFilter(keyword.toString());
        mSubSubjectAdapter.filterItems();
    }

    @Override
    public void clearFilters() {
        mSubSubjectAdapter.setFilter(null);
        mSubSubjectAdapter.filterItems();
    }

    @Override
    public boolean onItemClick(View view, int position) {
        final SubSubjectFlexible item = mSubSubjectAdapter.getItem(position);
        if (item != null) {
            mViewModel.onSubSubjectClicked(item.getSubSubject());
        }

        return false;
    }
}
