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

import timber.log.Timber;

@AutoValue
public abstract class Memo implements Parcelable {

    public abstract String uid();

    @GsonTypeAdapter(UtcDateTypeAdapter.class)
    public abstract Date datePosted();

    public abstract String message();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Memo that = (Memo) o;
        return Objects.equals(uid(), that.uid());
    }

    @Override
    public int hashCode() {
        return Objects.hash(uid());
    }

    public static Memo create(String uid, Date datePosted, String message) {
        return builder()
                .uid(uid)
                .datePosted(datePosted)
                .message(message)
                .build();
    }

    public static TypeAdapter<Memo> typeAdapter(Gson gson) {
        return new AutoValue_Memo.GsonTypeAdapter(gson);
    }

    public static Memo createFromSnapshot(DocumentSnapshot documentSnapshot) {
        Gson gson = new Gson();
        String jsonString = gson.toJson(documentSnapshot.getData());

        Memo memo = null;
        try {
            memo = AutoValue_Memo.typeAdapter(gson).fromJson(jsonString);
        } catch (IOException e) {
            Timber.e(CommonUtils.getExceptionString(e));
        }
        return memo;
    }

    public Map<String, Object> toMap() {
        Gson gson = new Gson();
        Type type = new TypeToken<Map<String, Object>>() {
        }.getType();
        Map<String, Object> mapped = gson.fromJson(AutoValue_Memo.typeAdapter(new Gson()).toJson(this), type);

        String dateStr = (String) mapped.get("datePosted");
        try {
            if (CommonUtils.isNotEmpty(dateStr)) {
                mapped.put("datePosted", new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").parse(dateStr));
            } else {
                mapped.remove("datePosted");
            }
        } catch (ParseException e) {
            Timber.e(CommonUtils.getExceptionString(e));
        }
        return mapped;
    }


    public static Builder builder() {
        return new AutoValue_Memo.Builder();
    }

    @AutoValue.Builder
    public abstract static class Builder {
        public abstract Builder uid(String uid);

        public abstract Builder datePosted(Date datePosted);

        public abstract Builder message(String message);

        public abstract Memo build();
    }
}
