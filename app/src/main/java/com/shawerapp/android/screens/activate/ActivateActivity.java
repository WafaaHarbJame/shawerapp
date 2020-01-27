package com.shawerapp.android.screens.activate;

import android.os.Bundle;
import androidx.annotation.Nullable;

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

public class ActivateActivity extends BaseActivity implements ActivateContract.View {

    @Inject
    ActivateContract.ViewModel mViewModel;

    @BindView(R.id.btnOk)
    Button mBtnOk;

    @Override
    public ActivityLifecycle.ViewModel getViewModel() {
        return mViewModel;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        ((ApplicationModel) getApplication()).getAppComponent()
                .plus(new ActivateModule(this, this))
                .inject(this);

        setContentView(R.layout.activity_activate);
        ButterKnife.bind(this);

        super.onCreate(savedInstanceState);
    }

    @Override
    public void initBindings() {
        RxView.clicks(mBtnOk)
                .throttleFirst(1, TimeUnit.SECONDS)
                .compose(bindUntilEvent(ActivityEvent.DESTROY))
                .subscribe(o -> mViewModel.onOKButtonClicked());
    }
}
