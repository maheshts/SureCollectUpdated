package com.loan.recovery.retrofit.model;

import com.google.gson.annotations.SerializedName;

public class ReportDayProgress {

    @SerializedName("v_seconds")
    private long vSecounds;
    @SerializedName("hrs")
    private int hrs;
    @SerializedName("hr_range")
    private String hrRange;
    @SerializedName("dialed_cases")
    private int dialedCases;
    @SerializedName("connected_cases")
    private int connectedCases;
    @SerializedName("active_time_min")
    private int activeTimeMin;
    @SerializedName("inactive_time_min")
    private int inActiveTimeMin;
    @SerializedName("official_time_min")
    private int officialTimeMin;
    @SerializedName("active_time_percent")
    private String activeTimePercent;
    @SerializedName("minutes_per_day")
    private int minutesPerDay;

    public ReportDayProgress() {
    }

    public long getvSecounds() {
        return vSecounds;
    }

    public void setvSecounds(long vSecounds) {
        this.vSecounds = vSecounds;
    }

    public int getHrs() {
        return hrs;
    }

    public void setHrs(int hrs) {
        this.hrs = hrs;
    }

    public String getHrRange() {
        return hrRange;
    }

    public void setHrRange(String hrRange) {
        this.hrRange = hrRange;
    }

    public int getDialedCases() {
        return dialedCases;
    }

    public void setDialedCases(int dialedCases) {
        this.dialedCases = dialedCases;
    }

    public int getConnectedCases() {
        return connectedCases;
    }

    public void setConnectedCases(int connectedCases) {
        this.connectedCases = connectedCases;
    }

    public int getActiveTimeMin() {
        return activeTimeMin;
    }

    public void setActiveTimeMin(int activeTimeMin) {
        this.activeTimeMin = activeTimeMin;
    }

    public int getInActiveTimeMin() {
        return inActiveTimeMin;
    }

    public void setInActiveTimeMin(int inActiveTimeMin) {
        this.inActiveTimeMin = inActiveTimeMin;
    }

    public int getOfficialTimeMin() {
        return officialTimeMin;
    }

    public void setOfficialTimeMin(int officialTimeMin) {
        this.officialTimeMin = officialTimeMin;
    }

    public String getActiveTimePercent() {
        return activeTimePercent;
    }

    public void setActiveTimePercent(String activeTimePercent) {
        this.activeTimePercent = activeTimePercent;
    }

    public int getMinutesPerDay() {
        return minutesPerDay;
    }

    public void setMinutesPerDay(int minutesPerDay) {
        this.minutesPerDay = minutesPerDay;
    }

    @Override
    public String toString() {
        return "ReportDayProgress{" +
                "vSecounds=" + vSecounds +
                ", hrs=" + hrs +
                ", hrRange='" + hrRange + '\'' +
                ", dialedCases=" + dialedCases +
                ", connectedCases=" + connectedCases +
                ", activeTimeMin=" + activeTimeMin +
                ", inActiveTimeMin=" + inActiveTimeMin +
                ", officialTimeMin=" + officialTimeMin +
                ", activeTimePercent='" + activeTimePercent + '\'' +
                ", minutesPerDay=" + minutesPerDay +
                '}';
    }
}
