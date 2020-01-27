package com.shawerapp.android.screens.conversation;

import com.google.auto.value.AutoValue;
import com.shawerapp.android.base.BaseFragment;
import com.shawerapp.android.base.BaseKey;

@AutoValue
public abstract class ConversationKey extends BaseKey {

  public static ConversationKey create() {
    return new AutoValue_ConversationKey();
  }

  @Override
  protected BaseFragment createFragment() {
    return ConversationFragment.newInstance();
  }
}
