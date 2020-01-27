package com.shawerapp.android.screens.settings;

import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Handler;

import com.google.firebase.messaging.FirebaseMessaging;
import com.shawerapp.android.BuildConfig;
import com.shawerapp.android.R;
import com.shawerapp.android.autovalue.LawyerUser;
import com.shawerapp.android.backend.base.AuthFramework;
import com.shawerapp.android.backend.base.RealTimeDataFramework;
import com.shawerapp.android.base.BaseFragment;
import com.shawerapp.android.screens.Invoices.InvoicesKey;
import com.shawerapp.android.screens.container.ContainerContract;
import com.shawerapp.android.screens.login.LoginActivity;
import com.shawerapp.android.screens.purchase.PurchaseCoinsKey;
import com.shawerapp.android.screens.tutorial.TutorialKey;
import com.shawerapp.android.utils.AnimationUtils;
import com.shawerapp.android.utils.CommonUtils;
import com.shawerapp.android.utils.LocaleHelper;
import com.shawerapp.android.utils.LoginUtil;

import javax.inject.Inject;

public final class SettingsViewModel implements SettingsContract.ViewModel {

    private BaseFragment mFragment;

    private SettingsContract.View mView;

    @Inject
    ContainerContract.ViewModel mContainerViewModel;

    @Inject
    ContainerContract.View mContainerView;

    @Inject
    RealTimeDataFramework mRTDataFramework;

    @Inject
    AuthFramework mAuthFramework;

    @Inject
    LoginUtil mLoginUtil;

    private boolean isSetup;

    @Inject
    public SettingsViewModel(BaseFragment fragment, SettingsContract.View view) {
        mFragment = fragment;
        mView = view;
    }

    @Override
    public void onViewCreated() {
        isSetup = true;
        mView.initBindings();

        mView.setVersion(BuildConfig.VERSION_NAME);

        mRTDataFramework.retrieveUserCoins()
                .doOnSubscribe(disposable -> mContainerView.showLoadingIndicator())
                .doFinally(() -> mContainerView.hideLoadingIndicator())
                .subscribe(coins -> {
                    String remainingCoins = mFragment.getString(R.string.label_settings_remaining_coins, CommonUtils.formatNumber(coins));
                    mView.setRemainingCoins(remainingCoins);
                });

        mView.setLanguageArabic(LocaleHelper.getLanguage(mFragment.getContext()).equals("ar"));

        if (mLoginUtil.getUserRole().equalsIgnoreCase(LawyerUser.ROLE_VALUE)) {
            mView.hideAddCoins();
        }
        isSetup = false;
    }

    @Override
    public void onAfterEnterAnimation() {

    }

    @Override
    public void onBackButtonClicked() {

    }

    @Override
    public void setupToolbar() {
        mContainerView.clearToolbarSubtitle();
        mContainerView.clearToolbarTitle();
        mContainerView.setToolbarTitle(mFragment.getString(R.string.app_name).toUpperCase());
        mContainerView.setLeftToolbarButtonImageResource(R.drawable.icon_info);
        mContainerView.setRightToolbarButtonImageResource(-1);
    }

    @Override
    public void onLeftToolbarButtonClicked() {
        mContainerViewModel
                .goTo(TutorialKey.create())
                .subscribe(mContainerViewModel.navigationObserver());
    }

    @Override
    public void onRightToolbarButtonClicked() {
        /* TODO Disabled reported users
        mContainerViewModel
                .goTo(ReportAbuseKey.create())
                .subscribe(mContainerViewModel.navigationObserver());*/
    }

    @Override
    public void onAddCoinsClicked() {
        mContainerViewModel
                .goTo(PurchaseCoinsKey.create())
                .subscribe(mContainerViewModel.navigationObserver());
    }

    @Override
    public void onInvoicesClicked() {
        mContainerViewModel
                .goTo(InvoicesKey.create())
                .subscribe(mContainerViewModel.navigationObserver());
    }

    @Override
    public void onSignOutClicked() {
        mContainerView.showConfirmationMessage(
                mFragment.getString(R.string.logout_confirm),
                mFragment.getString(R.string.yes),
                mFragment.getString(R.string.no),
                () -> mAuthFramework
                        .logout()
                        .doOnSubscribe(disposable -> mContainerView.showLoadingIndicator())
                        .doFinally(() -> mContainerView.hideLoadingIndicator())
                        .subscribe(() -> {
                            FirebaseMessaging.getInstance().unsubscribeFromTopic("individual");
                            FirebaseMessaging.getInstance().unsubscribeFromTopic("lawyer");
                            Intent intent = new Intent(mFragment.getContext(), LoginActivity.class);
                            mFragment.getContext().startActivity(intent);
                            mFragment.getActivity().finish();
                            AnimationUtils.overridePendingTransition(mFragment.getActivity(), AnimationUtils.ANIM_STYLE.SLIDE_IN_FROM_LEFT);
                        }));
    }

    @Override
    public void setLanguage(Boolean isArabic) {
        if (!isSetup) {
            LocaleHelper.setLocale(mFragment.getActivity(), isArabic ? "ar" : "en");

            mContainerView.showLoadingIndicator();
            new Handler().postDelayed(() -> {
                mContainerView.hideLoadingIndicator();

                new Handler().postDelayed(() -> {
                    Intent launchIntent = mFragment.getActivity().getBaseContext().getPackageManager().
                            getLaunchIntentForPackage(mFragment.getActivity().getBaseContext().getPackageName());
                    launchIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    launchIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    mFragment.startActivity(launchIntent);
                    mFragment.getActivity().finish();
                }, 300);
            }, 2000);
        }
    }

    @Override
    public void onSupportClicked() {
        Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                "mailto", "support@shawerapp.com", null));
        mFragment.startActivity(Intent.createChooser(emailIntent, "Send email..."));
    }

    @Override
    public void onRatingClicked() {
        final String appPackageName = mFragment.getContext().getPackageName();
        try {
            mFragment.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
        } catch (android.content.ActivityNotFoundException anfe) {
            mFragment.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
        }
    }

    @Override
    public void onTermsClicked() {
        String url = LocaleHelper.getLanguage(mFragment.getContext()).equals(LocaleHelper.ARABIC) ? "http://www.shawerapp.com/TermsConditions.html" : "http://www.shawerapp.com/TermsConditions_en.html";
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(url));
        mFragment.startActivity(intent);
    }

    @Override
    public void onPrivacyClicked() {
        String url = LocaleHelper.getLanguage(mFragment.getContext()).equals(LocaleHelper.ARABIC) ? "http://www.shawerapp.com/PrivacyPolicy.html" : "http://www.shawerapp.com/PrivacyPolicy_en.html";
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(url));
        mFragment.startActivity(intent);
    }

    @Override
    public void onShareClicked() {
        final String appPackageName = mFragment.getContext().getPackageName();

        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT, "https://play.google.com/store/apps/details?id=" + appPackageName);
        mFragment.startActivity(Intent.createChooser(intent, "Share URL"));
    }

    @Override
    public void onSocialFacebookClicked() {
        String url = "https://www.facebook.com/shawerapp/";
        Uri uri = Uri.parse(url);
        try {
            PackageManager pm = mFragment.getContext().getPackageManager();
            ApplicationInfo applicationInfo = pm.getApplicationInfo("com.facebook.katana", 0);
            if (applicationInfo.enabled) {
                uri = Uri.parse("fb://facewebmodal/f?href=" + url);
            }
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            mFragment.startActivity(intent);
        } catch (PackageManager.NameNotFoundException ignored) {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            mFragment.startActivity(intent);
        }
    }

    @Override
    public void onSocialTwitterClicked() {
        String url = "https://twitter.com/shawerapp";
        Uri uri = Uri.parse(url);
        try {
            PackageManager pm = mFragment.getContext().getPackageManager();
            ApplicationInfo applicationInfo = pm.getApplicationInfo("com.twitter.android", 0);
            if (applicationInfo.enabled) {
                uri = Uri.parse("twitter://user?screen_name=shawerapp");
            }
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            mFragment.startActivity(intent);
        } catch (PackageManager.NameNotFoundException ignored) {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            mFragment.startActivity(intent);
        }
    }

    @Override
    public void onSocialInstagramClicked() {
        String url = "https://www.instagram.com/shawerapp/";
        Uri uri = Uri.parse(url);
        try {
            PackageManager pm = mFragment.getContext().getPackageManager();
            ApplicationInfo applicationInfo = pm.getApplicationInfo("com.instagram.android", 0);
            if (applicationInfo.enabled) {
                uri = Uri.parse("http://instagram.com/_u/shawerapp");
            }
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            mFragment.startActivity(intent);
        } catch (PackageManager.NameNotFoundException ignored) {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            mFragment.startActivity(intent);
        }
    }

    @Override
    public void onSocialYoutubeClicked() {
        String url = "https://www.youtube.com/channel/UCKGXY47UPgk714yx625wWvg/";
        Uri uri = Uri.parse(url);
        try {
            PackageManager pm = mFragment.getContext().getPackageManager();
            ApplicationInfo applicationInfo = pm.getApplicationInfo("com.google.android.youtube", 0);
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            if (applicationInfo.enabled) {
                intent.setPackage("com.google.android.youtube");
            }
            intent.setData(Uri.parse(url));
            mFragment.startActivity(intent);
        } catch (PackageManager.NameNotFoundException ignored) {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            mFragment.startActivity(intent);
        }
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
