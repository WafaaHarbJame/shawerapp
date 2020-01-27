package com.shawerapp.android.autovalue;

import android.os.Parcelable;

import com.google.auto.value.AutoValue;

import javax.annotation.Nullable;

@AutoValue
public abstract class CoinPackageEvent implements Parcelable {

    public abstract int type();

    public abstract CoinPackage coinPackage();

    @Nullable
    public abstract String amount();

    public static CoinPackageEvent create(int type, CoinPackage coinPackage, String amount) {
        return builder()
                .type(type)
                .coinPackage(coinPackage)
                .amount(amount)
                .build();
    }

    public static Builder builder() {
        return new AutoValue_CoinPackageEvent.Builder();
    }

    @AutoValue.Builder
    public abstract static class Builder {
        public abstract Builder type(int type);

        public abstract Builder coinPackage(CoinPackage coinPackage);

        public abstract Builder amount(String amount);

        public abstract CoinPackageEvent build();
    }
}
