package com.loan.recovery.retrofit.model;

import com.google.gson.annotations.SerializedName;

public class NonRetraCaseRequest {


    public String getFkPartnerId() {
        return fkPartnerId;
    }

    public void setFkPartnerId(String fkPartnerId) {
        this.fkPartnerId = fkPartnerId;
    }

    public String getForExport() {
        return forExport;
    }

    public void setForExport(String forExport) {
        this.forExport = forExport;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getLoggedInRoleId() {
        return loggedInRoleId;
    }

    public void setLoggedInRoleId(String loggedInRoleId) {
        this.loggedInRoleId = loggedInRoleId;
    }

    public String getLoggedinUserId() {
        return loggedinUserId;
    }

    public void setLoggedinUserId(String loggedinUserId) {
        this.loggedinUserId = loggedinUserId;
    }

    public String getPartnerCaseId() {
        return partnerCaseId;
    }

    public void setPartnerCaseId(String partnerCaseId) {
        this.partnerCaseId = partnerCaseId;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getSelectedUserId() {
        return selectedUserId;
    }

    public void setSelectedUserId(String selectedUserId) {
        this.selectedUserId = selectedUserId;
    }

    public String getSortByColumn() {
        return sortByColumn;
    }

    public void setSortByColumn(String sortByColumn) {
        this.sortByColumn = sortByColumn;
    }

    public String getStartRows() {
        return startRows;
    }

    public void setStartRows(String startRows) {
        this.startRows = startRows;
    }

    public String getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(String statusCode) {
        this.statusCode = statusCode;
    }


    public String getEndRows() {
        return endRows;
    }

    public void setEndRows(String endRows) {
        this.endRows = endRows;
    }

    @SerializedName("endRows")
    private String endRows;

    @SerializedName("fkPartnerId")
    private String fkPartnerId;

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    @SerializedName("firstName")
    private String firstName;

    @SerializedName("forExport")
    private String forExport;

    @SerializedName("lastName")
    private String lastName;

    @SerializedName("loggedInRoleId")
    private String loggedInRoleId;

    @SerializedName("loggedinUserId")
    private String loggedinUserId;

    @SerializedName("partnerCaseId")
    private String partnerCaseId;

   @SerializedName("phoneNumber")
    private String phoneNumber;

    @SerializedName("selectedUserId")
    private String selectedUserId;

    @SerializedName("sortByColumn")
    private String sortByColumn;

    @SerializedName("startRows")
    private String startRows;

    @SerializedName("statusCode")
    private String statusCode;


}
