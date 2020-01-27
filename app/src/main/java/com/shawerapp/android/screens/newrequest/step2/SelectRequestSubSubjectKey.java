package com.shawerapp.android.screens.newrequest.step2;

import com.google.auto.value.AutoValue;
import com.shawerapp.android.base.BaseFragment;
import com.shawerapp.android.base.BaseKey;

@AutoValue
public abstract class SelectRequestSubSubjectKey extends BaseKey {

  public static SelectRequestSubSubjectKey create() {
    return new AutoValue_SelectRequestSubSubjectKey();
  }

  @Override
  protected BaseFragment createFragment() {
    return SelectRequestSubSubjectFragment.newInstance();
  }
}
