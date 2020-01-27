package com.shawerapp.android.screens.profile.user.edit;

import android.app.DatePickerDialog;
import android.content.Context;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.jakewharton.rxbinding2.view.RxView;
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

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.functions.Consumer;
import timber.log.Timber;

public class ProfileEditFragment extends BaseFragment implements ProfileEditContract.View {

    public static final String ARG_TYPE = "type";

    public static ProfileEditFragment newInstance(String type) {

        Bundle args = new Bundle();
        args.putString(ARG_TYPE, type);

        ProfileEditFragment fragment = new ProfileEditFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Inject
    ProfileEditContract.ViewModel mViewModel;

    @BindView(R.id.profileEdit)
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

    @BindView(R.id.positionContainer)
    View mPositionContainer;

    @BindView(R.id.position)
    EditText mPosition;

    @BindView(R.id.companyNameContainer)
    View mCompanyNameContainer;

    @BindView(R.id.companyName)
    EditText mCompanyName;

    @BindView(R.id.companyRegisterNumberContainer)
    View mCompanyRegisterNumberContainer;

    @BindView(R.id.companyRegisterNumber)
    EditText mCompanyRegisterNumber;

    @BindView(R.id.companySizeContainer)
    View mCompanySizeContainer;

    @BindView(R.id.companySize)
    Spinner mCompanySize;

    @BindView(R.id.companyTypeContainer)
    View mCompanyTypeContainer;

    @BindView(R.id.companyType)
    Spinner mCompanyType;

    @BindView(R.id.companyNationalityContainer)
    View mCompanyNationalityContainer;

    @BindView(R.id.companyNationality)
    Spinner mCompanyNationality;

    @BindView(R.id.companyFormationDateContainer)
    View mCompanyFormationDateContainer;

    @BindView(R.id.companyFormationDate)
    TextView mCompanyFormationDate;

    @BindView(R.id.companyPhoneNumberContainer)
    View mCompanyPhoneNumberContainer;

    @BindView(R.id.companyPhoneNumber)
    EditText mCompanyPhoneNumber;

    @BindView(R.id.companyHeadquartersCountryContainer)
    View mCompanyHeadquartersCountryContainer;

    @BindView(R.id.companyHeadquartersCountry)
    Spinner mCompanyHeadquartersCountry;

    @BindView(R.id.companyHeadquartersCityContainer)
    View mCompanyHeadquartersCityContainer;

    @BindView(R.id.companyHeadquartersCity)
    EditText mCompanyHeadquartersCity;

    @BindView(R.id.currentPassword)
    EditText mCurrentPassword;

    @BindView(R.id.newPassword)
    EditText mNewPassword;

    @BindView(R.id.confirmNewPassword)
    EditText mConfirmNewPassword;

    private ArrayAdapter<String> mGenderAdapter;

    private ArrayAdapter<String> mCountryAdapter;

    private ArrayAdapter<String> mCompanyTypeAdapter;

    private ArrayAdapter<String> mCompanySizeAdapter;

    private ArrayAdapter<String> mCompanyNationalityAdapter;

    private Calendar mBirthdayCalendar;

    private Calendar mCompanyFormationDateCalendar;

    @Override
    protected FragmentLifecycle.ViewModel getViewModel() {
        return mViewModel;
    }

    @Override
    public void onAttach(Context context) {
        DaggerProfileEditComponent.builder()
                .containerComponent(((ContainerActivity) context).getContainerComponent())
                .profileEditModule(new ProfileEditModule(this, this))
                .build()
                .inject(this);

        super.onAttach(context);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile_edit, container, false);
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

        RxView.clicks(mCompanyFormationDate)
                .compose(bindUntilEvent(FragmentEvent.DESTROY_VIEW))
                .throttleFirst(1, TimeUnit.SECONDS)
                .subscribe(o -> showDateChooser(mCompanyFormationDateCalendar != null ? mCompanyFormationDateCalendar : Calendar.getInstance(),
                        dateSelected -> {
                            mCompanyFormationDate.setText(dateFormat.format(new Date(dateSelected)));
                            mCompanyFormationDateCalendar = Calendar.getInstance();
                            mCompanyFormationDateCalendar.setTimeInMillis(dateSelected);
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
        mCompanyHeadquartersCountry.setAdapter(mCountryAdapter);

        List<String> companyTypeChoices = Arrays.asList(getResources().getStringArray(R.array.company_type));
        mCompanyTypeAdapter = new ArrayAdapter<String>(getContext(), R.layout.item_company_type_display, companyTypeChoices) {
            @Override
            public long getItemId(int position) {
                return position;
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                if (convertView == null) {
                    convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_company_type_display, parent, false);
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
        mCompanyType.setAdapter(mCompanyTypeAdapter);

        List<String> companySizeChoices = Arrays.asList(getResources().getStringArray(R.array.company_size));
        mCompanySizeAdapter = new ArrayAdapter<String>(getContext(), R.layout.item_company_size_display, companySizeChoices) {
            @Override
            public long getItemId(int position) {
                return position;
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                if (convertView == null) {
                    convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_company_size_display, parent, false);
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
        mCompanySize.setAdapter(mCompanySizeAdapter);

        List<String> companyNationalityChoices = Arrays.asList(getResources().getStringArray(R.array.company_nationality));
        mCompanyNationalityAdapter = new ArrayAdapter<String>(getContext(), R.layout.item_company_nationality_display, companyNationalityChoices) {
            @Override
            public long getItemId(int position) {
                return position;
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                if (convertView == null) {
                    convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_company_nationality_display, parent, false);
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
        mCompanyNationality.setAdapter(mCompanyNationalityAdapter);

        mUsername.setEnabled(true);
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
        mCompanyHeadquartersCityContainer.setVisibility(View.GONE);

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
        mCompanyHeadquartersCityContainer.setVisibility(View.VISIBLE);

        mPositionContainer.setVisibility(View.VISIBLE);
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
        mUsername.setTag(username);
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
            } catch (NullPointerException | ParseException e) {
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
    public void setPosition(CharSequence position) {
        mPosition.setText(position);
        mPosition.setTag(position);
    }

    @Override
    public String getPosition() {
        return mPosition.getText().toString();
    }

    @Override
    public boolean isPositionChanged() {
        return (mPosition.getTag() == null && CommonUtils.isNotEmpty(getPosition())) || (CommonUtils.isNotEmpty((CharSequence) mPosition.getTag()) && !getPosition().equals(mPosition.getTag()));
    }

    @Override
    public void setCompanyName(CharSequence companyName) {
        mCompanyName.setText(companyName);
        mCompanyName.setTag(companyName);
    }

    @Override
    public String getCompanyName() {
        return mCompanyName.getText().toString();
    }

    @Override
    public boolean isCompanyNameChanged() {
        return (mCompanyName.getTag() == null && CommonUtils.isNotEmpty(getCompanyName())) || (CommonUtils.isNotEmpty((CharSequence) mCompanyName.getTag()) && !getCompanyName().equals(mCompanyName.getTag()));
    }

    @Override
    public void setCompanyRegisterNumber(CharSequence companyRegisterNumber) {
        mCompanyRegisterNumber.setText(companyRegisterNumber);
        mCompanyRegisterNumber.setTag(companyRegisterNumber);
    }

    @Override
    public String getCompanyRegisterNumber() {
        return mCompanyRegisterNumber.getText().toString();
    }

    @Override
    public boolean isCompanyRegisterNumberChanged() {
        return (mCompanyRegisterNumber.getTag() == null && CommonUtils.isNotEmpty(getCompanyRegisterNumber())) || (CommonUtils.isNotEmpty((CharSequence) mCompanyRegisterNumber.getTag()) && !getCompanyRegisterNumber().equals(mCompanyRegisterNumber.getTag()));
    }

    @Override
    public void setCompanySize(CharSequence companySize) {
        if (CommonUtils.isNotEmpty(companySize)) {
            int selectedPosition = mCompanySizeAdapter.getPosition(companySize.toString());
            mCompanySize.setSelection(selectedPosition);
            mCompanySize.setTag(companySize);
        }
    }

    @Override
    public String getCompanySize() {
        return (String) mCompanySize.getSelectedItem();
    }

    @Override
    public boolean isCompanySizeChanged() {
        return !getCompanySize().equals(getString(R.string.hint_company_size)) && ((mCompanySize.getTag() == null && CommonUtils.isNotEmpty(getCompanySize())) || (CommonUtils.isNotEmpty((CharSequence) mCompanySize.getTag()) && !getCompanySize().equals(mCompanySize.getTag())));
    }

    @Override
    public void setCompanyType(CharSequence companyType) {
        if (CommonUtils.isNotEmpty(companyType)) {
            int selectedPosition = mCompanyTypeAdapter.getPosition(companyType.toString());
            mCompanyType.setSelection(selectedPosition);
            mCompanyType.setTag(companyType);
        }
    }

    @Override
    public String getCompanyType() {
        return (String) mCompanyType.getSelectedItem();
    }

    @Override
    public boolean isCompanyTypeChanged() {
        return !getCompanyType().equals(getString(R.string.hint_company_type)) && ((mCompanyType.getTag() == null && CommonUtils.isNotEmpty(getCompanyType())) || (CommonUtils.isNotEmpty((CharSequence) mCompanyType.getTag()) && !getCompanyType().equals(mCompanyType.getTag())));
    }

    @Override
    public void setCompanyNationality(CharSequence companyNationality) {
        if (CommonUtils.isNotEmpty(companyNationality)) {
            int selectedPosition = mCompanyNationalityAdapter.getPosition(companyNationality.toString());
            mCompanyNationality.setSelection(selectedPosition);
            mCompanyNationality.setTag(companyNationality);
        }
    }

    @Override
    public String getCompanyNationality() {
        return (String) mCompanyNationality.getSelectedItem();
    }

    @Override
    public boolean isCompanyNationalityChanged() {
        return !getCompanyNationality().equals(getString(R.string.hint_company_nationality)) && ((mCompanyNationality.getTag() == null && CommonUtils.isNotEmpty(getCompanyNationality())) || (CommonUtils.isNotEmpty((CharSequence) mCompanyNationality.getTag()) && !getCompanyNationality().equals(mCompanyNationality.getTag())));
    }

    @Override
    public void setCompanyFormationDate(CharSequence companyFormationDate) {
        if (CommonUtils.isNotEmpty(companyFormationDate)) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
            try {
                Date date = dateFormat.parse(companyFormationDate.toString());
                mCompanyFormationDateCalendar = Calendar.getInstance();
                mCompanyFormationDateCalendar.setTime(date);
                mCompanyFormationDate.setText(companyFormationDate);
                mCompanyFormationDate.setTag(companyFormationDate);
            } catch (ParseException e) {
                Timber.e(CommonUtils.getExceptionString(e));
            }
        }
    }

    @Override
    public Date getCompanyFormationDate() {
        return mCompanyFormationDateCalendar != null ? mCompanyFormationDateCalendar.getTime() : null;
    }

    @Override
    public boolean isCompanyFormationDateChanged() {
        if (CommonUtils.isNotEmpty((CharSequence) mCompanyFormationDate.getTag())) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
            try {
                Date date = dateFormat.parse(((CharSequence) mCompanyFormationDate.getTag()).toString());
                return !date.equals(mCompanyFormationDateCalendar.getTime());
            } catch (NullPointerException | ParseException e) {
                Timber.e(CommonUtils.getExceptionString(e));
                return false;
            }
        } else {
            return mCompanyFormationDateCalendar != null && mCompanyFormationDateCalendar.getTimeInMillis() != 0L;
        }
    }

    @Override
    public void setCompanyPhoneNumber(CharSequence companyPhoneNumber) {
        mCompanyPhoneNumber.setText(companyPhoneNumber);
        mCompanyPhoneNumber.setTag(companyPhoneNumber);
    }

    @Override
    public String getCompanyPhoneNumber() {
        return mCompanyPhoneNumber.getText().toString();
    }

    @Override
    public boolean isCompanyPhoneNumberChanged() {
        return (mCompanyPhoneNumber.getTag() == null && CommonUtils.isNotEmpty(getCompanyPhoneNumber())) || (CommonUtils.isNotEmpty((CharSequence) mCompanyPhoneNumber.getTag()) && !getCompanyPhoneNumber().equals(mCompanyPhoneNumber.getTag()));
    }

    @Override
    public void setCompanyHeadquartersCountry(CharSequence companyHeadquartersCountry) {
        if (CommonUtils.isNotEmpty(companyHeadquartersCountry)) {
            int selectedPosition = mCountryAdapter.getPosition(companyHeadquartersCountry.toString());
            mCompanyHeadquartersCountry.setSelection(selectedPosition);
            mCompanyHeadquartersCountry.setTag(companyHeadquartersCountry);
        }
    }

    @Override
    public String getCompanyHeadquartersCountry() {
        return (String) mCompanyHeadquartersCountry.getSelectedItem();
    }

    @Override
    public boolean isCompanyHeadquartersCountryChanged() {
        return !getCountry().equals(getString(R.string.hint_country)) && ((mCompanyHeadquartersCountry.getTag() == null && CommonUtils.isNotEmpty(getCompanyHeadquartersCountry())) || (CommonUtils.isNotEmpty((CharSequence) mCompanyHeadquartersCountry.getTag()) && !getCompanyHeadquartersCountry().equals(mCompanyHeadquartersCountry.getTag())));
    }

    @Override
    public void setCompanyHeadquartersCity(CharSequence companyHeadquartersCity) {
        mCompanyHeadquartersCity.setText(companyHeadquartersCity);
        mCompanyHeadquartersCity.setTag(companyHeadquartersCity);
    }

    @Override
    public String getCompanyHeadquartersCity() {
        return mCompanyHeadquartersCity.getText().toString();
    }

    @Override
    public boolean isCompanyHeadquartersCityChanged() {
        return (mCompanyHeadquartersCity.getTag() == null && CommonUtils.isNotEmpty(getCompanyHeadquartersCity())) || (CommonUtils.isNotEmpty((CharSequence) mCompanyHeadquartersCity.getTag()) && !getCompanyHeadquartersCity().equals(mCompanyHeadquartersCity.getTag()));
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

    @Override
    public ViewGroup getFragmentView() {
        return mFragmentView;
    }
}
