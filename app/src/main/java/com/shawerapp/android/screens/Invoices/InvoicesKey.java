package com.shawerapp.android.screens.Invoices;


import com.google.auto.value.AutoValue;
import com.shawerapp.android.base.BaseFragment;
import com.shawerapp.android.base.BaseKey;

@AutoValue
public abstract class InvoicesKey extends BaseKey {

    public static InvoicesKey create() {
        return new AutoValue_InvoicesKey();
    }

    @Override
    protected BaseFragment createFragment() {
        return InvoicesFragment.newInstance();
    }
}

