package com.shawerapp.android.backend.base;

import android.content.Intent;
import androidx.fragment.app.Fragment;

import org.solovyev.android.checkout.Billing;
import org.solovyev.android.checkout.Sku;

import java.util.List;

import io.reactivex.Flowable;
import io.reactivex.Maybe;

public interface BillingFramework {

    Billing getBilling();

    Flowable<Sku> getInAppItems(List<String> skuIds);

    Maybe<Sku> getInAppItemDetails(String productId);

    Maybe<String> purchase(Fragment fragment, String productId);

    void onActivityResult(int requestCode, int resultCode, Intent data);
}
