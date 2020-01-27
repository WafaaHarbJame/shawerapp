package com.shawerapp.android.backend.checkout;

import android.app.Application;
import android.content.Context;
import android.content.Intent;

import androidx.fragment.app.Fragment;

import com.shawerapp.android.BuildConfig;
import com.shawerapp.android.backend.base.BillingFramework;

import org.solovyev.android.checkout.Billing;
import org.solovyev.android.checkout.BillingRequests;
import org.solovyev.android.checkout.Checkout;
import org.solovyev.android.checkout.EmptyRequestListener;
import org.solovyev.android.checkout.Inventory;
import org.solovyev.android.checkout.ProductTypes;
import org.solovyev.android.checkout.Purchase;
import org.solovyev.android.checkout.RequestListener;
import org.solovyev.android.checkout.Sku;
import org.solovyev.android.checkout.UiCheckout;

import java.util.Iterator;
import java.util.List;

import javax.annotation.Nonnull;
import javax.inject.Inject;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.Maybe;
import io.reactivex.android.schedulers.AndroidSchedulers;

import static org.solovyev.android.checkout.ProductTypes.IN_APP;

public class CheckoutBillingImpl implements BillingFramework {

    private static final String TAG = "SHAWER_IN_APP_BILLING";

    private Billing mBilling;

    private Context mContext;

    @Inject
    public CheckoutBillingImpl(Application application) {
        mContext = application;
        mBilling = new Billing(application, new Billing.DefaultConfiguration() {
            @Override
            public String getPublicKey() {
                return BuildConfig.PUBLIC_KEY;
            }
        });
    }

    @Override
    public Billing getBilling() {
        return mBilling;
    }

    @Override
    public Flowable<Sku> getInAppItems(List<String> skuIds) {
        Checkout checkout = Checkout.forApplication(mBilling);

        return Flowable.defer(() -> Flowable.<Sku>create(emitter -> {
            checkout.start();

            Inventory.Request request = Inventory.Request.create();
            request.loadAllPurchases();
            request.loadSkus(IN_APP, skuIds);
            checkout.loadInventory(request, products -> {
                Iterator<Inventory.Product> ite = products.iterator();
                while (ite.hasNext()) {
                    Inventory.Product product = ite.next();
                    for (Sku sku : products.get(product.id).getSkus()) {
                        emitter.onNext(sku);
                    }
                }
            });
        }, BackpressureStrategy.BUFFER))
                .doFinally(checkout::stop)
                .unsubscribeOn(AndroidSchedulers.mainThread())
                .subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public Maybe<Sku> getInAppItemDetails(String productId) {
        return Maybe.defer(() -> Maybe.<Sku>create(
                emitter -> {
                    Checkout checkout = Checkout.forApplication(mBilling);
                    checkout.start();

                    Inventory.Request request = Inventory.Request.create();
                    request.loadAllPurchases();
                    request.loadSkus(IN_APP, productId);

                    checkout.loadInventory(request, products -> {
                        List<Purchase> purchaseList = products.get(IN_APP).getPurchases();
                        if (purchaseList != null && purchaseList.size() > 0) {
                            Iterator<Purchase> purchaseIterator = purchaseList.iterator();
                            while (purchaseIterator.hasNext()) {
                                Purchase purchase = purchaseIterator.next();
                                mBilling.getRequests().consume(purchase.token, new EmptyRequestListener<Object>());
                            }
                        }
                        Iterator<Inventory.Product> productIterator = products.iterator();
                        while (productIterator.hasNext()) {
                            Inventory.Product product = productIterator.next();
                            for (Sku sku : products.get(product.id).getSkus()) {
                                emitter.onSuccess(sku);
                            }
                        }
                        emitter.onComplete();
                        emitter.setCancellable(checkout::stop);
                    });
                }))
                .unsubscribeOn(AndroidSchedulers.mainThread())
                .subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public Maybe<String> purchase(Fragment fragment, String productId) {
        return Maybe.<String>create(
                emitter -> {
                    UiCheckout checkout = Checkout.forUi((intentSender, requestCode, intent) ->
                            fragment.startIntentSenderForResult(
                                    intentSender,
                                    requestCode,
                                    intent,
                                    0,
                                    0,
                                    0,
                                    null), TAG, mBilling);
                    checkout.start();
                    checkout.whenReady(new Checkout.EmptyListener() {
                        @Override
                        public void onReady(@Nonnull BillingRequests requests) {
                            requests.purchase(ProductTypes.IN_APP, productId, TAG,
                                    checkout.createOneShotPurchaseFlow(new RequestListener<Purchase>() {
                                        @Override
                                        public void onSuccess(@Nonnull Purchase purchaseResult) {
                                            requests.consume(purchaseResult.token, new EmptyRequestListener<Object>() {
                                                @Override
                                                public void onSuccess(@Nonnull Object consumeResult) {
                                                    emitter.onSuccess(purchaseResult.toJson());
                                                }

                                                @Override
                                                public void onError(int response, @Nonnull Exception e) {
                                                    emitter.onError(e);
                                                }
                                            });
                                        }

                                        @Override
                                        public void onError(int response, @Nonnull Exception e) {
                                            emitter.onError(e);
                                        }
                                    }));
                        }
                    });

                    emitter.setCancellable(checkout::stop);
                })
                .unsubscribeOn(AndroidSchedulers.mainThread())
                .subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

    }
}
