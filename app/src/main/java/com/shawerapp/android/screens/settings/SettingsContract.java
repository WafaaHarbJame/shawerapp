package com.shawerapp.android.screens.settings;

import com.shawerapp.android.base.FragmentLifecycle;

public final class SettingsContract {

    interface View extends FragmentLifecycle.View {
        void initBindings();

        void setRemainingCoins(String remainingCoins);

        void setVersion(String versionName);

        void setLanguageArabic(boolean languageArabic);

        void hideAddCoins();
    }

    interface ViewModel extends FragmentLifecycle.ViewModel {
        void onAddCoinsClicked();

        void onInvoicesClicked();

        void onSignOutClicked();

        void setLanguage(Boolean isArabic);

        void onSupportClicked();

        void onRatingClicked();

        void onTermsClicked();

        void onPrivacyClicked();

        void onShareClicked();

        void onSocialFacebookClicked();

        void onSocialTwitterClicked();

        void onSocialInstagramClicked();

        void onSocialYoutubeClicked();
    }
}
