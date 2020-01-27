package com.shawerapp.android.screens.purchase;

import android.app.AlertDialog;
import android.content.Intent;

import com.shawerapp.android.R;
import com.shawerapp.android.autovalue.CoinPackageEvent;
import com.shawerapp.android.backend.base.BillingFramework;
import com.shawerapp.android.backend.base.RealTimeDataFramework;
import com.shawerapp.android.base.BaseFragment;
import com.shawerapp.android.autovalue.CoinPackage;
import com.shawerapp.android.screens.container.ContainerContract;
import com.shawerapp.android.utils.CommonUtils;
import com.trello.rxlifecycle2.android.FragmentEvent;

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

import io.reactivex.Flowable;
import io.reactivex.functions.Consumer;
import io.reactivex.processors.BehaviorProcessor;
import timber.log.Timber;

import static com.shawerapp.android.backend.base.RealTimeDataFramework.EVENT_ADDED;
import static org.solovyev.android.checkout.ProductTypes.IN_APP;

public final class PurchaseCoinsViewModel implements PurchaseCoinsContract.ViewModel {

    private static final String TAG = "SHAWER_IN_APP_BILLING";

    private BaseFragment mFragment;

    private PurchaseCoinsContract.View mView;

    @Inject
    ContainerContract.View mContainerView;

    @Inject
    ContainerContract.ViewModel mContainerViewModel;

    @Inject
    RealTimeDataFramework mRTDataFramework;

    @Inject
    BillingFramework mBillingFramework;

    private BehaviorProcessor<CoinPackageEvent> mCoinPackageProcessor = BehaviorProcessor.create();

    private UiCheckout mCheckout;

    @Inject
    public PurchaseCoinsViewModel(BaseFragment fragment, PurchaseCoinsContract.View view) {
        mFragment = fragment;
        mView = view;

    }

    @Override
    public void onViewCreated() {
        mCheckout = Checkout.forUi((intentSender, requestCode, intent) -> mFragment
                        .startIntentSenderForResult(intentSender, requestCode, intent, 0, 0, 0, null),
                TAG, mBillingFramework.getBilling());
        mCheckout.start();

        mView.initBindings();

        mCoinPackageProcessor.serialize()
                .compose(mFragment.bindUntilEvent(FragmentEvent.DESTROY_VIEW))
                .subscribe(coinPackageConsumer(), mContainerViewModel.catchErrorThrowable());
    }

    private Consumer<? super CoinPackageEvent> coinPackageConsumer() {
        return coinPackageEvent -> {
            switch (coinPackageEvent.type()) {
                case EVENT_ADDED:
                    mView.addItem(coinPackageEvent.coinPackage(), coinPackageEvent.amount());
                    break;
                case RealTimeDataFramework.EVENT_UPDATED:
                    mView.updateItem(coinPackageEvent.coinPackage(), coinPackageEvent.amount());
                    break;
                case RealTimeDataFramework.EVENT_REMOVED:
                    mView.removeItem(coinPackageEvent.coinPackage(), coinPackageEvent.amount());
                    break;
            }
        };
    }

    @Override
    public void onAfterEnterAnimation() {
        mRTDataFramework.retrieveUserCoins()
                .compose(mFragment.bindUntilEvent(FragmentEvent.DESTROY_VIEW))
                .subscribe(userCoins -> mContainerView.setToolbarSubTitle(mFragment.getString(R.string.label_buy_coins_subtitle, CommonUtils.formatNumber(userCoins))));

        retrieveData();
    }

    @Override
    public void retrieveData() {
        mRTDataFramework.retrieveCoinPackages()
                .compose(mFragment.bindUntilEvent(FragmentEvent.DESTROY_VIEW))
                .doOnSubscribe(subscription -> mContainerView.showLoadingIndicator())
                .doOnNext(coinPackageEvent -> mContainerView.hideLoadingIndicator())
                .doOnError(throwable -> mContainerView.hideLoadingIndicator())
                .subscribe(coinPackageEvent -> {
                    Inventory.Request request = Inventory.Request.create();
                    request.loadAllPurchases();
                    request.loadSkus(IN_APP, coinPackageEvent.coinPackage().productId());

                    mCheckout.loadInventory(request, products -> {
                        List<Purchase> purchaseList = products.get(IN_APP).getPurchases();
                        if (purchaseList != null && purchaseList.size() > 0) {
                            Iterator<Purchase> purchaseIterator = purchaseList.iterator();
                            while (purchaseIterator.hasNext()) {
                                Purchase purchase = purchaseIterator.next();
                                mRTDataFramework
                                        .addPurchase(purchase.toJson(), coinPackageEvent.coinPackage().coinAmount())
                                        .doOnSubscribe(disposable -> mContainerView.showLoadingIndicator())
                                        .doFinally(() -> mContainerView.hideLoadingIndicator())
                                        .subscribe(purchaseUid -> mBillingFramework.getBilling().getRequests()
                                                .consume(purchase.token, new EmptyRequestListener<Object>() {
                                                    @Override
                                                    public void onSuccess(@Nonnull Object consumeResult) {
                                                        mRTDataFramework
                                                                .consumePurchase(purchaseUid)
                                                                .doOnSubscribe(disposable -> mContainerView.showLoadingIndicator())
                                                                .flatMap(purchaseUid1 -> mRTDataFramework
                                                                        .addPurchasedCoins(coinPackageEvent.coinPackage().coinAmount()))
                                                                .doFinally(() -> mContainerView.hideLoadingIndicator())
                                                                .subscribe(currentCoins -> mContainerView.setToolbarSubTitle(mFragment.getString(R.string.label_buy_coins_subtitle, CommonUtils.formatNumber(currentCoins))));
                                                    }

                                                    @Override
                                                    public void onError(int response, @Nonnull Exception e) {
                                                        Timber.e(CommonUtils.getExceptionString(e));
                                                    }
                                                }));
                            }
                        }

                        Iterator<Inventory.Product> productIterator = products.iterator();
                        while (productIterator.hasNext()) {
                            Inventory.Product product = productIterator.next();
                            for (Sku sku : products.get(product.id).getSkus()) {
                                mCoinPackageProcessor.onNext(CoinPackageEvent.builder()
                                        .coinPackage(coinPackageEvent.coinPackage())
                                        .type(EVENT_ADDED)
                                        .amount(sku.price)
                                        .build());
                            }
                        }
                    });
                }, mContainerViewModel.catchErrorThrowable());
    }

    @Override
    public Flowable<CoinPackageEvent> retrieveActiveCoinPackages() {
        return mRTDataFramework.retrieveCoinPackages()
                .compose(mFragment.bindUntilEvent(FragmentEvent.DESTROY_VIEW))
                .filter(coinPackageEvent -> coinPackageEvent.coinPackage().status().equalsIgnoreCase(CoinPackage.Status.ACTIVE));
    }

    @Override
    public Flowable<Sku> getInAppProducts(List<String> productIds) {
        return mBillingFramework.getInAppItems(productIds)
                .compose(mFragment.bindUntilEvent(FragmentEvent.DESTROY_VIEW));
    }

    @Override
    public void onBackButtonClicked() {
        mContainerViewModel.goBack()
                .subscribe(mContainerViewModel.navigationObserver());
    }

    @Override
    public void setupToolbar() {
        mContainerView.clearToolbarSubtitle();
        mContainerView.setToolbarTitle(mFragment.getString(R.string.label_buy_coins_title));
        mContainerView.setLeftToolbarButtonImageResource(R.drawable.icon_back);
        mContainerView.setRightToolbarButtonImageResource(-1);
    }

    @Override
    public void onLeftToolbarButtonClicked() {
        onBackButtonClicked();
    }

    @Override
    public void onRightToolbarButtonClicked() {

    }

    @Override
    public void onPackageClicked(CoinPackage coinPackage, String amount) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mFragment.getContext(), R.style.DialogTheme);
        builder.setMessage(mFragment.getString(R.string.format_purchase, coinPackage.title(), coinPackage.description(), amount));
        builder.setPositiveButton(mFragment.getString(R.string.yes), (dialog, which) -> {
            dialog.dismiss();
            mCheckout.whenReady(new Checkout.EmptyListener() {
                @Override
                public void onReady(@Nonnull BillingRequests requests) {
                    requests.purchase(ProductTypes.IN_APP, coinPackage.productId(), TAG,
                            mCheckout.createOneShotPurchaseFlow(new RequestListener<Purchase>() {
                                @Override
                                public void onSuccess(@Nonnull Purchase purchaseResult) {
                                    mRTDataFramework
                                            .addPurchase(purchaseResult.toJson(), coinPackage.coinAmount())
                                            .doOnSubscribe(disposable -> mContainerView.showLoadingIndicator())
                                            .doFinally(() -> mContainerView.hideLoadingIndicator())
                                            .subscribe(purchaseUid -> requests
                                                    .consume(purchaseResult.token, new EmptyRequestListener<Object>() {
                                                        @Override
                                                        public void onSuccess(@Nonnull Object consumeResult) {
                                                            mRTDataFramework
                                                                    .consumePurchase(purchaseUid)
                                                                    .doOnSubscribe(disposable -> mContainerView.showLoadingIndicator())
                                                                    .flatMap(purchaseUid1 -> mRTDataFramework
                                                                            .addPurchasedCoins(coinPackage.coinAmount()))
                                                                    .doFinally(() -> mContainerView.hideLoadingIndicator())
                                                                    .subscribe(currentCoins -> mContainerView.setToolbarSubTitle(mFragment.getString(R.string.label_buy_coins_subtitle, CommonUtils.formatNumber(currentCoins))));
                                                        }

                                                        @Override
                                                        public void onError(int response, @Nonnull Exception e) {
                                                            Timber.e(CommonUtils.getExceptionString(e));
                                                        }
                                                    }));
                                }

                                @Override
                                public void onError(int response, @Nonnull Exception e) {
                                    Timber.e(CommonUtils.getExceptionString(e));
                                }
                            }));
                }
            });
        });
        builder.setNegativeButton(mFragment.getString(R.string.no), (dialog, which) -> dialog.dismiss());
        builder.show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        mCheckout.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onStart() {

    }

    @Override
    public void onStop() {
        mCheckout.stop();
    }

    @Override
    public void onDetach() {

    }

    @Override
    public void onDestroy() {

    }
}
