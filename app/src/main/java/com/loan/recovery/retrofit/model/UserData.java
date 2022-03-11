package com.loan.recovery.retrofit.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by mjonnakuti on 25/10/2020.
 */

public class UserData {

    @SerializedName("lu_user_id")
    private String userId;
    @SerializedName("user_uuid")
    private String userUuid;
    @SerializedName("fk_partner_id")
    private String partnerId;
    @SerializedName("first_name")
    private String firstName;
    @SerializedName("last_name")
    private String lastName;
    @SerializedName("user_email_id")
    private String userEmailId;
    @SerializedName("user_password")
    private String userPassword;
    @SerializedName("partner_uuid")
    private String partnerUuid;
    @SerializedName("partner_name")
    private String partnerName;
    @SerializedName("rolesCount")
    private int rolesCount;
    @SerializedName("roles")
    private Roles roles;

    public UserData() {
    }

    public String getUserUuid() {
        return userUuid;
    }

    public void setUserUuid(String userUuid) {
        this.userUuid = userUuid;
    }

    public String getPartnerId() {
        return partnerId;
    }

    public void setPartnerId(String partnerId) {
        this.partnerId = partnerId;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getUserEmailId() {
        return userEmailId;
    }

    public void setUserEmailId(String userEmailId) {
        this.userEmailId = userEmailId;
    }

    public String getUserPassword() {
        return userPassword;
    }

    public void setUserPassword(String userPassword) {
        this.userPassword = userPassword;
    }

    public String getPartnerUuid() {
        return partnerUuid;
    }

    public void setPartnerUuid(String partnerUuid) {
        this.partnerUuid = partnerUuid;
    }

    public String getPartnerName() {
        return partnerName;
    }

    public void setPartnerName(String partnerName) {
        this.partnerName = partnerName;
    }

    public int getRolesCount() {
        return rolesCount;
    }

    public void setRolesCount(int rolesCount) {
        this.rolesCount = rolesCount;
    }

    public Roles getRoles() {
        return roles;
    }

    public void setRoles(Roles roles) {
        this.roles = roles;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}