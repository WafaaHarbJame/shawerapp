package com.shawerapp.android.adapter;

import androidx.annotation.Nullable;
import com.shawerapp.android.adapter.item.QuestionFlexible;
import eu.davidea.flexibleadapter.FlexibleAdapter;
import java.util.List;

public class SubSubjectCaseAdapter extends FlexibleAdapter<QuestionFlexible> {

  public SubSubjectCaseAdapter(
          @Nullable List<QuestionFlexible> items, @Nullable Object listeners) {
    super(items, listeners);
  }
}
