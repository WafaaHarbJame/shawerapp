package com.shawerapp.android.screens.profile.lawyer.edit;

import android.app.DatePickerDialog;
import android.content.Context;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.functions.Consumer;
import timber.log.Timber;

import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.jakewharton.rxbinding2.view.RxView;
import com.jakewharton.rxbinding2.widget.RxTextView;
import com.shawerapp.android.R;
import com.shawerapp.android.backend.glide.GlideApp;
import com.shawerapp.android.base.BaseFragment;
import com.shawerapp.android.base.FragmentLifecycle;
import com.shawerapp.android.screens.container.ContainerActivity;
import com.shawerapp.android.utils.CommonUtils;
import com.trello.rxlifecycle2.android.FragmentEvent;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

public class LawyerEditFragment extends BaseFragment implements LawyerEditContract.View {

    public static LawyerEditFragment newInstance() {

        Bundle args = new Bundle();

        LawyerEditFragment
                fragment = new LawyerEditFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Inject
    LawyerEditContract.ViewModel mViewModel;

    @BindView(R.id.lawyerProfileEdit)
    ViewGroup mFragmentView;

    @BindView(R.id.profilePicture)
    ImageView mProfilePicture;

    @BindView(R.id.username)
    EditText mUsername;

    @BindView(R.id.name)
    EditText mName;

    @BindView(R.id.emailAddress)
    EditText mEmailAddress;

    @BindView(R.id.phoneNumber)
    EditText mPhoneNumber;

    @BindView(R.id.genderChooser)
    Spinner mGender;

    @BindView(R.id.birthdayContainer)
    View mBirthdayContainer;

    @BindView(R.id.birthday)
    TextView mBirthday;

    @BindView(R.id.countryChooser)
    Spinner mCountry;

    @BindView(R.id.city)
    EditText mCity;

    @BindView(R.id.profileBio)
    EditText mProfileBio;

    @BindView(R.id.profileBioImageHint)
    ImageView mProfileBioImageHint;

    @BindView(R.id.profileBioCount)
    TextView mProfileBioCount;

    @BindView(R.id.currentPassword)
    EditText mCurrentPassword;

    @BindView(R.id.newPassword)
    EditText mNewPassword;

    @BindView(R.id.confirmNewPassword)
    EditText mConfirmNewPassword;

    @BindView(R.id.btnRequestEdit)
    Button mBtnRequestEdit;

    private ArrayAdapter<String> mGenderAdapter;

    private ArrayAdapter<String> mCountryAdapter;

    private Calendar mBirthdayCalendar;

    @Override
    protected FragmentLifecycle.ViewModel getViewModel() {
        return mViewModel;
    }

    @Override
    public void onAttach(Context context) {
        DaggerLawyerEditComponent.builder()
                .containerComponent(((ContainerActivity) context).getContainerComponent())
                .lawyerEditModule(new LawyerEditModule(this, this))
                .build()
                .inject(this);

        super.onAttach(context);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_lawyer_profile_edit, container, false);
        mUnbinder = ButterKnife.bind(this, view);

        return view;
    }

    @Override
    public void initBindings() {
        RxView.clicks(mProfilePicture)
                .compose(bindUntilEvent(FragmentEvent.DESTROY_VIEW))
                .throttleFirst(1, TimeUnit.SECONDS)
                .subscribe(o -> mViewModel.onProfilePictureClicked());

        SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
        RxView.clicks(mBirthday)
                .compose(bindUntilEvent(FragmentEvent.DESTROY_VIEW))
                .throttleFirst(1, TimeUnit.SECONDS)
                .subscribe(o -> showDateChooser(mBirthdayCalendar != null ? mBirthdayCalendar : Calendar.getInstance(),
                        dateSelected -> {
                            mBirthday.setText(dateFormat.format(new Date(dateSelected)));
                            mBirthdayCalendar = Calendar.getInstance();
                            mBirthdayCalendar.setTimeInMillis(dateSelected);
                        }));

        List<String> genderChoices = new ArrayList();
        genderChoices.add(getString(R.string.hint_gender));
        genderChoices.add(getString(R.string.male));
        genderChoices.add(getString(R.string.female));
        mGenderAdapter = new ArrayAdapter<String>(getContext(), R.layout.item_gender_display, genderChoices) {
            @Override
            public long getItemId(int position) {
                return position;
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                if (convertView == null) {
                    convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_gender_display, parent, false);
                }
                TextView option = (TextView) convertView;
                option.setText(getItem(position));
                return convertView;
            }

            @Override
            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                if (convertView == null) {
                    convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_dropdown, parent, false);
                }
                TextView option = (TextView) convertView;
                option.setText(getItem(position));
                return convertView;
            }
        };
        mGender.setAdapter(mGenderAdapter);

        List<String> countryNames = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.countries)));
        countryNames.add(0, getString(R.string.hint_country));
        mCountryAdapter = new ArrayAdapter<String>(getContext(), R.layout.item_country_display, countryNames) {
            @Override
            public long getItemId(int position) {
                return position;
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                if (convertView == null) {
                    convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_country_display, parent, false);
                }
                TextView option = (TextView) convertView;
                option.setText(getItem(position));
                return convertView;
            }

            @Override
            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                if (convertView == null) {
                    convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_dropdown, parent, false);
                }
                TextView option = (TextView) convertView;
                option.setText(getItem(position));
                return convertView;
            }
        };
        mCountry.setAdapter(mCountryAdapter);

        RxView.focusChanges(mProfileBio)
                .compose(bindUntilEvent(FragmentEvent.DESTROY_VIEW))
                .subscribe(isFocused -> {
                    if (isFocused) {
                        mProfileBioImageHint.setImageResource(R.mipmap.icon_profile_bio_active);
                    } else {
                        mProfileBioImageHint.setImageResource(R.mipmap.icon_profile_bio_inactive);
                    }
                });

        RxTextView.afterTextChangeEvents(mProfileBio)
                .compose(bindUntilEvent(FragmentEvent.DESTROY_VIEW))
                .subscribe(textChangeEvent -> mViewModel.onProfileBioTextChanged(textChangeEvent.editable()));

        RxView.clicks(mBtnRequestEdit)
                .compose(bindUntilEvent(FragmentEvent.DESTROY_VIEW))
                .throttleFirst(1, TimeUnit.SECONDS)
                .subscribe(o -> mViewModel.onRequestEditButtonClicked());
    }

    @Override
    public ViewGroup getFragmentView() {
        return mFragmentView;
    }

    @Override
    public void showDateChooser(Calendar initialCalendar, Consumer<Long> dateSelected) {
        new DatePickerDialog(getContext(), R.style.DialogStyle,
                (view, year, month, dayOfMonth) -> {
                    initialCalendar.set(Calendar.YEAR, year);
                    initialCalendar.set(Calendar.MONTH, month);
                    initialCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                    try {
                        dateSelected.accept(initialCalendar.getTimeInMillis());
                    } catch (Exception e) {
                        Timber.e(CommonUtils.getExceptionString(e));
                    }
                },
                initialCalendar.get(Calendar.YEAR),
                initialCalendar.get(Calendar.MONTH),
                initialCalendar.get(Calendar.DAY_OF_MONTH))
                .show();
    }

    @Override
    public void setProfilePicture(String filePath) {
        if (CommonUtils.isNotEmpty(filePath)) {
            GlideApp.with(this)
                    .load(filePath)
                    .centerCrop()
                    .transform(new CircleCrop())
                    .into(mProfilePicture);
        }
    }

    @Override
    public void setUsername(CharSequence username) {
        mUsername.setText(username);
    }

    @Override
    public String getUsername() {
        return mUsername.getText().toString();
    }

    @Override
    public boolean isUserNameChanged() {
        return (mUsername.getTag() == null && CommonUtils.isNotEmpty(mUsername.getText())) || (CommonUtils.isNotEmpty((CharSequence) mUsername.getTag()) && !getUsername().equals(mUsername.getTag()));
    }

    @Override
    public void setName(CharSequence name) {
        mName.setText(name);
        mName.setTag(name);
    }

    @Override
    public String getName() {
        return mName.getText().toString();
    }

    @Override
    public boolean isNameChanged() {
        return (mName.getTag() == null && CommonUtils.isNotEmpty(mName.getText())) || (CommonUtils.isNotEmpty((CharSequence) mName.getTag()) && !getName().equals(mName.getTag()));
    }

    @Override
    public void setEmail(CharSequence emailAddress) {
        mEmailAddress.setText(emailAddress);
        mEmailAddress.setTag(emailAddress);
    }

    @Override
    public String getEmail() {
        return mEmailAddress.getText().toString();
    }

    @Override
    public boolean isEmailChanged() {
        return (mEmailAddress.getTag() == null && CommonUtils.isNotEmpty(mEmailAddress.getText())) || (CommonUtils.isNotEmpty((CharSequence) mEmailAddress.getTag()) && !getEmail().equals(mEmailAddress.getTag()));
    }

    @Override
    public void setPhoneNumber(CharSequence phoneNumber) {
        mPhoneNumber.setText(phoneNumber);
        mPhoneNumber.setTag(phoneNumber);
    }

    @Override
    public String getPhoneNumber() {
        return mPhoneNumber.getText().toString();
    }

    @Override
    public boolean isPhoneNumberChanged() {
        return (mPhoneNumber.getTag() == null && CommonUtils.isNotEmpty(mPhoneNumber.getText())) || (CommonUtils.isNotEmpty((CharSequence) mPhoneNumber.getTag()) && !getPhoneNumber().equals(mPhoneNumber.getTag()));
    }

    @Override
    public void setGender(CharSequence gender) {
        if (CommonUtils.isNotEmpty(gender)) {
            int selectedPosition = mGenderAdapter.getPosition(gender.toString());
            mGender.setSelection(selectedPosition);
            mGender.setTag(gender);
        }
    }

    @Override
    public String getGender() {
        return (String) mGender.getSelectedItem();
    }

    @Override
    public boolean isGenderChanged() {
        return !getGender().equals(getString(R.string.hint_gender)) && (mGender.getTag() == null && CommonUtils.isNotEmpty(getGender())) || (CommonUtils.isNotEmpty((CharSequence) mGender.getTag()) && !getGender().equals(mGender.getTag()));
    }

    @Override
    public void setBirthday(CharSequence birthday) {
        if (CommonUtils.isNotEmpty(birthday)) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
            try {
                Date date = dateFormat.parse(birthday.toString());
                mBirthdayCalendar = Calendar.getInstance();
                mBirthdayCalendar.setTime(date);
                mBirthday.setText(birthday);
                mBirthday.setTag(birthday);
            } catch (ParseException e) {
                Timber.e(CommonUtils.getExceptionString(e));
            }
        }
    }

    @Override
    public Date getBirthday() {
        return mBirthdayCalendar != null ? mBirthdayCalendar.getTime() : null;
    }

    @Override
    public boolean isBirthdayChanged() {
        if (CommonUtils.isNotEmpty((CharSequence) mBirthday.getTag())) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
            try {
                Date date = dateFormat.parse(((CharSequence) mBirthday.getTag()).toString());
                return !date.equals(mBirthdayCalendar.getTime());
            } catch (ParseException e) {
                Timber.e(CommonUtils.getExceptionString(e));
                return false;
            }
        } else {
            return mBirthdayCalendar != null && mBirthdayCalendar.getTimeInMillis() != 0L;
        }
    }

    @Override
    public void setCountry(CharSequence country) {
        if (CommonUtils.isNotEmpty(country)) {
            int selectedPosition = mCountryAdapter.getPosition(country.toString());
            mCountry.setSelection(selectedPosition);
            mCountry.setTag(country);
        }
    }

    @Override
    public String getCountry() {
        return (String) mCountry.getSelectedItem();
    }

    @Override
    public boolean isCountryChanged() {
        return !getCountry().equals(getString(R.string.hint_country)) && ((mCountry.getTag() == null && CommonUtils.isNotEmpty(getCountry())) || (CommonUtils.isNotEmpty((CharSequence) mCountry.getTag()) && !getCountry().equals(mCountry.getTag())));
    }

    @Override
    public void setCity(CharSequence city) {
        mCity.setText(city);
        mCity.setTag(city);
    }

    @Override
    public String getCity() {
        return mCity.getText().toString();
    }

    @Override
    public boolean isCityChanged() {
        return (mCity.getTag() == null && CommonUtils.isNotEmpty(getCity())) || (CommonUtils.isNotEmpty((CharSequence) mCity.getTag()) && !getCity().equals(mCity.getTag()));
    }

    @Override
    public void setProfileBio(CharSequence position) {
        mProfileBio.setText(position);
    }

    @Override
    public String getProfileBio() {
        return mProfileBio.getText().toString();
    }

    @Override
    public boolean isProfileBioChanged() {
        return (mProfileBio.getTag() == null && CommonUtils.isNotEmpty(getProfileBio())) || (CommonUtils.isNotEmpty((CharSequence) mProfileBio.getTag()) && !getProfileBio().equals(mProfileBio.getTag()));
    }

    @Override
    public void setProfileBioCount(String count) {
        mProfileBioCount.setText(count);
    }

    @Override
    public String getCurrentPassword() {
        return mCurrentPassword.getText().toString();
    }

    @Override
    public String getNewPassword() {
        String newPassword = mNewPassword.getText().toString();
        String confirmPassword = mConfirmNewPassword.getText().toString();

        if (CommonUtils.isNotEmpty(newPassword) && CommonUtils.isNotEmpty(confirmPassword)) {
            if (newPassword.equals(confirmPassword)) {
                return newPassword;
            } else {
                return null;
            }
        } else {
            return null;
        }
    }
}
