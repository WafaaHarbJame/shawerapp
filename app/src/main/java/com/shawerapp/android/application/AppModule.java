package com.shawerapp.android.application;

import android.app.Application;

import com.shawerapp.android.utils.LoginUtil;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by johnernest on 04/10/2017.
 */
@Module
class AppModule {

    private Application mApp;

    public AppModule(Application app) {
        this.mApp = app;
    }

    @Singleton
    @Provides
    public Application provideApplication() {
        return mApp;
    }

    @Singleton
    @Provides
    public LoginUtil provideLoginUtil() {
        return new LoginUtil(mApp);
    }
}
