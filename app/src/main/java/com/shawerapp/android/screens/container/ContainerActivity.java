package com.shawerapp.android.screens.container;

import android.app.AlertDialog;
import android.os.Bundle;

import androidx.annotation.Nullable;

import android.view.View;
import android.view.ViewGroup;
import android.widget.Checkable;
import android.widget.ImageButton;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;

import com.jakewharton.rxbinding2.view.RxView;
import com.shawerapp.android.R;
import com.shawerapp.android.application.ApplicationModel;
import com.shawerapp.android.base.ActivityLifecycle;
import com.shawerapp.android.base.BaseActivity;
import com.shawerapp.android.base.BaseFragment;
import com.shawerapp.android.custom.views.ArabicTextView;
import com.shawerapp.android.custom.views.IndeterminateTransparentProgressDialog;
import com.shawerapp.android.utils.CommonUtils;
import com.trello.rxlifecycle2.android.ActivityEvent;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Action;
import timber.log.Timber;

import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

/**
 * Created by john.ernest on 2/16/18.
 */

public class ContainerActivity extends BaseActivity implements ContainerContract.View {

    private static final long ANIM_DURATION = 150;

    public static final String EXTRA_TYPE = "type";

    public static final String TYPE_INDIVIDUAL = "INDIVIDUAL";

    public static final String TYPE_COMMERCIAL = "COMMERCIAL";

    public static final String TYPE_LAWYER = "LAWYER";

    @BindView(R.id.toolbar)
    ViewGroup mToolbar;

    @BindView(R.id.container)
    ViewGroup mActivityView;

    @BindView(R.id.btnToolbarLeft)
    ImageButton mBtnToolbarLeft;

    @BindView(R.id.btnToolbarRight)
    ImageButton mBtnToolbarRight;

    @BindView(R.id.btnTextLeft)
    TextView mBtnLeftText;

    @BindView(R.id.btnTextRight)
    TextView mBtnRightText;

    @BindView(R.id.toolbarTitle)
    TextView mToolbarTitle;

    @BindView(R.id.toolbarSubTitle)
    TextView mToolbarSubTitle;

    @BindView(R.id.tabProfile)
    View mTabProfile;

    @BindView(R.id.tabLawyers)
    View mTabLawyers;

    @BindView(R.id.tabShawer)
    View mTabShawer;

    @BindView(R.id.tabPractice)
    View mTabPractice;

    @BindView(R.id.tabMore)
    View mTabMore;

    @BindView(R.id.bottomTabs)
    View mBottomTabs;

    @Inject
    ContainerContract.ViewModel mViewModel;

    private ContainerComponent mContainerComponent;

    private IndeterminateTransparentProgressDialog mIndeterminateTransparentProgressDialog;

    private Checkable mSelectedTab;

    @Override
    public ActivityLifecycle.ViewModel getViewModel() {
        return mViewModel;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        mContainerComponent = ((ApplicationModel) getApplication()).getAppComponent()
                .plus(new ContainerModule(this, this));
        mContainerComponent.inject(this);

        setContentView(R.layout.activity_container);
        ButterKnife.bind(this);
        super.onCreate(savedInstanceState);
    }

    @Override
    public ViewGroup getActivityView() {
        return mActivityView;
    }

    @Override
    public void initBindings(String type) {
        if (type.equals(TYPE_LAWYER)) {
            mTabLawyers.setVisibility(View.GONE);
            mTabPractice.setVisibility(View.GONE);
        } else {
            mTabLawyers.setVisibility(View.VISIBLE);
            mTabPractice.setVisibility(View.VISIBLE);
        }

        RxView.globalLayouts(mActivityView)
                .compose(bindUntilEvent(ActivityEvent.DESTROY))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(o -> mViewModel.onGlobalLayoutChange(mActivityView));

        RxView.clicks(mBtnToolbarLeft)
                .compose(bindUntilEvent(ActivityEvent.DESTROY))
                .throttleFirst(1, TimeUnit.SECONDS)
                .subscribe(o -> mViewModel.onLeftToolbarButtonClicked());

        RxView.clicks(mBtnToolbarRight)
                .compose(bindUntilEvent(ActivityEvent.DESTROY))
                .throttleFirst(1, TimeUnit.SECONDS)
                .subscribe(o -> mViewModel.onRightToolbarButtonClicked());

        RxView.clicks(mBtnLeftText)
                .compose(bindUntilEvent(ActivityEvent.DESTROY))
                .throttleFirst(1, TimeUnit.SECONDS)
                .subscribe(o -> mViewModel.onLeftToolbarButtonClicked());

        RxView.clicks(mBtnRightText)
                .compose(bindUntilEvent(ActivityEvent.DESTROY))
                .throttleFirst(1, TimeUnit.SECONDS)
                .subscribe(o -> mViewModel.onRightToolbarButtonClicked());

        Observable.mergeArray(
                RxView.clicks(mTabProfile).map(o -> mTabProfile.getId()),
                RxView.clicks(mTabLawyers).map(o -> mTabLawyers.getId()),
                RxView.clicks(mTabShawer).map(o -> mTabShawer.getId()),
                RxView.clicks(mTabPractice).map(o -> mTabPractice.getId()),
                RxView.clicks(mTabMore).map(o -> mTabMore.getId()))
                .compose(bindUntilEvent(ActivityEvent.DESTROY))
                .throttleFirst(1, TimeUnit.SECONDS)
                .subscribe(mViewModel::onTabClicked);


    }

    @Override
    public void selectTab(int tabId) {
        if (mSelectedTab != null) {
            mSelectedTab.setChecked(false);
        }
        mSelectedTab = findViewById(tabId);
        mSelectedTab.setChecked(true);
    }

    @Override
    public void showMessage(String message, boolean isError) {
        if (isError) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.DialogTheme);
            builder.setTitle(R.string.title_error);
            builder.setMessage(message);
            builder.setPositiveButton(R.string.ok_caps, (dialog, which) -> dialog.dismiss());
            builder.show();
        } else {
            showMessage(message);
        }
    }

    @Override
    public void showMessage(String title, String message, boolean isError) {
        if (isError) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.DialogTheme);
            builder.setTitle(title);
            builder.setMessage(message);
            builder.setPositiveButton(R.string.ok_caps, (dialog, which) -> dialog.dismiss());
            builder.show();
        } else {
            showMessage(message);
        }
    }

    @Override
    public void showMessage(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.DialogTheme);
        builder.setMessage(message);
        builder.setPositiveButton(R.string.ok_caps, (dialog, which) -> dialog.dismiss());
        builder.show();
    }

    @Override
    public void showConfirmationMessage(String message, String confirmText, String cancelText, Action onConfirm) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.DialogTheme);
        builder.setMessage(message);
        builder.setPositiveButton(confirmText, (dialog, which) -> {
            dialog.dismiss();
            try {
                onConfirm.run();
            } catch (Exception e) {
                Timber.e(CommonUtils.getExceptionString(e));
            }
        });
        builder.setNegativeButton(cancelText, (dialog, which) -> dialog.dismiss());
        builder.show();
    }

    @Override
    public void exitScreen() {
        finish();
    }

    @Override
    public BaseFragment getCurrentFragmentInFrame() {
        return (BaseFragment) getSupportFragmentManager().findFragmentById(R.id.container);
    }

    public ContainerComponent getContainerComponent() {
        return mContainerComponent;
    }

    @Override
    public void showLoadingIndicator() {
        if (mIndeterminateTransparentProgressDialog == null ||
                !mIndeterminateTransparentProgressDialog.isShowing()) {
            runOnUiThread(() -> mIndeterminateTransparentProgressDialog = IndeterminateTransparentProgressDialog.show(this, true, false));
        }
    }

    @Override
    public void hideLoadingIndicator() {
        if (mIndeterminateTransparentProgressDialog != null || mIndeterminateTransparentProgressDialog.isShowing()) {
            mIndeterminateTransparentProgressDialog.dismiss();
        }
    }

    @Override
    public void hideRightToolbarButton() {
        mBtnToolbarRight.setVisibility(View.INVISIBLE);
        mBtnRightText.setVisibility(View.GONE);
    }

    @Override
    public void ShowRightToolbarButton() {
        mBtnToolbarRight.setVisibility(View.VISIBLE);
        mBtnRightText.setVisibility(View.GONE);
    }

    @Override
    public void ShowRight_ToolbarButton() {
        mBtnToolbarRight.setVisibility(View.VISIBLE);
        mBtnRightText.setVisibility(View.GONE);
    }

    @Override
    public void ShowRight__ToolbarButton() {
        mBtnToolbarRight.setVisibility(View.GONE);
        mBtnRightText.setVisibility(View.VISIBLE);
    }

    @Override
    public void setToolbarTitle(String title) {
        mToolbarTitle.setText(title);
    }

    @Override
    public void setToolbarSubTitle(String subTitle) {
        mToolbarSubTitle.setText(subTitle);
        mToolbarSubTitle.setVisibility(View.VISIBLE);
    }

    @Override
    public void clearToolbarTitle() {
        mToolbarTitle.setText("");
    }

    @Override
    public void clearToolbarSubtitle() {
        mToolbarSubTitle.setText("");
        mToolbarSubTitle.setVisibility(View.GONE);
    }

    @Override
    public void setLeftToolbarButtonImageResource(int resId) {
        mBtnToolbarLeft.setImageResource(resId);
    }

    @Override
    public void setRightToolbarButtonImageResource(int resId) {
        mBtnToolbarRight.setImageResource(resId);
//        mBtnToolbarRight.setVisibility(View.VISIBLE);
    }

    @Override
    public void setLeftToolbarTextResource(int resId) {
        mBtnLeftText.setText(resId);
        mBtnLeftText.setVisibility(View.VISIBLE);
        mBtnToolbarLeft.setVisibility(View.GONE);
    }

    @Override
    public void setRightToolbarTextResource(int resId) {
        mBtnRightText.setText(resId);
        mBtnRightText.setVisibility(View.VISIBLE);
        mBtnToolbarRight.setVisibility(View.GONE);
    }

    @Override
    public void hideRightToolbarTextButton() {
//        mBtnToolbarRight.setVisibility(View.INVISIBLE);
        mBtnRightText.setVisibility(View.INVISIBLE);
    }

    @Override
    public void hideLeftText() {
        mBtnLeftText.setVisibility(View.GONE);
        mBtnToolbarLeft.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideRightText() {
        mBtnRightText.setVisibility(View.GONE);
        mBtnToolbarRight.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideRightText_() {
        mBtnRightText.setVisibility(View.GONE);
        mBtnToolbarRight.setVisibility(View.GONE);
    }

    @Override
    public View getRightToolbarButton() {
        return mBtnToolbarRight;
    }

    @Override
    public void hideTabs() {
        ViewGroup.LayoutParams layoutParams = mBottomTabs.getLayoutParams();
        layoutParams.height = 0;
        mBottomTabs.setLayoutParams(layoutParams);
    }

    @Override
    public void showTabs() {
        ViewGroup.LayoutParams layoutParams = mBottomTabs.getLayoutParams();
        layoutParams.height = getResources().getDimensionPixelSize(R.dimen.size_70);
        mBottomTabs.setLayoutParams(layoutParams);
    }

    @Override
    public ViewGroup getToolbar() {
        return mToolbar;
    }
}
