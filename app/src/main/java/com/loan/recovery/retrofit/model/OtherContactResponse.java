package com.loan.recovery.retrofit.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class OtherContactResponse extends BaseResponse {

    @SerializedName("data")
    private List<OtherContact> otherContactList;

    public OtherContactResponse() {
    }

    public OtherContactResponse(List<OtherContact> otherContactList) {
        this.otherContactList = otherContactList;
    }

    public List<OtherContact> getOtherContactList() {
        return otherContactList;
    }

    public void setOtherContactList(List<OtherContact> otherContactList) {
        this.otherContactList = otherContactList;
    }
}
