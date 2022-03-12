package com.loan.recovery.retrofit.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by mjonnakuti on 25/10/2020.
 */

public class NonRetraBaseResponse extends BaseResponse {


    public NonRetraCasesResponse getResult() {
        return result;
    }

    public void setResult(NonRetraCasesResponse result) {
        this.result = result;
    }

    @SerializedName("result")
    private NonRetraCasesResponse result;


}