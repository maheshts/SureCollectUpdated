package com.loan.recovery.retrofit.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by mjonnakuti on 25/10/2020.
 */

public class StatusCallResponse extends BaseResponse{

    @SerializedName("data")
    private List<Status> statusList;

    public StatusCallResponse() {
    }

    public StatusCallResponse(List<Status> statusList) {
        this.statusList = statusList;
    }

    public List<Status> getStatusList() {
        return statusList;
    }

    public void setStatusList(List<Status> statusList) {
        this.statusList = statusList;
    }
}