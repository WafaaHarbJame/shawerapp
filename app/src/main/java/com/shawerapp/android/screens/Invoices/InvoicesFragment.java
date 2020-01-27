package com.shawerapp.android.screens.Invoices;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.shawerapp.android.R;
import com.shawerapp.android.adapter.item.InvoiceFlexible;
import com.shawerapp.android.adapter.item.QuestionAssignedVerticalFlexible;
import com.shawerapp.android.autovalue.Invoice;
import com.shawerapp.android.base.BaseFragment;
import com.shawerapp.android.base.FragmentLifecycle;
import com.shawerapp.android.screens.container.ContainerActivity;

import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import eu.davidea.flexibleadapter.FlexibleAdapter;

public final class InvoicesFragment extends BaseFragment implements InvoicesContract.View {

    public static InvoicesFragment newInstance() {
        return new InvoicesFragment();
    }

    @Inject
    InvoicesContract.ViewModel viewModel;

    @BindView(R.id.invoicesRecyclerView)
    RecyclerView recyclerView;

    private FlexibleAdapter<InvoiceFlexible> adapter;

    @Override
    protected FragmentLifecycle.ViewModel getViewModel() {
        return viewModel;
    }

    @Override
    public void onAttach(Context context) {
        DaggerInvoicesComponent.builder()
                .containerComponent(((ContainerActivity) context).getContainerComponent())
                .invoicesModule(new InvoicesModule(this, this))
                .build()
                .inject(this);
        super.onAttach(context);
    }

    @Override
    public void initBindings() {
        adapter = new FlexibleAdapter<>(new ArrayList<>());
        adapter.addListener((FlexibleAdapter.OnItemClickListener) (view, position) -> {
            InvoiceFlexible item = adapter.getItem(position);
            viewModel.showInvoice(Objects.requireNonNull(item).getInvoice());
            return false;
        });
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void addItem(Invoice invoice) {
        InvoiceFlexible item = new InvoiceFlexible(invoice, Objects.requireNonNull(getActivity()).getApplicationContext());

        if (!adapter.contains(item)) {
            adapter.addItem(item);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_invoices, container, false);
        mUnbinder = ButterKnife.bind(this, view);

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        viewModel.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public boolean onItemClick(View view, int position) {
        final InvoiceFlexible item = adapter.getItem(position);
        if (item != null) {
            viewModel.showInvoice(item.getInvoice());
        }
        return false;
    }
}
