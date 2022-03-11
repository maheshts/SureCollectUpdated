package com.loan.recovery.retrofit.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by mjonnakuti on 15/03/2020.
 */

public class UserPhoneResponse extends BaseResponse {
    @SerializedName("data")
    private List<UserPhone> userPhone;

    public UserPhoneResponse(List<UserPhone> userPhone) {
        this.userPhone = userPhone;
    }

    public List<UserPhone> getUserPhone() {
        return userPhone;
    }

    public void setUserPhone(List<UserPhone> userPhone) {
        this.userPhone = userPhone;
    }
}