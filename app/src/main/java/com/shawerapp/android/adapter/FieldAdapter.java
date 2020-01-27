package com.shawerapp.android.adapter;

import androidx.annotation.Nullable;

import com.shawerapp.android.adapter.item.FieldFlexible;

import eu.davidea.flexibleadapter.FlexibleAdapter;
import java.util.List;

public class FieldAdapter extends FlexibleAdapter<FieldFlexible> {

  public FieldAdapter(
          @Nullable List<FieldFlexible> items, @Nullable Object listeners) {
    super(items, listeners);
  }
}
