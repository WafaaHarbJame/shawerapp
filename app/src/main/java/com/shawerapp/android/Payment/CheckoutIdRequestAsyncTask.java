package com.shawerapp.android.Payment;

import android.util.Log;

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

    CheckoutIdRequestAsyncTask(String amount, String lang, String token,String merchantTransactionId, CheckoutIdRequestListener listener) {
        this.listener = listener;
        requestCheckoutId(amount,lang,token,merchantTransactionId);
    }


    private void requestCheckoutId(final String amount,final  String lang,final String token,
                                   final  String merchantTransactionId) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                AppConstants.requestCheckoutId + amount+"&merchantTransactionId="+merchantTransactionId,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            Log.e("checout == ", response);


                            JSONObject r = new JSONObject(response);
                            Log.d("payment", "payment " + response);
                            String message = r.getString("message");
                            checkoutId = r.getString("id");
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
                map.put("amount", amount + "");
                map.put("merchantTransactionId",merchantTransactionId+"");


                return map;


            }

            @Override
            public Map<String, String> getHeaders() {
                HashMap<String, String> headers = new HashMap<>();
               //// headers.put("Authorization", "Bearer"+" " +token);
                headers.put("Authorization", "Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiIsImp0aSI6ImM2OTMxZDNkM2U3ZTViYzEwZDg0NDhmNDBlODQ3NTBmYWRmNGI5MTNmODA5NGQ2NmM1ZjhlMDAwYjdmYzgxM2MwODNmZDdjNGRhMjk4MzczIn0.eyJhdWQiOiIxIiwianRpIjoiYzY5MzFkM2QzZTdlNWJjMTBkODQ0OGY0MGU4NDc1MGZhZGY0YjkxM2Y4MDk0ZDY2YzVmOGUwMDBiN2ZjODEzYzA4M2ZkN2M0ZGEyOTgzNzMiLCJpYXQiOjE1Njc5OTE0NDcsIm5iZiI6MTU2Nzk5MTQ0NywiZXhwIjoxNTk5NjEzODQ3LCJzdWIiOiIyIiwic2NvcGVzIjpbXX0.Tfw3ex_BnGdr26Vr4U9X2jcsBa2kKddf8xf-Go0kALnQn1PJpqJuXoxou9WjRtODtRUDvwPoW3U4vn0EpTzZVU6udBxi9J7MaiDqKL3QTlt1OHLoby9T8pSoHMl0PMTlfg28mSthoAf8O0jijaO4Nb1_btKzcTS5-dro2g_jATTmw_RuVQGsG1nXgHvUm6H3hlQyA8WNA17OraOUzOk8oadTXDcT5X7aO5avk8skxLH_rA9-4FfgyzVY_HGSxFmbva3LJ0KCVkXWt9IbkdssBd2L3f0kkc8UkuC3tL5SioG_IjaO1lkmdL6bR_LdD9gELe1V9u1aJR6wab3LjrEh1zcXVaiJfEUVwJuMNs3PQ6-BaUVbcKQTo98MtrgmnoUGNCkBcFqINPIBxiVo3EfK_pajuHpQx6X83Gp4XakXqG6lu4hyPRWyEUvJXeJPM6t3ElAs6jffbnOz9p3sD53NtCpbKeC4v7LVcwxfGTYY4cjei0ShJyhsxPT05Lx6JZ564Rm4QTRsMaSwr262y1X6pe0vMGBk4TcA5FZ5IbbzD3-pmxE9H-INiLf2kpMX93WH6cd1vei16mvjcO8IGyR3bI2_omKPHmRD3qxAYxavMlpStVR7UAA35zBuS5eVqIJne4xP6f0Ekl9q9doIhBhz9LgmuCJ1_jyoXOgZsYSgDbU");

                headers.put("lang",lang);


                return headers;


            }

        };

        ApplicationModel.getInstance().addToRequestQueue(stringRequest);


    }
}