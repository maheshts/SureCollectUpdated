package com.loan.recovery.retrofit.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by mjonnakuti on 25/10/2020.
 */

public class PartnerCallResponse extends BaseResponse{

    @SerializedName("list")
    private List<Partner> partnersList;

    public PartnerCallResponse() {
    }

    public PartnerCallResponse(List<Partner> partnersList) {
        this.partnersList = partnersList;
    }

    public List<Partner> getPartnersList() {
        return partnersList;
    }

    public void setPartnersList(List<Partner> partnersList) {
        this.partnersList = partnersList;
    }
}