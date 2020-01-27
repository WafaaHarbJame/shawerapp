package com.shawerapp.android.screens.newrequest.step3;

import com.shawerapp.android.R;
import com.shawerapp.android.base.BaseFragment;
import com.shawerapp.android.screens.composer.ComposerFragment;
import com.shawerapp.android.screens.container.ContainerContract;
import com.shawerapp.android.screens.requestlist.RequestListKey;
import javax.inject.Inject;

public final class ComposeRequestViewModel implements ComposeRequestContract.ViewModel {

  private BaseFragment mFragment;
  private ComposeRequestContract.View mView;

  public static int step = 0;

  @Inject ContainerContract.View mContainerView;
  @Inject ContainerContract.ViewModel mContainerViewModel;

  //fix me
  private int currentPage = 0;

  @Inject public ComposeRequestViewModel(BaseFragment fragment,
      ComposeRequestContract.View view) {
    mFragment = fragment;
    mView = view;
  }

  @Override public void onViewCreated() {
    mView.initBindings();
  }

  @Override public void onAfterEnterAnimation() {

  }

  @Override public void onBackButtonClicked() {

  }

  @Override public void setupToolbar() {
    mContainerView.setToolbarTitle("{Sub-Subject Name}");
    mContainerView.setToolbarSubTitle(
        mFragment.getResources().getStringArray(R.array.label_compose_request_subtitle)[0]);
    mContainerView.setLeftToolbarButtonImageResource(R.drawable.icon_back);
    mContainerView.setRightToolbarButtonImageResource(R.drawable.ic_icon_navigation_right);
  }

  @Override public void onLeftToolbarButtonClicked() {
    if (currentPage == 0) {
      mContainerViewModel.goBack().subscribe(mContainerViewModel.navigationObserver());
      ComposerFragment.status = 0;
    } else {
      --step;
      --ComposerFragment.status;
      mView.changeViewPagerPage(--currentPage);
    }
  }

  @Override public void onRightToolbarButtonClicked() {
    ++step;
    ++ComposerFragment.status;
    mView.changeViewPagerPage(++currentPage);
  }

  @Override public void onStart() {

  }

  @Override public void onStop() {

  }

  @Override public void onDetach() {

  }

  @Override public void onDestroy() {

  }

  @Override public void onPageStateChanged(int position) {
    currentPage = position;
    String instruction = mFragment.getResources()
        .getStringArray(R.array.label_compose_secondary_instruction)[position];
    String subtitle =
        mFragment.getResources().getStringArray(R.array.label_compose_request_subtitle)[position];

    mView.setSecondaryInsructionText(instruction);
    mContainerView.setToolbarSubTitle(subtitle);

    //hack fix me
    if (position == 0) {
      mContainerView.setToolbarTitle("{Sub-Subject Name}");
    } else {
      String toolbarTitle = mFragment.getString(R.string.label_compose_request_title);
      mContainerView.setToolbarTitle(toolbarTitle);
    }

    if (position == 2) {
      mContainerView.setRightToolbarButtonImageResource(0);
    } else {
      mContainerView.setRightToolbarButtonImageResource(R.drawable.ic_icon_navigation_right);
    }
  }

  @Override public void onSubmitComposition() {
    mContainerView.showMessage("TODO: Add Success Dialog");
    mContainerViewModel.newTop(RequestListKey.create())
        .subscribe(mContainerViewModel.navigationObserver());
  }
}
