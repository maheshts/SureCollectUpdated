package com.loan.recovery.retrofit.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by mjonnakuti on 25/10/2020.
 */

public class ConfigResponse extends BaseResponse{

    @SerializedName("data")
    private List<ProjectConfigData> partnerTypeList;

    public ConfigResponse() {
    }

    public ConfigResponse(List<ProjectConfigData> paymentTypeList) {
        this.partnerTypeList = paymentTypeList;
    }

    public List<ProjectConfigData> getPaymentTypeList() {
        return partnerTypeList;
    }

    public void setPaymentTypeList(List<ProjectConfigData> paymentTypeList) {
        this.partnerTypeList = paymentTypeList;
    }
}