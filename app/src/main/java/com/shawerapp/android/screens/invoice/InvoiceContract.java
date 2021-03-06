package com.shawerapp.android.screens.invoice;

import android.content.Intent;

import com.shawerapp.android.autovalue.Invoice;
import com.shawerapp.android.base.FragmentLifecycle;

import eu.davidea.flexibleadapter.FlexibleAdapter;

public final class InvoiceContract {

    interface View extends FragmentLifecycle.View, FlexibleAdapter.OnItemClickListener {

        void initBindings();

        void addItem(Invoice invoice);
    }

    interface ViewModel extends FragmentLifecycle.ViewModel {

        void retrieveData();

        void onActivityResult(int requestCode, int resultCode, Intent data);


    }
}
