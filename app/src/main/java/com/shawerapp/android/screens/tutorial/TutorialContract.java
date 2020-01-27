package com.shawerapp.android.screens.tutorial;

import com.shawerapp.android.base.FragmentLifecycle;

public final class TutorialContract {

  interface View extends FragmentLifecycle.View {
    void initBindings();
  }

  interface ViewModel extends FragmentLifecycle.ViewModel {

  }
}
