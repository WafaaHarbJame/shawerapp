package com.shawerapp.android.screens.profile.user.view;

import com.shawerapp.android.autovalue.CommercialUser;
import com.shawerapp.android.autovalue.IndividualUser;
import com.shawerapp.android.base.FragmentLifecycle;

public interface ProfileViewContract {

    interface View extends FragmentLifecycle.View {

        void initBindings();

        void showIndividualFields();

        void showCommercialFields();

        void setUsername(CharSequence username);

        void setSpecificInfo(CharSequence specificInfo);

        void setCoins(CharSequence coins);

        void setProfilePicture(String imageUrl);

        void setName(CharSequence name);

        void setEmail(CharSequence emailAddress);

        void setPhoneNumber(CharSequence phoneNumber);

        void setGender(CharSequence gender);

        void setBirthday(CharSequence birthday);

        void setCountry(CharSequence country);

        void setCity(CharSequence city);

        void setPosition(CharSequence position);

        void setCompanyName(CharSequence companyName);

        void setCompanyRegisterNumber(CharSequence companyRegisterNumber);

        void setCompanySize(CharSequence companySize);

        void setCompanyType(CharSequence companyType);

        void setCompanyNationality(CharSequence companyNationality);

        void setCompanyFormationDate(CharSequence companyFormationDate);

        void setCompanyPhoneNumber(CharSequence companyPhoneNumber);

        void setCompanyHeadquartersCountry(CharSequence companyHeadquartersCountry);

        void setCompanyHeadquartersCity(CharSequence companyHeadquartersCity);

        void setQuestionsAskedCount(String questionsAsked);

        void setAnswersRecieved(String answersReceived);

        void setAnswersRated(String answersRated);

        void setPracticeRequests(String practiceRequests);
    }

    interface ViewModel extends FragmentLifecycle.ViewModel {

        void setupProfileForIndividualUsers(IndividualUser individualUser);

        void setupProfileForCommercialUsers(CommercialUser commercialUser);

        void onQuestionsClicked();

        void onAnswersClicked();

        void onRatedAnswersClicked();

        void onPracticeRequestsClicked();

        void onAddCoinsButtonClicked();

        void openInvoices();

        void openPayment();
    }
}
