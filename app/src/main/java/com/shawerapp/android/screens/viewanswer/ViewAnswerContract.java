package com.shawerapp.android.screens.viewanswer;

import com.shawerapp.android.base.FragmentLifecycle;

public class ViewAnswerContract {

  interface View extends FragmentLifecycle.View {
      void initBindings();
  }

  interface ViewModel extends FragmentLifecycle.ViewModel {

  }
}
