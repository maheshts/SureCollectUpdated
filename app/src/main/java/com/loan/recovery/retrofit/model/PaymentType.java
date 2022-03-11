package com.loan.recovery.retrofit.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by mjonnakuti on 25/10/2020.
 */

public class PaymentType extends BaseResponse{

    @SerializedName("lu_payment_type_id")
    private int paymentTypeId;
    @SerializedName("lu_payment_type_name")
    private String paymentTypeName;
    @SerializedName("created_date")
    private String createdDate;

    public PaymentType() {
    }

    public PaymentType(int paymentTypeId, String paymentTypeName, String createdDate) {
        this.paymentTypeId = paymentTypeId;
        this.paymentTypeName = paymentTypeName;
        this.createdDate = createdDate;
    }

    public int getPaymentTypeId() {
        return paymentTypeId;
    }

    public void setPaymentTypeId(int paymentTypeId) {
        this.paymentTypeId = paymentTypeId;
    }

    public String getPaymentTypeName() {
        return paymentTypeName;
    }

    public void setPaymentTypeName(String paymentTypeName) {
        this.paymentTypeName = paymentTypeName;
    }

    public String getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(String createdDate) {
        this.createdDate = createdDate;
    }

    @Override
    public String toString() {
        return paymentTypeName;
    }
}