package com.shawerapp.android.Payment;


public interface PaymentStatusRequestListener {

    void onErrorOccurred();
    void onPaymentStatusReceived(String paymentStatus);
}
