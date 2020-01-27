package com.shawerapp.android.screens.newresponse;

import com.shawerapp.android.base.FragmentLifecycle;

public final class ComposeResponseContract {

  interface View extends FragmentLifecycle.View {
    void initBindings();

    void setSecondaryInstructionText(String text);

    void changeViewPagerPage(int page);
  }

  interface ViewModel extends FragmentLifecycle.ViewModel {
    void onPageStateChanged(int position);

    void onSubmitComposition();
  }
}
