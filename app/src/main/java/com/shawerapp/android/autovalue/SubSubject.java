package com.shawerapp.android.autovalue;

import android.os.Parcelable;

import com.google.auto.value.AutoValue;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.reflect.TypeToken;
import com.shawerapp.android.utils.CommonUtils;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Map;
import java.util.Objects;

import javax.annotation.Nullable;

import timber.log.Timber;

@AutoValue
public abstract class SubSubject implements Parcelable {

    public abstract String uid();

    @Nullable
    public abstract String subSubjectName();

    @Nullable
    public abstract String ar_subSubjectName();

    @Nullable
    public abstract String description();

    @Nullable
    public abstract String ar_description();

    public abstract int lawyerCount();

    @Nullable
    public abstract Map<String, Boolean> lawyers();

    @Nullable
    public abstract String fieldUid();

    public static TypeAdapter<SubSubject> typeAdapter(Gson gson) {
        return new $AutoValue_SubSubject.GsonTypeAdapter(gson);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SubSubject that = (SubSubject) o;
        return Objects.equals(uid(), that.uid());
    }

    @Override
    public int hashCode() {
        return Objects.hash(uid());
    }

    public static SubSubject createFromSnapshot(DocumentSnapshot documentSnapshot) {
        Gson gson = new Gson();
        String jsonString = gson.toJson(documentSnapshot.getData());

        SubSubject SubSubject = null;
        try {
            SubSubject = AutoValue_SubSubject.typeAdapter(gson).fromJson(jsonString);
        } catch (IOException e) {
            Timber.e(CommonUtils.getExceptionString(e));
        }
        return SubSubject;
    }

    public Map<String, Object> toMap() {
        Gson gson = new Gson();
        Type type = new TypeToken<Map<String, Object>>() {
        }.getType();
        Map<String, Object> mapped = gson.fromJson(AutoValue_SubSubject.typeAdapter(new Gson()).toJson(this), type);
        return mapped;
    }

    public static SubSubject create(String uid, String subSubjectName, String ar_subSubjectName, String description, String ar_description, int lawyerCount, Map<String, Boolean> lawyers, String fieldUid) {
        return builder()
                .uid(uid)
                .subSubjectName(subSubjectName)
                .ar_subSubjectName(ar_subSubjectName)
                .description(description)
                .ar_description(ar_description)
                .lawyerCount(lawyerCount)
                .lawyers(lawyers)
                .fieldUid(fieldUid)
                .build();
    }

    public static Builder builder() {
        return new AutoValue_SubSubject.Builder();
    }

    @AutoValue.Builder
    public abstract static class Builder {
        public abstract Builder uid(String uid);

        public abstract Builder subSubjectName(String subSubjectName);

        public abstract Builder description(String description);

        public abstract Builder lawyerCount(int lawyerCount);

        public abstract Builder lawyers(Map<String, Boolean> lawyers);

        public abstract Builder fieldUid(String fieldUid);

        public abstract Builder ar_subSubjectName(String ar_subSubjectName);

        public abstract Builder ar_description(String ar_description);

        public abstract SubSubject build();
    }
}
