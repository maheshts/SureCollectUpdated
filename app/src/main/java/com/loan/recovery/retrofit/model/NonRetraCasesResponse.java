package com.loan.recovery.retrofit.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by mjonnakuti on 25/10/2020.
 */

public class NonRetraCasesResponse extends BaseResponse {



    public int getStatuscode() {
        return statuscode;
    }

    public void setStatuscode(int statuscode) {
        this.statuscode = statuscode;
    }

//    @Override
//    public String getMessage() {
//        return message;
//    }
//
//    @Override
//    public void setMessage(String message) {
//        this.message = message;
//    }

//    @SerializedName("data")
//    private NonRetraCasesData casedata;

    public List<NonRetraCasesData> getCaseListData() {
        return caseListData;
    }

    public void setCaseListData(List<NonRetraCasesData> caseListData) {
        this.caseListData = caseListData;
    }

    @SerializedName("data")
    private List<NonRetraCasesData> caseListData;

    @SerializedName("statusCode")
    private int statuscode;

//    @SerializedName("message")
//    private String message;


}