package com.shawerapp.android.adapter;

import androidx.annotation.Nullable;
import com.shawerapp.android.adapter.item.SubSubjectFlexible;
import eu.davidea.flexibleadapter.FlexibleAdapter;
import java.util.List;

public class SubSubjectAdapter extends FlexibleAdapter<SubSubjectFlexible> {

  public SubSubjectAdapter(
          @Nullable List<SubSubjectFlexible> items, @Nullable Object listeners) {
    super(items, listeners);
  }
}
