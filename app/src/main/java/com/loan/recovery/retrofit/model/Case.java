package com.loan.recovery.retrofit.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by mjonnakuti on 25/10/2020.
 */

public class Case {

    @SerializedName("application_count")
    private int applicationCount;

    @SerializedName("case_list")
    private CaseData caseData;

    public Case() {
    }

    public Case(int applicationCount, CaseData caseData) {
        this.applicationCount = applicationCount;
        this.caseData = caseData;
    }

    public int getApplicationCount() {
        return applicationCount;
    }

    public void setApplicationCount(int applicationCount) {
        this.applicationCount = applicationCount;
    }

    public CaseData getCaseData() {
        return caseData;
    }

    public void setCaseData(CaseData caseData) {
        this.caseData = caseData;
    }
}