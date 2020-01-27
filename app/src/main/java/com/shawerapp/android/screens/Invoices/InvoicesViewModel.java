package com.shawerapp.android.screens.Invoices;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import android.content.Intent;

import com.shawerapp.android.R;
import com.shawerapp.android.autovalue.IndividualUser;
import com.shawerapp.android.autovalue.Invoice;
import com.shawerapp.android.autovalue.LawyerUser;
import com.shawerapp.android.backend.base.BillingFramework;
import com.shawerapp.android.backend.base.RealTimeDataFramework;
import com.shawerapp.android.base.BaseFragment;
import com.shawerapp.android.screens.container.ContainerContract;
import com.shawerapp.android.screens.invoice.InvoiceKey;
import com.shawerapp.android.screens.payment.PaymentKey;
import com.shawerapp.android.screens.questiondetails.QuestionDetailsKey;
import com.shawerapp.android.utils.LoginUtil;
import com.trello.rxlifecycle2.android.FragmentEvent;

import org.solovyev.android.checkout.Checkout;
import org.solovyev.android.checkout.UiCheckout;

import javax.inject.Inject;

import io.reactivex.functions.Consumer;
import io.reactivex.processors.BehaviorProcessor;

public final class InvoicesViewModel implements InvoicesContract.ViewModel {

    private static final String TAG = "SHAWER_IN_APP_BILLING";

    private BaseFragment mFragment;

    private InvoicesContract.View mView;

    @Inject
    ContainerContract.View mContainerView;

    @Inject
    ContainerContract.ViewModel mContainerViewModel;

    @Inject
    RealTimeDataFramework mRTDataFramework;

    @Inject
    BillingFramework mBillingFramework;

    private BehaviorProcessor<Invoice> invoiceProcessor = BehaviorProcessor.create();

//    private UiCheckout mCheckout;

    @Inject
    public InvoicesViewModel(BaseFragment fragment, InvoicesContract.View view) {
        mFragment = fragment;
        mView = view;
    }

    @Override
    public void onViewCreated() {
//        mCheckout = Checkout.forUi((intentSender, requestCode, intent) -> mFragment
//                        .startIntentSenderForResult(intentSender, requestCode, intent, 0, 0, 0, null),
//                TAG, mBillingFramework.getBilling());
//        mCheckout.start();

        mView.initBindings();

        invoiceProcessor.serialize()
                .compose(mFragment.bindUntilEvent(FragmentEvent.DESTROY_VIEW))
                .subscribe(invoiceConsumer(), mContainerViewModel.catchErrorThrowable());
    }

    private Consumer<? super Invoice> invoiceConsumer() {
        return invoice -> mView.addItem(invoice);
    }

    @SuppressLint("CheckResult")
    @Override
    public void onAfterEnterAnimation() {
        LoginUtil mLoginUtil = new LoginUtil(mFragment.getContext());
        boolean isLawyer;
        if (mLoginUtil.getUserRole().toLowerCase().equals(LawyerUser.ROLE_VALUE)) {
            isLawyer = true;
        } else {
            isLawyer = false;
        }
        mRTDataFramework.retrieveInvoices(isLawyer)
                .compose(mFragment.bindUntilEvent(FragmentEvent.DESTROY_VIEW))
                .subscribe(invoiceProcessor::onNext, mContainerViewModel.catchErrorThrowable());
        mContainerView.setToolbarTitle(mFragment.getString(R.string.invoices));
        retrieveData();
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
    public void retrieveData() {

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        mCheckout.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void showInvoice(Invoice invoiceItem) {
//        mContainerViewModel.goTo(InvoiceKey.create(invoiceItem))
//                .subscribe(mContainerViewModel.navigationObserver());
        mContainerViewModel
                .goTo(InvoiceKey.builder()
                        .invoice(invoiceItem)
                        .build())
                .subscribe(mContainerViewModel.navigationObserver());
//        mContainerViewModel
//                .goTo(InvoiceKey.create(invoiceItem))
//                .subscribe(mContainerViewModel.navigationObserver());
    }

    @Override
    public void onStart() {

    }

    @Override
    public void onStop() {
//        mCheckout.stop();
    }

    @Override
    public void onDetach() {

    }

    @Override
    public void onDestroy() {

    }
}
