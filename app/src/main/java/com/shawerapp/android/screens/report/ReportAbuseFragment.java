package com.shawerapp.android.screens.report;

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
import eu.davidea.flexibleadapter.SelectableAdapter;

import com.jakewharton.rxbinding2.widget.RxTextView;
import com.shawerapp.android.R;
import com.shawerapp.android.adapter.ReportUserAdapter;
import com.shawerapp.android.adapter.item.ReportUserFlexible;
import com.shawerapp.android.base.BaseFragment;
import com.shawerapp.android.base.FragmentLifecycle;
import com.shawerapp.android.screens.container.ContainerActivity;
import com.trello.rxlifecycle2.android.FragmentEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

public final class ReportAbuseFragment extends BaseFragment implements ReportAbuseContract.View {

    public static ReportAbuseFragment newInstance() {

        Bundle args = new Bundle();

        ReportAbuseFragment fragment = new ReportAbuseFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Inject
    ReportAbuseContract.ViewModel mViewModel;

    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;

    @BindView(R.id.searchView)
    EditText mSearchView;

    private ReportUserAdapter adapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_report_abuse, container, false);
        mUnbinder = ButterKnife.bind(this, view);

        return view;
    }

    @Override
    public void onAttach(Context context) {
        DaggerReportAbuseComponent.builder()
                .containerComponent(((ContainerActivity) context).getContainerComponent())
                .reportAbuseModule(new ReportAbuseModule(this, this))
                .build()
                .inject(this);
        super.onAttach(context);
    }

    @Override
    protected FragmentLifecycle.ViewModel getViewModel() {
        return mViewModel;
    }

    @Override
    public void initBindings(boolean isShowLawyers) {
        adapter = new ReportUserAdapter(new ArrayList<>(), isShowLawyers, v -> {
            ReportUserFlexible item = (ReportUserFlexible) v.getTag();
            int position = adapter.getGlobalPositionOf(item);
            if (position != -1) {
                adapter.toggleSelection(position);
                adapter.notifyItemChanged(position, Payload.CHANGE);
            }
        });
        adapter.setMode(SelectableAdapter.Mode.MULTI);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);

        RxTextView.afterTextChangeEvents(mSearchView)
                .compose(bindUntilEvent(FragmentEvent.DESTROY_VIEW))
                .throttleLast(700, TimeUnit.MILLISECONDS)
                .subscribe(textChangeEvent -> mViewModel.onSearchTextChanged(textChangeEvent.editable().toString()));
    }

    @Override
    public void addItem(Object user, String currentUserUid) {
        ReportUserFlexible item = new ReportUserFlexible(user, currentUserUid);
        if (!adapter.contains(item)) {
            adapter.addItem(item);
        }
    }

    @Override
    public void updateItem(Object user, String currentUserUid) {
        ReportUserFlexible item = new ReportUserFlexible(user, currentUserUid);
        if (adapter.contains(item)) {
            adapter.updateItem(item);
        }
    }

    @Override
    public void removeItem(Object user, String currentUserUid) {
        ReportUserFlexible item = new ReportUserFlexible(user, currentUserUid);
        int position = adapter.getGlobalPositionOf(item);
        if (position != -1) {
            adapter.removeItem(position);
        }
    }

    @Override
    public void filterList(String keyword) {
        adapter.setFilter(keyword.toString());
        adapter.filterItems();
    }

    @Override
    public void clearFilters() {
        adapter.setFilter(null);
        adapter.filterItems();
    }

    @Override
    public List<Object> getSelectedUsers() {
        List<Object> selectedUsers = new ArrayList<>();
        for (int position : adapter.getSelectedPositions()) {
            ReportUserFlexible item = adapter.getItem(position);
            selectedUsers.add(item.getUser());
        }
        return selectedUsers;
    }
}
