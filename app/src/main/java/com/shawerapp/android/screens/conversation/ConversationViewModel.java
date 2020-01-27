package com.shawerapp.android.screens.conversation;

import com.shawerapp.android.base.BaseFragment;
import com.shawerapp.android.screens.container.ContainerContract;
import javax.inject.Inject;

public class ConversationViewModel implements ConversationContract.ViewModel {

  private BaseFragment mFragment;
  private ConversationContract.View mView;

  @Inject ContainerContract.View mContainerView;
  @Inject ContainerContract.ViewModel mContainerViewModel;

  @Inject public ConversationViewModel(BaseFragment mFragment,
      ConversationContract.View mView) {
    mFragment = mFragment;
    mView = mView;
  }

  @Override public void onViewCreated() {

  }

  @Override public void onAfterEnterAnimation() {

  }

  @Override public void onBackButtonClicked() {

  }

  @Override public void setupToolbar() {

  }

  @Override public void onLeftToolbarButtonClicked() {

  }

  @Override public void onRightToolbarButtonClicked() {

  }

  @Override public void onStart() {

  }

  @Override public void onStop() {

  }

  @Override public void onDetach() {

  }

  @Override public void onDestroy() {

  }
}
