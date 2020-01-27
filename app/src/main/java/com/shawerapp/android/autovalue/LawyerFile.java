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
public abstract class LawyerFile implements Parcelable {

    @Nullable
    public abstract String uid();

    @Nullable
    public abstract String fileName();

    @Nullable
    public abstract String ar_fileName();

    @Nullable
    public abstract String description();

    @Nullable
    public abstract String ar_description();

    @Nullable
    public abstract Long fileSize();

    @Nullable
    public abstract String downloadUrl();

    @Nullable
    public abstract String fileType();

    public static TypeAdapter<LawyerFile> typeAdapter(Gson gson) {
        return new AutoValue_LawyerFile.GsonTypeAdapter(gson);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LawyerFile that = (LawyerFile) o;
        return Objects.equals(uid(), that.uid());
    }

    @Override
    public int hashCode() {
        return Objects.hash(uid());
    }

    public static LawyerFile createFromSnapshot(DocumentSnapshot documentSnapshot) {
        Gson gson = new Gson();
        String jsonString = gson.toJson(documentSnapshot.getData());

        LawyerFile LawyerFile = null;
        try {
            LawyerFile = AutoValue_LawyerFile.typeAdapter(gson).fromJson(jsonString);
        } catch (IOException e) {
            Timber.e(CommonUtils.getExceptionString(e));
        }
        return LawyerFile;
    }

    public Map<String, Object> toMap() {
        Gson gson = new Gson();
        Type type = new TypeToken<Map<String, Object>>() {
        }.getType();
        Map<String, Object> mapped = gson.fromJson(AutoValue_LawyerFile.typeAdapter(new Gson()).toJson(this), type);
        return mapped;
    }

    public static LawyerFile create(String uid, String fileName, String ar_fileName, String description, String ar_description, Long fileSize, String downloadUrl, String fileType) {
        return builder()
                .uid(uid)
                .fileName(fileName)
                .ar_fileName(ar_fileName)
                .description(description)
                .ar_description(ar_description)
                .fileSize(fileSize)
                .downloadUrl(downloadUrl)
                .fileType(fileType)
                .build();
    }

    public static Builder builder() {
        return new AutoValue_LawyerFile.Builder();
    }

    @AutoValue.Builder
    public abstract static class Builder {
        public abstract Builder uid(String uid);

        public abstract Builder fileName(String fileName);

        public abstract Builder fileSize(Long fileSize);

        public abstract Builder downloadUrl(String downloadUrl);

        public abstract Builder description(String description);

        public abstract Builder fileType(String fileType);

        public abstract Builder ar_fileName(String ar_fileName);

        public abstract Builder ar_description(String ar_description);

        public abstract LawyerFile build();
    }
}
