package com.shawerapp.android.application;

import android.app.Application;

import com.shawerapp.android.backend.base.AuthFramework;
import com.shawerapp.android.backend.base.BackendModule;
import com.shawerapp.android.backend.base.BillingFramework;
import com.shawerapp.android.backend.base.FileFramework;
import com.shawerapp.android.backend.base.RealTimeDataFramework;
import com.shawerapp.android.backend.base.RestFramework;
import com.shawerapp.android.backend.base.RestFramework_;
import com.shawerapp.android.screens.activate.ActivateComponent;
import com.shawerapp.android.screens.activate.ActivateModule;
import com.shawerapp.android.screens.container.ContainerComponent;
import com.shawerapp.android.screens.container.ContainerModule;
import com.shawerapp.android.screens.login.LoginComponent;
import com.shawerapp.android.screens.login.LoginModule;
import com.shawerapp.android.screens.onboarding.OnboardingComponent;
import com.shawerapp.android.screens.onboarding.OnboardingModule;
import com.shawerapp.android.screens.profile.lawyer.view.LawyerViewComponent;
import com.shawerapp.android.screens.profile.lawyer.view.LawyerViewModule;
import com.shawerapp.android.screens.resetpassword.request.ResetPasswordRequestComponent;
import com.shawerapp.android.screens.resetpassword.request.ResetPasswordRequestModule;
import com.shawerapp.android.screens.resetpassword.sent.ResetPasswordSentComponent;
import com.shawerapp.android.screens.resetpassword.sent.ResetPasswordSentModule;
import com.shawerapp.android.screens.signup.SignupComponent;
import com.shawerapp.android.screens.signup.SignupModule;
import com.shawerapp.android.screens.splash.SplashComponent;
import com.shawerapp.android.screens.splash.SplashModule;
import com.shawerapp.android.screens.validate.ValidateComponent;
import com.shawerapp.android.screens.validate.ValidateModule;

import javax.inject.Singleton;

import dagger.Component;

/**
 * Created by johnernest on 04/10/2017.
 */
@Singleton
@Component(modules = {AppModule.class, BackendModule.class})
public interface AppComponent {
    void inject(ApplicationModel app);

    void inject(AppFirebaseInstanceIDService service);

    void inject(AppFirebaseMessagingService appFirebaseMessagingService);

    Application application();

    RealTimeDataFramework realTimeDataFramework();

    AuthFramework authFramework();

    FileFramework fileFramework();

    RestFramework restFramework();

    RestFramework_ restFramework_();

    BillingFramework billingFramework();

    SplashComponent plus(SplashModule splashModule);

    OnboardingComponent plus(OnboardingModule onboardingModule);

    SignupComponent plus(SignupModule signupModule);

    ActivateComponent plus(ActivateModule activateModule);

    ValidateComponent plus(ValidateModule validateModule);

    LoginComponent plus(LoginModule validateModule);

    ResetPasswordRequestComponent plus(ResetPasswordRequestModule resetPasswordRequestModule);

    ResetPasswordSentComponent plus(ResetPasswordSentModule resetPasswordSentModule);

    ContainerComponent plus(ContainerModule containerModule);
}
