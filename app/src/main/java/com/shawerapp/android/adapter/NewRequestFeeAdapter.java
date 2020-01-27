package com.shawerapp.android.adapter;

import androidx.annotation.Nullable;
import com.shawerapp.android.adapter.item.NewRequestFeeItem;
import eu.davidea.flexibleadapter.FlexibleAdapter;
import java.util.List;

public class NewRequestFeeAdapter extends FlexibleAdapter<NewRequestFeeItem> {

  public NewRequestFeeAdapter(
      @Nullable List<NewRequestFeeItem> items, @Nullable Object listeners) {
    super(items, listeners);
  }
}
