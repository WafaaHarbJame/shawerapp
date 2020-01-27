package com.shawerapp.android.adapter;

import androidx.annotation.Nullable;

import com.shawerapp.android.adapter.item.PracticeRequestFlexible;

import eu.davidea.flexibleadapter.FlexibleAdapter;
import java.util.List;

public class PracticeRequestAdapter extends FlexibleAdapter<PracticeRequestFlexible> {

  public PracticeRequestAdapter(
          @Nullable List<PracticeRequestFlexible> items, @Nullable Object listeners) {
    super(items, listeners);
  }
}
