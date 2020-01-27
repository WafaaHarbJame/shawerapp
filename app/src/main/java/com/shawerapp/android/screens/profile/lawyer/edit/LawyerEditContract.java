package com.shawerapp.android.screens.profile.lawyer.edit;

import android.view.ViewGroup;

import com.jakewharton.rxbinding2.widget.TextViewAfterTextChangeEvent;
import com.shawerapp.android.autovalue.LawyerUser;
import com.shawerapp.android.base.FragmentLifecycle;

import java.util.Calendar;
import java.util.Date;

import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;

public interface LawyerEditContract {

    interface View extends FragmentLifecycle.View {

        void initBindings();

        ViewGroup getFragmentView();

        void showDateChooser(Calendar initialCalendar, Consumer<Long> dateSelected);

        void setProfilePicture(String filePath);

        void setUsername(CharSequence username);

        String getUsername();

        boolean isUserNameChanged();

        void setName(CharSequence name);

        String getName();

        boolean isNameChanged();

        void setEmail(CharSequence emailAddress);

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

        void setProfileBio(CharSequence position);

        String getProfileBio();

        boolean isProfileBioChanged();

        void setProfileBioCount(String count);

        String getCurrentPassword();

        String getNewPassword();
    }

    interface ViewModel extends FragmentLifecycle.ViewModel {

        void setupProfileForLawyerUsers(LawyerUser lawyerUser);

        void onProfileBioTextChanged(CharSequence textChange);

        void onProfilePictureClicked();

        void updateLawyerProfile();

        void onRequestEditButtonClicked();

        void showLoginPopup(String email, String currentPassword, Action onLoginCompleted);
    }
}
