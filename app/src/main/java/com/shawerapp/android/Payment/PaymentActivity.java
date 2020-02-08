package com.shawerapp.android.Payment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.iid.FirebaseInstanceId;
import com.oppwa.mobile.connect.checkout.dialog.CheckoutActivity;
import com.oppwa.mobile.connect.checkout.meta.CheckoutSettings;
import com.oppwa.mobile.connect.checkout.meta.CheckoutSkipCVVMode;
import com.oppwa.mobile.connect.exception.PaymentError;
import com.oppwa.mobile.connect.provider.Connect;
import com.oppwa.mobile.connect.provider.Transaction;
import com.oppwa.mobile.connect.provider.TransactionType;
import com.shawerapp.android.GlobalData;
import com.shawerapp.android.R;
import com.shawerapp.android.SharedPManger;
import com.shawerapp.android.application.ApplicationModel;
import com.shawerapp.android.application.MyPreferenceManager;
import com.shawerapp.android.autovalue.CommercialUser;
import com.shawerapp.android.autovalue.Field;
import com.shawerapp.android.autovalue.IndividualUser;
import com.shawerapp.android.autovalue.Invoice;
import com.shawerapp.android.autovalue.InvoiceNew;
import com.shawerapp.android.autovalue.Invoice_;
import com.shawerapp.android.autovalue.LawyerUser;
import com.shawerapp.android.autovalue.SubSubject;
import com.shawerapp.android.base.ActivityLifecycle;
import com.shawerapp.android.base.FragmentLifecycle;
import com.shawerapp.android.screens.answerlist.AnswerListFragment;
import com.shawerapp.android.screens.composer.ComposerViewModel;
import com.shawerapp.android.screens.container.ContainerActivity;
import com.shawerapp.android.screens.invoice.InvoiceFragment;
import com.shawerapp.android.screens.splash.SplashModule;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import javax.inject.Inject;

import butterknife.ButterKnife;
import io.reactivex.functions.Action;

import static com.shawerapp.android.screens.composer.ComposerFragment.ARG_REQUEST_TYPE;
import static com.shawerapp.android.screens.composer.ComposerFragment.ARG_SELECTED_FIELD;
import static com.shawerapp.android.screens.composer.ComposerFragment.ARG_SELECTED_LAWYER;
import static com.shawerapp.android.screens.composer.ComposerFragment.ARG_SELECTED_SUBSUBJECT;
import static com.shawerapp.android.screens.composer.ComposerFragment.serviceFee;
import static com.shawerapp.android.screens.composer.ComposerKey.QUESTION;
import static com.shawerapp.android.screens.payment.PaymentFragment.removeWords;

public class PaymentActivity extends BaseActivity implements CheckoutIdRequestListener, PaymentStatusRequestListener {

    public int mRequestType;
    public SubSubject mSelectedSubSubject;
    protected String resourcePath = null;
    String lang, token, cost;
    int counter = 0;
    SharedPreferences.Editor editor;
    SharedPreferences sharedPreferences;
    private static final String STATE_RESOURCE_PATH = "STATE_RESOURCE_PATH";

    String payment_method;
    String checkoutId;
    String transActionID;
    long questionServiceFee;
    LawyerUser mSelectedLawyer;
    CollectionReference dbInvoices;
    CollectionReference dbQuestion;
    String transActionDate;
    String userUId, lawerUId;
    String amount;
    String type;
    SharedPManger sharedPManger;
    String assignedLawyerName,assignedLawyerUsername,assignedLawyerUid;
    long serviceFee;


    // @Inject
    PaymentActivityContract.ViewModel viewModel;

    // @Inject
    PaymentActivityModule mViewModel;
    Field mSelectedField;
    DocumentReference invoiceRef;
    String date_string;
    Date date;
    private Button mButton;
    private FirebaseFirestore db;
    private ProgressDialog progressDialog = null;
    private PaymentStatusRequestListener listener = null;
    DocumentReference questionRef;
    public  String ar_fieldName;
    public  String ar_subSubjectName;
    public  String subSubjectName;
    public  String fieldUid,fieldName;
    public  String subSubjectUid;
    String askerRole,askerUsername,askerUid;
    Date dateAdded;
    String status;






    @Override
    protected void onCreate(Bundle savedInstanceState) {

        setContentView(R.layout.activity_payment);

        super.onCreate(savedInstanceState);

        System.out.println("Token => " + FirebaseInstanceId.getInstance().getToken());
        sharedPManger = new SharedPManger(PaymentActivity.this);

        sharedPreferences = getSharedPreferences(AppConstants.KEY_SIGN_UP, MODE_PRIVATE);
        counter = sharedPreferences.getInt("counter", 0);
        Bundle bundle = getIntent().getExtras();

        type = getString(R.string.request_a_esteshara);

        db = FirebaseFirestore.getInstance();
         questionRef = db.collection("questions")
                .document();

        if (bundle != null) {
            amount = bundle.getString("amount");
        }

        userUId = sharedPManger.getDataString("uid");
        if(sharedPManger.getDataString("uid")==null){
            userUId=GlobalData.uid;
        }
        lawerUId = GlobalData.lawyerId;
        Log.e("Laweryidshared", "Laweryidshared" + lawerUId);


        // lawerUId = "3ahmDGvEh9UnczbarL6DqNczGBn2";

        requestCheckoutId(getString(R.string.checkout_ui_callback_scheme));
        transActionID = "" + System.currentTimeMillis();
        Log.e("transActionID", transActionID + "");


        ar_fieldName=GlobalData.ar_fieldName;
        ar_subSubjectName= GlobalData.ar_subSubjectName;
        subSubjectName=GlobalData.subSubjectName;
        GlobalData.subSubjectName=subSubjectName;
        fieldUid=GlobalData.fieldUid;
        GlobalData.fieldUid=fieldUid;
        subSubjectUid= GlobalData.subSubjectUid;
        fieldName= GlobalData.fieldName;
        askerRole=GlobalData.askerRole;
        askerUsername=GlobalData.askerUsername;
        askerUid=GlobalData.askerUid;
        assignedLawyerName=GlobalData.assignedLawyerName;
        assignedLawyerUsername=GlobalData.assignedLawyerUsername;
        assignedLawyerUid=GlobalData.assignedLawyerUid;
        assignedLawyerName=GlobalData.assignedLawyerUsername;
        dateAdded=GlobalData.dateAdded;
        status= GlobalData.status;
        Toast.makeText(this, ""+askerRole, Toast.LENGTH_SHORT).show();




        if (savedInstanceState != null) {
            resourcePath = savedInstanceState.getString(STATE_RESOURCE_PATH);
        }
        //  dbInvoices = db.collection("invoices");
    }
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        setIntent(intent);

        /* Check if the intent contains the callback scheme. */
        if (resourcePath != null && hasCallbackScheme(intent)) {
           // requestPaymentStatus(resourcePath, "ar", this);
        }
    }



    protected boolean hasCallbackScheme(Intent intent) {
        String scheme = intent.getScheme();

        return getString(R.string.checkout_ui_callback_scheme).equals(scheme) ||
                getString(R.string.payment_button_callback_scheme).equals(scheme) ||
                getString(R.string.custom_ui_callback_scheme).equals(scheme);
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
        new CheckoutIdRequestAsyncTask(amount, "ar", transActionID, this);

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
        // Toast.makeText(this, "result", Toast.LENGTH_SHORT).show();


      /*  long questionServiceFee;
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
        createQuestionPractise();*/
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
                    requestPaymentStatus(resourcePath, lang, this);
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
                finish();
//                openMain();
            } else if (resultCode == CheckoutActivity.RESULT_ERROR) {
                hideProgressDialog();
                PaymentError error = data.getParcelableExtra(CheckoutActivity.CHECKOUT_RESULT_ERROR);
                assert error != null;
                Log.e("resourcePath", error.getErrorMessage());
                showAlertDialog(R.string.error_message);
            }
    }

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
        new AlertDialog.Builder(this).setMessage(message).setPositiveButton(R.string.button_ok, (dialog, which) -> {
//            openMain();
        })
                .setCancelable(false).show();
    }

    protected void showAlertDialog(int messageId) {
        showAlertDialog(getString(messageId));
    }

    public void requestPaymentStatus(String resourcePath,
                                     final String lang, PaymentStatusRequestListener listener) {
        showProgressDialog();
        this.listener = listener;
        StringRequest stringRequest = new StringRequest(Request.Method.GET, AppConstants.requestPaymentStatus + checkoutId, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {


                try {
                    Log.e("RequestPaymentStatus", "RequestPaymentStatus " + response);

                    hideProgressDialog();
                    JSONObject r = new JSONObject(response);
                    Log.e("RequestPaymentStatus", "RequestPaymentStatus " + r);

                    JSONObject result = r.getJSONObject("result");
                    String description = result.getString("description");
                    Toast.makeText(PaymentActivity.this, "" + description, Toast.LENGTH_SHORT).show();
                    String code = result.getString("code");
                    if (code.matches("000.000.000") || code.matches("000.000.100") ||

                            code.matches("000.100.110") || code.matches("000.100.111") ||

                            code.matches("000.100.112") || code.matches("000.400.000") ||

                            code.matches("000.400.020") || code.matches("000.400.010") || code.matches("000.400.100") || code.matches("000.400.020")) {


                        showDialog(PaymentActivity.this, getString(R.string.SuccessPayed),true);
                        // Toast.makeText(PaymentActivity.this, "" + getString(R.string.SuccessPayed), Toast.LENGTH_SHORT).show();



                        /*
                        (String uid,String orderVatPrice, String orderVat,
                      String orderTypePrice, String orderType, String orderSubTotal,
                      String orderRequestNumber,Date orderDate, String collection, String userUid,
                      String lawyerUid, String paid)
                         */


                    } else {

                        Toast.makeText(PaymentActivity.this, "" + getString(R.string.failedpay), Toast.LENGTH_SHORT).show();
//addInvoice();
                        showFailedDialog(PaymentActivity.this, getString(R.string.failedpay));




                       /* if (counter == 3) {

                            Toast.makeText(PaymentActivity.this, "" + getString(R.string.failedpay), Toast.LENGTH_SHORT).show();
                            Intent intent=new Intent(PaymentActivity.this,ContainerActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_NEW_TASK
                       |Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                            finish();
                              sharedPreferences.edit().remove("counter").commit();


                        } else {
                             Toast.makeText(PaymentActivity.this, "" + description, Toast.LENGTH_SHORT).show();
                            Toast.makeText(PaymentActivity.this, "" + getString(R.string.failedpayagain), Toast.LENGTH_SHORT).show();
                            Intent intent=new Intent(PaymentActivity.this,ContainerActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_NEW_TASK
                                    |Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                            finish();

                        }*/


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
                map.put("id", checkoutId + "");
                map.put("type", "paymentStatus");


                return map;

            }

            @Override
            public Map<String, String> getHeaders() {
                HashMap<String, String> headers = new HashMap<>();
                //headers.put("lang", lang);


                return headers;
            }

        };

        ApplicationModel.getInstance().addToRequestQueue(stringRequest);


    }

    /*private void openCheckoutUI(String checkoutId, String url) {

        Log.e("checkout***", "chechout");
        CheckoutSettings checkoutSettings = createCheckoutSettings(checkoutId, url);

        /* Set componentName if you want to receive callbacks from the checkout */
//        ComponentName componentName = new ComponentName(getPackageName(),
                //CheckoutBroadcastReceiver.class.getName());

        /* Set up the Intent and start the checkout activity. */
       // Intent intent = checkoutSettings.createCheckoutActivityIntent(this, componentName);

        //Log.e("ddddd", CheckoutActivity.REQUEST_CODE_CHECKOUT + "");

      //  startActivityForResult(intent, CheckoutActivity.REQUEST_CODE_CHECKOUT);
    //}*/

    private void openCheckoutUI(String checkoutId) {
        CheckoutSettings checkoutSettings = createCheckoutSettings(checkoutId,
                getString(R.string.checkout_ui_callback_scheme));

        /* Set componentName if you want to receive callbacks from the checkout */
        ComponentName componentName = new ComponentName(
                getPackageName(), CheckoutBroadcastReceiver.class.getName());

        /* Set up the Intent and start the checkout activity. */
        Intent intent = checkoutSettings.createCheckoutActivityIntent(this, componentName);

        startActivityForResult(intent, CheckoutActivity.REQUEST_CODE_CHECKOUT);
    }

    protected CheckoutSettings createCheckoutSettings(String checkoutId, String callbackScheme) {


        return new CheckoutSettings(checkoutId, Constants.Config.PAYMENT_BRANDS,
                Connect.ProviderMode.TEST).setSkipCVVMode(CheckoutSkipCVVMode.FOR_STORED_CARDS)
//                .setShopperResultUrl(callbackScheme);
                .setShopperResultUrl("checkoutui://result");
        //                .setGooglePayPaymentDataRequest(getGooglePayRequest())
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(PaymentActivity.this, ContainerActivity.class);;
        startActivity(intent);
        finish();
    }

    public void showDialog(Activity activity, String msg,boolean paied) {


        final Dialog dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.paymentdialagg);

        TextView text = (TextView) dialog.findViewById(R.id.text_dialog);
        ImageView imageView=dialog.findViewById(R.id.imageviewsucc);
        imageView.setImageDrawable(getDrawable(R.drawable.sucess));
        text.setText(msg);

        Button dialogButton = (Button) dialog.findViewById(R.id.btn_dialog);
        dialogButton.setText(getString(R.string.thankyou));


        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        dialog.getWindow().setAttributes(lp);

        dialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();

                date = new Date();
                date_string = DateFormat.getDateInstance(DateFormat.SHORT).format(date);
                dbInvoices = db.collection("invoices");

                invoiceRef = db.collection("invoices").document();
                dbQuestion=db.collection("questions");

                questionRef= db.collection("questions").document();

                addInvoice(true);
                addQuestion(true);

                Bundle bundle = new Bundle();
                bundle.putString(AppConstants.amount, amount);
                bundle.putString(AppConstants.orderRequestTxt, transActionID);
                bundle.putString(AppConstants.mOrderVATTxt, "0.0%");
                bundle.putString(AppConstants.newdate, date_string);
                        /*Fragment fragment = new InvoiceFragment();
                        fragment.setArguments(bundle);
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.billContainer, fragment, "SuccessPayFragment").commit();*/
                Intent intent = new Intent(PaymentActivity.this, InvoiceActivity.class);
                bundle.putString(AppConstants.amount, amount);
                bundle.putString(AppConstants.orderRequestTxt, transActionID);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                intent.putExtras(bundle);
                startActivity(intent);
                finish();
            }
        });

        dialog.show();

    }

    public void showFailedDialog(Activity activity, String msg) {
        final Dialog dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.paymentdialagg);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        dialog.getWindow().setAttributes(lp);

        TextView text = (TextView) dialog.findViewById(R.id.text_dialog);
        text.setText(msg);

        ImageView imageView=dialog.findViewById(R.id.imageviewsucc);
        imageView.setImageDrawable(getDrawable(R.drawable.failed));
        text.setText(msg);

        Button dialogButton = (Button) dialog.findViewById(R.id.btn_dialog);
        dialogButton.setText(getString(R.string.gotohome));
        dialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                date = new Date();
                date_string = DateFormat.getDateInstance(DateFormat.SHORT).format(date);
                dbInvoices = db.collection("invoices");
                invoiceRef = db.collection("invoices").document();
                dbQuestion=db.collection("questions");

                questionRef= db.collection("questions").document();



                addInvoice(false);
                addQuestion(false);

                Intent intent = new Intent(PaymentActivity.this, ContainerActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }
        });

        dialog.show();

    }

    public void addInvoice(boolean isPaied) {
        InvoiceNew invoice_ = new InvoiceNew(invoiceRef.getId(),"0", "0.0%",
                amount, type, amount
                , transActionID, date, "invoices", userUId, lawerUId, isPaied ? "true" : "false");

//                Map<String,Object> invoiceMap = new HashMap<>();
//                invoiceMap.put("",invoiceRef.getId());

        dbInvoices.add(invoice_).addOnSuccessListener(documentReference -> {
            Toast.makeText(PaymentActivity.this, "wafaa test", Toast.LENGTH_SHORT).show();
            Log.e(" documentReference", " Added" + documentReference.toString());
        }).addOnFailureListener(e -> {
            Log.e(" e", e.getLocalizedMessage());
        });

    }


    public void addQuestion(boolean isPaied) {
        Question question = new Question(questionRef.getId(),ar_subSubjectName, subSubjectName,
                subSubjectUid,fieldUid,ar_fieldName,fieldName,askerUid,askerRole ,
                askerUsername,assignedLawyerUid,assignedLawyerName,assignedLawyerUsername,
                amount,date,"",isPaied ? "true" : "false",isPaied ? "active" : "false",
                transActionID, "","questions",date);

//                Map<String,Object> invoiceMap = new HashMap<>();
//                invoiceMap.put("",invoiceRef.getId());

        dbQuestion.add(question).addOnSuccessListener(documentReference -> {
            Toast.makeText(PaymentActivity.this, "Question added", Toast.LENGTH_SHORT).show();
            Log.e(" documentReference", " Added" + documentReference.toString());
        }).addOnFailureListener(e -> {
            Log.e(" e", e.getLocalizedMessage());
        });

    }
    public  void openMain(){

        Intent intent=new Intent(PaymentActivity.this,ContainerActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_NEW_TASK
                |Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    public void onCheckoutIdReceived(String checkoutId, String url) {
        this.checkoutId = checkoutId;
        Log.e("checkoutId", checkoutId);
        Log.e("callBack", url);
        hideProgressDialog();
        openCheckoutUI(checkoutId);

    }
}
