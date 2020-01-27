package com.shawerapp.android.screens.questionlist;

import com.google.auto.value.AutoValue;
import com.shawerapp.android.base.BaseFragment;
import com.shawerapp.android.base.BaseKey;

@AutoValue
public abstract class QuestionListKey extends BaseKey {

  public static QuestionListKey create() {
    return new AutoValue_QuestionListKey();
  }

  @Override
  protected BaseFragment createFragment() {
    return QuestionListFragment.newInstance();
  }
}
