package com.loan.recovery.retrofit.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class DayReportList {

    @SerializedName("data")
    private List<ReportDayProgress> reportData;

    public DayReportList(List<ReportDayProgress> reportData) {
        this.reportData = reportData;
    }

    public DayReportList() {
    }

    public List<ReportDayProgress> getReportData() {
        return reportData;
    }

    public void setReportData(List<ReportDayProgress> reportData) {
        this.reportData = reportData;
    }
}
