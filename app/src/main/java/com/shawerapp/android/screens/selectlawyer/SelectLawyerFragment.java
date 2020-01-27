package com.shawerapp.android.screens.selectlawyer;

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

import com.jakewharton.rxbinding2.widget.RxTextView;
import com.shawerapp.android.R;
import com.shawerapp.android.adapter.item.HireableLawyerFlexible;
import com.shawerapp.android.autovalue.Field;
import com.shawerapp.android.autovalue.LawyerUser;
import com.shawerapp.android.base.BaseFragment;
import com.shawerapp.android.base.FragmentLifecycle;
import com.shawerapp.android.autovalue.SubSubject;
import com.shawerapp.android.screens.container.ContainerActivity;
import com.trello.rxlifecycle2.android.FragmentEvent;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import eu.davidea.flexibleadapter.FlexibleAdapter;
import eu.davidea.flexibleadapter.Payload;

public final class SelectLawyerFragment extends BaseFragment implements SelectLawyerContract.View {

    public static final String ARG_REQUEST_TYPE = "requestType";

    public static final String ARG_SELECTED_FIELD = "selectedField";

    public static final String ARG_SELECTED_SUBSUBJECT = "selectedSubSubject";

    public static SelectLawyerFragment newInstance(int requestType, Field selectedField, SubSubject selectedSubSubject) {

        Bundle args = new Bundle();
        args.putInt(ARG_REQUEST_TYPE, requestType);
        args.putParcelable(ARG_SELECTED_FIELD, selectedField);
        args.putParcelable(ARG_SELECTED_SUBSUBJECT, selectedSubSubject);
        SelectLawyerFragment fragment = new SelectLawyerFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Inject
    SelectLawyerContract.ViewModel mViewModel;

    @BindView(R.id.recyclerView)
    RecyclerView mLawyersList;

    @BindView(R.id.searchView)
    EditText mSearchView;

    private FlexibleAdapter<HireableLawyerFlexible> mLawyersAdapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_select_lawyer, container, false);
        mUnbinder = ButterKnife.bind(this, view);

        return view;
    }

    @Override
    public void onAttach(Context context) {
        DaggerSelectLawyerComponent.builder()
                .containerComponent(((ContainerActivity) context).getContainerComponent())
                .selectLawyerModule(new SelectLawyerModule(this, this))
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
        mLawyersAdapter = new FlexibleAdapter<>(new ArrayList<>(), this);
        mLawyersAdapter.addListener(this);
        mLawyersAdapter.setNotifyMoveOfFilteredItems(true);
        mLawyersList.setItemAnimator(new DefaultItemAnimator());
        mLawyersList.setLayoutManager(new LinearLayoutManager(getContext()));
        mLawyersList.setAdapter(mLawyersAdapter);

        RxTextView.afterTextChangeEvents(mSearchView)
                .compose(bindUntilEvent(FragmentEvent.DESTROY_VIEW))
                .throttleLast(700, TimeUnit.MILLISECONDS)
                .subscribe(textChangeEvent -> mViewModel.onSearchTextChanged(textChangeEvent.editable().toString()));
    }

    @Override
    public void addItem(LawyerUser lawyerUser, String userRole, String uid, String userID) {
        HireableLawyerFlexible item = new HireableLawyerFlexible(lawyerUser, userRole, uid, userID);
        if (!mLawyersAdapter.contains(item)) {
            mLawyersAdapter.addItem(item);
        }
    }

    @Override
    public void updateItem(LawyerUser lawyerUser, String userRole, String uid, String userID) {
        HireableLawyerFlexible item = new HireableLawyerFlexible(lawyerUser, userRole, uid, userID);
        if (mLawyersAdapter.contains(item)) {
            mLawyersAdapter.updateItem(item, Payload.CHANGE);
        }
    }

    @Override
    public void removeItem(LawyerUser lawyerUser, String userRole, String uid, String userID) {
        HireableLawyerFlexible item = new HireableLawyerFlexible(lawyerUser, userRole, uid, userID);
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
    public boolean onItemClick(View view, int position) {
        final HireableLawyerFlexible item = mLawyersAdapter.getItem(position);
        if (item != null) {
            mViewModel.onLawyerClicked(item.getLawyerUser());
        }

        return false;
    }
}
