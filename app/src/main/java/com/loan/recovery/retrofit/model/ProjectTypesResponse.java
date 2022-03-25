package com.loan.recovery.retrofit.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by mjonnakuti on 25/10/2020.
 */

public class ProjectTypesResponse extends BaseResponse{

    @SerializedName("data")
    private List<ProjectData> projectTypeList;

    public ProjectTypesResponse() {
    }

    public ProjectTypesResponse(List<ProjectData> paymentTypeList) {
        this.projectTypeList = paymentTypeList;
    }

    public List<ProjectData> getPaymentTypeList() {
        return projectTypeList;
    }

    public void setPaymentTypeList(List<ProjectData> paymentTypeList) {
        this.projectTypeList = paymentTypeList;
    }
}