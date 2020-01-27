package com.shawerapp.android.screens.newresponse;

import com.shawerapp.android.R;
import com.shawerapp.android.base.BaseFragment;
import com.shawerapp.android.screens.container.ContainerContract;
import com.shawerapp.android.screens.viewanswer.ViewAnswerKey;
import javax.inject.Inject;

public final class ComposeResponseViewModel implements ComposeResponseContract.ViewModel {

  private BaseFragment mFragment;
  private ComposeResponseContract.View mView;

  @Inject ContainerContract.View mContainerView;
  @Inject ContainerContract.ViewModel mContainerViewModel;

  //fix me
  private int currentPage = 0;

  @Inject public ComposeResponseViewModel(BaseFragment fragment,
      ComposeResponseContract.View view) {
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
    mContainerView.setToolbarSubTitle("{Lawyer Name}");
    mContainerView.setLeftToolbarButtonImageResource(R.drawable.icon_back);
    mContainerView.setRightToolbarButtonImageResource(R.drawable.ic_icon_navigation_right);
  }

  @Override public void onLeftToolbarButtonClicked() {
    if (currentPage == 0) {
      mContainerViewModel.goBack().subscribe(mContainerViewModel.navigationObserver());
    } else {
      mView.changeViewPagerPage(--currentPage);
    }
  }

  @Override public void onRightToolbarButtonClicked() {
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

    mView.setSecondaryInstructionText(instruction);

    String toolbarTitle;
    String toolbarSubTitle;
    switch (position) {
      default:
      case 0:
        toolbarTitle = "{Sub-Subject Name}";
        toolbarSubTitle = "{Lawyer Name}";
        mContainerView.setRightToolbarButtonImageResource(R.drawable.ic_icon_navigation_right);
        break;
      case 1:
        toolbarTitle = mFragment.getString(R.string.label_compose_question_title_new_case);
        toolbarSubTitle = mFragment.getResources()
            .getStringArray(R.array.label_compose_question_subtitle)[position];
        mContainerView.setToolbarTitle(toolbarTitle);
        mContainerView.setToolbarSubTitle(toolbarSubTitle);
        mContainerView.setRightToolbarButtonImageResource(R.drawable.ic_icon_navigation_right);
        break;
      case 2:
        toolbarTitle = mFragment.getString(R.string.label_compose_question_title_new_case);
        toolbarSubTitle = mFragment.getResources()
            .getStringArray(R.array.label_compose_question_subtitle)[position];
        mContainerView.setRightToolbarButtonImageResource(0);
        break;
    }
    mContainerView.setToolbarTitle(toolbarTitle);
    mContainerView.setToolbarSubTitle(toolbarSubTitle);
  }

  @Override public void onSubmitComposition() {
    mContainerView.showMessage("TODO: Add Success Dialog");
    mContainerViewModel.goTo(ViewAnswerKey.create())
        .subscribe(mContainerViewModel.navigationObserver());
  }
}
