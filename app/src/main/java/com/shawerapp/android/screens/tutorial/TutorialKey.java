package com.shawerapp.android.screens.tutorial;

import com.google.auto.value.AutoValue;
import com.shawerapp.android.base.BaseFragment;
import com.shawerapp.android.base.BaseKey;

@AutoValue
public abstract class TutorialKey extends BaseKey {

  public static TutorialKey create() {
    return new AutoValue_TutorialKey();
  }

  @Override
  protected BaseFragment createFragment() {
    return TutorialFragment.newInstance();
  }
}
