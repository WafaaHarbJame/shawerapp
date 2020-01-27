package com.shawerapp.android.screens.newrequest.step3;

import com.google.auto.value.AutoValue;
import com.shawerapp.android.base.BaseFragment;
import com.shawerapp.android.base.BaseKey;

@AutoValue
public abstract class ComposeRequestKey extends BaseKey {

  public static ComposeRequestKey create() {
    return new AutoValue_ComposeRequestKey();
  }

  @Override
  protected BaseFragment createFragment() {
    return ComposeRequestFragment.newInstance();
  }
}
