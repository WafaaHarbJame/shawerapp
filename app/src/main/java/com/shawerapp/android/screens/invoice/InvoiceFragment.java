package com.shawerapp.android.screens.invoice;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.crashlytics.android.Crashlytics;
import com.google.common.hash.Hashing;
import com.shawerapp.android.R;
import com.shawerapp.android.autovalue.CommercialUser;
import com.shawerapp.android.autovalue.IndividualUser;
import com.shawerapp.android.autovalue.Invoice;
import com.shawerapp.android.backend.base.AuthFramework;
import com.shawerapp.android.backend.retrofit.API_Client;
import com.shawerapp.android.backend.retrofit.apiClass;
import com.shawerapp.android.base.BaseFragment;
import com.shawerapp.android.base.FragmentLifecycle;
import com.shawerapp.android.custom.views.IndeterminateTransparentProgressDialog;
import com.shawerapp.android.screens.container.ContainerActivity;

import org.apache.http.util.EncodingUtils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.DateFormat;
import java.util.Objects;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Maybe;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.shawerapp.android.screens.composer.ComposerKey.QUESTION;

public final class InvoiceFragment extends BaseFragment implements InvoiceContract.View {

    public static final String ARG_INVOICE = "invoice";

    private IndeterminateTransparentProgressDialog mIndeterminateTransparentProgressDialog;

    public static InvoiceFragment newInstance(Invoice invoice) {
        Bundle args = new Bundle();
        args.putParcelable(ARG_INVOICE, invoice);
        InvoiceFragment fragment = new InvoiceFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Inject
    InvoiceContract.ViewModel viewModel;

    @Inject
    AuthFramework mAuthFramework;

    @BindView(R.id.orderRequestTxt)
    TextView orderRequestTxt;

    @BindView(R.id.orderDateTxt)
    TextView orderDateTxt;

    @BindView(R.id.OrderTypeTxt)
    TextView orderTypeTxt;

    @BindView(R.id.orderFeeTxt)
    TextView orderFeeTxt;

    @BindView(R.id.orderSubTotalTxt)
    TextView orderSubTotalTxt;

    @BindView(R.id.orderVATTxt)
    TextView orderVATTxt;

    @BindView(R.id.orderTotalVATTxt)
    TextView orderTotalVATTxt;

    @BindView(R.id.totalLbl)
    TextView totalLbl;

    @BindView(R.id.faildTxt)
    TextView faildTxt;

    @BindView(R.id.webView)
    WebView webView;

    @BindView(R.id.contentView_)
    TextView contentView;

    Invoice invoice;

    String hash;


    @Override
    protected FragmentLifecycle.ViewModel getViewModel() {
        return viewModel;
    }

    @Override
    public void onAttach(Context context) {
        DaggerInvoiceComponent.builder()
                .containerComponent(((ContainerActivity) context).getContainerComponent())
                .invoiceModule(new InvoiceModule(this, this))
                .build()
                .inject(this);
        super.onAttach(context);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void initBindings() {
        orderRequestTxt.setText(invoice.orderRequestNumber());

        String date = DateFormat.getDateInstance(DateFormat.SHORT).format(invoice.orderDate());

        orderDateTxt.setText(date);
        if (invoice.orderType() != null) {
            if (Objects.requireNonNull(invoice.orderType()).equals("Esteshara Fee") || Objects.requireNonNull(invoice.orderType()).equals("رسوم استشارة")) {
                orderTypeTxt.setText(getString(R.string.EstesharaFee));
            } else if (Objects.requireNonNull(invoice.orderType()).equals("Question Fee") || Objects.requireNonNull(invoice.orderType()).equals("رسوم سؤال")) {
                orderTypeTxt.setText(getString(R.string.QuestionFee));
            } else {
                orderTypeTxt.setText(getString(R.string.lawyerOfficeFee));
            }
        } else {
            orderTypeTxt.setText(getString(R.string.QuestionFee));
        }


        if (invoice.orderTypePrice().equals("null")) {
            orderFeeTxt.setText("0 " + getString(R.string.sar));
        } else {
            orderFeeTxt.setText(invoice.orderTypePrice() + " " + getString(R.string.sar));
        }

        if (invoice.orderSubTotal().equals("null")) {
            orderSubTotalTxt.setText("0 " + getString(R.string.sar));
        } else {
            orderSubTotalTxt.setText(invoice.orderSubTotal() + " " + getString(R.string.sar));
        }

        if (invoice.orderVat().equals("null")) {
            orderVATTxt.setText("0");
        } else {
            orderVATTxt.setText(invoice.orderVat());
        }

        if (invoice.orderVatPrice().equals("null")) {
            orderTotalVATTxt.setText("0");
        } else {
            orderTotalVATTxt.setText(invoice.orderVatPrice());
        }


        if (invoice.paid() == null) {
            if (invoice.paid().equals("true")) {
                totalLbl.setVisibility(View.VISIBLE);
                faildTxt.setVisibility(View.GONE);
            } else {
                totalLbl.setVisibility(View.GONE);
                faildTxt.setVisibility(View.VISIBLE);
            }
        } else {
            if (invoice.paid().equals("true")) {
                totalLbl.setVisibility(View.VISIBLE);
                faildTxt.setVisibility(View.GONE);
            } else {
                totalLbl.setVisibility(View.GONE);
                faildTxt.setVisibility(View.VISIBLE);
            }
        }
        hideLoadingIndicator();

    }

//    public void getInvoiceStatus() {
//        String post = "MTg2NWEzYjViNGI1NzNmZDMzODNmODY0";
//        post += "0100000205";
//        post += "2";
//        post += invoice.orderRequestNumber();
//        post += "2.0";
//        getHash(post);
//
//        apiClass getCategoryProducts = API_Client.getClient().create(apiClass.class);
//        Call<Response<ResponseBody>> categoryProducts = getCategoryProducts.getInvoiceStatus("application/json","0100000205","2",invoice.orderRequestNumber(),"2.0",hash);
//        categoryProducts.enqueue(new Callback<Response<ResponseBody>>() {
//            @Override
//            public void onResponse(@NonNull Call<Response<ResponseBody>> call, @NonNull Response<Response<ResponseBody>> response) {
//                if (response.isSuccessful()) {
//                    Log.i(" Response =>", response.toString());
//                }
//
//            }
//
//            @Override
//            public void onFailure(@NonNull Call<Response<ResponseBody>> call, @NonNull Throwable t) {
//                Log.i(" Response =>", t.toString());
//            }
//        });
//    }

    @SuppressLint({"JavascriptInterface", "SetJavaScriptEnabled"})
    void getInvoiceStatus() {
        String post = "MTg2NWEzYjViNGI1NzNmZDMzODNmODY0";
        post += "0100000205";
        post += "2";
        post += invoice.orderRequestNumber();
        post += "2.0";
        getHash(post);

        String bodyData = "MTg2NWEzYjViNGI1NzNmZDMzODNmODY0&MerchantID=0100000205&MessageID=2&OriginalTransactionID=" +
                invoice.orderRequestNumber() +
                "&Version=2.0&SecureHash=" + hash;

        try {
            String query = URLEncoder.encode(bodyData, "utf-8");
            String url = "https://srstaging.stspayone.com/SmartRoutePaymentWeb/SRMsgHandler";
            final String[] url2 = {""};
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
                                showHidePaymentStatus(html);
                            });
                }
            });

            webView.evaluateJavascript(
                    "(function() { return ('<html>'+document.getElementsByTagName('html')[0].innerHTML+'</html>'); })();",
                    html -> {
                        showHidePaymentStatus(html);
                        webView.evaluateJavascript(
                                "(function() { return ('<html>'+document.getElementsByTagName('html')[0].innerHTML+'</html>'); })();",
                                html1 -> {
                                    showHidePaymentStatus(html1);
                                });
                    });


        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    void showHidePaymentStatus(String txt) {
        Handler handler = new Handler();
        handler.postDelayed(() -> {
            if (txt.contains("\"\\u003Chtml>\\u003Chead>\\u003C/head>\\u003Cbody>") && txt.contains("Response.GatewayStatusCode")) {
                if (txt.contains("Response.GatewayStatusCode=0000")) {
                    totalLbl.setVisibility(View.VISIBLE);
                    faildTxt.setVisibility(View.GONE);
                } else {
                    totalLbl.setVisibility(View.GONE);
                    faildTxt.setVisibility(View.VISIBLE);
                }
            }
            hideLoadingIndicator();
        }, 500);
    }

    void getHash(String str) {
        hash = Hashing.sha256().hashString(str, StandardCharsets.UTF_8).toString();
    }

    public void hideLoadingIndicator() {
        if (mIndeterminateTransparentProgressDialog != null) {
            if (mIndeterminateTransparentProgressDialog.isShowing()) {
                mIndeterminateTransparentProgressDialog.dismiss();
            }
        }
    }

    @Override
    public void addItem(Invoice invoice) {

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_invoice, container, false);

        mUnbinder = ButterKnife.bind(this, view);

        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        invoice = getArguments().getParcelable(ARG_INVOICE);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        viewModel.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public boolean onItemClick(View view, int position) {

        return false;
    }

    @Override
    public void onResume() {
        super.onResume();
        getInvoiceStatus();
    }
}
