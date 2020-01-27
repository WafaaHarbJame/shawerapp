package com.shawerapp.android.screens.viewanswer;

import com.shawerapp.android.R;
import com.shawerapp.android.base.BaseFragment;
import com.shawerapp.android.screens.container.ContainerContract;
import com.shawerapp.android.screens.newanswer.ComposeAnswerKey;
import com.shawerapp.android.screens.newresponse.ComposeResponseKey;
import javax.inject.Inject;

public class ViewAnswerViewModel implements ViewAnswerContract.ViewModel {

  private BaseFragment mFragment;
  private ViewAnswerContract.View mView;

  @Inject ContainerContract.View mContainerView;
  @Inject ContainerContract.ViewModel mContainerViewModel;

  @Inject public ViewAnswerViewModel(BaseFragment fragment,
      ViewAnswerContract.View view) {
    mFragment = fragment;
    mView = view;
  }

  @Override public void onViewCreated() {

  }

  @Override public void onAfterEnterAnimation() {

  }

  @Override public void onBackButtonClicked() {

  }

  @Override public void setupToolbar() {
    mContainerView.clearToolbarSubtitle();
    mContainerView.clearToolbarTitle();
    mContainerView.setLeftToolbarButtonImageResource(R.drawable.icon_back);
    mContainerView.setRightToolbarButtonImageResource(R.drawable.icon_plain_circle_light);
  }

  @Override public void onLeftToolbarButtonClicked() {
    mContainerViewModel.goBack().subscribe(mContainerViewModel.navigationObserver());
  }

  @Override public void onRightToolbarButtonClicked() {
    mContainerViewModel.goTo(ComposeResponseKey.create()).subscribe(mContainerViewModel.navigationObserver());
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
