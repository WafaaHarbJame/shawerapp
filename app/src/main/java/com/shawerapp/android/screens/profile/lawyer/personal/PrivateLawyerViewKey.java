package com.shawerapp.android.screens.profile.lawyer.personal;

import com.google.auto.value.AutoValue;
import com.shawerapp.android.base.BaseFragment;
import com.shawerapp.android.base.BaseKey;

@AutoValue
public abstract class PrivateLawyerViewKey extends BaseKey {

  public static PrivateLawyerViewKey create() {
    return new AutoValue_PrivateLawyerViewKey();
  }

  @Override
  protected BaseFragment createFragment() {
    return PrivateLawyerViewFragment.newInstance();
  }
}
