package com.shawerapp.android.screens.invoice;

import com.google.auto.value.AutoValue;
import com.shawerapp.android.autovalue.Invoice;
import com.shawerapp.android.autovalue.Question;
import com.shawerapp.android.base.BaseFragment;
import com.shawerapp.android.base.BaseKey;
import com.shawerapp.android.screens.questiondetails.QuestionDetailsFragment;
import com.shawerapp.android.screens.questiondetails.QuestionDetailsKey;

@AutoValue
public abstract class InvoiceKey extends BaseKey {

    public abstract Invoice invoice();

    @Override
    protected BaseFragment createFragment() {
        return InvoiceFragment.newInstance(invoice());
    }

    public static InvoiceKey create(Invoice invoice) {
        return builder()
                .invoice(invoice)
                .build();
    }

    public static Builder builder() { return new AutoValue_InvoiceKey.Builder(); }

    @AutoValue.Builder
    public abstract static class Builder {
        public abstract InvoiceKey.Builder invoice(Invoice invoice);

        public abstract InvoiceKey build();
    }
}

