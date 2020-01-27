package com.shawerapp.android.autovalue;

import android.os.Parcelable;

import com.google.auto.value.AutoValue;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.reflect.TypeToken;
import com.ryanharter.auto.value.gson.GsonTypeAdapter;
import com.shawerapp.android.autovalue.annotation.IncludeHashEquals;
import com.shawerapp.android.utils.CommonUtils;
import com.shawerapp.android.utils.UtcDateTypeAdapter;

import java.io.IOException;
import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.Objects;

import javax.annotation.Nullable;

import timber.log.Timber;

@AutoValue
public abstract class CommercialUser implements Parcelable {

    public static final String ROLE_VALUE = "company";

    public static final String FCM_TOKEN = "fcmToken";

    public static final String FULL_NAME = "fullName";

    public static final String PHONE_NUMBER = "phoneNumber";

    public static final String GENDER = "gender";

    public static final String BIRTHDAY = "birthday";

    public static final String COUNTRY = "country";

    public static final String CITY = "city";

    public static final String POSITION = "position";

    public static final String COMPANY_NAME = "companyName";

    public static final String COMPANY_REGISTER_NUMBER = "companyRegisterNumber";

    public static final String COMPANY_SIZE = "companySize";

    public static final String COMPANY_TYPE = "companyType";

    public static final String COMPANY_NATIONALITY = "companyNationality";

    public static final String COMPANY_FORMATION_DATE = "companyFormationDate";

    public static final String COMPANY_PHONE_NUMBER = "companyPhoneNumber";

    public static final String COMPANY_HEADQUARTERS_COUNTRY = "companyHeadquartersCountry";

    public static final String COMPANY_HEADQUARTERS_CITY = "companyHeadquartersCity";

    public static final String EMAIL = "email";

    @IncludeHashEquals
    public abstract String uid();

    @Nullable
    public abstract String imageUrl();

    @Nullable
    public abstract String username();

    @Nullable
    public abstract String emailAddress();

    @Nullable
    public abstract String role();

    @Nullable
    public abstract String fullName();

    @Nullable
    public abstract String phoneNumber();

    @Nullable
    public abstract String gender();

    @Nullable
    @GsonTypeAdapter(UtcDateTypeAdapter.class)
    public abstract Date birthday();

    @Nullable
    public abstract String country();

    @Nullable
    public abstract String city();

    @Nullable
    public abstract String position();

    @Nullable
    public abstract Long questionsAsked();

    @Nullable
    public abstract Long practiceRequests();

    @Nullable
    public abstract Long answersRated();

    @Nullable
    public abstract Long answersReceived();

    @Nullable
    public abstract String companyName();

    @Nullable
    public abstract String status();

    @Nullable
    @GsonTypeAdapter(UtcDateTypeAdapter.class)
    public abstract Date dateJoined();

    @Nullable
    @GsonTypeAdapter(UtcDateTypeAdapter.class)
    public abstract Date dateActivated();

    @Nullable
    public abstract String companyRegisterNumber();

    @Nullable
    public abstract String companySize();

    @Nullable
    public abstract String companyType();

    @Nullable
    public abstract String companyNationality();

    @Nullable
    @GsonTypeAdapter(UtcDateTypeAdapter.class)
    public abstract Date companyFormationDate();

    @Nullable
    public abstract String companyPhoneNumber();

    @Nullable
    public abstract String companyHeadquartersCountry();

    @Nullable
    public abstract String companyHeadquartersCity();

    @Nullable
    public abstract String fcmToken();

    @Nullable
    public abstract Long coins();

    @Nullable
    public abstract Map<String, Boolean> reportedBy();

    @Nullable
    public abstract String presence();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CommercialUser that = (CommercialUser) o;
        return Objects.equals(uid(), that.uid());
    }

    @Override
    public int hashCode() {
        return Objects.hash(uid());
    }

    public static Builder builder() {
        return new AutoValue_CommercialUser.Builder();
    }

    public static TypeAdapter<CommercialUser> typeAdapter(Gson gson) {
        return new AutoValue_CommercialUser.GsonTypeAdapter(gson);
    }

    public static CommercialUser createFromSnapshot(DocumentSnapshot documentSnapshot) {
        Gson gson = new Gson();
        String jsonString = gson.toJson(documentSnapshot.getData());

        CommercialUser CommercialUser = null;
        try {
            CommercialUser = AutoValue_CommercialUser.typeAdapter(gson).fromJson(jsonString);
        } catch (IOException e) {
            Timber.e(CommonUtils.getExceptionString(e));
        }
        return CommercialUser;
    }

    public Map<String, Object> toMap() {
        Gson gson = new Gson();
        Type type = new TypeToken<Map<String, Object>>() {
        }.getType();
        Map<String, Object> mapped = gson.fromJson(AutoValue_CommercialUser.typeAdapter(new Gson()).toJson(this), type);

        String birthday = (String) mapped.get("birthday");
        String dateJoined = (String) mapped.get("dateJoined");
        String dateActivated = (String) mapped.get("dateActivated");
        String companyFormationDate = (String) mapped.get("companyFormationDate");
        try {
            if (CommonUtils.isNotEmpty(birthday)) {
                mapped.put("birthday", new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").parse(birthday));
            } else {
                mapped.remove("birthday");
            }
            if (CommonUtils.isNotEmpty(dateJoined)) {
                mapped.put("dateJoined", new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").parse(dateJoined));
            } else {
                mapped.remove("dateJoined");
            }
            if (CommonUtils.isNotEmpty(dateActivated)) {
                mapped.put("dateActivated", new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").parse(dateActivated));
            } else {
                mapped.remove("dateActivated");
            }
            if (CommonUtils.isNotEmpty(companyFormationDate)) {
                mapped.put("companyFormationDate", new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").parse(companyFormationDate));
            } else {
                mapped.remove("companyFormationDate");
            }
        } catch (ParseException e) {
            Timber.e(CommonUtils.getExceptionString(e));
        }
        return mapped;
    }

    public static CommercialUser create(String uid, String imageUrl, String username, String emailAddress, String role, String fullName, String phoneNumber, String gender, Date birthday, String country, String city, String position, Long questionsAsked, Long practiceRequests, Long answersRated, Long answersReceived, String companyName, String status, Date dateJoined, Date dateActivated, String companyRegisterNumber, String companySize, String companyType, String companyNationality, Date companyFormationDate, String companyPhoneNumber, String companyHeadquartersCountry, String companyHeadquartersCity, String fcmToken, Long coins, Map<String, Boolean> reportedBy, String presence) {
        return builder()
                .uid(uid)
                .imageUrl(imageUrl)
                .username(username)
                .emailAddress(emailAddress)
                .role(role)
                .fullName(fullName)
                .phoneNumber(phoneNumber)
                .gender(gender)
                .birthday(birthday)
                .country(country)
                .city(city)
                .position(position)
                .questionsAsked(questionsAsked)
                .practiceRequests(practiceRequests)
                .answersRated(answersRated)
                .answersReceived(answersReceived)
                .companyName(companyName)
                .status(status)
                .dateJoined(dateJoined)
                .dateActivated(dateActivated)
                .companyRegisterNumber(companyRegisterNumber)
                .companySize(companySize)
                .companyType(companyType)
                .companyNationality(companyNationality)
                .companyFormationDate(companyFormationDate)
                .companyPhoneNumber(companyPhoneNumber)
                .companyHeadquartersCountry(companyHeadquartersCountry)
                .companyHeadquartersCity(companyHeadquartersCity)
                .fcmToken(fcmToken)
                .coins(coins)
                .reportedBy(reportedBy)
                .presence(presence)
                .build();
    }

    @AutoValue.Builder
    public abstract static class Builder {
        public abstract Builder uid(String uid);

        public abstract Builder username(String username);

        public abstract Builder emailAddress(String emailAddress);

        public abstract Builder role(String role);

        public abstract Builder fullName(String fullName);

        public abstract Builder phoneNumber(String phoneNumber);

        public abstract Builder gender(String gender);

        public abstract Builder birthday(Date birthday);

        public abstract Builder country(String country);

        public abstract Builder city(String city);

        public abstract Builder companyName(String companyName);

        public abstract Builder companyRegisterNumber(String companyRegisterNumber);

        public abstract Builder companyType(String companyType);

        public abstract Builder companyNationality(String companyNationality);

        public abstract Builder companyFormationDate(Date companyFormationDate);

        public abstract Builder companyPhoneNumber(String companyPhoneNumber);

        public abstract Builder companyHeadquartersCountry(String companyHeadquartersCountry);

        public abstract Builder companyHeadquartersCity(String companyHeadquartersCity);

        public abstract Builder status(String status);

        public abstract Builder dateJoined(Date dateJoined);

        public abstract Builder dateActivated(Date dateActivated);

        public abstract Builder fcmToken(String fcmToken);

        public abstract Builder companySize(String companySize);

        public abstract Builder imageUrl(String imageUrl);

        public abstract Builder position(String position);

        public abstract Builder questionsAsked(Long questionsAsked);

        public abstract Builder practiceRequests(Long practiceRequests);

        public abstract Builder answersRated(Long answersRated);

        public abstract Builder answersReceived(Long answersReceived);

        public abstract Builder coins(Long coins);

        public abstract Builder reportedBy(Map<String, Boolean> reportedBy);

        public abstract Builder presence(String presence);

        public abstract CommercialUser build();
    }

    public class Status {
        public static final String ACTIVATED = "activated";

        public static final String PENDING = "pending";

        public static final String DEACTIVATED = "deactivated";
    }
}
