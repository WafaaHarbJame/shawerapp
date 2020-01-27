package com.shawerapp.android.screens.conversation;

import com.shawerapp.android.base.FragmentLifecycle;

public class ConversationContract {

  interface View extends FragmentLifecycle.View {
      void initBindings();
  }

  interface ViewModel extends FragmentLifecycle.ViewModel {

  }
}
