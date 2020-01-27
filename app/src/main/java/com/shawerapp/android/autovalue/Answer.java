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
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.annotation.Nullable;

import timber.log.Timber;

@AutoValue
public abstract class Answer implements Parcelable {

    @Nullable
    public abstract String uid();

    @Nullable
    public abstract String audioRecordingUrl();

    @Nullable
    public abstract String questionDescription();

    @Nullable
    public abstract List<String> fileAttachments();

    @Nullable
    public abstract String senderUid();

    @Nullable
    public abstract String senderName();

    @Nullable
    public abstract String senderRole();

    @Nullable
    @GsonTypeAdapter(UtcDateTypeAdapter.class)
    public abstract Date dateSent();

    @Nullable
    public abstract String questionUid();

    @Nullable
    public abstract String answerFor();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Answer that = (Answer) o;
        return Objects.equals(uid(), that.uid());
    }

    @Override
    public int hashCode() {
        return Objects.hash(uid());
    }

    public static TypeAdapter<Answer> typeAdapter(Gson gson) {
        return new AutoValue_Answer.GsonTypeAdapter(gson);
    }

    public static Answer createFromSnapshot(DocumentSnapshot documentSnapshot) {
        Gson gson = new Gson();
        String jsonString = gson.toJson(documentSnapshot.getData());

        Answer Answer = null;
        try {
            Answer = AutoValue_Answer.typeAdapter(gson).fromJson(jsonString);
        } catch (IOException e) {
            Timber.e(CommonUtils.getExceptionString(e));
        }
        return Answer;
    }

    public Map<String, Object> toMap() {
        Gson gson = new Gson();
        Type type = new TypeToken<Map<String, Object>>() {
        }.getType();
        Map<String, Object> mapped = gson.fromJson(AutoValue_Answer.typeAdapter(new Gson()).toJson(this), type);

        String dateSent = (String) mapped.get("dateSent");
        try {
            mapped.put("dateSent", new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").parse(dateSent));
        } catch (ParseException e) {
            Timber.e(CommonUtils.getExceptionString(e));
        }
        return mapped;
    }

    public static Answer create(String uid, String audioRecordingUrl, String questionDescription, List<String> fileAttachments, String senderUid, String senderName, String senderRole, Date dateSent, String questionUid, String answerFor) {
        return builder()
                .uid(uid)
                .audioRecordingUrl(audioRecordingUrl)
                .questionDescription(questionDescription)
                .fileAttachments(fileAttachments)
                .senderUid(senderUid)
                .senderName(senderName)
                .senderRole(senderRole)
                .dateSent(dateSent)
                .questionUid(questionUid)
                .answerFor(answerFor)
                .build();
    }

    public static Builder builder() {
        return new AutoValue_Answer.Builder();
    }

    @AutoValue.Builder
    public abstract static class Builder {
        public abstract Builder uid(String uid);

        public abstract Builder audioRecordingUrl(String audioRecordingUrl);

        public abstract Builder questionDescription(String questionDescription);

        public abstract Builder fileAttachments(List<String> fileAttachments);

        public abstract Builder senderUid(String senderUid);

        public abstract Builder senderRole(String senderRole);

        public abstract Builder dateSent(Date dateSent);

        public abstract Builder questionUid(String questionUid);

        public abstract Builder senderName(String senderName);

        public abstract Builder answerFor(String answerFor);

        public abstract Answer build();
    }

    public static class Comparator implements java.util.Comparator<AnswerEvent> {
        @Override
        public int compare(AnswerEvent o1, AnswerEvent o2) {
            Date date1 = o1.answer().dateSent();
            Date date2 = o2.answer().dateSent();

            return Objects.requireNonNull(date2).compareTo(date1);
        }
    }
}
