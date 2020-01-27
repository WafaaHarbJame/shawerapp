package com.shawerapp.android.screens.settings;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SwitchCompat;
import androidx.appcompat.widget.Toolbar;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;

import com.jakewharton.rxbinding2.view.RxView;
import com.jakewharton.rxbinding2.widget.RxCompoundButton;
import com.shawerapp.android.R;
import com.shawerapp.android.base.BaseFragment;
import com.shawerapp.android.base.FragmentLifecycle;
import com.shawerapp.android.screens.container.ContainerActivity;
import com.shawerapp.android.screens.container.ContainerContract;
import com.trello.rxlifecycle2.android.FragmentEvent;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

public final class SettingsFragment extends BaseFragment implements SettingsContract.View {

    public static SettingsFragment newInstance() {
        return new SettingsFragment();
    }

    @Inject
    SettingsContract.ViewModel viewModel;

    @Inject
    ContainerContract.View mContainerView;

    @BindView(R.id.remainingCoinsTextView)
    TextView remainingCoinsTextView;

    @BindView(R.id.invoicesBtn)
    Button invoicesBtn;

    @BindView(R.id.addCoinsTextView)
    View addCoinsView;

    @BindView(R.id.signOutImageView)
    View signOutImageView;

    @BindView(R.id.appVersionTextView)
    TextView appVersionTextView;

    @BindView(R.id.languageSwitch)
    SwitchCompat languageSwitch;

    @BindView(R.id.feedbackContainer)
    LinearLayout feedbackContainer;

    @BindView(R.id.supportContainer)
    LinearLayout supportContainer;

    @BindView(R.id.ratingContainer)
    LinearLayout ratingContainer;

    @BindView(R.id.termsContainer)
    LinearLayout termsContainer;

    @BindView(R.id.privacyContainer)
    LinearLayout privacyContainer;

    @BindView(R.id.shareContainer)
    LinearLayout shareContainer;

    @BindView(R.id.socialFacebookImageView)
    ImageView socialFacebookImageView;

    @BindView(R.id.socialTwitterImageView)
    ImageView socialTwitterImageView;

    @BindView(R.id.socialInstagramImageView)
    ImageView socialInstagramImageView;

    @BindView(R.id.socialYoutubeImageView)
    ImageView socialYoutubeImageView;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);
        mUnbinder = ButterKnife.bind(this, view);

        return view;
    }

    private Timer myTimer;


    @Override
    public void onAttach(Context context) {
        DaggerSettingsComponent.builder()
                .containerComponent(((ContainerActivity) context).getContainerComponent())
                .settingsModule(new SettingsModule(this, this))
                .build()
                .inject(this);

        super.onAttach(context);
    }

    @Override
    protected FragmentLifecycle.ViewModel getViewModel() {
        return viewModel;
    }

    @Override
    public void initBindings() {
        RxView.clicks(addCoinsView)
                .compose(bindUntilEvent(FragmentEvent.DESTROY_VIEW))
                .throttleFirst(1, TimeUnit.SECONDS)
                .subscribe(o -> viewModel.onAddCoinsClicked());

        RxView.clicks(invoicesBtn)
                .compose(bindUntilEvent(FragmentEvent.DESTROY_VIEW))
                .throttleFirst(1, TimeUnit.SECONDS)
                .subscribe(o -> viewModel.onInvoicesClicked());

        RxView.clicks(signOutImageView)
                .compose(bindUntilEvent(FragmentEvent.DESTROY_VIEW))
                .throttleFirst(1, TimeUnit.SECONDS)
                .subscribe(o -> viewModel.onSignOutClicked());

        RxCompoundButton.checkedChanges(languageSwitch)
                .compose(bindUntilEvent(FragmentEvent.DESTROY_VIEW))
                .subscribe(viewModel::setLanguage);

        RxView.clicks(feedbackContainer)
                .compose(bindUntilEvent(FragmentEvent.DESTROY_VIEW))
                .throttleFirst(1, TimeUnit.SECONDS)
                .subscribe(o -> viewModel.onSupportClicked());

        RxView.clicks(supportContainer)
                .compose(bindUntilEvent(FragmentEvent.DESTROY_VIEW))
                .throttleFirst(1, TimeUnit.SECONDS)
                .subscribe(o -> viewModel.onSupportClicked());

        RxView.clicks(ratingContainer)
                .compose(bindUntilEvent(FragmentEvent.DESTROY_VIEW))
                .throttleFirst(1, TimeUnit.SECONDS)
                .subscribe(o -> viewModel.onRatingClicked());

        RxView.clicks(termsContainer)
                .compose(bindUntilEvent(FragmentEvent.DESTROY_VIEW))
                .throttleFirst(1, TimeUnit.SECONDS)
                .subscribe(o -> viewModel.onTermsClicked());

        RxView.clicks(privacyContainer)
                .compose(bindUntilEvent(FragmentEvent.DESTROY_VIEW))
                .throttleFirst(1, TimeUnit.SECONDS)
                .subscribe(o -> viewModel.onPrivacyClicked());

        RxView.clicks(shareContainer)
                .compose(bindUntilEvent(FragmentEvent.DESTROY_VIEW))
                .throttleFirst(1, TimeUnit.SECONDS)
                .subscribe(o -> viewModel.onShareClicked());

        RxView.clicks(socialFacebookImageView)
                .compose(bindUntilEvent(FragmentEvent.DESTROY_VIEW))
                .throttleFirst(1, TimeUnit.SECONDS)
                .subscribe(o -> viewModel.onSocialFacebookClicked());

        RxView.clicks(socialTwitterImageView)
                .compose(bindUntilEvent(FragmentEvent.DESTROY_VIEW))
                .throttleFirst(1, TimeUnit.SECONDS)
                .subscribe(o -> viewModel.onSocialTwitterClicked());

        RxView.clicks(socialInstagramImageView)
                .compose(bindUntilEvent(FragmentEvent.DESTROY_VIEW))
                .throttleFirst(1, TimeUnit.SECONDS)
                .subscribe(o -> viewModel.onSocialInstagramClicked());

        RxView.clicks(socialYoutubeImageView)
                .compose(bindUntilEvent(FragmentEvent.DESTROY_VIEW))
                .throttleFirst(1, TimeUnit.SECONDS)
                .subscribe(o -> viewModel.onSocialYoutubeClicked());
    }

    @Override
    public void setRemainingCoins(String remainingCoins) {
        remainingCoinsTextView.setText(remainingCoins);
    }

    @Override
    public void setVersion(String versionName) {
        appVersionTextView.setText(getString(R.string.label_settings_app_version, versionName));
    }

    @Override
    public void setLanguageArabic(boolean languageArabic) {
        languageSwitch.setChecked(languageArabic);
    }

    @Override
    public void hideAddCoins() {
        addCoinsView.setVisibility(View.GONE);
    }

    @Override
    public void onPause() {
        super.onPause();
//        myTimer = new Timer();
//        myTimer.schedule(new TimerTask() {
//            @Override
//            public void run() {
//                try {
//                    getActivity().runOnUiThread(() -> {
//                        mContainerView.ShowRightToolbarButton();
////                        mContainerView.setRightToolbarTextResource(R.string.empty);
//                        mContainerView.ShowRight_ToolbarButton();
//                    });
//                } catch (Exception x) {
//
//                }
//
//
//            }
//
//        }, 0, 500);
    }

    @Override
    public void onResume() {
        super.onResume();
        mContainerView.hideRightToolbarButton();
        mContainerView.hideRightText_();
        mContainerView.hideRightToolbarButton();

        myTimer = new Timer();
        myTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                try {
                    getActivity().runOnUiThread(() -> {
                        mContainerView.ShowRightToolbarButton();
//                        mContainerView.setRightToolbarTextResource(R.string.empty);
                        mContainerView.ShowRight_ToolbarButton();
                    });
                } catch (Exception x) {

                }


            }

        }, 0, 500);
    }
}
