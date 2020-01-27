package com.shawerapp.android.screens.newrequest.step2;

import com.shawerapp.android.R;
import com.shawerapp.android.base.BaseFragment;
import com.shawerapp.android.model.RequestFee;
import com.shawerapp.android.screens.newrequest.step3.ComposeRequestKey;
import com.shawerapp.android.screens.container.ContainerContract;
import javax.inject.Inject;

public final class SelectRequestSubSubjectViewModel implements SelectRequestSubSubjectContract.ViewModel {

  private BaseFragment mFragment;

  private SelectRequestSubSubjectContract.View mView;

  @Inject ContainerContract.View mContainerView;

  @Inject ContainerContract.ViewModel mContainerViewModel;

  @Inject public SelectRequestSubSubjectViewModel(BaseFragment fragment,
      SelectRequestSubSubjectContract.View view) {
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
    mContainerView.setToolbarTitle("{Field Name}");
    mContainerView.setToolbarSubTitle(
        mFragment.getString(R.string.label_subtitle_select_subsubject));
    mContainerView.setLeftToolbarButtonImageResource(R.drawable.icon_back);
    mContainerView.setRightToolbarButtonImageResource(-1);
  }

  @Override public void onLeftToolbarButtonClicked() {
    mContainerViewModel.goBack().subscribe(mContainerViewModel.navigationObserver());
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

  @Override public void onRequestFeeClicked(RequestFee newRequestFee) {
    mContainerViewModel.goTo(ComposeRequestKey.create())
        .subscribe(mContainerViewModel.navigationObserver());
  }
}
