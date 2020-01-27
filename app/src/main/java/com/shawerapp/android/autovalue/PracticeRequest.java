package com.shawerapp.android.autovalue;

import android.os.Parcelable;

import com.google.android.gms.common.internal.service.Common;
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
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.annotation.Nullable;

import timber.log.Timber;

@AutoValue
public abstract class PracticeRequest implements Parcelable {

    public abstract String uid();

    @Nullable
    public abstract String ar_subSubjectName();

    @Nullable
    public abstract String subSubjectName();

    @Nullable
    public abstract Long serviceFee();

    @Nullable
    public abstract String origin();

    @Nullable
    @GsonTypeAdapter(UtcDateTypeAdapter.class)
    public abstract Date dateCreated();

    @Nullable
    @GsonTypeAdapter(UtcDateTypeAdapter.class)
    public abstract Date dateFulfilled();

    @Nullable
    public abstract String audioRecordingUrl();

    @Nullable
    public abstract String questionDescription();

    @Nullable
    public abstract List<String> fileAttachments();

    @Nullable
    public abstract String requesterUid();

    @Nullable
    public abstract String lawyerName();

    @Nullable
    public abstract String lawyerAddress();

    @Nullable
    public abstract String lawyerContactNo();

    @Nullable
    public abstract String lawyerOfficeName();

    @Nullable
    public abstract String shawerSpecialPromoCode();

    @Nullable
    public abstract String status();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PracticeRequest that = (PracticeRequest) o;
        return Objects.equals(uid(), that.uid());
    }

    @Override
    public int hashCode() {
        return Objects.hash(uid());
    }

    public static TypeAdapter<PracticeRequest> typeAdapter(Gson gson) {
        return new $AutoValue_PracticeRequest.GsonTypeAdapter(gson);
    }

    public static PracticeRequest createFromSnapshot(DocumentSnapshot documentSnapshot) {
        Gson gson = new Gson();
        String jsonString = gson.toJson(documentSnapshot.getData());

        PracticeRequest PracticeRequest = null;
        try {
            PracticeRequest = AutoValue_PracticeRequest.typeAdapter(gson).fromJson(jsonString);
        } catch (IOException e) {
            Timber.e(CommonUtils.getExceptionString(e));
        }
        return PracticeRequest;
    }

    public Map<String, Object> toMap() {
        Gson gson = new Gson();
        Type type = new TypeToken<Map<String, Object>>() {
        }.getType();
        Map<String, Object> mapped = gson.fromJson(AutoValue_PracticeRequest.typeAdapter(new Gson()).toJson(this), type);

        String dateCreated = (String) mapped.get("dateCreated");
        String dateFulfilled = (String) mapped.get("dateFulfilled");
        try {
            if (CommonUtils.isNotEmpty(dateCreated)) {
                mapped.put("dateCreated", new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").parse(dateCreated));
            } else {
                mapped.remove("dateCreated");
            }
            if (CommonUtils.isNotEmpty(dateFulfilled)) {
                mapped.put("dateFulfilled", new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").parse(dateFulfilled));
            } else {
                mapped.remove("dateFulfilled");
            }
        } catch (ParseException e) {
            Timber.e(CommonUtils.getExceptionString(e));
        }
        return mapped;
    }

    public static PracticeRequest create(String uid, String ar_subSubjectName, String subSubjectName, Long serviceFee, String origin, Date dateCreated, Date dateFulfilled, String audioRecordingUrl, String questionDescription, List<String> fileAttachments, String requesterUid, String lawyerName, String lawyerAddress, String lawyerContactNo, String lawyerOfficeName, String shawerSpecialPromoCode, String status) {
        return builder()
                .uid(uid)
                .ar_subSubjectName(ar_subSubjectName)
                .subSubjectName(subSubjectName)
                .serviceFee(serviceFee)
                .origin(origin)
                .dateCreated(dateCreated)
                .dateFulfilled(dateFulfilled)
                .audioRecordingUrl(audioRecordingUrl)
                .questionDescription(questionDescription)
                .fileAttachments(fileAttachments)
                .requesterUid(requesterUid)
                .lawyerName(lawyerName)
                .lawyerAddress(lawyerAddress)
                .lawyerContactNo(lawyerContactNo)
                .lawyerOfficeName(lawyerOfficeName)
                .shawerSpecialPromoCode(shawerSpecialPromoCode)
                .status(status)
                .build();
    }

    public static Builder builder() {
        return new AutoValue_PracticeRequest.Builder();
    }

    @AutoValue.Builder
    public abstract static class Builder {
        public abstract Builder uid(String uid);

        public abstract Builder subSubjectName(String subSubjectName);

        public abstract Builder origin(String origin);

        public abstract Builder dateCreated(Date dateCreated);

        public abstract Builder dateFulfilled(Date dateFulfilled);

        public abstract Builder status(String status);

        public abstract Builder audioRecordingUrl(String audioRecordingUrl);

        public abstract Builder questionDescription(String questionDescription);

        public abstract Builder fileAttachments(List<String> fileAttachments);

        public abstract Builder requesterUid(String requesterUid);

        public abstract Builder serviceFee(Long serviceFee);

        public abstract Builder lawyerName(String lawyerName);

        public abstract Builder lawyerAddress(String lawyerAddress);

        public abstract Builder lawyerContactNo(String lawyerContactNo);

        public abstract Builder lawyerOfficeName(String lawyerOfficeName);

        public abstract Builder shawerSpecialPromoCode(String shawerSpecialPromoCode);

        public abstract Builder ar_subSubjectName(String ar_subSubjectName);

        public abstract PracticeRequest build();
    }

    public class Status {

        public static final String FULFILLED = "fulfilled";

        public static final String UNFULFILLED = "unfulfilled";
    }
}
