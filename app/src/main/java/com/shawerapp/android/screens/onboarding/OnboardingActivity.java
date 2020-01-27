package com.shawerapp.android.screens.onboarding;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.viewpager.widget.ViewPager;

import android.widget.Button;

import com.jakewharton.rxbinding2.view.RxView;
import com.shawerapp.android.R;
import com.shawerapp.android.application.ApplicationModel;
import com.shawerapp.android.base.ActivityLifecycle;
import com.shawerapp.android.base.BaseActivity;
import com.trello.rxlifecycle2.android.ActivityEvent;

import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.relex.circleindicator.CircleIndicator;

public class OnboardingActivity extends BaseActivity implements OnboardingContract.View {

    @Inject
    OnboardingContract.ViewModel mViewModel;

    @BindView(R.id.viewPager)
    ViewPager mViewPager;

    @BindView(R.id.indicator)
    CircleIndicator mCircleIndicator;

    @BindView(R.id.btnRegister)
    Button mBtnRegister;

    @BindView(R.id.btnLogin)
    Button mBtnLogin;

    @Override
    public ActivityLifecycle.ViewModel getViewModel() {
        return mViewModel;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        ((ApplicationModel) getApplication()).getAppComponent()
                .plus(new OnboardingModule(this, this))
                .inject(this);

        setContentView(R.layout.activity_onboarding);
        ButterKnife.bind(this);

        super.onCreate(savedInstanceState);
    }

    @Override
    public void initBindings() {
        WizardPagerAdapter adapter = new WizardPagerAdapter();
        mViewPager.setOffscreenPageLimit(3);
        mViewPager.setAdapter(adapter);
        mCircleIndicator.setViewPager(mViewPager);

        RxView.clicks(mBtnRegister)
                .throttleFirst(1, TimeUnit.SECONDS)
                .compose(bindUntilEvent(ActivityEvent.DESTROY))
                .subscribe(o -> mViewModel.onRegisterButtonClicked());

        RxView.clicks(mBtnLogin)
                .throttleFirst(1, TimeUnit.SECONDS)
                .compose(bindUntilEvent(ActivityEvent.DESTROY))
                .subscribe(o -> mViewModel.onLoginButtonClicked());
    }
}
