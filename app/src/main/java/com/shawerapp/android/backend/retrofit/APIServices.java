package com.shawerapp.android.backend.retrofit;

import com.shawerapp.android.autovalue.AvailabilityCheckResponse;
import com.shawerapp.android.autovalue.annotation.GetEmailResponse;

import io.reactivex.Completable;
import io.reactivex.Maybe;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface APIServices {

    @GET("authCheckUsername")
    Maybe<AvailabilityCheckResponse> checkUsernameAvailability(@Query("username") String username);

    @GET("authCheckEmail")
    Maybe<AvailabilityCheckResponse> checkEmailAvailability(@Query("email") String email);

    @GET("authGetEmail")
    Maybe<GetEmailResponse> getUserEmail(@Query("username") String username);


}
