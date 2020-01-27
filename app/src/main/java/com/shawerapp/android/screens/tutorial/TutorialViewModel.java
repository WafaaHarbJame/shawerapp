package com.shawerapp.android.screens.tutorial;

import android.util.Log;

import com.shawerapp.android.R;
import com.shawerapp.android.base.BaseFragment;
import com.shawerapp.android.screens.container.ContainerContract;

import javax.inject.Inject;

public final class TutorialViewModel implements TutorialContract.ViewModel {

    private BaseFragment mFragment;

    private TutorialContract.View mView;

    @Inject
    ContainerContract.View mContainerView;

    @Inject
    ContainerContract.ViewModel mContainerViewModel;

    @Inject
    public TutorialViewModel(BaseFragment fragment,
                             TutorialContract.View view) {
        mFragment = fragment;
        mView = view;
    }

    @Override
    public void onViewCreated() {
        mView.initBindings();
    }

    @Override
    public void onAfterEnterAnimation() {

    }

    @Override
    public void onBackButtonClicked() {
        onLeftToolbarButtonClicked();
    }

    @Override
    public void setupToolbar() {
        mContainerView.setToolbarTitle(mFragment.getString(R.string.label_tutorial_title));
        mContainerView.setToolbarSubTitle(mFragment.getString(R.string.app_name));
        mContainerView.setLeftToolbarButtonImageResource(R.drawable.icon_back);
        mContainerView.setRightToolbarButtonImageResource(-1);
    }

    @Override
    public void onLeftToolbarButtonClicked() {
        mContainerViewModel.goBack()
                .subscribe(mContainerViewModel.navigationObserver());
    }

    @Override
    public void onRightToolbarButtonClicked() {

    }

    @Override
    public void onStart() {

    }

    @Override
    public void onStop() {

    }

    @Override
    public void onDetach() {

    }

    @Override
    public void onDestroy() {

    }
}
