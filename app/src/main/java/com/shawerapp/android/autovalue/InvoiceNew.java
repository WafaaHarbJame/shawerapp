package com.shawerapp.android.autovalue;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.Date;


public class InvoiceNew {


    public String orderVatPrice;
    public String orderVat;
    public String orderTypePrice;
    public String orderType;
    public String orderSubTotal;
    public String orderRequestNumber;
    public String collection;
    public String UserUid;
    public String LawyerUid;
    public Date orderDate;
    public String paid;
    public String uid;


    public InvoiceNew(String uid, String orderVatPrice, String orderVat, String orderTypePrice, String orderType, String orderSubTotal, String orderRequestNumber, Date orderDate, String collection, String UserUid, String LawyerUid, String paid) {

        this.orderVatPrice = orderVatPrice;
        this.orderVat = orderVat;
        this.orderTypePrice = orderTypePrice;
        this.orderType = orderType;
        this.orderSubTotal = orderSubTotal;
        this.orderRequestNumber = orderRequestNumber;
        this.collection = collection;
        this.UserUid = UserUid;
        this.LawyerUid = LawyerUid;
        this.orderDate = orderDate;
        this.paid = paid;
        this.uid = uid;
    }

}
