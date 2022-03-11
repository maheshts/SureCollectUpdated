package com.loan.recovery.retrofit.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by mjonnakuti on 15/03/2020.
 */

public class BaseResponse {

    @SerializedName("status")
    private String status;
    @SerializedName("message")
    private String message;

    @SerializedName("messageCode")
    private int messageCode;

    @SerializedName("trans_agent_call_session_id")
    private int agentCallSessionId;

    @SerializedName("StatusCode")
    private int statusCode;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getMessageCode() {
        return messageCode;
    }

    public void setMessageCode(int messageCode) {
        this.messageCode = messageCode;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public int getAgentCallSessionId() {
        return agentCallSessionId;
    }

    public void setAgentCallSessionId(int agentCallSessionId) {
        this.agentCallSessionId = agentCallSessionId;
    }

    @Override
    public String toString() {
        return "BaseResponse{" +
                "status='" + status + '\'' +
                ", message='" + message + '\'' +
                ", messageCode=" + messageCode +
                ", agentCallSessionId=" + agentCallSessionId +
                ", statusCode=" + statusCode +
                '}';
    }
}