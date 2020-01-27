package com.shawerapp.android.backend.base;

import android.app.Application;

import com.shawerapp.android.backend.checkout.CheckoutBillingImpl;
import com.shawerapp.android.backend.firebase.FirebaseAuthFrameworkImpl;
import com.shawerapp.android.backend.firebase.FirebaseFileFrameworkImpl;
import com.shawerapp.android.backend.firebase.FirestoreRealTimeDataFrameworkImpl;
import com.shawerapp.android.backend.retrofit.RetrofitRestFrameworkImpl;
import com.shawerapp.android.backend.retrofit.RetrofitRestFrameworkImplCheckPayment;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by john.ernest on 05/10/2017.
 */
@Module
public class BackendModule {

    @Singleton
    @Provides
    public ConnectivityUtils providesBackendUtils(Application application) {
        return new ConnectivityUtils(application);
    }

    @Singleton
    @Provides
    public RealTimeDataFramework providesRTDataFramework(FirestoreRealTimeDataFrameworkImpl rtDataFramework) {
        return rtDataFramework;
    }

    @Singleton
    @Provides
    public AuthFramework providesAuthFramework(FirebaseAuthFrameworkImpl authFramework) {
        return authFramework;
    }

    @Singleton
    @Provides
    public FileFramework providesFileFramework(FirebaseFileFrameworkImpl fileFramework) {
        return fileFramework;
    }

    @Singleton
    @Provides
    public RestFramework providesRestFramework(RetrofitRestFrameworkImpl restFramework) {
        return restFramework;
    }

    @Singleton
    @Provides
    public RestFramework_ providesRestFramework_(RetrofitRestFrameworkImplCheckPayment restFramework) {
        return restFramework;
    }

    @Singleton
    @Provides
    public BillingFramework providesBillingFramework(CheckoutBillingImpl billingFramework) {
        return billingFramework;
    }
}
