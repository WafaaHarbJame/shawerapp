package com.shawerapp.android.screens.report;

import com.google.auto.value.AutoValue;
import com.shawerapp.android.base.BaseFragment;
import com.shawerapp.android.base.BaseKey;

@AutoValue
public abstract class ReportAbuseKey extends BaseKey {

  public static ReportAbuseKey create() {
    return new AutoValue_ReportAbuseKey();
  }

  @Override
  protected BaseFragment createFragment() {
    return ReportAbuseFragment.newInstance();
  }
}
