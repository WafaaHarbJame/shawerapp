package com.shawerapp.android.autovalue;

import android.os.Parcelable;

import com.google.android.gms.common.internal.service.Common;
import com.google.auto.value.AutoValue;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.messaging.FirebaseMessaging;
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
public abstract class LawyerUser implements Parcelable {

    public static final String ROLE_VALUE = "lawyer";

    public static final String FCM_TOKEN = "fcmToken";

    public static final String FULL_NAME = "fullName";

    public static final String PHONE_NUMBER = "phoneNumber";

    public static final String GENDER = "gender";

    public static final String BIRTHDAY = "birthday";

    public static final String COUNTRY = "country";

    public static final String CITY = "city";

    public static final String EMAIL = "email";

    public static final String IMAGE_URL = "imageUrl";

    public static final String PROFILE_BIO = "profileBio";

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
    public abstract Long questionsReceived();

    @Nullable
    public abstract Long answersSent();

    @Nullable
    public abstract String profileBio();

    @Nullable
    public abstract String status();

    @Nullable
    @GsonTypeAdapter(UtcDateTypeAdapter.class)
    public abstract Date dateJoined();

    @Nullable
    @GsonTypeAdapter(UtcDateTypeAdapter.class)
    public abstract Date dateActivated();

    @Nullable
    public abstract String bankName();

    @Nullable
    public abstract String bankAccountNumber();

    @Nullable
    public abstract Map<String, Boolean> subSubjects();

    @Nullable
    public abstract Map<String, Long> individualFees();

    @Nullable
    public abstract Map<String, Long> commercialFees();

    @Nullable
    public abstract Map<String, Boolean> likes();

    @Nullable
    public abstract Map<String, Memo> memos();

    @Nullable
    public abstract Map<String, Boolean> favoritedBy();

    @Nullable
    public abstract Map<String, Boolean> reportedBy();

    @Nullable
    public abstract String yearsOfExperience();

    @Nullable
    public abstract String fcmToken();

    @Nullable
    public abstract Long coins();

    @Nullable
    public abstract String presence();

    public static Builder builder() {
        FirebaseMessaging.getInstance().subscribeToTopic("lawyer")
                .addOnCompleteListener(task -> {

                });
        return new AutoValue_LawyerUser.Builder();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LawyerUser that = (LawyerUser) o;
        return Objects.equals(uid(), that.uid());
    }

    @Override
    public int hashCode() {
        return Objects.hash(uid());
    }

    @Override
    public String toString() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }

    public static TypeAdapter<LawyerUser> typeAdapter(Gson gson) {
        return new AutoValue_LawyerUser.GsonTypeAdapter(gson);
    }

    public static LawyerUser createFromSnapshot(DocumentSnapshot documentSnapshot) {
        Gson gson = new Gson();
        String jsonString = gson.toJson(documentSnapshot.getData());

        LawyerUser LawyerUser = null;
        try {
            LawyerUser = AutoValue_LawyerUser.typeAdapter(gson).fromJson(jsonString);
        } catch (IOException e) {
            Timber.e(CommonUtils.getExceptionString(e));
        }
        return LawyerUser;
    }

    public Map<String, Object> toMap() {
        Gson gson = new Gson();
        Type type = new TypeToken<Map<String, Object>>() {
        }.getType();
        Map<String, Object> mapped = gson.fromJson(AutoValue_LawyerUser.typeAdapter(new Gson()).toJson(this), type);

        String birthday = (String) mapped.get("birthday");
        String dateJoined = (String) mapped.get("dateJoined");
        String dateActivated = (String) mapped.get("dateActivated");
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
        } catch (ParseException e) {
            Timber.e(CommonUtils.getExceptionString(e));
        }
        return mapped;
    }

    public static LawyerUser create(String uid, String imageUrl, String username, String emailAddress, String role, String fullName, String phoneNumber, String gender, Date birthday, String country, String city, Long questionsReceived, Long answersSent, String profileBio, String status, Date dateJoined, Date dateActivated, String bankName, String bankAccountNumber, Map<String, Boolean> subSubjects, Map<String, Long> individualFees, Map<String, Long> commercialFees, Map<String, Boolean> likes, Map<String, Memo> memos, Map<String, Boolean> favoritedBy, Map<String, Boolean> reportedBy, String yearsOfExperience, String fcmToken, Long coins, String presence) {
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
                .questionsReceived(questionsReceived)
                .answersSent(answersSent)
                .profileBio(profileBio)
                .status(status)
                .dateJoined(dateJoined)
                .dateActivated(dateActivated)
                .bankName(bankName)
                .bankAccountNumber(bankAccountNumber)
                .subSubjects(subSubjects)
                .individualFees(individualFees)
                .commercialFees(commercialFees)
                .likes(likes)
                .memos(memos)
                .favoritedBy(favoritedBy)
                .reportedBy(reportedBy)
                .yearsOfExperience(yearsOfExperience)
                .fcmToken(fcmToken)
                .coins(coins)
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

        public abstract Builder status(String status);

        public abstract Builder profileBio(String profileBio);

        public abstract Builder dateJoined(Date dateJoined);

        public abstract Builder dateActivated(Date dateActivated);

        public abstract Builder fcmToken(String fcmToken);

        public abstract Builder imageUrl(String imageUrl);

        public abstract Builder subSubjects(Map<String, Boolean> subSubjects);

        public abstract Builder likes(Map<String, Boolean> likes);

        public abstract Builder yearsOfExperience(String yearsOfExperience);

        public abstract Builder memos(Map<String, Memo> memos);

        public abstract Builder individualFees(Map<String, Long> individualFees);

        public abstract Builder commercialFees(Map<String, Long> commercialFees);

        public abstract Builder coins(Long coins);

        public abstract Builder questionsReceived(Long questionsReceived);

        public abstract Builder answersSent(Long answersSent);

        public abstract Builder bankName(String bankName);

        public abstract Builder bankAccountNumber(String bankAccountNumber);

        public abstract Builder favoritedBy(Map<String, Boolean> favoritedBy);

        public abstract Builder reportedBy(Map<String, Boolean> reportedBy);

        public abstract Builder presence(String presence);

        public abstract LawyerUser build();
    }

    public class Status {
        public static final String ACTIVATED = "activated";

        public static final String PENDING = "pending";

        public static final String DEACTIVATED = "deactivated";
    }
}
