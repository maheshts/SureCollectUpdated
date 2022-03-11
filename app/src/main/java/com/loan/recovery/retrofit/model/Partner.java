package com.loan.recovery.retrofit.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by mjonnakuti on 25/11/2020.
 */

public class Partner extends BaseResponse {

    @SerializedName("lu_partner_id")
    private int partnerId;
    @SerializedName("partner_uuid")
    private String partnerUuid;
    @SerializedName("partner_name")
    private String partnerName;
    @SerializedName("contact_email")
    private String contactEmail;
    @SerializedName("partner_display_name")
    private String partnerDisplayName;
    @SerializedName("fk_entity_id")
    private int entityId;
    @SerializedName("is_active")
    private String isActive;
    @SerializedName("created_date")
    private String createdDate;

    public Partner() {
    }

    public Partner(int partnerId, String partnerUuid, String partnerName, String contactEmail, String partnerDisplayName, int entityId, String isActive, String createdDate) {
        this.partnerId = partnerId;
        this.partnerUuid = partnerUuid;
        this.partnerName = partnerName;
        this.contactEmail = contactEmail;
        this.partnerDisplayName = partnerDisplayName;
        this.entityId = entityId;
        this.isActive = isActive;
        this.createdDate = createdDate;
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

    public String getContactEmail() {
        return contactEmail;
    }

    public void setContactEmail(String contactEmail) {
        this.contactEmail = contactEmail;
    }

    public String getPartnerDisplayName() {
        return partnerDisplayName;
    }

    public void setPartnerDisplayName(String partnerDisplayName) {
        this.partnerDisplayName = partnerDisplayName;
    }

    public int getEntityId() {
        return entityId;
    }

    public void setEntityId(int entityId) {
        this.entityId = entityId;
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

    public int getPartnerId() {
        return partnerId;
    }

    public void setPartnerId(int partnerId) {
        this.partnerId = partnerId;
    }

    @Override
    public String toString() {
        return partnerName;
    }
}