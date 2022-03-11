package com.loan.recovery.retrofit.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by mjonnakuti on 25/10/2020.
 */

public class Roles {
    @SerializedName("lu_user_role_id")
    private String userRoleId;
    @SerializedName("fk_user_id")
    private String userId;
    @SerializedName("fk_role_id")
    private String roleId;
    @SerializedName("is_active")
    private String isActive;
    @SerializedName("created_date")
    private String createdDate;
    @SerializedName("role_name")
    private String roleName;
    @SerializedName("role_desc")
    private String roleDesc;

    public Roles() {
    }

    public Roles(String userRoleId, String userId, String roleId, String isActive, String createdDate, String roleName, String roleDesc) {
        this.userRoleId = userRoleId;
        this.userId = userId;
        this.roleId = roleId;
        this.isActive = isActive;
        this.createdDate = createdDate;
        this.roleName = roleName;
        this.roleDesc = roleDesc;
    }

    public String getUserRoleId() {
        return userRoleId;
    }

    public void setUserRoleId(String userRoleId) {
        this.userRoleId = userRoleId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getRoleId() {
        return roleId;
    }

    public void setRoleId(String roleId) {
        this.roleId = roleId;
    }

    public String getIsActive() {
        return isActive;
    }

    public void setIsActive(String isActive) {
        this.isActive = isActive;
    }

    public String getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(String createdDate) {
        this.createdDate = createdDate;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public String getRoleDesc() {
        return roleDesc;
    }

    public void setRoleDesc(String roleDesc) {
        this.roleDesc = roleDesc;
    }
}