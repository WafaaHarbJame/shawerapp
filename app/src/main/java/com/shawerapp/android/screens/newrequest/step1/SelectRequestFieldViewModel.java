package com.shawerapp.android.screens.newrequest.step1;

import com.shawerapp.android.R;
import com.shawerapp.android.base.BaseFragment;
import com.shawerapp.android.autovalue.Field;
import com.shawerapp.android.screens.container.ContainerContract;
import com.shawerapp.android.screens.newrequest.step2.SelectRequestSubSubjectKey;
import javax.inject.Inject;

public class SelectRequestFieldViewModel implements SelectRequestFieldContract.ViewModel {

  private BaseFragment mFragment;
  private SelectRequestFieldContract.View mView;

  @Inject ContainerContract.View mContainerView;
  @Inject ContainerContract.ViewModel mContainerViewModel;

  @Inject public SelectRequestFieldViewModel(BaseFragment fragment,
      SelectRequestFieldContract.View view) {
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
    mContainerView.setToolbarTitle(mFragment.getString(R.string.label_title_new_request));
    mContainerView.setToolbarSubTitle(mFragment.getString(R.string.label_subtitle_field_selection));
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

  @Override public void onFieldClicked(Field field) {
    mContainerViewModel.goTo(SelectRequestSubSubjectKey.create())
        .subscribe(mContainerViewModel.navigationObserver());
  }
}
