package com.loan.recovery.retrofit.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by mjonnakuti on 25/11/2020.
 */

public class CaseData {

    @SerializedName("pan")
    private String pan;
    @SerializedName("case_uuid")
    private String caseUuid;
    @SerializedName("user_name")
    private String userName;
    @SerializedName("due_amount")
    private String dueAmount;
    @SerializedName("emi_amount")
    private String emiAmount;
    @SerializedName("assigned_to")
    private String assignedTo;
    @SerializedName("status_code")
    private int statusCode;
    @SerializedName("status_name")
    private String statusName;
    @SerializedName("od_principal")
    private String odPrincipal;
    @SerializedName("partner_name")
    private String partnerName;
    @SerializedName("phone_number")
    private String phoneNumber;

    @SerializedName("portfolio_id")
    private String portfolioId;
    @SerializedName("customer_name")
    private String customerName;
    @SerializedName("email_address")
    private String email;
    @SerializedName("employer_name")
    private String employerName;
    @SerializedName("modified_date")
    private String modifiedDate;
    @SerializedName("res_city")
    private String offCityName;
    @SerializedName("retra_case_id")
    private String retraCaseId;
    @SerializedName("partner_case_id")
    private String partnerCaseId;
    @SerializedName("next_action_date")
    private String nextActionDate;
    @SerializedName("partner_bucket_number")
    private String partnerBucketNumber;
    @SerializedName("propensity_score")
    private String propensityScore;
    @SerializedName("show_other_contacts")
    private char showOtherContacts;
    @SerializedName("alt_phone_number1")
    private String altPhoneNumber1;
    @SerializedName("alt_phone_number2")
    private String altPhoneNumber2;
    @SerializedName("other_contacts")
    private String otherContacts;
    @SerializedName("last_remarks")
    private String remarks;
    @SerializedName("fk_contact_mode_type_id")
    private int contactMode;
    @SerializedName("is_approved_multi_payment")
    private String multiPayment;
    @SerializedName("res_post_code")
    private String postalCode;
    @SerializedName("collected_amount")
    private String collectedAmount;

    public CaseData() {
    }

    public CaseData(String pan, String caseUuid, String userName, String dueAmount, String emiAmount,
                    String assignedTo, int statusCode, String statusName, String odPrincipal,
                    String partnerName, String phoneNumber, String portfolioId, String customerName,
                    String email, String employerName, String modifiedDate, String offCityName,
                    String retraCaseId, String partnerCaseId, String nextActionDate,
                    String partnerBucketNumber, String propensityScore, char showOtherContacts,
                    String altPhoneNumber1, String altPhoneNumber2,String otherContacts,
                    String remarks, int contactMode, String multiPayment, String postalCode,
                    String collectedAmount) {
        this.pan = pan;
        this.caseUuid = caseUuid;
        this.userName = userName;
        this.dueAmount = dueAmount;
        this.emiAmount = emiAmount;
        this.assignedTo = assignedTo;
        this.statusCode = statusCode;
        this.statusName = statusName;
        this.odPrincipal = odPrincipal;
        this.partnerName = partnerName;
        this.phoneNumber = phoneNumber;
        this.portfolioId = portfolioId;
        this.customerName = customerName;
        this.email = email;
        this.employerName = employerName;
        this.modifiedDate = modifiedDate;
        this.offCityName = offCityName;
        this.retraCaseId = retraCaseId;
        this.partnerCaseId = partnerCaseId;
        this.nextActionDate = nextActionDate;
        this.partnerBucketNumber = partnerBucketNumber;
        this.propensityScore = propensityScore;
        this.showOtherContacts = showOtherContacts;
        this.altPhoneNumber1 = altPhoneNumber1;
        this.altPhoneNumber2 = altPhoneNumber2;
        this.otherContacts = otherContacts;
        this.remarks = remarks;
        this.contactMode = contactMode;
        this.multiPayment = multiPayment;
        this.postalCode = postalCode;
        this.collectedAmount = collectedAmount;
    }

    public String getPan() {
        return pan;
    }

    public void setPan(String pan) {
        this.pan = pan;
    }

    public String getCaseUuid() {
        return caseUuid;
    }

    public void setCaseUuid(String caseUuid) {
        this.caseUuid = caseUuid;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getDueAmount() {
        return dueAmount;
    }

    public void setDueAmount(String dueAmount) {
        this.dueAmount = dueAmount;
    }

    public String getAssignedTo() {
        return assignedTo;
    }

    public void setAssignedTo(String assignedTo) {
        this.assignedTo = assignedTo;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public String getStatusName() {
        return statusName;
    }

    public void setStatusName(String statusName) {
        this.statusName = statusName;
    }

    public String getOdPrincipal() {
        return odPrincipal;
    }

    public void setOdPrincipal(String odPrincipal) {
        this.odPrincipal = odPrincipal;
    }

    public String getPartnerName() {
        return partnerName;
    }

    public void setPartnerName(String partnerName) {
        this.partnerName = partnerName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getPortfolioId() {
        return portfolioId;
    }

    public void setPortfolioId(String portfolioId) {
        this.portfolioId = portfolioId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getEmployerName() {
        return employerName;
    }

    public void setEmployerName(String employerName) {
        this.employerName = employerName;
    }

    public String getModifiedDate() {
        return modifiedDate;
    }

    public void setModifiedDate(String modifiedDate) {
        this.modifiedDate = modifiedDate;
    }

    public String getOffCityName() {
        return offCityName;
    }

    public void setOffCityName(String offCityName) {
        this.offCityName = offCityName;
    }

    public String getRetraCaseId() {
        return retraCaseId;
    }

    public void setRetraCaseId(String retraCaseId) {
        this.retraCaseId = retraCaseId;
    }

    public String getPartnerCaseId() {
        return partnerCaseId;
    }

    public void setPartnerCaseId(String partnerCaseId) {
        this.partnerCaseId = partnerCaseId;
    }

    public String getNextActionDate() {
        return nextActionDate;
    }

    public void setNextActionDate(String nextActionDate) {
        this.nextActionDate = nextActionDate;
    }

    public String getPartnerBucketNumber() {
        return partnerBucketNumber;
    }

    public void setPartnerBucketNumber(String partnerBucketNumber) {
        this.partnerBucketNumber = partnerBucketNumber;
    }

    public String getPropensityScore() {
        return propensityScore;
    }

    public void setPropensityScore(String propensityScore) {
        this.propensityScore = propensityScore;
    }

    public String getOtherContacts() {
        return otherContacts;
    }

    public void setOtherContacts(String otherContacts) {
        this.otherContacts = otherContacts;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public int getContactMode() {
        return contactMode;
    }

    public void setContactMode(int contactMode) {
        this.contactMode = contactMode;
    }

    public String getEmiAmount() {
        return emiAmount;
    }

    public void setEmiAmount(String emiAmount) {
        this.emiAmount = emiAmount;
    }

    public String getMultiPayment() {
        return multiPayment;
    }

    public void setMultiPayment(String multiPayment) {
        this.multiPayment = multiPayment;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public String getCollectedAmount() {
        return collectedAmount;
    }

    public void setCollectedAmount(String collectedAmount) {
        this.collectedAmount = collectedAmount;
    }

    public char getShowOtherContacts() {
        return showOtherContacts;
    }

    public void setShowOtherContacts(char showOtherContacts) {
        this.showOtherContacts = showOtherContacts;
    }

    public String getAltPhoneNumber1() {
        return altPhoneNumber1;
    }

    public void setAltPhoneNumber1(String altPhoneNumber1) {
        this.altPhoneNumber1 = altPhoneNumber1;
    }

    public String getAltPhoneNumber2() {
        return altPhoneNumber2;
    }

    public void setAltPhoneNumber2(String altPhoneNumber2) {
        this.altPhoneNumber2 = altPhoneNumber2;
    }
}