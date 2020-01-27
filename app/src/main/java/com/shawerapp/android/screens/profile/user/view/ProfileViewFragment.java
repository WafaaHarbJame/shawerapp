package com.shawerapp.android.screens.profile.user.view;

import android.content.Context;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import androidx.appcompat.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.jakewharton.rxbinding2.support.design.widget.RxAppBarLayout;
import com.jakewharton.rxbinding2.view.RxView;
import com.shawerapp.android.R;
import com.shawerapp.android.backend.glide.GlideApp;
import com.shawerapp.android.base.BaseFragment;
import com.shawerapp.android.base.FragmentLifecycle;
import com.shawerapp.android.screens.container.ContainerActivity;
import com.shawerapp.android.screens.container.ContainerContract;
import com.shawerapp.android.utils.CommonUtils;
import com.transitionseverywhere.AutoTransition;
import com.transitionseverywhere.TransitionManager;
import com.trello.rxlifecycle2.android.FragmentEvent;

import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.android.schedulers.AndroidSchedulers;

public class ProfileViewFragment extends BaseFragment implements ProfileViewContract.View {

    public static final String ARG_TYPE = "type";

    public static ProfileViewFragment newInstance(String type) {

        Bundle args = new Bundle();
        args.putString(ARG_TYPE, type);

        ProfileViewFragment fragment = new ProfileViewFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Inject
    ProfileViewContract.ViewModel mViewModel;

    @Inject
    ContainerContract.View mContainerView;

    @BindView(R.id.profileView)
    ViewGroup mFragmentView;

    @BindView(R.id.questions)
    View mQuestions;

    @BindView(R.id.questionsAskedCount)
    TextView mQuestionsAskedCount;

    @BindView(R.id.answers)
    View mAnswers;

    @BindView(R.id.answersReceivedCount)
    TextView mAnswersReceivedCount;

    @BindView(R.id.ratedAnswers)
    View mRatedAnswers;

    @BindView(R.id.ratedAnswersCount)
    TextView mRatedAnswersCount;

    @BindView(R.id.practiceRequests)
    View mPracticeRequests;

    @BindView(R.id.practiceRequestsCount)
    TextView mPracticeRequestsCount;

    @BindView(R.id.profilePicture)
    ImageView mProfilePicture;

    @BindView(R.id.username)
    TextView mUsername;

    @BindView(R.id.specificInfo)
    TextView mSpecificInfo;

    @BindView(R.id.coins)
    TextView mCoins;

    @BindView(R.id.btnAddCoins)
    View mBtnAddCoins;

    @BindView(R.id.invoicesButton)
    Button invoicesButton;

    @BindView(R.id.name)
    TextView mName;

    @BindView(R.id.emailAddress)
    TextView mEmailAddress;

    @BindView(R.id.phoneNumber)
    TextView mPhoneNumber;

    @BindView(R.id.gender)
    TextView mGender;

    @BindView(R.id.birthdayContainer)
    View mBirthdayContainer;

    @BindView(R.id.birthday)
    TextView mBirthday;

    @BindView(R.id.country)
    TextView mCountry;

    @BindView(R.id.city)
    TextView mCity;

    @BindView(R.id.positionContainer)
    View mPositionContainer;

    @BindView(R.id.position)
    TextView mPosition;

    @BindView(R.id.companyNameContainer)
    View mCompanyNameContainer;

    @BindView(R.id.companyName)
    TextView mCompanyName;

    @BindView(R.id.companyRegisterNumberContainer)
    View mCompanyRegisterNumberContainer;

    @BindView(R.id.companyRegisterNumber)
    TextView mCompanyRegisterNumber;

    @BindView(R.id.companySizeContainer)
    View mCompanySizeContainer;

    @BindView(R.id.companySize)
    TextView mCompanySize;

    @BindView(R.id.companyTypeContainer)
    View mCompanyTypeContainer;

    @BindView(R.id.companyType)
    TextView mCompanyType;

    @BindView(R.id.companyNationalityContainer)
    View mCompanyNationalityContainer;

    @BindView(R.id.companyNationality)
    TextView mCompanyNationality;

    @BindView(R.id.companyFormationDateContainer)
    View mCompanyFormationDateContainer;

    @BindView(R.id.companyFormationDate)
    TextView mCompanyFormationDate;

    @BindView(R.id.companyPhoneNumberContainer)
    View mCompanyPhoneNumberContainer;

    @BindView(R.id.companyPhoneNumber)
    TextView mCompanyPhoneNumber;

    @BindView(R.id.companyHeadquartersCountryContainer)
    View mCompanyHeadquartersCountryContainer;

    @BindView(R.id.companyHeadquartersCountry)
    TextView mCompanyHeadquartersCountry;

    @BindView(R.id.companyHeadquartersCity)
    TextView mCompanyHeadquartersCity;

    @BindView(R.id.app_bar_layout)
    AppBarLayout mAppBarLayout;

    @BindView(R.id.collapsing_toolbar)
    CollapsingToolbarLayout mCollapsingToolbarLayout;

    @BindView(R.id.profilePictureCollapsed)
    ImageView mProfilePictureCollapsed;

    @BindView(R.id.usernameCollapsed)
    TextView mUsernameCollapsed;

    @BindView(R.id.collapsedToolbar)
    Toolbar mCollapsedToolbar;

    @Override
    protected FragmentLifecycle.ViewModel getViewModel() {
        return mViewModel;
    }

    @Override
    public void onAttach(Context context) {
        DaggerProfileViewComponent.builder()
                .containerComponent(((ContainerActivity) context).getContainerComponent())
                .profileViewModule(new ProfileViewModule(this, this))
                .build()
                .inject(this);

        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        ViewGroup.LayoutParams toolbarParams = mContainerView.getToolbar().getLayoutParams();
        toolbarParams.height = getResources().getDimensionPixelSize(R.dimen.size_55);
        mContainerView.getToolbar().setLayoutParams(toolbarParams);
        super.onDetach();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile_view, container, false);
        mUnbinder = ButterKnife.bind(this, view);

        return view;
    }

    @Override
    public void initBindings() {

        //        RxView.clicks(mBtnAddCoins)
//                .throttleFirst(1, TimeUnit.SECONDS)
//                .compose(bindUntilEvent(FragmentEvent.DESTROY_VIEW))
//                .subscribe(o -> mViewModel.onAddCoinsButtonClicked());

        mBtnAddCoins.setVisibility(View.GONE);

//        RxView.clicks(invoicesButton)
//                .throttleFirst(1, TimeUnit.SECONDS)
//                .compose(bindUntilEvent(FragmentEvent.DESTROY_VIEW))
//                .subscribe(o -> mViewModel.openPayment());

        RxView.clicks(invoicesButton)
                .throttleFirst(1, TimeUnit.SECONDS)
                .compose(bindUntilEvent(FragmentEvent.DESTROY_VIEW))
                .subscribe(o -> mViewModel.openInvoices());

        RxAppBarLayout.offsetChanges(mAppBarLayout)
                .throttleLatest(100, TimeUnit.MILLISECONDS)
                .compose(bindUntilEvent(FragmentEvent.DESTROY_VIEW))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(offset -> {
                    AutoTransition transition = new AutoTransition();
                    transition.setDuration(100);
                    transition.excludeTarget(mFragmentView, true);
                    TransitionManager.beginDelayedTransition(mContainerView.getActivityView(), transition);
                    ViewGroup.LayoutParams toolbarParams = mContainerView.getToolbar().getLayoutParams();
                    if (offset == 0) {
                        mCollapsedToolbar.setVisibility(View.INVISIBLE);
                        toolbarParams.height = getResources().getDimensionPixelSize(R.dimen.size_55);
                    } else {
                        if (Math.abs(offset) == mAppBarLayout.getTotalScrollRange()) {
                            mCollapsedToolbar.setVisibility(View.VISIBLE);
                            toolbarParams.height = 0;
                        } else {
                            mCollapsedToolbar.setVisibility(View.INVISIBLE);
                        }
                    }
                    mContainerView.getToolbar().setLayoutParams(toolbarParams);
                });
    }

    @Override
    public void showIndividualFields() {
        mBirthdayContainer.setVisibility(View.VISIBLE);

        mCompanyNameContainer.setVisibility(View.GONE);
        mCompanyRegisterNumberContainer.setVisibility(View.GONE);
        mCompanySizeContainer.setVisibility(View.GONE);
        mCompanyTypeContainer.setVisibility(View.GONE);
        mCompanyNationalityContainer.setVisibility(View.GONE);
        mCompanyFormationDateContainer.setVisibility(View.GONE);
        mCompanyPhoneNumberContainer.setVisibility(View.GONE);
        mCompanyHeadquartersCountryContainer.setVisibility(View.GONE);

        mPositionContainer.setVisibility(View.GONE);
    }

    @Override
    public void showCommercialFields() {
        mBirthdayContainer.setVisibility(View.GONE);

        mCompanyNameContainer.setVisibility(View.VISIBLE);
        mCompanyRegisterNumberContainer.setVisibility(View.VISIBLE);
        mCompanySizeContainer.setVisibility(View.VISIBLE);
        mCompanyTypeContainer.setVisibility(View.VISIBLE);
        mCompanyNationalityContainer.setVisibility(View.VISIBLE);
        mCompanyFormationDateContainer.setVisibility(View.VISIBLE);
        mCompanyPhoneNumberContainer.setVisibility(View.VISIBLE);
        mCompanyHeadquartersCountryContainer.setVisibility(View.VISIBLE);

        mPositionContainer.setVisibility(View.VISIBLE);
    }

    @Override
    public void setUsername(CharSequence username) {
        mUsername.setText(username);
        mUsernameCollapsed.setText(username);
    }

    @Override
    public void setSpecificInfo(CharSequence specificInfo) {
        mSpecificInfo.setText(specificInfo);
    }

    @Override
    public void setCoins(CharSequence coins) {
        mCoins.setText(getString(R.string.invoices));
    }

    @Override
    public void setProfilePicture(String imageUrl) {
        if (CommonUtils.isNotEmpty(imageUrl)) {
            GlideApp.with(this)
                    .load(imageUrl)
                    .centerCrop()
                    .transform(new CircleCrop())
                    .into(mProfilePicture);

            GlideApp.with(this)
                    .load(imageUrl)
                    .centerCrop()
                    .transform(new CircleCrop())
                    .into(mProfilePictureCollapsed);
        }
    }

    @Override
    public void setName(CharSequence name) {
        mName.setText(name);
    }

    @Override
    public void setEmail(CharSequence emailAddress) {
        mEmailAddress.setText(emailAddress);
    }

    @Override
    public void setPhoneNumber(CharSequence phoneNumber) {
        mPhoneNumber.setText(phoneNumber);
    }

    @Override
    public void setGender(CharSequence gender) {
        mGender.setText(gender);
    }

    @Override
    public void setBirthday(CharSequence birthday) {
        mBirthday.setText(birthday);
    }

    @Override
    public void setCountry(CharSequence country) {
        mCountry.setText(country);
    }

    @Override
    public void setCity(CharSequence city) {
        mCity.setText(city);
    }

    @Override
    public void setPosition(CharSequence position) {
        mPosition.setText(position);
    }

    @Override
    public void setCompanyName(CharSequence companyName) {
        mCompanyName.setText(companyName);
    }

    @Override
    public void setCompanyRegisterNumber(CharSequence companyRegisterNumber) {
        mCompanyRegisterNumber.setText(companyRegisterNumber);
    }

    @Override
    public void setCompanySize(CharSequence companySize) {
        mCompanySize.setText(companySize);
    }

    @Override
    public void setCompanyType(CharSequence companyType) {
        mCompanyType.setText(companyType);
    }

    @Override
    public void setCompanyNationality(CharSequence companyNationality) {
        mCompanyNationality.setText(companyNationality);
    }

    @Override
    public void setCompanyFormationDate(CharSequence companyFormationDate) {
        mCompanyFormationDate.setText(companyFormationDate);
    }

    @Override
    public void setCompanyPhoneNumber(CharSequence companyPhoneNumber) {
        mCompanyPhoneNumber.setText(companyPhoneNumber);
    }

    @Override
    public void setCompanyHeadquartersCountry(CharSequence companyHeadquartersCountry) {
        mCompanyHeadquartersCountry.setText(companyHeadquartersCountry);
    }

    @Override
    public void setCompanyHeadquartersCity(CharSequence companyHeadquartersCity) {
        mCompanyHeadquartersCity.setText(companyHeadquartersCity);
    }

    @Override
    public void setQuestionsAskedCount(String questionsAsked) {
        mQuestionsAskedCount.setText(questionsAsked);
    }

    @Override
    public void setAnswersRecieved(String answersReceived) {
        mAnswersReceivedCount.setText(answersReceived);
    }

    @Override
    public void setAnswersRated(String answersRated) {
        mRatedAnswersCount.setText(answersRated);
    }

    @Override
    public void setPracticeRequests(String practiceRequests) {
        mPracticeRequestsCount.setText(practiceRequests);
    }
}
