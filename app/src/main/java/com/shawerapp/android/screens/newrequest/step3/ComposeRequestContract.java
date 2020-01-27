package com.shawerapp.android.screens.newrequest.step3;

import com.shawerapp.android.base.FragmentLifecycle;

public final class ComposeRequestContract {

  interface View extends FragmentLifecycle.View {
    void initBindings();

    void setSecondaryInsructionText(String text);

    void changeViewPagerPage(int page);
  }

  interface ViewModel extends FragmentLifecycle.ViewModel {
    void onPageStateChanged(int position);

    void onSubmitComposition();
  }
}
