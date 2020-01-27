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
import java.util.Map;
import java.util.Objects;

import javax.annotation.Nullable;

import timber.log.Timber;

@AutoValue
public abstract class Field implements Parcelable {

    public abstract String uid();

    @Nullable
    public abstract String fieldName();

    @Nullable
    public abstract String ar_fieldName();

    @Nullable
    public abstract String description();

    @Nullable
    public abstract String ar_description();

    public abstract int subSubjectCount();

    @Nullable
    public abstract String addedBy();

    @Nullable
    @GsonTypeAdapter(UtcDateTypeAdapter.class)
    public abstract Date addedOn();

    public static TypeAdapter<Field> typeAdapter(Gson gson) {
        return new AutoValue_Field.GsonTypeAdapter(gson);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Field that = (Field) o;
        return Objects.equals(uid(), that.uid());
    }

    @Override
    public int hashCode() {
        return Objects.hash(uid());
    }

    public static Field createFromSnapshot(DocumentSnapshot documentSnapshot) {
        Gson gson = new Gson();
        String jsonString = gson.toJson(documentSnapshot.getData());

        Field Field = null;
        try {
            Field = AutoValue_Field.typeAdapter(gson).fromJson(jsonString);
        } catch (IOException e) {
            Timber.e(CommonUtils.getExceptionString(e));
        }
        return Field;
    }

    public Map<String, Object> toMap() {
        Gson gson = new Gson();
        Type type = new TypeToken<Map<String, Object>>() {
        }.getType();
        Map<String, Object> mapped = gson.fromJson(AutoValue_Field.typeAdapter(new Gson()).toJson(this), type);

        String addedOn = (String) mapped.get("addedOn");
        try {
            if (CommonUtils.isNotEmpty(addedOn)) {
                mapped.put("addedOn", new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").parse(addedOn));
            } else {
                mapped.remove("addedOn");
            }
        } catch (ParseException e) {
            Timber.e(CommonUtils.getExceptionString(e));
        }
        return mapped;
    }

    public static Field create(String uid, String fieldName, String ar_fieldName, String description, String ar_description, int subSubjectCount, String addedBy, Date addedOn) {
        return builder()
                .uid(uid)
                .fieldName(fieldName)
                .ar_fieldName(ar_fieldName)
                .description(description)
                .ar_description(ar_description)
                .subSubjectCount(subSubjectCount)
                .addedBy(addedBy)
                .addedOn(addedOn)
                .build();
    }

    public static Builder builder() {
        return new AutoValue_Field.Builder();
    }

    @AutoValue.Builder
    public abstract static class Builder {
        public abstract Builder uid(String uid);

        public abstract Builder fieldName(String fieldName);

        public abstract Builder description(String description);

        public abstract Builder subSubjectCount(int subSubjectCount);

        public abstract Builder addedBy(String addedBy);

        public abstract Builder addedOn(Date addedOn);

        public abstract Builder ar_fieldName(String ar_fieldName);

        public abstract Builder ar_description(String ar_description);

        public abstract Field build();
    }
}
