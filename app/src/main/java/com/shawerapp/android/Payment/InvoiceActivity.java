package com.shawerapp.android.Payment;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.webkit.WebView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.Toolbar;

import com.shawerapp.android.R;
import com.shawerapp.android.screens.container.ContainerActivity;

import java.text.DateFormat;
import java.util.Date;

public class InvoiceActivity extends AppCompatActivity {

    private TextView mContentView;
    private AppCompatTextView mFaildTxt;
    private AppCompatTextView mOrderRequestTxt;
    private AppCompatTextView mOrderDateTxt;
    private TextView mTextView4;
    private AppCompatTextView mOrderTypeTxt;
    private AppCompatTextView mOrderFeeTxt;
    private TextView mTextView5;
    private AppCompatTextView mOrderSubTotalTxt;
    private AppCompatTextView mOrderVATTxt;
    private AppCompatTextView mOrderTotalVATTxt;
    private AppCompatTextView mTotalLbl;
    private WebView mWebView;
    private LinearLayout mInvoiceContainer;
    public  String mOrderDate,mOrderRequest,mOrderSubTotal,mOrderTotalVAT;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invoice);
        Toolbar toolbar = findViewById(R.id.toolbar_back_arrow);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        mContentView = findViewById(R.id.contentView_);
        mFaildTxt = findViewById(R.id.faildTxt);
        mOrderRequestTxt = findViewById(R.id.orderRequestTxt);
        mOrderDateTxt = findViewById(R.id.orderDateTxt);
        mTextView4 = findViewById(R.id.textView4);
        mOrderTypeTxt = findViewById(R.id.OrderTypeTxt);
        mOrderFeeTxt = findViewById(R.id.orderFeeTxt);
        mTextView5 = findViewById(R.id.textView5);
        mOrderSubTotalTxt = findViewById(R.id.orderSubTotalTxt);
        mOrderVATTxt = findViewById(R.id.orderVATTxt);
        mOrderTotalVATTxt = findViewById(R.id.orderTotalVATTxt);
        mTotalLbl = findViewById(R.id.totalLbl);
        mWebView = findViewById(R.id.webView);
        mInvoiceContainer = findViewById(R.id.InvoiceContainer);

        Bundle bundle = getIntent().getExtras();

        if (bundle!= null){
            mOrderSubTotal = bundle.getString(AppConstants.amount);
            mOrderRequest = bundle.getString(AppConstants.orderRequestTxt);
            mOrderTotalVAT=bundle.getString(AppConstants.mOrderVATTxt);
            mOrderDate=bundle.getString(AppConstants.newdate);

        }


        mOrderDateTxt.setText(mOrderDate);
        mOrderTotalVATTxt.setText(mOrderTotalVAT);
        mOrderRequestTxt.setText(mOrderRequest);
        mOrderSubTotalTxt.setText(mOrderSubTotal+getString(R.string.sar));
        mOrderFeeTxt.setText(mOrderSubTotal+getString(R.string.sar));



    }

    @Override
    public void onBackPressed() {
        Intent intent=new Intent(InvoiceActivity.this, ContainerActivity.class);;
        startActivity(intent);
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            Intent intent=new Intent(InvoiceActivity.this, ContainerActivity.class);;
            startActivity(intent);
            finish();        }
        return super.onOptionsItemSelected(item);
    }
}
