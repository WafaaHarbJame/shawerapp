package com.shawerapp.android.screens.profile.user.edit;

import android.view.ViewGroup;

import com.shawerapp.android.autovalue.CommercialUser;
import com.shawerapp.android.autovalue.IndividualUser;
import com.shawerapp.android.base.FragmentLifecycle;

import java.util.Calendar;
import java.util.Date;

import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;

public interface ProfileEditContract {

    interface View extends FragmentLifecycle.View {

        void initBindings();

        void showDateChooser(Calendar initialCalendar, Consumer<Long> dateSelected);

        void showIndividualFields();

        void showCommercialFields();

        void setProfilePicture(String filePath);

        void setUsername(CharSequence username);

        String getUsername();

        boolean isUserNameChanged();

        void setName(CharSequence name);

        String getName();

        void setEmail(CharSequence emailAddress);

        boolean isNameChanged();

        String getEmail();

        boolean isEmailChanged();

        void setPhoneNumber(CharSequence phoneNumber);

        String getPhoneNumber();

        boolean isPhoneNumberChanged();

        void setGender(CharSequence gender);

        String getGender();

        boolean isGenderChanged();

        void setBirthday(CharSequence birthday);

        Date getBirthday();

        boolean isBirthdayChanged();

        void setCountry(CharSequence country);

        String getCountry();

        boolean isCountryChanged();

        void setCity(CharSequence city);

        String getCity();

        boolean isCityChanged();

        void setPosition(CharSequence position);

        String getPosition();

        boolean isPositionChanged();

        void setCompanyName(CharSequence companyName);

        String getCompanyName();

        boolean isCompanyNameChanged();

        void setCompanyRegisterNumber(CharSequence companyRegisterNumber);

        String getCompanyRegisterNumber();

        boolean isCompanyRegisterNumberChanged();

        void setCompanySize(CharSequence companySize);

        String getCompanySize();

        boolean isCompanySizeChanged();

        void setCompanyType(CharSequence companyType);

        String getCompanyType();

        boolean isCompanyTypeChanged();

        void setCompanyNationality(CharSequence companyNationality);

        String getCompanyNationality();

        boolean isCompanyNationalityChanged();

        void setCompanyFormationDate(CharSequence companyFormationDate);

        Date getCompanyFormationDate();

        boolean isCompanyFormationDateChanged();

        void setCompanyPhoneNumber(CharSequence companyPhoneNumber);

        String getCompanyPhoneNumber();

        boolean isCompanyPhoneNumberChanged();

        void setCompanyHeadquartersCountry(CharSequence companyHeadquartersCountry);

        String getCompanyHeadquartersCountry();

        boolean isCompanyHeadquartersCountryChanged();

        void setCompanyHeadquartersCity(CharSequence companyHeadquartersCity);

        String getCompanyHeadquartersCity();

        boolean isCompanyHeadquartersCityChanged();

        String getCurrentPassword();

        String getNewPassword();

        ViewGroup getFragmentView();
    }

    interface ViewModel extends FragmentLifecycle.ViewModel {

        void setupProfileForIndividualUsers(IndividualUser individualUser);

        void setupProfileForCommercialUsers(CommercialUser commercialUser);

        void onProfilePictureClicked();

        void updateIndividualProfile();

        void updateCommercialProfile();

        void showLoginPopup(String email, String currentPassword, Action onLoginCompleted);
    }
}
