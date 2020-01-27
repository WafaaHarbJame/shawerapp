package com.shawerapp.android.screens.purchase;

import com.google.auto.value.AutoValue;
import com.shawerapp.android.base.BaseFragment;
import com.shawerapp.android.base.BaseKey;

@AutoValue
public abstract class PurchaseCoinsKey extends BaseKey {

  public static PurchaseCoinsKey create() {
    return new AutoValue_PurchaseCoinsKey();
  }

  @Override
  protected BaseFragment createFragment() {
    return PurchaseCoinsFragment.newInstance();
  }
}
