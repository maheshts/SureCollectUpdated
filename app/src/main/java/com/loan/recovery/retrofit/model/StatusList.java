package com.loan.recovery.retrofit.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by mjonnakuti on 25/10/2020.
 */

public class StatusList{

    @SerializedName("parter_list")
    private List<Status> statuses;

    @SerializedName("totalCount")
    private int totalCount;

    public StatusList() {
    }

    public StatusList(List<Status> statuses, int totalCount) {
        this.statuses = statuses;
        this.totalCount = totalCount;
    }

    public List<Status> getStatuses() {
        return statuses;
    }

    public void setStatuses(List<Status> statuses) {
        this.statuses = statuses;
    }

    public int getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }
}