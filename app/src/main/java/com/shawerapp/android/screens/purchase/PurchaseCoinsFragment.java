package com.shawerapp.android.screens.purchase;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.BindView;
import butterknife.ButterKnife;

import com.shawerapp.android.R;
import com.shawerapp.android.adapter.item.CoinPackageFlexible;
import com.shawerapp.android.autovalue.CoinPackage;
import com.shawerapp.android.base.BaseFragment;
import com.shawerapp.android.base.FragmentLifecycle;
import com.shawerapp.android.screens.container.ContainerActivity;

import eu.davidea.flexibleadapter.FlexibleAdapter;
import eu.davidea.flexibleadapter.Payload;

import java.util.ArrayList;

import javax.inject.Inject;

public final class PurchaseCoinsFragment extends BaseFragment implements PurchaseCoinsContract.View {

    public static PurchaseCoinsFragment newInstance() {
        return new PurchaseCoinsFragment();
    }

    @Inject
    PurchaseCoinsContract.ViewModel viewModel;

    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;

    private FlexibleAdapter<CoinPackageFlexible> adapter;

    @Override
    protected FragmentLifecycle.ViewModel getViewModel() {
        return viewModel;
    }

    @Override
    public void onAttach(Context context) {
        DaggerPurchaseCoinsComponent.builder()
                .containerComponent(((ContainerActivity) context).getContainerComponent())
                .purchaseCoinsModule(new PurchaseCoinsModule(this, this))
                .build()
                .inject(this);
        super.onAttach(context);
    }

    @Override
    public void initBindings() {
        adapter = new FlexibleAdapter<>(new ArrayList<>());
        adapter.addListener((FlexibleAdapter.OnItemClickListener) (view, position) -> {
            CoinPackageFlexible item = adapter.getItem(position);
            viewModel.onPackageClicked(item.getCoinPackage(), item.getAmount());
            return false;
        });
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void addItem(CoinPackage coinPackage, String amount) {
        CoinPackageFlexible item = new CoinPackageFlexible(coinPackage, amount);
        if (!adapter.contains(item)) {
            adapter.addItem(item);
        }
    }

    @Override
    public void updateItem(CoinPackage coinPackage, String amount) {
        CoinPackageFlexible item = new CoinPackageFlexible(coinPackage, amount);
        if (adapter.contains(item)) {
            adapter.updateItem(item, Payload.CHANGE);
        }
    }

    @Override
    public void removeItem(CoinPackage coinPackage, String amount) {
        CoinPackageFlexible item = new CoinPackageFlexible(coinPackage, amount);
        int position = adapter.getGlobalPositionOf(item);
        if (position != -1) {
            adapter.removeItem(position);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_purchase_coins, container, false);
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
        final CoinPackageFlexible item = adapter.getItem(position);
        if (item != null) {
            viewModel.onPackageClicked(item.getCoinPackage(), item.getAmount());
        }

        return false;
    }
}
