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

import timber.log.Timber;

@AutoValue
public abstract class CoinPackage implements Parcelable {

    public abstract String uid();

    public abstract String productId();

    public abstract String title();

    public abstract String description();

    public abstract Long coinAmount();

    public abstract String status();

    public static TypeAdapter<CoinPackage> typeAdapter(Gson gson) {
        return new AutoValue_CoinPackage.GsonTypeAdapter(gson);
    }

    public static Builder builder() {
        return new AutoValue_CoinPackage.Builder();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CoinPackage that = (CoinPackage) o;
        return Objects.equals(uid(), that.uid());
    }

    @Override
    public int hashCode() {
        return Objects.hash(uid());
    }

    public static CoinPackage createFromSnapshot(DocumentSnapshot documentSnapshot) {
        Gson gson = new Gson();
        String jsonString = gson.toJson(documentSnapshot.getData());

        CoinPackage CoinPackage = null;
        try {
            CoinPackage = AutoValue_CoinPackage.typeAdapter(gson).fromJson(jsonString);
        } catch (IOException e) {
            Timber.e(CommonUtils.getExceptionString(e));
        }
        return CoinPackage;
    }

    public Map<String, Object> toMap() {
        Gson gson = new Gson();
        Type type = new TypeToken<Map<String, Object>>() {
        }.getType();
        Map<String, Object> mapped = gson.fromJson(AutoValue_CoinPackage.typeAdapter(new Gson()).toJson(this), type);
        return mapped;
    }

    public static CoinPackage create(String uid, String productId, String title, String description, Long coinAmount, String status) {
        return builder()
                .uid(uid)
                .productId(productId)
                .title(title)
                .description(description)
                .coinAmount(coinAmount)
                .status(status)
                .build();
    }

    @AutoValue.Builder
    public abstract static class Builder {
        public abstract Builder uid(String uid);

        public abstract Builder productId(String productId);

        public abstract Builder title(String title);

        public abstract Builder description(String description);

        public abstract Builder status(String status);

        public abstract Builder coinAmount(Long coinAmount);

        public abstract CoinPackage build();
    }

    public static class Status {
        public static final String ACTIVE = "active";

        public static final String INACTIVE = "inactive";

    }
}
