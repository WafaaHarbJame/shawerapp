package com.shawerapp.android.screens.viewanswer;

import com.google.auto.value.AutoValue;
import com.shawerapp.android.base.BaseFragment;
import com.shawerapp.android.base.BaseKey;

@AutoValue
public abstract class ViewAnswerKey extends BaseKey {

  public static ViewAnswerKey create() {
    return new AutoValue_ViewAnswerKey();
  }

  @Override
  protected BaseFragment createFragment() {
    return ViewAnswerFragment.newInstance();
  }
}
