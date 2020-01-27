package com.shawerapp.android.screens.answerlist;

import com.google.auto.value.AutoValue;
import com.shawerapp.android.base.BaseFragment;
import com.shawerapp.android.base.BaseKey;

@AutoValue
public abstract class AnswerListKey extends BaseKey {

  public static AnswerListKey create() {
    return new AutoValue_AnswerListKey();
  }

  @Override
  protected BaseFragment createFragment() {
    return AnswerListFragment.newInstance();
  }
}
