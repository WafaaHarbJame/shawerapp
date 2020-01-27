package com.shawerapp.android.backend.retrofit;


import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface apiClass {

    @POST("SRMsgHandler")
    Call<Response<ResponseBody>> getInvoiceStatus(
            @Header("Accept") String accept,
            @Query("MerchantID") String MerchantID,
            @Query("MessageID") String MessageID,
            @Query("OriginalTransactionID") String OriginalTransactionID,
            @Query("Version") String Version,
            @Query("SecureHash") String SecureHash
    );

}

