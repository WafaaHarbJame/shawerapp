package com.shawerapp.android.screens.newrequest.step1;

import com.shawerapp.android.base.FragmentLifecycle;
import com.shawerapp.android.autovalue.Field;
import eu.davidea.flexibleadapter.FlexibleAdapter;

public class SelectRequestFieldContract {

  interface View extends FragmentLifecycle.View, FlexibleAdapter.OnItemClickListener {
    void initBindings();
  }

  interface ViewModel extends FragmentLifecycle.ViewModel {
    void onFieldClicked(Field field);
  }
}
