package com.shawerapp.android.application;

import android.annotation.SuppressLint;
import android.content.Context;

//import androidx.multidex.BuildConfig;
import androidx.multidex.BuildConfig;
import androidx.multidex.MultiDexApplication;

import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import com.akaita.java.rxjava2debug.RxJava2Debug;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.crashlytics.android.Crashlytics;
import com.google.common.hash.Hashing;
//import com.shawerapp.android.BuildConfig;
import com.shawerapp.android.R;
import com.shawerapp.android.autovalue.CommercialUser;
import com.shawerapp.android.autovalue.IndividualUser;
import com.shawerapp.android.autovalue.Invoice;
import com.shawerapp.android.backend.base.BackendModule;
import com.shawerapp.android.backend.base.RealTimeDataFramework;
import com.shawerapp.android.screens.invoice.InvoiceKey;
import com.shawerapp.android.utils.CommonUtils;
import com.trello.rxlifecycle2.android.FragmentEvent;

import org.apache.http.util.EncodingUtils;

import at.favre.lib.planb.PlanB;
import io.fabric.sdk.android.Fabric;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.SocketException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;

import javax.inject.Inject;

import eu.davidea.flexibleadapter.FlexibleAdapter;
import io.reactivex.exceptions.UndeliverableException;
import io.reactivex.plugins.RxJavaPlugins;
import timber.log.Timber;

import static eu.davidea.flexibleadapter.utils.Log.Level.DEBUG;

/**
 * Created by john.ernest on 12/15/17.
 */
public class ApplicationModel extends MultiDexApplication {

    public static String LOG_TAG = "DEBUG-";

    private static AppComponent appComponent;

    String hash;

    WebView webView;
    TextView contentView;
    private RequestQueue requestQueue;
    private static ApplicationModel anInstance;

    @Inject
    RealTimeDataFramework mRTDataFramework;

    @Override
    public void onCreate() {
        super.onCreate();
        anInstance = this;

        Fabric.with(this, new Crashlytics());
        if (BuildConfig.DEBUG) {
            Timber.plant(new HubChartDebugTree());
        } else {
            Timber.plant(new CrashReportingTree());
        }

        FlexibleAdapter.enableLogs(DEBUG);
        initDaggerComponents();
        initRxJavaErrorHandler();
        initCrashRecoveryLibrary();

        webView = new WebView(getApplicationContext());
        webView.setVisibility(View.GONE);

        contentView = new TextView(getApplicationContext());
        contentView.setVisibility(View.GONE);

        checkTransActions();

    }

    private void checkTransActions() {
        MyPreferenceManager SP = MyPreferenceManager.getInstance(getApplicationContext());
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

            if (TRANSACTIONSArry.length > 1) {
                for (int i = 0; i < TRANSACTIONSArry.length - 1; i++) {
                    try {
                        Date date = dateFormat.parse(TRANSACTIONSDATESArry[i]);
                        if(today.after(date)){
                            Log.e("app", "today is after date");
                            getInvoiceStatus(TRANSACTIONSArry[i], TRANSACTIONSDATESArry[i]);
                        }
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
            } else if (TRANSACTIONSArry.length == 1) {
                try {
                    Date date = dateFormat.parse(TRANSACTIONSDATESArry[0]);
                    if(currentDate.after(date)){
                        Log.e("app", "today is after date");
                        getInvoiceStatus(TRANSACTIONSArry[0], TRANSACTIONSDATESArry[0]);
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }


        }

    }

    void getHash(String str) {
        hash = Hashing.sha256().hashString(str, StandardCharsets.UTF_8).toString();
    }
    @SuppressLint({"JavascriptInterface", "SetJavaScriptEnabled"})
    void getInvoiceStatus(String orderRequestNumber, String date) {

        String post = "MTg2NWEzYjViNGI1NzNmZDMzODNmODY0";
        post += "0100000205";
        post += "2";
        post += orderRequestNumber;
        post += "2.0";
        getHash(post);

        String bodyData = "MTg2NWEzYjViNGI1NzNmZDMzODNmODY0&MerchantID=0100000205&MessageID=2&OriginalTransactionID=" +
                orderRequestNumber +
                "&Version=2.0&SecureHash=" + hash;

        String url = "https://srstaging.stspayone.com/SmartRoutePaymentWeb/SRMsgHandler";

        webView.postUrl(
                url,
                EncodingUtils.getBytes(bodyData, "utf-8"));

        class MyJavaScriptInterface {
            private TextView contentView;

            public MyJavaScriptInterface(TextView aContentView) {
                contentView = aContentView;
            }

            @SuppressWarnings("unused")
            public void processContent(String aContent) {
                final String content = aContent;
                contentView.post(() -> {
                    contentView.setText(content);
//                        showHidePaymentStatus(content);
                });
            }
        }
        webView.getSettings().setJavaScriptEnabled(true);
        webView.addJavascriptInterface(new MyJavaScriptInterface(contentView), "INTERFACE");
        webView.setWebViewClient(new WebViewClient() {
            public void onPageFinished(WebView view, String url) {
                // do your stuff here
                view.loadUrl("javascript:window.INTERFACE.processContent(document.getElementsByTagName('body')[0].innerText);");
                webView.evaluateJavascript(
                        "(function() { return ('<html>'+document.getElementsByTagName('html')[0].innerHTML+'</html>'); })();",
                        html -> {
                            showHidePaymentStatus(html, orderRequestNumber, date);
                        });
            }
        });

        webView.evaluateJavascript(
                "(function() { return ('<html>'+document.getElementsByTagName('html')[0].innerHTML+'</html>'); })();",
                html -> {
                    showHidePaymentStatus(html, orderRequestNumber, date);
                    webView.evaluateJavascript(
                            "(function() { return ('<html>'+document.getElementsByTagName('html')[0].innerHTML+'</html>'); })();",
                            html1 -> {
                                showHidePaymentStatus(html, orderRequestNumber, date);
                            });
                });


    }

    void showHidePaymentStatus(String txt, String orderRequestNumber, String date) {
        Handler handler = new Handler();
        handler.postDelayed(() -> {
            if (txt.contains("\"\\u003Chtml>\\u003Chead>\\u003C/head>\\u003Cbody>") && txt.contains("Response.GatewayStatusCode")) {
                if (txt.contains("Response.GatewayStatusCode=0000")) {
                    addNotPaidInvoice(orderRequestNumber, "true", date);
                } else {
                    addNotPaidInvoice(orderRequestNumber, "false", date);
                }
            }
//            hideLoadingIndicator();
        }, 500);
    }

    public static String removeWords(String word ,String remove) {
        return word.replace(remove,"");
    }

    @SuppressLint("CheckResult")
    public void addNotPaidInvoice(String orderRequestNumber, String paid, String date) {

        Invoice invoice_ = Invoice.builder()
                .collection("invoices")
                .orderDate(new Date())
                .orderRequestNumber(orderRequestNumber)
                .orderVat("0.0%")
                .orderVatPrice("0")
                .paid(paid)
                .build();


        mRTDataFramework.addInvoice(invoice_)
                .subscribe();

        MyPreferenceManager SP = MyPreferenceManager.getInstance(getApplicationContext());
        String transActions = SP.getString("TRANSACTIONS", "");
        String transActionsDates = SP.getString("TRANSACTIONSDATES", "");

        String ss = removeWords(transActions, orderRequestNumber);
        String sss = removeWords(transActionsDates, date);

        SP.putString("TRANSACTIONS", ss);
        SP.putString("TRANSACTIONSDATES", sss);

    }
    public static ApplicationModel get(Context context) {
        return (ApplicationModel) context.getApplicationContext();
    }

    public AppComponent getAppComponent() {
        return appComponent;
    }

    public void initDaggerComponents() {
        appComponent = DaggerAppComponent.builder()
                .appModule(new AppModule(this))
                .backendModule(new BackendModule())
                .build();
        appComponent.inject(this);
    }

    public void initCrashRecoveryLibrary() {
        PlanB.get().init(true,
                PlanB.newConfig(this)
                        .applicationVariant(BuildConfig.BUILD_TYPE, BuildConfig.FLAVOR).build());

        PlanB.get().enableCrashHandler(this,
                PlanB.behaviourFactory().createRestartLauncherActivityCrashBehaviour());
    }

    private void initRxJavaErrorHandler() {
        RxJavaPlugins.setErrorHandler(e -> {
            Timber.e("%s %s", e.getClass().toString(), CommonUtils.getExceptionString(e) + "APP");
            if (e instanceof UndeliverableException) {
                e = e.getCause();
                Timber.e("%s %s", e.getClass().toString(), CommonUtils.getExceptionString(e));
            }
            if ((e instanceof IOException) || (e instanceof SocketException)) {
                // fine, irrelevant network problem or API that throws on cancellation
                Timber.e("%s %s", e.getClass().toString(), CommonUtils.getExceptionString(e));
                Timber.e("IOException" + e.getMessage());
                return;
            }
            if (e instanceof InterruptedException) {
                // fine, some blocking code was interrupted by a dispose call
                Timber.e("%s %s", e.getClass().toString(), CommonUtils.getExceptionString(e));
                return;
            }
            if ((e instanceof NullPointerException) || (e instanceof IllegalArgumentException)) {
                // that's likely a bug in the application
                Timber.e("%s %s", e.getClass().toString(), CommonUtils.getExceptionString(e));
                return;
            }
            if (e instanceof IllegalStateException) {
                // that's a bug in RxJava or in a custom operator
                Timber.e("%s %s", e.getClass().toString(), CommonUtils.getExceptionString(e));
                return;
            }
        });
        RxJava2Debug.enableRxJava2AssemblyTracking(new String[]{
                "com.shawerapp.android"
        });
    }

    /**
     * A custom debug tree with a custom tag
     */
    private static class HubChartDebugTree extends Timber.DebugTree {
        @Override
        protected void log(int priority, String callingClass, String message, Throwable t) {
            Log.println(priority, LOG_TAG + callingClass, message);
        }
    }

    /**
     * A tree which logs important information for crash reporting.
     */
    private static class CrashReportingTree extends Timber.Tree {
        @Override
        protected void log(int priority, String tag, String message, Throwable t) {
            if (BuildConfig.DEBUG) {
                Log.println(priority, LOG_TAG, message);
            } else {
                if (priority == Log.VERBOSE || priority == Log.DEBUG) {
                    return;
                }

                if (t != null) {
                    if (priority == Log.ERROR) {
                        // Crashlytics.getInstance().core.logException(t);
                    }
                }
            }
        }
    }



    public void addToRequestQueue(Request request){
        if(getRequestQueue() !=null) {
            getRequestQueue().add(request);
        }
        else {
            requestQueue = Volley.newRequestQueue(this);
            getRequestQueue().add(request);
        }
    }

    private RequestQueue getRequestQueue(){
        return  requestQueue;
    }

    public void cancelRequest(String tag){
        getRequestQueue().cancelAll(tag);
    }

    public static synchronized ApplicationModel getInstance(){
        return anInstance;
    }

}
