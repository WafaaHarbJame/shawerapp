package com.shawerapp.android.screens.container;

import android.app.Application;

import com.shawerapp.android.backend.base.AuthFramework;
import com.shawerapp.android.backend.base.BillingFramework;
import com.shawerapp.android.backend.base.FileFramework;
import com.shawerapp.android.backend.base.RealTimeDataFramework;
import com.shawerapp.android.backend.base.RestFramework;
import com.shawerapp.android.base.ActivityScope;
import com.shawerapp.android.utils.LoginUtil;

import dagger.Subcomponent;

/**
 * Created by john.ernest on 2/16/18.
 */
@ActivityScope
@Subcomponent(modules = ContainerModule.class)
public interface ContainerComponent {
    void inject(ContainerActivity activity);

    ContainerContract.View containerView();

    ContainerContract.ViewModel containerViewModel();

    LoginUtil loginUtil();

    Application application();

    RealTimeDataFramework realTimeDataFramework();

    AuthFramework authFramework();

    FileFramework fileFramework();

    RestFramework restFramework();

    BillingFramework billingFramework();
}
