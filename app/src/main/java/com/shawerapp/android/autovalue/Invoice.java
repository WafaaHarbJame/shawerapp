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
public abstract class Invoice implements Parcelable {

    @Nullable
    public abstract String uid();

    @Nullable
    public abstract String orderVatPrice();

    @Nullable
    public abstract String orderVat();

    @Nullable
    public abstract String orderTypePrice();

    @Nullable
    public abstract String orderType();

    @Nullable
    public abstract String orderSubTotal();

    @Nullable
    public abstract String orderRequestNumber();

    @Nullable
    @GsonTypeAdapter(UtcDateTypeAdapter.class)
    public abstract Date orderDate();

    @Nullable
    public abstract String collection();

    @Nullable
    public abstract String UserUid();

    @Nullable
    public abstract String LawyerUid();

    @Nullable
    public abstract String paid();


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Invoice that = (Invoice) o;
        return Objects.equals(uid(), that.uid());
    }

    public static TypeAdapter<Invoice> typeAdapter(Gson gson) {
        return new AutoValue_Invoice.GsonTypeAdapter(gson);
    }

    public static Invoice createFromSnapshot(DocumentSnapshot documentSnapshot) {
        Gson gson = new Gson();
        String jsonString = gson.toJson(documentSnapshot.getData());

        Invoice invoice = null;
        try {
            invoice = AutoValue_Invoice.typeAdapter(gson).fromJson(jsonString);
        } catch (IOException e) {
            Timber.e(CommonUtils.getExceptionString(e));
        }
        return invoice;
    }

    public Map<String, Object> toMap() {
        Gson gson = new Gson();

        Type type = new TypeToken<Map<String, Object>>() {
        }.getType();
        Map<String, Object> mapped = gson.fromJson(AutoValue_Invoice.typeAdapter(new Gson()).toJson(this), type);

        String dateStr = (String) mapped.get("orderDate");
        try {
            if (CommonUtils.isNotEmpty(dateStr)) {
                mapped.put("orderDate", new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").parse(dateStr));
            } else {
                mapped.remove("orderDate");
            }
        } catch (ParseException e) {
            Timber.e(CommonUtils.getExceptionString(e));
        }
        return mapped;
    }

    public static Invoice create(String uid, String orderVatPrice, String orderVat, String orderTypePrice, String orderType,
                                 String orderSubTotal, String orderRequestNumber, Date orderDate, String collection, String UserUid, String LawyerUid,
            String paid) {
        return builder()
                .uid(uid)
                .orderVatPrice(orderVatPrice)
                .orderVat(orderVat)
                .orderTypePrice(orderTypePrice)
                .orderType(orderType)
                .orderSubTotal(orderSubTotal)
                .orderRequestNumber(orderRequestNumber)
                .orderDate(orderDate)
                .collection(collection)
                .UserUid(UserUid)
                .LawyerUid(LawyerUid)
                .paid(paid)
                .build();
    }

    public static Builder builder() {
        return new AutoValue_Invoice.Builder();
    }

    @AutoValue.Builder
    public abstract static class Builder {
        public abstract Builder uid(String uid);

        public abstract Builder orderVatPrice(String orderVatPrice);

        public abstract Builder orderVat(String orderVat);

        public abstract Builder orderTypePrice(String orderTypePrice);

        public abstract Builder orderType(String orderType);

        public abstract Builder orderSubTotal(String orderSubTotal);

        public abstract Builder orderRequestNumber(String orderRequestNumber);

        public abstract Builder orderDate(Date orderDate);

        public abstract Builder collection(String collection);

        public abstract Builder UserUid(String UserUid);

        public abstract Builder LawyerUid(String LawyerUid);

        public abstract Builder paid(String paid);

        public abstract Invoice build();
    }


}
