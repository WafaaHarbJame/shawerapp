package com.shawerapp.android.Payment;

import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.shawerapp.android.application.ApplicationModel;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

class CheckoutIdRequestAsyncTask {

    private CheckoutIdRequestListener listener;
    private String checkoutId;

    CheckoutIdRequestAsyncTask(String amount, String lang,String merchantTransactionId,
                               CheckoutIdRequestListener listener) {
        this.listener = listener;
        requestCheckoutId(amount,lang,merchantTransactionId);
    }


    private void requestCheckoutId(final String amount,final  String lang,
                                   final  String merchantTransactionId) {
        StringRequest stringRequest = new StringRequest(Request.Method.GET,
                AppConstants.requestCheckoutId + amount+"&merchantTransactionId="+merchantTransactionId,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            Log.e("checout == ", response);


                            JSONObject r = new JSONObject(response);
                            Log.e("paymentdialagg", "paymentdialagg " + response);
                            checkoutId = r.getString("id");
                            Log.e("checkoutId", "checkoutId " + response);

                            listener.onCheckoutIdReceived(checkoutId, "");

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Log.d("KHH", "JSONException " + e);
                        }


                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("KHH", "VolleyError " + error);


            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> map = new HashMap();
                map.put("merchantTransactionId",merchantTransactionId+"");
                map.put("amount",amount+"");
                map.put("type", "checkout" + "");




                return map;


            }

            @Override
            public Map<String, String> getHeaders() {
                HashMap<String, String> headers = new HashMap<>();

                return headers;


            }

        };

        ApplicationModel.getInstance().addToRequestQueue(stringRequest);


    }
}