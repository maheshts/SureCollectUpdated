package com.loan.recovery.retrofit.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by mjonnakuti on 25/10/2020.
 */

public class NonRetraCasesData extends BaseResponse {

//    public List<Case> getCaseList() {
//        return caseList;
//    }
//
//    public void setCaseList(List<Case> caseList) {
//        this.caseList = caseList;
//    }

//    public Case getCasedata() {
//        return casedata;
//    }
//
//    public void setCasedata(Case casedata) {
//        this.casedata = casedata;
//    }

//    @SerializedName("data")
//    private List<Case> caseList;

    public String getCount() {
        return count;
    }

    public void setCount(String count) {
        this.count = count;
    }

    //    @SerializedName("caseList")
//    private Case casedata;//
    @SerializedName("applicationCount")
    private String  count;


}