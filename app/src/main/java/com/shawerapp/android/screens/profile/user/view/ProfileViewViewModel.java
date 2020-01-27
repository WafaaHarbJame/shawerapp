package com.shawerapp.android.screens.profile.user.view;

import android.content.Intent;
import androidx.core.content.ContextCompat;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;

import com.google.firebase.messaging.FirebaseMessaging;
import com.shawerapp.android.R;
import com.shawerapp.android.autovalue.CommercialUser;
import com.shawerapp.android.autovalue.IndividualUser;
import com.shawerapp.android.backend.base.AuthFramework;
import com.shawerapp.android.backend.base.RealTimeDataFramework;
import com.shawerapp.android.base.BaseFragment;
import com.shawerapp.android.screens.Invoices.InvoicesKey;
import com.shawerapp.android.screens.container.ContainerContract;
import com.shawerapp.android.screens.login.LoginActivity;
import com.shawerapp.android.screens.payment.PaymentKey;
import com.shawerapp.android.screens.profile.user.edit.ProfileEditKey;
import com.shawerapp.android.screens.purchase.PurchaseCoinsKey;
import com.shawerapp.android.utils.AnimationUtils;
import com.shawerapp.android.utils.CommonUtils;
import com.shawerapp.android.utils.LoginUtil;
import com.trello.rxlifecycle2.android.FragmentEvent;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;

import javax.inject.Inject;

import static com.shawerapp.android.screens.profile.user.view.ProfileViewFragment.ARG_TYPE;

public class ProfileViewViewModel implements ProfileViewContract.ViewModel {

    private BaseFragment mFragment;

    private ProfileViewContract.View mView;

    @Inject
    ContainerContract.View mContainerView;

    @Inject
    ContainerContract.ViewModel mContainerViewModel;

    @Inject
    RealTimeDataFramework mRTDataFramework;

    @Inject
    AuthFramework mAuthFramework;

    @Inject
    LoginUtil mLoginUtil;

    private String mType;

    @Inject
    public ProfileViewViewModel(BaseFragment fragment, ProfileViewContract.View view) {
        mFragment = fragment;
        mView = view;

        mType = fragment.getArguments().getString(ARG_TYPE);
    }

    @Override
    public void onViewCreated() {
        mView.initBindings();
        mContainerView.selectTab(R.id.tabProfile);
    }

    @Override
    public void onAfterEnterAnimation() {
        mRTDataFramework.fetchUser(mLoginUtil.getUserID())
                .doOnSubscribe(disposable -> mContainerView.showLoadingIndicator())
                .doFinally(() -> mContainerView.hideLoadingIndicator())
                .compose(mFragment.bindUntilEvent(FragmentEvent.DESTROY_VIEW))
                .subscribe(user -> {
                    if (user instanceof IndividualUser) {
                        IndividualUser individualUser = (IndividualUser) user;
                        setupProfileForIndividualUsers(individualUser);
                    } else if (user instanceof CommercialUser) {
                        CommercialUser commercialUser = (CommercialUser) user;
                        setupProfileForCommercialUsers(commercialUser);
                    }
                });
    }

    @Override
    public void setupProfileForIndividualUsers(IndividualUser individualUser) {
        mView.showIndividualFields();
        DecimalFormat decimalFormat = new DecimalFormat("#,###,###");

        mView.setProfilePicture(individualUser.imageUrl());
        mView.setUsername(individualUser.username());
        if (individualUser.questionsAsked() != null) {
            mView.setQuestionsAskedCount(decimalFormat.format(individualUser.questionsAsked()));
        }
        if (individualUser.answersReceived() != null) {
            mView.setAnswersRecieved(decimalFormat.format(individualUser.answersReceived()));
        }
        if (individualUser.answersRated() != null) {
            mView.setAnswersRated(decimalFormat.format(individualUser.answersRated()));
        }
        if (individualUser.practiceRequests() != null) {
            mView.setPracticeRequests(decimalFormat.format(individualUser.practiceRequests()));
        }

        if (CommonUtils.isNotEmpty(individualUser.fullName())) {
            mView.setSpecificInfo(individualUser.fullName());
        } else {
            SpannableStringBuilder nameHint = new SpannableStringBuilder("Add full name");
            mView.setSpecificInfo(nameHint);
        }

        if (individualUser.coins() != null) {
            mView.setCoins(mFragment.getString(R.string.format_coins, decimalFormat.format(individualUser.coins())));
        } else {
            mView.setCoins(mFragment.getString(R.string.format_coins, "0"));
        }

        ForegroundColorSpan foregroundColorSpan = new ForegroundColorSpan(ContextCompat.getColor(mFragment.getContext(), R.color.timberwolf));
        if (CommonUtils.isNotEmpty(individualUser.fullName())) {
            mView.setName(individualUser.fullName());
        } else {
            SpannableStringBuilder nameHint = new SpannableStringBuilder(mFragment.getString(R.string.hint_name));
            nameHint.setSpan(foregroundColorSpan, 0, nameHint.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            mView.setName(nameHint);
        }

        if (CommonUtils.isNotEmpty(individualUser.emailAddress())) {
            mView.setEmail(individualUser.emailAddress());
        } else {
            SpannableStringBuilder emailAddressHint = new SpannableStringBuilder(mFragment.getString(R.string.hint_email_address));
            emailAddressHint.setSpan(foregroundColorSpan, 0, emailAddressHint.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            mView.setEmail(emailAddressHint);
        }

        if (CommonUtils.isNotEmpty(individualUser.phoneNumber())) {
            mView.setPhoneNumber(individualUser.phoneNumber());
        } else {
            SpannableStringBuilder phoneNumberHint = new SpannableStringBuilder(mFragment.getString(R.string.hint_phonenumber));
            phoneNumberHint.setSpan(foregroundColorSpan, 0, phoneNumberHint.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            mView.setPhoneNumber(phoneNumberHint);
        }

        if (CommonUtils.isNotEmpty(individualUser.gender())) {
            mView.setGender(individualUser.gender());
        } else {
            SpannableStringBuilder phoneNumberHint = new SpannableStringBuilder(mFragment.getString(R.string.hint_gender));
            phoneNumberHint.setSpan(foregroundColorSpan, 0, phoneNumberHint.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            mView.setGender(phoneNumberHint);
        }

        SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
        if (individualUser.birthday() != null) {
            mView.setBirthday(dateFormat.format(individualUser.birthday()));
        } else {
            SpannableStringBuilder birthdayHint = new SpannableStringBuilder(mFragment.getString(R.string.hint_birthday));
            birthdayHint.setSpan(foregroundColorSpan, 0, birthdayHint.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            mView.setBirthday(birthdayHint);
        }

        if (CommonUtils.isNotEmpty(individualUser.country())) {
            mView.setCountry(individualUser.country());
        } else {
            SpannableStringBuilder countryHint = new SpannableStringBuilder(mFragment.getString(R.string.hint_country));
            countryHint.setSpan(foregroundColorSpan, 0, countryHint.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            mView.setCountry(countryHint);
        }

        if (CommonUtils.isNotEmpty(individualUser.city())) {
            mView.setCity(", " + individualUser.city());
        } else {
            SpannableStringBuilder cityHint = new SpannableStringBuilder(mFragment.getString(R.string.hint_city));
            cityHint.setSpan(foregroundColorSpan, 0, cityHint.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            mView.setCity(cityHint);
        }
    }

    @Override
    public void setupProfileForCommercialUsers(CommercialUser commercialUser) {
        mView.showCommercialFields();
        DecimalFormat decimalFormat = new DecimalFormat("#,###,###");

        mView.setProfilePicture(commercialUser.imageUrl());
        mView.setUsername(commercialUser.username());
        if (commercialUser.questionsAsked() != null) {
            mView.setQuestionsAskedCount(decimalFormat.format(commercialUser.questionsAsked()));
        }
        if (commercialUser.answersReceived() != null) {
            mView.setAnswersRecieved(decimalFormat.format(commercialUser.answersReceived()));
        }
        if (commercialUser.answersRated() != null) {
            mView.setAnswersRated(decimalFormat.format(commercialUser.answersRated()));
        }
        if (commercialUser.practiceRequests() != null) {
            mView.setPracticeRequests(decimalFormat.format(commercialUser.practiceRequests()));
        }

        if (CommonUtils.isNotEmpty(commercialUser.companyName())) {
            mView.setSpecificInfo(commercialUser.companyName());
        } else {
            SpannableStringBuilder nameHint = new SpannableStringBuilder("Add company name");
            mView.setSpecificInfo(nameHint);
        }

        ForegroundColorSpan foregroundColorSpan = new ForegroundColorSpan(ContextCompat.getColor(mFragment.getContext(), R.color.timberwolf));

        if (CommonUtils.isNotEmpty(commercialUser.companyName())) {
            mView.setCompanyName(commercialUser.companyName());
        } else {
            SpannableStringBuilder companyNameHint = new SpannableStringBuilder(mFragment.getString(R.string.hint_company_name));
            companyNameHint.setSpan(foregroundColorSpan, 0, companyNameHint.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            mView.setCompanyName(companyNameHint);
        }

        if (CommonUtils.isNotEmpty(commercialUser.companyRegisterNumber())) {
            mView.setCompanyRegisterNumber(commercialUser.companyRegisterNumber());
        } else {
            SpannableStringBuilder companyRegisterNumberHint = new SpannableStringBuilder(mFragment.getString(R.string.hint_company_register_number));
            companyRegisterNumberHint.setSpan(foregroundColorSpan, 0, companyRegisterNumberHint.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            mView.setCompanyRegisterNumber(companyRegisterNumberHint);
        }

        if (CommonUtils.isNotEmpty(commercialUser.companySize())) {
            mView.setCompanySize(commercialUser.companySize());
        } else {
            SpannableStringBuilder companySizeHint = new SpannableStringBuilder(mFragment.getString(R.string.hint_company_size));
            companySizeHint.setSpan(foregroundColorSpan, 0, companySizeHint.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            mView.setCompanySize(companySizeHint);
        }

        if (CommonUtils.isNotEmpty(commercialUser.companyType())) {
            mView.setCompanyType(commercialUser.companyType());
        } else {
            SpannableStringBuilder companyTypeHint = new SpannableStringBuilder(mFragment.getString(R.string.hint_company_type));
            companyTypeHint.setSpan(foregroundColorSpan, 0, companyTypeHint.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            mView.setCompanyType(companyTypeHint);
        }
        if (CommonUtils.isNotEmpty(commercialUser.companyNationality())) {
            mView.setCompanyNationality(commercialUser.companyNationality());
        } else {
            SpannableStringBuilder companyNationalityHint = new SpannableStringBuilder(mFragment.getString(R.string.hint_company_nationality));
            companyNationalityHint.setSpan(foregroundColorSpan, 0, companyNationalityHint.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            mView.setCompanyNationality(companyNationalityHint);
        }

        SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
        if (commercialUser.companyFormationDate() != null) {
            mView.setCompanyFormationDate(dateFormat.format(commercialUser.companyFormationDate()));
        } else {
            SpannableStringBuilder companyFormationDateHint = new SpannableStringBuilder(mFragment.getString(R.string.hint_company_formation_date));
            companyFormationDateHint.setSpan(foregroundColorSpan, 0, companyFormationDateHint.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            mView.setCompanyFormationDate(companyFormationDateHint);
        }

        if (CommonUtils.isNotEmpty(commercialUser.companyPhoneNumber())) {
            mView.setCompanyPhoneNumber(commercialUser.companyPhoneNumber());
        } else {
            SpannableStringBuilder companyPhoneNumberHint = new SpannableStringBuilder(mFragment.getString(R.string.hint_company_phone_number));
            companyPhoneNumberHint.setSpan(foregroundColorSpan, 0, companyPhoneNumberHint.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            mView.setCompanyPhoneNumber(companyPhoneNumberHint);
        }

        if (CommonUtils.isNotEmpty(commercialUser.companyHeadquartersCountry())) {
            mView.setCompanyHeadquartersCountry(commercialUser.companyHeadquartersCountry());
        } else {
            SpannableStringBuilder companyHeadquartersCountryHint = new SpannableStringBuilder(mFragment.getString(R.string.hint_company_headquarters_country));
            companyHeadquartersCountryHint.setSpan(foregroundColorSpan, 0, companyHeadquartersCountryHint.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            mView.setCompanyHeadquartersCountry(companyHeadquartersCountryHint);
        }

        if (CommonUtils.isNotEmpty(commercialUser.companyHeadquartersCity())) {
            mView.setCompanyHeadquartersCity(", " + commercialUser.companyHeadquartersCity());
        } else {
            SpannableStringBuilder companyHeadquartersCityHint = new SpannableStringBuilder(mFragment.getString(R.string.hint_company_headquarters_city));
            companyHeadquartersCityHint.setSpan(foregroundColorSpan, 0, companyHeadquartersCityHint.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            mView.setCompanyHeadquartersCity(companyHeadquartersCityHint);
        }

        if (commercialUser.coins() != null) {
            mView.setCoins(mFragment.getString(R.string.format_coins, decimalFormat.format(commercialUser.coins())));
        } else {
            mView.setCoins(mFragment.getString(R.string.format_coins, "0"));
        }

        if (CommonUtils.isNotEmpty(commercialUser.fullName())) {
            mView.setName(commercialUser.fullName());
        } else {
            SpannableStringBuilder nameHint = new SpannableStringBuilder(mFragment.getString(R.string.hint_name));
            nameHint.setSpan(foregroundColorSpan, 0, nameHint.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            mView.setName(nameHint);
        }

        if (CommonUtils.isNotEmpty(commercialUser.emailAddress())) {
            mView.setEmail(commercialUser.emailAddress());
        } else {
            SpannableStringBuilder emailAddressHint = new SpannableStringBuilder(mFragment.getString(R.string.hint_email_address));
            emailAddressHint.setSpan(foregroundColorSpan, 0, emailAddressHint.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            mView.setEmail(emailAddressHint);
        }

        if (CommonUtils.isNotEmpty(commercialUser.phoneNumber())) {
            mView.setPhoneNumber(commercialUser.phoneNumber());
        } else {
            SpannableStringBuilder phoneNumberHint = new SpannableStringBuilder(mFragment.getString(R.string.hint_phonenumber));
            phoneNumberHint.setSpan(foregroundColorSpan, 0, phoneNumberHint.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            mView.setPhoneNumber(phoneNumberHint);
        }

        if (CommonUtils.isNotEmpty(commercialUser.gender())) {
            mView.setGender(commercialUser.gender());
        } else {
            SpannableStringBuilder phoneNumberHint = new SpannableStringBuilder(mFragment.getString(R.string.hint_gender));
            phoneNumberHint.setSpan(foregroundColorSpan, 0, phoneNumberHint.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            mView.setGender(phoneNumberHint);
        }
        if (CommonUtils.isNotEmpty(commercialUser.country())) {
            mView.setCountry(commercialUser.country());
        } else {
            SpannableStringBuilder countryHint = new SpannableStringBuilder(mFragment.getString(R.string.hint_country));
            countryHint.setSpan(foregroundColorSpan, 0, countryHint.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            mView.setCountry(countryHint);
        }

        if (CommonUtils.isNotEmpty(commercialUser.city())) {
            mView.setCity(", " + commercialUser.city());
        } else {
            SpannableStringBuilder cityHint = new SpannableStringBuilder(mFragment.getString(R.string.hint_city));
            cityHint.setSpan(foregroundColorSpan, 0, cityHint.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            mView.setCity(cityHint);
        }

        if (CommonUtils.isNotEmpty(commercialUser.position())) {
            mView.setPosition(commercialUser.position());
        } else {
            SpannableStringBuilder positionHint = new SpannableStringBuilder(mFragment.getString(R.string.hint_position));
            positionHint.setSpan(foregroundColorSpan, 0, positionHint.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            mView.setPosition(positionHint);
        }
    }

    @Override
    public void onBackButtonClicked() {
        mContainerViewModel
                .goBack()
                .subscribe(mContainerViewModel.navigationObserver());
    }

    @Override
    public void setupToolbar() {
        //Consider resetting toolbar state
        mContainerView.clearToolbarTitle();
        mContainerView.clearToolbarSubtitle();
        mContainerView.setLeftToolbarButtonImageResource(R.drawable.icon_sign_out);
        mContainerView.setRightToolbarButtonImageResource(R.drawable.icon_edit_profile);
    }

    @Override
    public void onLeftToolbarButtonClicked() {
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
    public void onRightToolbarButtonClicked() {
        mContainerViewModel
                .goTo(ProfileEditKey.create(mType))
                .subscribe(mContainerViewModel.navigationObserver());
    }

    @Override
    public void onQuestionsClicked() {

    }

    @Override
    public void onAnswersClicked() {

    }

    @Override
    public void onRatedAnswersClicked() {

    }

    @Override
    public void onPracticeRequestsClicked() {

    }

    @Override
    public void onAddCoinsButtonClicked() {
        mContainerViewModel
                .goTo(PurchaseCoinsKey.create())
                .subscribe(mContainerViewModel.navigationObserver());
    }

    @Override
    public void openInvoices() {
        mContainerViewModel
                .goTo(InvoicesKey.create())
                .subscribe(mContainerViewModel.navigationObserver());
    }

    @Override
    public void openPayment() {
//        mContainerViewModel.hideRightToolbarButton();
//        mContainerViewModel
//                .goTo(PaymentKey.create())
//                .subscribe(mContainerViewModel.navigationObserver());
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
