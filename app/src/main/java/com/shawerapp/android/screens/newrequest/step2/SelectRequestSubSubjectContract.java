package com.shawerapp.android.screens.newrequest.step2;

import com.shawerapp.android.base.FragmentLifecycle;
import com.shawerapp.android.model.RequestFee;
import eu.davidea.flexibleadapter.FlexibleAdapter;

public final class SelectRequestSubSubjectContract {

  interface View extends FragmentLifecycle.View, FlexibleAdapter.OnItemClickListener {
    void initBindings();
  }

  interface ViewModel extends FragmentLifecycle.ViewModel {

    void onRequestFeeClicked(RequestFee newRequestFee);
  }
}
