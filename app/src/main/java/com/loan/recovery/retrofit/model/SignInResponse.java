package com.loan.recovery.retrofit.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by mjonnakuti on 15/03/2020.
 */

public class SignInResponse extends BaseResponse {
    @SerializedName("data")
    private UserData data;

    public SignInResponse() {
    }

    public SignInResponse(UserData data) {
        this.data = data;
    }

    public UserData getData() {
        return data;
    }

    public void setData(UserData data) {
        this.data = data;
    }
}