package com.shawerapp.android.Payment;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.oppwa.mobile.connect.checkout.dialog.CheckoutActivity;
import com.oppwa.mobile.connect.checkout.meta.CheckoutSettings;
import com.oppwa.mobile.connect.checkout.meta.CheckoutSkipCVVMode;
import com.oppwa.mobile.connect.exception.PaymentError;
import com.oppwa.mobile.connect.provider.Connect;
import com.oppwa.mobile.connect.provider.Transaction;
import com.oppwa.mobile.connect.provider.TransactionType;
import com.shawerapp.android.MainActivity;
import com.shawerapp.android.R;
import com.shawerapp.android.application.ApplicationModel;
import com.shawerapp.android.application.MyPreferenceManager;
import com.shawerapp.android.autovalue.CommercialUser;
import com.shawerapp.android.autovalue.IndividualUser;
import com.shawerapp.android.autovalue.Invoice;
import com.shawerapp.android.autovalue.Invoice_;
import com.shawerapp.android.autovalue.LawyerUser;
import com.shawerapp.android.screens.answerlist.AnswerListFragment;
import com.shawerapp.android.screens.composer.ComposerViewModel;
import com.shawerapp.android.screens.invoice.InvoiceFragment;
import com.shawerapp.android.screens.invoice.InvoiceKey;
import com.shawerapp.android.screens.payment.PaymentContract;
import com.shawerapp.android.screens.payment.PaymentViewModel;
import com.shawerapp.android.utils.LoginUtil;
import com.trello.rxlifecycle2.android.FragmentEvent;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static com.shawerapp.android.screens.composer.ComposerKey.QUESTION;
import static com.shawerapp.android.screens.payment.PaymentFragment.removeWords;

public class PaymentActivity extends AppCompatActivity implements CheckoutIdRequestListener, PaymentStatusRequestListener {

    String consultationId, lang, token, cost;
    int counter = 0;
    SharedPreferences.Editor editor;
    SharedPreferences sharedPreferences;
    protected String resourcePath = null;
    String payment_method;
    String checkoutId;
    private Button mButton;
    String transActionID;
    private FirebaseFirestore db;
    long questionServiceFee;
    LoginUtil mLoginUtil;
    PaymentViewModel mViewModel;
    LawyerUser mSelectedLawyer;
    ComposerViewModel mComposerViewModel;
    CollectionReference dbInvoices;
    String transActionDate;
    PaymentContract.ViewModel viewModel;








    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);
        sharedPreferences = getSharedPreferences(AppConstants.KEY_SIGN_UP, MODE_PRIVATE);
        counter = sharedPreferences.getInt("counter", 0);
        db = FirebaseFirestore.getInstance();
        requestCheckoutId(getString(R.string.checkout_ui_callback_scheme));
        transActionID = "" + System.currentTimeMillis();
        Log.e("transActionID", transActionID +"");
         dbInvoices = db.collection("invoices");









    }

    protected void requestCheckoutId(String callbackScheme) {

        //CheckoutIdRequestAsyncTask
        counter = counter + 1;
        editor = sharedPreferences.edit();
        editor.putInt("counter", counter);
        editor.commit();
        editor.apply();
        ++counter;


        Log.e("checkout", "chechout");
        new CheckoutIdRequestAsyncTask("100", "ar", token, "5", this);

    }

    @Override
    public void onCheckoutIdReceived(String checkoutId, String url) {
//        super.onCheckoutIdReceivedved(checkoutId = checkoutId)
        this.checkoutId = checkoutId;
        Log.e("checkoutId", checkoutId);
        Log.e("callBack", url);
        hideProgressDialog();
        openCheckoutUI(checkoutId, AppConstants.Callbackurl);
    }

    @Override
    public void onErrorOccurred() {
        hideProgressDialog();
        showAlertDialog(R.string.error_message);
    }



    @Override
    public void onPaymentStatusReceived(String paymentStatus) {
        hideProgressDialog();

        if ("true".equals(paymentStatus)) {
            showAlertDialog(R.string.message_successful_payment);
            return;
        }

        showAlertDialog(R.string.message_unsuccessful_payment);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        long questionServiceFee;
        if (mViewModel.mRequestType == QUESTION) {
            if (mLoginUtil.getUserRole().equals(IndividualUser.ROLE_VALUE)) {
                questionServiceFee = mViewModel.mSelectedLawyer.individualFees().get(mViewModel.mSelectedSubSubject.uid());
            } else if (mLoginUtil.getUserRole().equals(CommercialUser.ROLE_VALUE)) {
                questionServiceFee = mViewModel.mSelectedLawyer.commercialFees().get(mViewModel.mSelectedSubSubject.uid());
            } else {
                questionServiceFee = 0L;
            }
        } else {
            questionServiceFee = 20L;
        }

        String type = "";
        if (mViewModel.mRequestType == QUESTION) {
            type = "Esteshara Fee";
        } else {
            type = "Coordinate fees with lawyer office";
        }

        String amount = "" + (questionServiceFee * 100);
        addTransactionToShared();
        createQuestionPractise();
        /* Override onActivityResult to get notified when the checkout process is done. */
        if (requestCode == CheckoutActivity.REQUEST_CODE_CHECKOUT)
            if (resultCode == CheckoutActivity.RESULT_OK) {

                Log.e("resourcePath", "resourcePath");
                /* Transaction completed. */
                Transaction transaction = data.getParcelableExtra(CheckoutActivity.CHECKOUT_RESULT_TRANSACTION);
                resourcePath = data.getStringExtra(CheckoutActivity.CHECKOUT_RESULT_RESOURCE_PATH);
                payment_method = transaction.getPaymentParams().getPaymentBrand();
                Log.e("payment_method", "payment_method" + payment_method);
                assert resourcePath != null;
                 Log.e("resourcePath", resourcePath);
                Log.e("transaction", transaction.getTransactionType().toString());
                /* Check the transaction type. */
                assert (transaction != null);
                if (transaction.getTransactionType() == TransactionType.ASYNC) {
                    /* Check the status of synchronous transaction. */
                    requestPaymentStatus(resourcePath, lang, token, this);
                    hideProgressDialog();


                }

                /* else {
                    /* Asynchronous transaction is processed in the onNewIntent(). */
                //onNewIntent(data);
                //requestPaymentStatus(resourcePath,lang,token, this);

                    /*hideProgressDialog();
                }*/
            } else if (resultCode == CheckoutActivity.RESULT_CANCELED) {
                hideProgressDialog();
            } else if (resultCode == CheckoutActivity.RESULT_ERROR) {
                hideProgressDialog();
                PaymentError error = data.getParcelableExtra(CheckoutActivity.CHECKOUT_RESULT_ERROR);
                assert error != null;
                Log.e("resourcePath", error.getErrorMessage());
                showAlertDialog(R.string.error_message);
            }
        }



    private ProgressDialog progressDialog = null;

    protected void showProgressDialog() {

        if (progressDialog != null && progressDialog.isShowing()) {
            return;
        }

        if (progressDialog == null) {
            progressDialog = new ProgressDialog(this);
            progressDialog.setCancelable(false);
        }

        progressDialog.setMessage(getString(R.string.progress_message_payment_status));
        progressDialog.show();
    }

    protected void hideProgressDialog() {
        if (progressDialog == null) {
            return;
        }

        progressDialog.dismiss();
    }

    protected void showAlertDialog(String message) {
        new AlertDialog.Builder(this).setMessage(message).setPositiveButton(R.string.button_ok, null).setCancelable(false).show();
    }


    private void showAlertDialog(int messageId) {
        showAlertDialog(getString(messageId));
    }

    private PaymentStatusRequestListener listener = null;


    public void requestPaymentStatus(String resourcePath, final String lang, final String token, PaymentStatusRequestListener listener) {
        showProgressDialog();
        this.listener = listener;
        StringRequest stringRequest = new StringRequest(Request.Method.POST, AppConstants.requestPaymentStatus + checkoutId, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {


                try {
                    Log.e("RequestPaymentStatus", "RequestPaymentStatus " + response);

                    hideProgressDialog();
                    JSONObject r = new JSONObject(response);
                    Log.e("RequestPaymentStatus", "RequestPaymentStatus " + r);

                    JSONObject result = r.getJSONObject("result");
                    String description = result.getString("description");
                    String code = result.getString("code");
                    if (code.matches("000.000.000") || code.matches("000.000.100") || code.matches("000.100.110") || code.matches("000.100.111") || code.matches("000.100.112") || code.matches("000.400.000") || code.matches("000.400.020") || code.matches("000.400.010") || code.matches("000.400.100") || code.matches("000.400.020")) {

                        Toast.makeText(PaymentActivity.this, "" + getString(R.string.SuccessPayed), Toast.LENGTH_SHORT).show();
                        Fragment fragment = new InvoiceFragment();
                        getSupportFragmentManager().beginTransaction().replace(R.id.contentContainer, fragment, "SuccessPayFragment").commit();
                        ;
                    } else {


                        if (counter == 3) {
                            Toast.makeText(PaymentActivity.this, "" + getString(R.string.failedpay), Toast.LENGTH_SHORT).show();
                            Fragment fragment = new AnswerListFragment();
                            getSupportFragmentManager().beginTransaction().replace(R.id.answersContainer, fragment, "SuccessPayFragment").commit();
                            sharedPreferences.edit().remove("counter").commit();


                        } else {
                             Toast.makeText(PaymentActivity.this, "" + description, Toast.LENGTH_SHORT).show();
                            Toast.makeText(PaymentActivity.this, "" + getString(R.string.failedpayagain), Toast.LENGTH_SHORT).show();


                        }


                    }
                    Log.e("description", "description " + description);

                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.d("KHH", "JSONException " + e);

                    hideProgressDialog();

                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                hideProgressDialog();
                Log.d("KHH", "VolleyError " + error);


            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map = new HashMap();
                //map.put("amount",amount+"");


                return map;

            }

            @Override
            public Map<String, String> getHeaders() {
                HashMap<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiIsImp0aSI6ImM2OTMxZDNkM2U3ZTViYzEwZDg0NDhmNDBlODQ3NTBmYWRmNGI5MTNmODA5NGQ2NmM1ZjhlMDAwYjdmYzgxM2MwODNmZDdjNGRhMjk4MzczIn0.eyJhdWQiOiIxIiwianRpIjoiYzY5MzFkM2QzZTdlNWJjMTBkODQ0OGY0MGU4NDc1MGZhZGY0YjkxM2Y4MDk0ZDY2YzVmOGUwMDBiN2ZjODEzYzA4M2ZkN2M0ZGEyOTgzNzMiLCJpYXQiOjE1Njc5OTE0NDcsIm5iZiI6MTU2Nzk5MTQ0NywiZXhwIjoxNTk5NjEzODQ3LCJzdWIiOiIyIiwic2NvcGVzIjpbXX0.Tfw3ex_BnGdr26Vr4U9X2jcsBa2kKddf8xf-Go0kALnQn1PJpqJuXoxou9WjRtODtRUDvwPoW3U4vn0EpTzZVU6udBxi9J7MaiDqKL3QTlt1OHLoby9T8pSoHMl0PMTlfg28mSthoAf8O0jijaO4Nb1_btKzcTS5-dro2g_jATTmw_RuVQGsG1nXgHvUm6H3hlQyA8WNA17OraOUzOk8oadTXDcT5X7aO5avk8skxLH_rA9-4FfgyzVY_HGSxFmbva3LJ0KCVkXWt9IbkdssBd2L3f0kkc8UkuC3tL5SioG_IjaO1lkmdL6bR_LdD9gELe1V9u1aJR6wab3LjrEh1zcXVaiJfEUVwJuMNs3PQ6-BaUVbcKQTo98MtrgmnoUGNCkBcFqINPIBxiVo3EfK_pajuHpQx6X83Gp4XakXqG6lu4hyPRWyEUvJXeJPM6t3ElAs6jffbnOz9p3sD53NtCpbKeC4v7LVcwxfGTYY4cjei0ShJyhsxPT05Lx6JZ564Rm4QTRsMaSwr262y1X6pe0vMGBk4TcA5FZ5IbbzD3-pmxE9H-INiLf2kpMX93WH6cd1vei16mvjcO8IGyR3bI2_omKPHmRD3qxAYxavMlpStVR7UAA35zBuS5eVqIJne4xP6f0Ekl9q9doIhBhz9LgmuCJ1_jyoXOgZsYSgDbU");

                // headers.put("Authorization", "Bearer" + " " + token);
                headers.put("lang", lang);

                return headers;
            }

        };

        ApplicationModel.getInstance().addToRequestQueue(stringRequest);


    }

    private void openCheckoutUI(String checkoutId, String url) {

        Log.e("checkout***", "chechout");
        CheckoutSettings checkoutSettings = createCheckoutSettings(checkoutId, url);

        /* Set componentName if you want to receive callbacks from the checkout */
        ComponentName componentName = new ComponentName(getPackageName(), CheckoutBroadcastReceiver.class.getName());

        /* Set up the Intent and start the checkout activity. */
        Intent intent = checkoutSettings.createCheckoutActivityIntent(this, componentName);

        Log.e("ddddd", CheckoutActivity.REQUEST_CODE_CHECKOUT + "");

        startActivityForResult(intent, CheckoutActivity.REQUEST_CODE_CHECKOUT);
    }

    protected CheckoutSettings createCheckoutSettings(String checkoutId, String callbackScheme) {
        return new CheckoutSettings(checkoutId, Constants.Config.PAYMENT_BRANDS, Connect.ProviderMode.TEST).setSkipCVVMode(CheckoutSkipCVVMode.FOR_STORED_CARDS)
//                .setShopperResultUrl(callbackScheme);
                .setShopperResultUrl("checkoutui://result");
        //                .setGooglePayPaymentDataRequest(getGooglePayRequest())
    }




    void addTransactionToShared() {
        MyPreferenceManager SP = MyPreferenceManager.getInstance(Objects.requireNonNull(PaymentActivity.this).getApplicationContext());
//                if(SP.contains("TRANSACTIONS")){
        String TRANSACTIONS = SP.getString("TRANSACTIONS", "");
        Date date = Calendar.getInstance().getTime();

        @SuppressLint("SimpleDateFormat")
        DateFormat dateFormat = new SimpleDateFormat("yyyy-mm-dd hh:mm:ss");
        String strDate = dateFormat.format(date);

        if (TRANSACTIONS.contains(",") && TRANSACTIONS.length() > 0) {
            TRANSACTIONS = "" + transActionID + ",";
            SP.putString("TRANSACTIONS", TRANSACTIONS);

            transActionDate = strDate;

            SP.putString("TRANSACTIONSDATES", strDate + ",");
        } else if (!TRANSACTIONS.contains(",") && TRANSACTIONS.length() > 0) {
            TRANSACTIONS = "," + transActionID;
            SP.putString("TRANSACTIONS", TRANSACTIONS);
            transActionDate = strDate;

            SP.putString("TRANSACTIONSDATES", "," + strDate);
        } else {
            TRANSACTIONS = transActionID;
            SP.putString("TRANSACTIONS", TRANSACTIONS);
            transActionDate = strDate;

            SP.putString("TRANSACTIONSDATES", strDate);
        }
    }


    void createQuestionPractise() {
        checkTransActions();
       // mViewModel.mComposerViewModel.setPaidStatus(true, mContainerView, this, viewModel, transActionID);
        mViewModel.mComposerViewModel.onSubmitComposition();
        db = FirebaseFirestore.getInstance();
        String type = "";
        if (mViewModel.mRequestType == QUESTION) {
            type = "Esteshara Fee";
        } else {
            type = "Coordinate fees with lawyer office";
        }
//        addInvoice(type);

    }
    private void checkTransActions() {
        MyPreferenceManager SP = MyPreferenceManager.getInstance(Objects.requireNonNull(PaymentActivity.this).getApplicationContext());
        if (SP.contains("TRANSACTIONS")) {
            String TRANSACTIONS = SP.getString("TRANSACTIONS", "");
            String TRANSACTIONSDATES = SP.getString("TRANSACTIONSDATES", "");
            @SuppressLint("SimpleDateFormat") DateFormat dateFormat = new SimpleDateFormat("yyyy-mm-dd hh:mm:ss");

            Date currentDate = Calendar.getInstance().getTime();
            Calendar today = Calendar.getInstance ();
            today.add(Calendar.DAY_OF_YEAR, 0);
            today.set(Calendar.HOUR_OF_DAY, 0);
            today.set(Calendar.MINUTE, -6 );
            today.set(Calendar.SECOND, 0);


            String[] TRANSACTIONSArry = TRANSACTIONS.split(",");
            String[] TRANSACTIONSDATESArry = TRANSACTIONSDATES.split(",");
            for (int i = 0; i < TRANSACTIONSArry.length - 1; i++) {
                if (TRANSACTIONSArry[i].equals(transActionID)) {

                    String transActions = SP.getString("TRANSACTIONS", "");
                    String transActionsDates = SP.getString("TRANSACTIONSDATES", "");

                    String ss = removeWords(transActions, transActionID);
                    String sss = removeWords(transActionsDates, transActionDate);

                    SP.putString("TRANSACTIONS", ss);
                    SP.putString("TRANSACTIONSDATES", sss);
                }
            }


        }

    }
}
