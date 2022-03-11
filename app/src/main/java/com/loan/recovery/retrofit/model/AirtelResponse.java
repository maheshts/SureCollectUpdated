package com.loan.recovery.retrofit.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by mjonnakuti on 15/03/2020.
 */

public class AirtelResponse extends BaseResponse {
    @SerializedName("data")
    private List<AirtelPhone> airtelPhones;

    public AirtelResponse(List<AirtelPhone> userPhone) {
        airtelPhones = userPhone;
    }

    public List<AirtelPhone> getAirtelPhones() {
        return airtelPhones;
    }

    public void setAirtelPhones(List<AirtelPhone> airtelPhones) {
        this.airtelPhones = airtelPhones;
    }
}