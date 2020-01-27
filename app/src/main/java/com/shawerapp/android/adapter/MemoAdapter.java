package com.shawerapp.android.adapter;

import androidx.annotation.Nullable;

import com.shawerapp.android.adapter.item.MemoFlexible;

import eu.davidea.flexibleadapter.FlexibleAdapter;
import java.util.List;

public class MemoAdapter extends FlexibleAdapter<MemoFlexible> {

  public MemoAdapter(
      @Nullable List<MemoFlexible> items) {
    super(items);
  }
}
