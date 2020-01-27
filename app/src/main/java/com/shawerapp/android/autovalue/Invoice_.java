package com.shawerapp.android.autovalue;

import java.util.Date;


public class Invoice_ {

    public String getOrderVatPrice() {
        return orderVatPrice;
    }

    public void setOrderVatPrice(String orderVatPrice) {
        this.orderVatPrice = orderVatPrice;
    }

    public String getOrderVat() {
        return orderVat;
    }

    public void setOrderVat(String orderVat) {
        this.orderVat = orderVat;
    }

    public String getOrderTypePrice() {
        return orderTypePrice;
    }

    public void setOrderTypePrice(String orderTypePrice) {
        this.orderTypePrice = orderTypePrice;
    }

    public String getOrderType() {
        return orderType;
    }

    public void setOrderType(String orderType) {
        this.orderType = orderType;
    }

    public String getOrderSubTotal() {
        return orderSubTotal;
    }

    public void setOrderSubTotal(String orderSubTotal) {
        this.orderSubTotal = orderSubTotal;
    }

    public String getOrderRequestNumber() {
        return orderRequestNumber;
    }

    public void setOrderRequestNumber(String orderRequestNumber) {
        this.orderRequestNumber = orderRequestNumber;
    }

    public String getCollection() {
        return collection;
    }

    public void setCollection(String collection) {
        this.collection = collection;
    }

    public String getUserUid() {
        return UserUid;
    }

    public void setUserUid(String userUid) {
        UserUid = userUid;
    }

    public String getlawyerUid() {
        return LawyerUid;
    }

    public void setlawyerUid(String lawyerUid) {
        LawyerUid = lawyerUid;
    }

    public Date getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(Date orderDate) {
        this.orderDate = orderDate;
    }

    private String orderVatPrice, orderVat, orderTypePrice,
            orderType, orderSubTotal, orderRequestNumber, collection, UserUid, LawyerUid;

    private Date orderDate;

    public Invoice_() {

    }

    public Invoice_(String orderVatPrice, String orderVat,
                    String orderTypePrice, String orderType, String orderSubTotal,
                    String orderRequestNumber, String collection, String userUid,
                    String lawyerUid, Date orderDate) {
        this.orderVatPrice = orderVatPrice;
        this.orderVat = orderVat;
        this.orderTypePrice = orderTypePrice;
        this.orderType = orderType;
        this.orderSubTotal = orderSubTotal;
        this.orderRequestNumber = orderRequestNumber;
        this.collection = collection;
        UserUid = userUid;
        LawyerUid = lawyerUid;
        this.orderDate = orderDate;
    }

}
