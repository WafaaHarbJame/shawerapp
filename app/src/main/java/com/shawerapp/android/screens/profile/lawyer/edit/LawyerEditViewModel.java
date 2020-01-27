package com.shawerapp.android.screens.profile.lawyer.edit;

import android.app.AlertDialog;
import android.graphics.Color;
import android.net.Uri;
import android.util.Patterns;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.androidadvance.topsnackbar.TSnackbar;
import com.esafirm.imagepicker.features.ImagePicker;
import com.esafirm.imagepicker.model.Image;
import com.esafirm.rximagepicker.RxImagePicker;
import com.jakewharton.rxbinding2.view.RxView;
import com.jakewharton.rxbinding2.widget.RxTextView;
import com.shawerapp.android.R;
import com.shawerapp.android.autovalue.IndividualUser;
import com.shawerapp.android.autovalue.LawyerUser;
import com.shawerapp.android.backend.base.AuthFramework;
import com.shawerapp.android.backend.base.FileFramework;
import com.shawerapp.android.backend.base.RealTimeDataFramework;
import com.shawerapp.android.backend.base.RestFramework;
import com.shawerapp.android.base.BaseFragment;
import com.shawerapp.android.screens.container.ContainerContract;
import com.shawerapp.android.utils.CommonUtils;
import com.shawerapp.android.utils.LoginUtil;
import com.trello.rxlifecycle2.android.FragmentEvent;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import io.reactivex.Completable;
import io.reactivex.Maybe;
import io.reactivex.functions.Action;
import timber.log.Timber;

public class LawyerEditViewModel implements LawyerEditContract.ViewModel {

    private BaseFragment mFragment;

    private LawyerEditContract.View mView;

    @Inject
    ContainerContract.ViewModel mContainerViewModel;

    @Inject
    ContainerContract.View mContainerView;

    @Inject
    AuthFramework mAuthFramework;

    @Inject
    RealTimeDataFramework mRTDataFramework;

    @Inject
    RestFramework mRestFramework;

    @Inject
    FileFramework mFileFramework;

    @Inject
    LoginUtil mLoginUtil;

    private Image mSelectedProfilePicture;

    @Inject
    public LawyerEditViewModel(BaseFragment fragment, LawyerEditContract.View view) {
        mFragment = fragment;
        mView = view;
    }

    @Override
    public void onViewCreated() {
        mView.initBindings();
    }

    @Override
    public void onAfterEnterAnimation() {
        mRTDataFramework.fetchUser(mLoginUtil.getUserID())
                .compose(mFragment.bindUntilEvent(FragmentEvent.DESTROY_VIEW))
                .doOnSubscribe(disposable -> mContainerView.showLoadingIndicator())
                .doFinally(() -> mContainerView.hideLoadingIndicator())
                .subscribe(user -> {
                    if (user instanceof LawyerUser) {
                        LawyerUser lawyerUser = (LawyerUser) user;
                        setupProfileForLawyerUsers(lawyerUser);
                    }
                });
    }

    @Override
    public void setupProfileForLawyerUsers(LawyerUser lawyerUser) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
        mView.setUsername(lawyerUser.username());
        mView.setProfilePicture(lawyerUser.imageUrl());
        mView.setName(lawyerUser.fullName());
        mView.setEmail(lawyerUser.emailAddress());
        mView.setPhoneNumber(lawyerUser.phoneNumber());
        mView.setGender(lawyerUser.gender());
        if (lawyerUser.birthday() != null) {
            mView.setBirthday(dateFormat.format(lawyerUser.birthday()));
        }
        mView.setCountry(lawyerUser.country());
        mView.setCity(lawyerUser.city());
    }

    @Override
    public void onProfileBioTextChanged(CharSequence textChange) {
        mView.setProfileBioCount(mFragment.getString(R.string.format_profile_bio_count, textChange.length()));
    }

    @Override
    public void onProfilePictureClicked() {
        RxImagePicker.getInstance()
                .start(mFragment.getContext(), ImagePicker.create(mFragment)
                        .single()
                        .showCamera(true)
                        .theme(R.style.ImagePickerTheme))
                .subscribe(images -> {
                    if (images != null && images.size() > 0) {
                        mSelectedProfilePicture = images.get(0);
                        mView.setProfilePicture(mSelectedProfilePicture.getPath());
                    }
                }, throwable -> {
                    Timber.e(CommonUtils.getExceptionString(throwable));
                    mContainerView.showMessage(throwable.getMessage(), true);
                });
    }

    @Override
    public void onBackButtonClicked() {
        mContainerViewModel
                .goBack()
                .subscribe(mContainerViewModel.navigationObserver());
    }

    @Override
    public void setupToolbar() {
        mContainerView.setLeftToolbarButtonImageResource(R.drawable.icon_back);
        mContainerView.setRightToolbarButtonImageResource(R.drawable.icon_check_light);
    }

    @Override
    public void onLeftToolbarButtonClicked() {
        onBackButtonClicked();
    }

    @Override
    public void onRightToolbarButtonClicked() {
        updateLawyerProfile();
    }

    @Override
    public void updateLawyerProfile() {
        String userName = mView.getUsername();
        String name = mView.getName();
        String newEmail = mView.getEmail();
        String phoneNumber = mView.getPhoneNumber();
        String gender = mView.getGender();
        Date birthday = mView.getBirthday();
        String country = mView.getCountry();
        String city = mView.getCity();
        String profileBio = mView.getProfileBio();
        String currentPassword = mView.getCurrentPassword();
        String newPassword = mView.getNewPassword();

        Completable uploadProfilePicture = Completable.complete();
        if (mSelectedProfilePicture != null) {
            uploadProfilePicture = mFileFramework.uploadProfileImage(Uri.fromFile(new File(mSelectedProfilePicture.getPath())))
                    .flatMapCompletable(imageUrl -> mRTDataFramework.updateUser(LawyerUser.IMAGE_URL, imageUrl));
        }

        Completable updateuserName = Completable.complete();
        if (mView.isUserNameChanged()) {
            updateuserName = mRTDataFramework.updateUser(IndividualUser.USER_NAME, userName);
        }

        Completable updateName = Completable.complete();
        if (mView.isNameChanged()) {
            updateName = mRTDataFramework.updateUser(LawyerUser.FULL_NAME, name);
        }

        Completable updatePhoneNumber = Completable.complete();
        if (mView.isPhoneNumberChanged()) {
            updatePhoneNumber = mRTDataFramework.updateUser(LawyerUser.PHONE_NUMBER, phoneNumber);
        }

        Completable updateGender = Completable.complete();
        if (mView.isGenderChanged()) {
            updateGender = mRTDataFramework.updateUser(LawyerUser.GENDER, gender);
        }

        Completable updateBirthday = Completable.complete();
        if (mView.isBirthdayChanged()) {
            updateBirthday = mRTDataFramework.updateUser(LawyerUser.BIRTHDAY, birthday);
        }

        Completable updateCountry = Completable.complete();
        if (mView.isCountryChanged()) {
            updateCountry = mRTDataFramework.updateUser(LawyerUser.COUNTRY, country);
        }

        Completable updateCity = Completable.complete();
        if (mView.isCityChanged()) {
            updateCity = mRTDataFramework.updateUser(LawyerUser.CITY, city);
        }

        Completable updateProfileBio = Completable.complete();
        if (mView.isProfileBioChanged()) {
            updateProfileBio = mRTDataFramework.updateUser(LawyerUser.PROFILE_BIO, profileBio);
        }

        Completable updateFlow = updateuserName
                .andThen(updateName)
                .andThen(updatePhoneNumber)
                .andThen(updateGender)
                .andThen(updateBirthday)
                .andThen(updateCountry)
                .andThen(updateCity)
                .andThen(updateProfileBio)
                .andThen(uploadProfilePicture);

        if ((CommonUtils.isNotEmpty(newEmail) && mView.isEmailChanged()) || (CommonUtils.isNotEmpty(newPassword))) {
            showLoginPopup(newEmail, currentPassword, () -> {
                mContainerView.showMessage("Successfully logged in!");

                Completable updateEmail = Completable.complete();
                if (mView.isEmailChanged()) {
                    updateEmail = mRTDataFramework
                            .updateUser(IndividualUser.EMAIL, newEmail)
                            .andThen(mAuthFramework.updateEmail(newEmail));
                }

                Completable updatePassword = Completable.complete();
                if (mView.isCityChanged()) {
                    updatePassword = mAuthFramework.updatePassword(newPassword);
                }

                updateEmail
                        .compose(mFragment.bindUntilEvent(FragmentEvent.DESTROY_VIEW))
                        .doOnSubscribe(disposable -> mContainerView.showLoadingIndicator())
                        .andThen(updatePassword)
                        .andThen(updateFlow)
                        .doFinally(() -> mContainerView.hideLoadingIndicator())
                        .subscribe(
                                () -> mContainerView.showMessage(mFragment.getString(R.string.message_success_profile)),
                                mContainerViewModel.catchErrorThrowable());
            });
        } else {
            updateFlow
                    .compose(mFragment.bindUntilEvent(FragmentEvent.DESTROY_VIEW))
                    .doOnSubscribe(disposable -> mContainerView.showLoadingIndicator())
                    .doFinally(() -> mContainerView.hideLoadingIndicator())
                    .subscribe(
                            () -> mContainerView.showMessage(mFragment.getString(R.string.message_success_profile)),
                            mContainerViewModel.catchErrorThrowable());
        }
    }

    @Override
    public void onRequestEditButtonClicked() {
        mRTDataFramework
                .requestLawyerProfileEdit()
                .compose(mFragment.bindUntilEvent(FragmentEvent.DESTROY_VIEW))
                .doOnSubscribe(disposable -> mContainerView.showLoadingIndicator())
                .doFinally(() -> mContainerView.hideLoadingIndicator())
                .subscribe(() -> {
                    onBackButtonClicked();
                    mContainerView.showMessage(mFragment.getString(R.string.success_edit_request));
                });
    }

    @Override
    public void showLoginPopup(String email, String currentPassword, Action onLoginCompleted) {
        AlertDialog loginDialog;
        AlertDialog.Builder builder = new AlertDialog.Builder(mFragment.getContext());
        View dialogView = LayoutInflater.from(mFragment.getContext()).inflate(R.layout.activity_login, mView.getFragmentView(), false);
        builder.setView(dialogView);

        TextView loginMessage1 = dialogView.findViewById(R.id.loginMessage1);
        loginMessage1.setText(R.string.relogin_message1);

        TextView loginMessage2 = dialogView.findViewById(R.id.loginMessage2);
        loginMessage2.setText(R.string.relogin_message2);

        View bottomButtons = dialogView.findViewById(R.id.bottomButtons);
        bottomButtons.setVisibility(View.GONE);

        EditText usernameField = dialogView.findViewById(R.id.username);
        RxTextView.afterTextChangeEvents(usernameField)
                .subscribe(textChangeEvent -> {
                    if (CommonUtils.isNotEmpty(textChangeEvent.editable())) {
                        usernameField.setBackgroundResource(R.drawable.input_field_bg);
                    }
                });
        usernameField.setText(email);

        EditText passwordField = dialogView.findViewById(R.id.password);
        RxTextView.afterTextChangeEvents(passwordField)
                .subscribe(textChangeEvent -> {
                    if (CommonUtils.isNotEmpty(textChangeEvent.editable())) {
                        passwordField.setBackgroundResource(R.drawable.input_field_bg);
                    }
                });
        passwordField.setText(currentPassword);

        Button btnLogin = dialogView.findViewById(R.id.btnLogin);
        RxView.clicks(btnLogin)
                .subscribe(o -> {
                    String username = "";
                    if (CommonUtils.isNotEmpty(usernameField.getText())) {
                        username = usernameField.getText().toString();
                    } else {
                        usernameField.setBackgroundResource(R.drawable.input_field_bg_error);
                    }

                    String password = "";
                    if (CommonUtils.isNotEmpty(passwordField.getText())) {
                        password = passwordField.getText().toString();
                    } else {
                        passwordField.setBackgroundResource(R.drawable.input_field_bg_error);
                    }

                    if (CommonUtils.isNotEmpty(username) && CommonUtils.isNotEmpty(password)) {
                        Maybe<String> signInFlow;
                        if (Patterns.EMAIL_ADDRESS.matcher(username).matches()) {
                            signInFlow = mAuthFramework
                                    .signInWithEmailAndPassword(username, password)
                                    .doOnSubscribe(disposable -> mContainerView.showLoadingIndicator())
                                    .doFinally(mContainerView::hideLoadingIndicator);
                        } else {
                            String finalPassword = password;
                            signInFlow = mRestFramework
                                    .getUserEmail(username)
                                    .doOnSubscribe(disposable -> mContainerView.showLoadingIndicator())
                                    .flatMap(retrievedEmail -> mAuthFramework
                                            .signInWithEmailAndPassword(retrievedEmail, finalPassword))
                                    .doFinally(mContainerView::hideLoadingIndicator);
                        }

                        signInFlow.subscribe(s -> onLoginCompleted.run(), mContainerViewModel.catchErrorThrowable());
                    } else {
                        TSnackbar snackbar = TSnackbar.make(dialogView, "Invalid username and/or password",
                                TSnackbar.LENGTH_LONG);

                        snackbar.setActionTextColor(Color.WHITE);
                        View snackbarView = snackbar.getView();
                        snackbarView.setBackgroundColor(mFragment.getResources().getColor(R.color.error));
                        TextView messageView = snackbarView.findViewById(com.androidadvance.topsnackbar.R.id.snackbar_text);
                        messageView.setTextColor(Color.WHITE);
                        messageView.setGravity(Gravity.CENTER);
                        snackbar.show();
                    }
                });

        loginDialog = builder.show();

        ImageButton btnClose = dialogView.findViewById(R.id.btnClose);
        btnClose.setVisibility(View.VISIBLE);
        RxView.clicks(btnClose)
                .throttleFirst(1, TimeUnit.SECONDS)
                .subscribe(o -> loginDialog.dismiss());
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
