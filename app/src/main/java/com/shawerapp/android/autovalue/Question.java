package com.shawerapp.android.autovalue;

import android.os.Parcelable;

import com.google.auto.value.AutoValue;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.reflect.TypeToken;
import com.ryanharter.auto.value.gson.GsonTypeAdapter;
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
public abstract class Question implements Parcelable {

    @Nullable
    public abstract String uid();

    @Nullable
    public abstract String ar_subSubjectName();

    @Nullable
    public abstract String subSubjectName();

    @Nullable
    public abstract String subSubjectUid();

    @Nullable
    public abstract String fieldUid();

    @Nullable
    public abstract String ar_fieldName();

    @Nullable
    public abstract String fieldName();

    @Nullable
    public abstract String askerUid();

    @Nullable
    public abstract String askerRole();

    @Nullable
    public abstract String askerUsername();

    @Nullable
    public abstract String askerImageUrl();

    @Nullable
    public abstract String assignedLawyerUid();

    @Nullable
    public abstract String assignedLawyerName();

    @Nullable
    public abstract String assignedLawyerUsername();

    @Nullable
    public abstract Long serviceFee();

    @Nullable
    @GsonTypeAdapter(UtcDateTypeAdapter.class)
    public abstract Date dateAdded();

    @Nullable
    public abstract String status();

    @Nullable
    public abstract Boolean paid();

    @Nullable
    public abstract Boolean activeStatus();

    @Nullable
    public abstract String transactionID();

    @Nullable
    public abstract String answerFeedback();


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Question that = (Question) o;
        return Objects.equals(uid(), that.uid());
    }

    @Override
    public int hashCode() {
        return Objects.hash(uid());
    }

    public static TypeAdapter<Question> typeAdapter(Gson gson) {
        return new AutoValue_Question.GsonTypeAdapter(gson);
    }

    public static Question createFromSnapshot(DocumentSnapshot documentSnapshot) {
        Gson gson = new Gson();
        String jsonString = gson.toJson(documentSnapshot.getData());

        Question Question = null;
        try {
            Question = AutoValue_Question.typeAdapter(gson).fromJson(jsonString);
        } catch (IOException e) {
            Timber.e(CommonUtils.getExceptionString(e));
        }
        return Question;
    }

    public Map<String, Object> toMap() {
        Gson gson = new Gson();

        Type type = new TypeToken<Map<String, Object>>() {
        }.getType();
        Map<String, Object> mapped = gson.fromJson(AutoValue_Question.typeAdapter(new Gson()).toJson(this), type);

        String dateStr = (String) mapped.get("dateAdded");
        try {
            if (CommonUtils.isNotEmpty(dateStr)) {
                mapped.put("dateAdded", new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").parse(dateStr));
            } else {
                mapped.remove("dateAdded");
            }
        } catch (ParseException e) {
            Timber.e(CommonUtils.getExceptionString(e));
        }
        return mapped;
    }

    public static Question create(String uid, String ar_subSubjectName,
                                  String subSubjectName, String subSubjectUid, String fieldUid, String ar_fieldName,
                                  String fieldName, String askerUid, String askerRole, String askerUsername, String askerImageUrl,
                                  String assignedLawyerUid, String assignedLawyerName, String assignedLawyerUsername,
                                  Long serviceFee, Date dateAdded, String status, String answerFeedback, Boolean paid, Boolean activeStatus, String transactionID) {
        return builder()
                .uid(uid)
                .ar_subSubjectName(ar_subSubjectName)
                .subSubjectName(subSubjectName)
                .subSubjectUid(subSubjectUid)
                .fieldUid(fieldUid)
                .ar_fieldName(ar_fieldName)
                .fieldName(fieldName)
                .askerUid(askerUid)
                .askerRole(askerRole)
                .askerUsername(askerUsername)
                .askerImageUrl(askerImageUrl)
                .assignedLawyerUid(assignedLawyerUid)
                .assignedLawyerName(assignedLawyerName)
                .assignedLawyerUsername(assignedLawyerUsername)
                .serviceFee(serviceFee)
                .dateAdded(dateAdded)
                .status(status)
                .answerFeedback(answerFeedback)
                .paid(paid)
                .activeStatus(activeStatus)
                .transactionID(transactionID)
                .build();
    }

    public static Builder builder() {
        return new AutoValue_Question.Builder();
    }

    @AutoValue.Builder
    public abstract static class Builder {
        public abstract Builder uid(String uid);

        public abstract Builder subSubjectName(String subSubjectName);

        public abstract Builder subSubjectUid(String subSubjectUid);

        public abstract Builder fieldUid(String fieldUid);

        public abstract Builder fieldName(String fieldName);

        public abstract Builder askerUid(String askerUid);

        public abstract Builder askerRole(String askerRole);

        public abstract Builder assignedLawyerUid(String assignedLawyerUid);

        public abstract Builder assignedLawyerName(String assignedLawyerName);

        public abstract Builder status(String status);

        public abstract Builder dateAdded(Date dateAdded);

        public abstract Builder serviceFee(Long serviceFee);

        public abstract Builder answerFeedback(String answerFeedback);

        public abstract Builder askerUsername(String askerUsername);

        public abstract Builder assignedLawyerUsername(String assignedLawyerUsername);

        public abstract Builder askerImageUrl(String askerImageUrl);

        public abstract Builder ar_subSubjectName(String ar_subSubjectName);

        public abstract Builder ar_fieldName(String ar_fieldName);

        public abstract Builder paid(Boolean paid);

        public abstract Builder activeStatus(Boolean activeStatus);

        public abstract Builder transactionID(String transactionID);

        public abstract Question build();
    }

    public class Status {

        public static final String PENDING_ANSWER = "pendingAnswer";

        public static final String OPEN_FOR_FEEDBACK = "openForFeedback";

        public static final String HAS_FEEDBACK = "hasFeedback";

        public static final String OPEN_FOR_MORE_DETAILS = "openForMoreDetails";

        public static final String HAS_MORE_DETAILS = "hasMoreDetails";

        public static final String CLOSED = "closed";
    }

    public class Feedback {

        public static final String GOOD = "good";

        public static final String BAD = "bad";
    }
}
