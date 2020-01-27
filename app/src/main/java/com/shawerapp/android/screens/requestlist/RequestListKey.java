package com.shawerapp.android.screens.requestlist;

import com.google.auto.value.AutoValue;
import com.shawerapp.android.base.BaseFragment;
import com.shawerapp.android.base.BaseKey;

@AutoValue
public abstract class RequestListKey extends BaseKey {

  public static RequestListKey create() {
    return new AutoValue_RequestListKey();
  }

  @Override
  protected BaseFragment createFragment() {
    return RequestListFragment.newInstance();
  }
}
