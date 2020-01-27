package com.shawerapp.android.screens.newresponse;

import com.google.auto.value.AutoValue;
import com.shawerapp.android.base.BaseFragment;
import com.shawerapp.android.base.BaseKey;

@AutoValue
public abstract class ComposeResponseKey extends BaseKey {

  public static ComposeResponseKey create() {
    return new AutoValue_ComposeResponseKey();
  }

  @Override
  protected BaseFragment createFragment() {
    return ComposeResponseFragment.newInstance();
  }
}
