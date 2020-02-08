package com.shawerapp.android.screens.payment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.collection.ArraySet;

import com.esafirm.imagepicker.model.Image;
import com.google.firebase.iid.FirebaseInstanceId;
import com.shawerapp.android.R;
import com.shawerapp.android.autovalue.Answer;
import com.shawerapp.android.autovalue.CommercialUser;
import com.shawerapp.android.autovalue.Field;
import com.shawerapp.android.autovalue.IndividualUser;
import com.shawerapp.android.autovalue.LawyerUser;
import com.shawerapp.android.autovalue.Question;
import com.shawerapp.android.autovalue.SubSubject;
import com.shawerapp.android.backend.base.AuthFramework;
import com.shawerapp.android.backend.base.FileFramework;
import com.shawerapp.android.backend.base.RealTimeDataFramework;
import com.shawerapp.android.backend.base.RestFramework;
import com.shawerapp.android.base.BaseActivity;
import com.shawerapp.android.base.BaseFragment;
import com.shawerapp.android.base.BaseKey;
import com.shawerapp.android.base.FragmentStateChanger;
import com.shawerapp.android.screens.answerlist.AnswerListKey;
import com.shawerapp.android.screens.composer.ComposerContract;
import com.shawerapp.android.screens.composer.ComposerViewModel;
import com.shawerapp.android.screens.container.ContainerContract;
import com.shawerapp.android.screens.profile.lawyer.personal.PrivateLawyerViewKey;
import com.shawerapp.android.screens.profile.user.view.ProfileViewKey;
import com.shawerapp.android.screens.requestlist.RequestListKey;
import com.shawerapp.android.utils.CommonUtils;
import com.shawerapp.android.utils.LoginUtil;
import com.trello.rxlifecycle2.android.ActivityEvent;
import com.trello.rxlifecycle2.android.FragmentEvent;
import com.zhuinden.simplestack.Backstack;
import com.zhuinden.simplestack.BackstackDelegate;
import com.zhuinden.simplestack.History;
import com.zhuinden.simplestack.HistoryBuilder;
import com.zhuinden.simplestack.StateChange;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CancellationException;

import javax.inject.Inject;

import io.reactivex.Completable;
import io.reactivex.CompletableObserver;
import io.reactivex.Flowable;
import io.reactivex.Maybe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.internal.operators.completable.CompletableCreate;
import io.reactivex.internal.operators.completable.CompletableDefer;
import timber.log.Timber;

import static com.shawerapp.android.screens.composer.ComposerFragment.ARG_REQUEST_TYPE;
import static com.shawerapp.android.screens.composer.ComposerFragment.ARG_SELECTED_FIELD;
import static com.shawerapp.android.screens.composer.ComposerFragment.ARG_SELECTED_LAWYER;
import static com.shawerapp.android.screens.composer.ComposerFragment.ARG_SELECTED_SUBSUBJECT;
import static com.shawerapp.android.screens.composer.ComposerKey.DETAILS;
import static com.shawerapp.android.screens.composer.ComposerKey.FEEDBACK;
import static com.shawerapp.android.screens.composer.ComposerKey.PRACTICE;
import static com.shawerapp.android.screens.composer.ComposerKey.QUESTION;
import static com.shawerapp.android.screens.container.ContainerActivity.TYPE_LAWYER;
import static com.shawerapp.android.screens.payment.PaymentFragment.ARG_ATTACHMENT_FILE_UPLOAD;
import static com.shawerapp.android.screens.payment.PaymentFragment.ARG_AUDIO_FILE_UPLOAD;
import static com.shawerapp.android.screens.payment.PaymentFragment.ARG_QUESTION_DESCRIPTION;

public class PaymentViewModel implements PaymentContract.ViewModel {

    private BaseFragment mFragment;

    private PaymentContract.View mView;


    private BaseActivity mActivity;


    ComposerViewModel mComposerViewModel;

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

    BackstackDelegate mBackstackDelegate;

    private Image mSelectedProfilePicture;

    int mRequestType;

    Field mSelectedField;

    SubSubject mSelectedSubSubject;

    LawyerUser mSelectedLawyer;

    Maybe<String> questionDescription;
    Maybe<String> audioFileUpload;
    Maybe<List<String>> attachmentFileUpload;

    private long mPracticeRequestCost = 20;

    File mRecordedAudioFile;
    ArraySet<String> mSelectedFilesPaths;
    CharSequence mComposition;

    @Inject
    public PaymentViewModel(BaseFragment fragment, PaymentContract.View view) {
        mActivity = (BaseActivity) fragment.getActivity();

        mFragment = fragment;
        mView = view;

        Bundle args = fragment.getArguments();
        mRequestType = args.getInt(ARG_REQUEST_TYPE);
        mSelectedField = args.getParcelable(ARG_SELECTED_FIELD);
        mSelectedSubSubject = args.getParcelable(ARG_SELECTED_SUBSUBJECT);
        mSelectedLawyer = args.getParcelable(ARG_SELECTED_LAWYER);
        questionDescription = args.getParcelable(ARG_QUESTION_DESCRIPTION);
        audioFileUpload = args.getParcelable(ARG_AUDIO_FILE_UPLOAD);
        List<String> ss = (List<String>) args.getSerializable(ARG_ATTACHMENT_FILE_UPLOAD);
        attachmentFileUpload = Maybe.just(Objects.requireNonNull(ss));
        mRecordedAudioFile = (File) args.getSerializable("mRecordedAudioFile");
//        mSelectedFilesPaths = args.getParcelableArray("mSelectedFilesPaths");
        mComposition = args.getCharSequence("mComposition");
        mComposerViewModel = (ComposerViewModel) args.getSerializable("mComposerViewModel");

    }

    @Override
    public void onViewCreated() {
        mView.initBindings();
    }

    @Override
    public void onAfterEnterAnimation() {
//        mRTDataFramework.fetchUser(mLoginUtil.getUserID())
//                .compose(mFragment.bindUntilEvent(FragmentEvent.DESTROY_VIEW))
//                .doOnSubscribe(disposable -> mContainerView.showLoadingIndicator())
//                .doFinally(() -> mContainerView.hideLoadingIndicator())
//                .subscribe(user -> {
//                    if (user instanceof LawyerUser) {
//                        LawyerUser lawyerUser = (LawyerUser) user;
//                        setupProfileForLawyerUsers(lawyerUser);
//                    }
//                });

        mView.initBindings();
//        mBackstackDelegate = new BackstackDelegate(null);
//        mBackstackDelegate.registerForLifecycleCallbacks(mActivity);
    }

//    public void initializeBackstack(Bundle savedInstanceState) {
//        BaseKey initialKey;
//

//        mBackstackDelegate.onCreate(savedInstanceState,
//                mActivity.getLastCustomNonConfigurationInstance(), History.single(initialKey));
//        mBackstackDelegate.registerForLifecycleCallbacks(mActivity);
//
//        mFragmentStateChanger = new FragmentStateChanger(mActivity.getSupportFragmentManager(), R.id.container);
//        mBackstackDelegate.setStateChanger((stateChange, completionCallback) -> {
//            if (stateChange.topNewState().equals(stateChange.topPreviousState())) {
//                completionCallback.stateChangeComplete();
//                return;
//            }
//            mFragmentStateChanger.handleStateChange(stateChange);
//            completionCallback.stateChangeComplete();
//        });
//    }

//    @Override
//    public void setupProfileForLawyerUsers(LawyerUser lawyerUser) {
//        SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
//        mView.setUsername(lawyerUser.username());
//        mView.setProfilePicture(lawyerUser.imageUrl());
//        mView.setName(lawyerUser.fullName());
//        mView.setEmail(lawyerUser.emailAddress());
//        mView.setPhoneNumber(lawyerUser.phoneNumber());
//        mView.setGender(lawyerUser.gender());
//        if (lawyerUser.birthday() != null) {
//            mView.setBirthday(dateFormat.format(lawyerUser.birthday()));
//        }
//        mView.setCountry(lawyerUser.country());
//        mView.setCity(lawyerUser.city());
//    }
//
//    @Override
//    public void onProfileBioTextChanged(CharSequence textChange) {
//        mView.setProfileBioCount(mFragment.getString(R.string.format_profile_bio_count, textChange.length()));
//    }
//
//    @Override
//    public void onProfilePictureClicked() {
//        RxImagePicker.getInstance()
//                .start(mFragment.getContext(), ImagePicker.create(mFragment)
//                        .single()
//                        .showCamera(true)
//                        .theme(R.style.ImagePickerTheme))
//                .subscribe(images -> {
//                    if (images != null && images.size() > 0) {
//                        mSelectedProfilePicture = images.get(0);
//                        mView.setProfilePicture(mSelectedProfilePicture.getPath());
//                    }
//                }, throwable -> {
//                    Timber.e(CommonUtils.getExceptionString(throwable));
//                    mContainerView.showMessage(throwable.getMessage(), true);
//                });
//    }

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

    }

//    @Override
//    public void updateLawyerProfile() {
//        String userName = mView.getUsername();
//        String name = mView.getName();
//        String newEmail = mView.getEmail();
//        String phoneNumber = mView.getPhoneNumber();
//        String gender = mView.getGender();
//        Date birthday = mView.getBirthday();
//        String country = mView.getCountry();
//        String city = mView.getCity();
//        String profileBio = mView.getProfileBio();
//        String currentPassword = mView.getCurrentPassword();
//        String newPassword = mView.getNewPassword();
//
//        Completable uploadProfilePicture = Completable.complete();
//        if (mSelectedProfilePicture != null) {
//            uploadProfilePicture = mFileFramework.uploadProfileImage(Uri.fromFile(new File(mSelectedProfilePicture.getPath())))
//                    .flatMapCompletable(imageUrl -> mRTDataFramework.updateUser(LawyerUser.IMAGE_URL, imageUrl));
//        }
//
//        Completable updateuserName = Completable.complete();
//        if (mView.isUserNameChanged()) {
//            updateuserName = mRTDataFramework.updateUser(IndividualUser.USER_NAME, userName);
//        }
//
//        Completable updateName = Completable.complete();
//        if (mView.isNameChanged()) {
//            updateName = mRTDataFramework.updateUser(LawyerUser.FULL_NAME, name);
//        }
//
//        Completable updatePhoneNumber = Completable.complete();
//        if (mView.isPhoneNumberChanged()) {
//            updatePhoneNumber = mRTDataFramework.updateUser(LawyerUser.PHONE_NUMBER, phoneNumber);
//        }
//
//        Completable updateGender = Completable.complete();
//        if (mView.isGenderChanged()) {
//            updateGender = mRTDataFramework.updateUser(LawyerUser.GENDER, gender);
//        }
//
//        Completable updateBirthday = Completable.complete();
//        if (mView.isBirthdayChanged()) {
//            updateBirthday = mRTDataFramework.updateUser(LawyerUser.BIRTHDAY, birthday);
//        }
//
//        Completable updateCountry = Completable.complete();
//        if (mView.isCountryChanged()) {
//            updateCountry = mRTDataFramework.updateUser(LawyerUser.COUNTRY, country);
//        }
//
//        Completable updateCity = Completable.complete();
//        if (mView.isCityChanged()) {
//            updateCity = mRTDataFramework.updateUser(LawyerUser.CITY, city);
//        }
//
//        Completable updateProfileBio = Completable.complete();
//        if (mView.isProfileBioChanged()) {
//            updateProfileBio = mRTDataFramework.updateUser(LawyerUser.PROFILE_BIO, profileBio);
//        }
//
//        Completable updateFlow = updateuserName
//                .andThen(updateName)
//                .andThen(updatePhoneNumber)
//                .andThen(updateGender)
//                .andThen(updateBirthday)
//                .andThen(updateCountry)
//                .andThen(updateCity)
//                .andThen(updateProfileBio)
//                .andThen(uploadProfilePicture);
//
//        if ((CommonUtils.isNotEmpty(newEmail) && mView.isEmailChanged()) || (CommonUtils.isNotEmpty(newPassword))) {
//            showLoginPopup(newEmail, currentPassword, () -> {
//                mContainerView.showMessage("Successfully logged in!");
//
//                Completable updateEmail = Completable.complete();
//                if (mView.isEmailChanged()) {
//                    updateEmail = mRTDataFramework
//                            .updateUser(IndividualUser.EMAIL, newEmail)
//                            .andThen(mAuthFramework.updateEmail(newEmail));
//                }
//
//                Completable updatePassword = Completable.complete();
//                if (mView.isCityChanged()) {
//                    updatePassword = mAuthFramework.updatePassword(newPassword);
//                }
//
//                updateEmail
//                        .compose(mFragment.bindUntilEvent(FragmentEvent.DESTROY_VIEW))
//                        .doOnSubscribe(disposable -> mContainerView.showLoadingIndicator())
//                        .andThen(updatePassword)
//                        .andThen(updateFlow)
//                        .doFinally(() -> mContainerView.hideLoadingIndicator())
//                        .subscribe(
//                                () -> mContainerView.showMessage(mFragment.getString(R.string.message_success_profile)),
//                                mContainerViewModel.catchErrorThrowable());
//            });
//        } else {
//            updateFlow
//                    .compose(mFragment.bindUntilEvent(FragmentEvent.DESTROY_VIEW))
//                    .doOnSubscribe(disposable -> mContainerView.showLoadingIndicator())
//                    .doFinally(() -> mContainerView.hideLoadingIndicator())
//                    .subscribe(
//                            () -> mContainerView.showMessage(mFragment.getString(R.string.message_success_profile)),
//                            mContainerViewModel.catchErrorThrowable());
//        }
//    }
//
//    @Override
//    public void onRequestEditButtonClicked() {
//        mRTDataFramework
//                .requestLawyerProfileEdit()
//                .compose(mFragment.bindUntilEvent(FragmentEvent.DESTROY_VIEW))
//                .doOnSubscribe(disposable -> mContainerView.showLoadingIndicator())
//                .doFinally(() -> mContainerView.hideLoadingIndicator())
//                .subscribe(() -> {
//                    onBackButtonClicked();
//                    mContainerView.showMessage(mFragment.getString(R.string.success_edit_request));
//                });
//    }
//
//    @Override
//    public void showLoginPopup(String email, String currentPassword, Action onLoginCompleted) {
//        AlertDialog loginDialog;
//        AlertDialog.Builder builder = new AlertDialog.Builder(mFragment.getContext());
//        View dialogView = LayoutInflater.from(mFragment.getContext()).inflate(R.layout.activity_login, mView.getFragmentView(), false);
//        builder.setView(dialogView);
//
//        TextView loginMessage1 = dialogView.findViewById(R.id.loginMessage1);
//        loginMessage1.setText(R.string.relogin_message1);
//
//        TextView loginMessage2 = dialogView.findViewById(R.id.loginMessage2);
//        loginMessage2.setText(R.string.relogin_message2);
//
//        View bottomButtons = dialogView.findViewById(R.id.bottomButtons);
//        bottomButtons.setVisibility(View.GONE);
//
//        EditText usernameField = dialogView.findViewById(R.id.username);
//        RxTextView.afterTextChangeEvents(usernameField)
//                .subscribe(textChangeEvent -> {
//                    if (CommonUtils.isNotEmpty(textChangeEvent.editable())) {
//                        usernameField.setBackgroundResource(R.drawable.input_field_bg);
//                    }
//                });
//        usernameField.setText(email);
//
//        EditText passwordField = dialogView.findViewById(R.id.password);
//        RxTextView.afterTextChangeEvents(passwordField)
//                .subscribe(textChangeEvent -> {
//                    if (CommonUtils.isNotEmpty(textChangeEvent.editable())) {
//                        passwordField.setBackgroundResource(R.drawable.input_field_bg);
//                    }
//                });
//        passwordField.setText(currentPassword);
//
//        Button btnLogin = dialogView.findViewById(R.id.btnLogin);
//        RxView.clicks(btnLogin)
//                .subscribe(o -> {
//                    String username = "";
//                    if (CommonUtils.isNotEmpty(usernameField.getText())) {
//                        username = usernameField.getText().toString();
//                    } else {
//                        usernameField.setBackgroundResource(R.drawable.input_field_bg_error);
//                    }
//
//                    String password = "";
//                    if (CommonUtils.isNotEmpty(passwordField.getText())) {
//                        password = passwordField.getText().toString();
//                    } else {
//                        passwordField.setBackgroundResource(R.drawable.input_field_bg_error);
//                    }
//
//                    if (CommonUtils.isNotEmpty(username) && CommonUtils.isNotEmpty(password)) {
//                        Maybe<String> signInFlow;
//                        if (Patterns.EMAIL_ADDRESS.matcher(username).matches()) {
//                            signInFlow = mAuthFramework
//                                    .signInWithEmailAndPassword(username, password)
//                                    .doOnSubscribe(disposable -> mContainerView.showLoadingIndicator())
//                                    .doFinally(mContainerView::hideLoadingIndicator);
//                        } else {
//                            String finalPassword = password;
//                            signInFlow = mRestFramework
//                                    .getUserEmail(username)
//                                    .doOnSubscribe(disposable -> mContainerView.showLoadingIndicator())
//                                    .flatMap(retrievedEmail -> mAuthFramework
//                                            .signInWithEmailAndPassword(retrievedEmail, finalPassword))
//                                    .doFinally(mContainerView::hideLoadingIndicator);
//                        }
//
//                        signInFlow.subscribe(s -> onLoginCompleted.run(), mContainerViewModel.catchErrorThrowable());
//                    } else {
//                        TSnackbar snackbar = TSnackbar.make(dialogView, "Invalid username and/or password",
//                                TSnackbar.LENGTH_LONG);
//
//                        snackbar.setActionTextColor(Color.WHITE);
//                        View snackbarView = snackbar.getView();
//                        snackbarView.setBackgroundColor(mFragment.getResources().getColor(R.color.error));
//                        TextView messageView = snackbarView.findViewById(com.androidadvance.topsnackbar.R.id.snackbar_text);
//                        messageView.setTextColor(Color.WHITE);
//                        messageView.setGravity(Gravity.CENTER);
//                        snackbar.show();
//                    }
//                });
//
//        loginDialog = builder.show();
//
//        ImageButton btnClose = dialogView.findViewById(R.id.btnClose);
//        btnClose.setVisibility(View.VISIBLE);
//        RxView.clicks(btnClose)
//                .throttleFirst(1, TimeUnit.SECONDS)
//                .subscribe(o -> loginDialog.dismiss());
//    }

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

    @Override
    public void retrieveData() {

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

    }

    @Override
    public Completable newTop(BaseKey baseKey) {
        return new CompletableDefer(() ->
                new CompletableCreate(
                        emitter -> {
                            Backstack backstack = mBackstackDelegate.getBackstack(); // lifecycle integration
                            backstack.setHistory(HistoryBuilder.single(baseKey), StateChange.REPLACE);
                            emitter.onComplete();
                        }))
                .subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread())
                .compose(mActivity.bindUntilEvent(ActivityEvent.DESTROY));
    }

    @Override
    public CompletableObserver navigationObserver() {
        return new CompletableObserver() {
            @Override
            public void onSubscribe(@NonNull Disposable d) {

            }

            @Override
            public void onComplete() {
                BaseFragment top = mView.getCurrentFragmentInFrame();
            }

            @Override
            public void onError(Throwable throwable) {
                if (!(throwable instanceof CancellationException)) {
                    Timber.e(CommonUtils.getExceptionString(throwable));
                    mView.showMessage(mActivity.getString(R.string.error_default), true);
                }
            }
        };
    }

    @Override
    public Consumer<? super Throwable> catchErrorThrowable() {
        return null;
    }

    @SuppressLint("CheckResult")
    @Override
    public void onSubmitComposition(int mRequestType, File mRecordedAudioFile, ArraySet<String> mSelectedFilesPaths, CharSequence mComposition ){
        Maybe<String> audioFileUpload = Maybe.just("");
        if (mRecordedAudioFile != null) {
            audioFileUpload = mFileFramework.uploadAnswerAttachment(Uri.fromFile(mRecordedAudioFile))
                    .compose(mFragment.bindUntilEvent(FragmentEvent.DESTROY_VIEW));
        }

        Maybe<List<String>> attachmentFileUpload = Maybe.just(new ArrayList<>());
        if (mSelectedFilesPaths != null) {
            attachmentFileUpload = Flowable.fromIterable(mSelectedFilesPaths)
                    .flatMapMaybe(filePath -> mFileFramework.uploadAnswerAttachment(Uri.fromFile(new File(filePath))))
                    .toList()
                    .toMaybe()
                    .compose(mFragment.bindUntilEvent(FragmentEvent.DESTROY_VIEW));
        }

        Maybe<String> composition = Maybe.just("");
        if (CommonUtils.isNotEmpty(mComposition)) {
            composition = Maybe.just(mComposition.toString());
        }

        mContainerViewModel.hideRightToolbarButton();

        final List<String>[] attachmentsFiles = new List[]{new ArrayList<>()};

        attachmentFileUpload.map(authResult -> attachmentsFiles[0].addAll(authResult));


        if (mRequestType == QUESTION) {
            addNewQuestion(audioFileUpload, attachmentFileUpload, composition);
//            mContainerViewModel
//                    .goTo(PaymentKey.builder()
//                            .requestType(mRequestType)
//                            .selectedField(mSelectedField)
//                            .selectedSubSubject(mSelectedSubSubject)
//                            .selectedLawyerUser(mSelectedLawyer)
//                            .questionDescription(String.valueOf(composition))
//                            .attachmentFileUpload(attachmentsFiles[0])
//                            .audioFileUpload(String.valueOf(audioFileUpload))
//                            .build())
//                    .subscribe(mContainerViewModel.navigationObserver());
        } else if (mRequestType == PRACTICE) {
//            mContainerViewModel
//                    .goTo(PaymentKey.builder()
//                            .requestType(mRequestType)
//                            .selectedField(mSelectedField)
//                            .selectedSubSubject(mSelectedSubSubject)
//                            .selectedLawyerUser(mSelectedLawyer)
//                            .questionDescription(String.valueOf(composition))
//                            .attachmentFileUpload(attachmentsFiles[0])
//                            .audioFileUpload(String.valueOf(audioFileUpload))
//                            .build())
//                    .subscribe(mContainerViewModel.navigationObserver());
            addNewPracticeRequest(audioFileUpload, attachmentFileUpload, composition);
        }
    }

//    @Override
//    public Completable goBack() {
//        return new CompletableDefer(() ->
//                new CompletableCreate(
//                        emitter -> {
//                            Backstack backstack = mBackstackDelegate.getBackstack(); // lifecycle integration
//                            if (backstack.goBack()) {
//                                emitter.onComplete();
//                            }
//                        }))
//                .compose(mFragment.bindUntilEvent(FragmentEvent.DESTROY))
//                .subscribeOn(AndroidSchedulers.mainThread())
//                .observeOn(AndroidSchedulers.mainThread());
//    }

    @SuppressLint("CheckResult")
    public void addNewQuestion(Maybe<String> audioFileUpload, Maybe<List<String>> attachmentFileUpload, Maybe<String> questionDescription) {
        long questionServiceFee;
        if (mLoginUtil.getUserRole().equals(IndividualUser.ROLE_VALUE)) {
            questionServiceFee = mSelectedLawyer.individualFees().get(mSelectedSubSubject.uid());
        } else if (mLoginUtil.getUserRole().equals(CommercialUser.ROLE_VALUE)) {
            questionServiceFee = mSelectedLawyer.commercialFees().get(mSelectedSubSubject.uid());
        } else {
            questionServiceFee = 0L;
        }

        //                    if (currentCoins < questionServiceFee) {
//                        return Maybe.error(new Throwable(mFragment.getString(R.string.error_not_enough_coins)));
//                    } else {
//                        return Maybe.just(currentCoins);
//                    }
        mRTDataFramework
                .retrieveUserCoins()
                .doOnSubscribe(disposable -> mContainerView.showLoadingIndicator())
                .flatMap(Maybe::just)
                .flatMap(currentCoins -> Maybe.zip(audioFileUpload, attachmentFileUpload, questionDescription,
                        (recordedAudioUrl, attachmentUrls, description) -> Answer.builder()
                                .audioRecordingUrl(recordedAudioUrl)
                                .fileAttachments(attachmentUrls)
                                .questionDescription(description)
                                .build()))
                .flatMapCompletable(answer -> mRTDataFramework
                        .addQuestion(
                                mSelectedField,
                                mSelectedSubSubject,
                                mSelectedLawyer,
                                answer,
                                "" + System.currentTimeMillis())
                        .compose(mFragment.bindUntilEvent(FragmentEvent.DESTROY)))
                .doFinally(mContainerView::hideLoadingIndicator)
                .subscribe(
                        () -> mView.showSuccessDialog(
                                mFragment.getString(R.string.Paymentprocessedsuccessfully),
                                () -> mContainerViewModel
                                        .newTop(AnswerListKey.create())
                                        .subscribe(mContainerViewModel.navigationObserver())),
                        throwable -> {
                            if (throwable.getMessage().equalsIgnoreCase(mFragment.getString(R.string.error_not_enough_coins))) {
//                                showNotEnoughCoinsPopup();
                            } else {
                                mContainerViewModel.catchErrorThrowable().accept(throwable);
                            }
                        });
    }

    public void addNewPracticeRequest(Maybe<String> audioFileUpload, Maybe<List<String>> attachmentFileUpload, Maybe<String> questionDescription) {
        mRTDataFramework
                .retrieveUserCoins()
                .doOnSubscribe(disposable -> mContainerView.showLoadingIndicator())
                .flatMap(currentCoins -> {
//                    if (currentCoins < mPracticeRequestCost) {
//                        return Maybe.error(new Throwable(mFragment.getString(R.string.error_not_enough_coins)));
//                    } else {
//                        return Maybe.just(currentCoins);
//                    }
                    return Maybe.just(currentCoins);
                })
                .flatMap(currentCoins -> Maybe.zip(audioFileUpload, attachmentFileUpload, questionDescription,
                        (recordedAudioUrl, attachmentUrls, description) -> Answer.builder()
                                .audioRecordingUrl(recordedAudioUrl)
                                .fileAttachments(attachmentUrls)
                                .questionDescription(description)
                                .build()))
                .flatMapCompletable(answer -> mRTDataFramework
                        .addPracticeRequest(
                                mSelectedField,
                                mSelectedSubSubject,
                                answer.audioRecordingUrl(),
                                answer.fileAttachments(),
                                answer.questionDescription(),
                                mPracticeRequestCost)
                        .compose(mFragment.bindUntilEvent(FragmentEvent.DESTROY)))
                .doFinally(mContainerView::hideLoadingIndicator)
                .subscribe(
                        () -> mView.showSuccessDialog(
                                mFragment.getString(R.string.Paymentprocessedsuccessfully),
                                () -> mContainerViewModel
                                        .newTop(RequestListKey.create())
                                        .subscribe(mContainerViewModel.navigationObserver())),
                        throwable -> {
                            if (throwable.getMessage().equalsIgnoreCase(mFragment.getString(R.string.error_not_enough_coins))) {
//                                showNotEnoughCoinsPopup();
                            } else {
                                mContainerViewModel.catchErrorThrowable().accept(throwable);
                            }
                        });
    }

}
