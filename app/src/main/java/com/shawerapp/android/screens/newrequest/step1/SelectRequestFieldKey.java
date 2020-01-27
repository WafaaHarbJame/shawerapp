package com.shawerapp.android.screens.newrequest.step1;

import com.google.auto.value.AutoValue;
import com.shawerapp.android.base.BaseFragment;
import com.shawerapp.android.base.BaseKey;

@AutoValue
public abstract class SelectRequestFieldKey extends BaseKey {

  public static SelectRequestFieldKey create() {
    return new AutoValue_SelectRequestFieldKey();
  }

  @Override
  protected BaseFragment createFragment() {
    return SelectRequestFieldFragment.newInstance();
  }
}
