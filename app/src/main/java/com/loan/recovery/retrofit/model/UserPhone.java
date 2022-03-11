package com.loan.recovery.retrofit.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by mjonnakuti on 15/03/2020.
 */

public class UserPhone {
    @SerializedName("user_phone_no")
    private String userPhone;

    @SerializedName("track_gps")
    private char isTrackGps;

    @SerializedName("airtel_number")
    private String aiqNumber;

    @SerializedName("isp_name")
    private String ispName;

    @SerializedName("virtuo_agent_number")
    private int virtuoId;

    @SerializedName("direct_call")
    private char isDirectCall;

    public UserPhone(String userPhone, char isTrackGps, String aiqNumber, String ispName, int virtuoId, char isDirectCall) {
        this.userPhone = userPhone;
        this.isTrackGps = isTrackGps;
        this.aiqNumber = aiqNumber;
        this.ispName = ispName;
        this.virtuoId = virtuoId;
        this.isDirectCall = isDirectCall;
    }

    public String getUserPhone() {
        return userPhone;
    }

    public void setUserPhone(String userPhone) {
        this.userPhone = userPhone;
    }

    public char getIsTrackGps() {
        return isTrackGps;
    }

    public void setIsTrackGps(char isTrackGps) {
        this.isTrackGps = isTrackGps;
    }

    public String getAiqNumber() {
        return aiqNumber;
    }

    public void setAiqNumber(String aiqNumber) {
        this.aiqNumber = aiqNumber;
    }

    public String getIspName() {
        return ispName;
    }

    public void setIspName(String ispName) {
        this.ispName = ispName;
    }

    public int getVirtuoId() {
        return virtuoId;
    }

    public void setVirtuoId(int virtuoId) {
        this.virtuoId = virtuoId;
    }

    public char getIsDirectCall() {
        return isDirectCall;
    }

    public void setIsDirectCall(char isDirectCall) {
        this.isDirectCall = isDirectCall;
    }
}