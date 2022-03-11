package com.loan.recovery.retrofit.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by mjonnakuti on 25/10/2020.
 */

public class CasesList extends BaseResponse {

    @SerializedName("data")
    private List<Case> caseList;

    public CasesList() {
    }

    public CasesList(List<Case> caseList) {
        this.caseList = caseList;
    }

    public List<Case> getCaseList() {
        return caseList;
    }

    public void setCaseList(List<Case> caseList) {
        this.caseList = caseList;
    }
}