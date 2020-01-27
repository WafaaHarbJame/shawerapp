package com.shawerapp.android.screens.discover;

import com.google.auto.value.AutoValue;
import com.shawerapp.android.base.BaseFragment;
import com.shawerapp.android.base.BaseKey;

@AutoValue
public abstract class DiscoverLawyerKey extends BaseKey {

  public static DiscoverLawyerKey create() {
    return new AutoValue_DiscoverLawyerKey();
  }

  @Override
  protected BaseFragment createFragment() {
    return DiscoverLawyerFragment.newInstance();
  }
}
