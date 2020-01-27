package com.shawerapp.android.screens.purchase;

import android.content.Intent;

import com.shawerapp.android.autovalue.CoinPackageEvent;
import com.shawerapp.android.base.FragmentLifecycle;
import com.shawerapp.android.autovalue.CoinPackage;

import org.solovyev.android.checkout.Sku;

import java.util.List;

import eu.davidea.flexibleadapter.FlexibleAdapter;
import io.reactivex.Flowable;

public final class PurchaseCoinsContract {

    interface View extends FragmentLifecycle.View, FlexibleAdapter.OnItemClickListener {
        void initBindings();

        void addItem(CoinPackage coinPackage, String amount);

        void updateItem(CoinPackage coinPackage, String amount);

        void removeItem(CoinPackage coinPackage, String amount);
    }

    interface ViewModel extends FragmentLifecycle.ViewModel {

        void retrieveData();

        Flowable<CoinPackageEvent> retrieveActiveCoinPackages();

        Flowable<Sku> getInAppProducts(List<String> productIds);

        void onPackageClicked(CoinPackage coinPackage, String amount);

        void onActivityResult(int requestCode, int resultCode, Intent data);
    }
}
