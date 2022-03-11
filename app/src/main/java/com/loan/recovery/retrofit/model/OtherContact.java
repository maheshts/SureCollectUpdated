package com.loan.recovery.retrofit.model;

import com.google.gson.annotations.SerializedName;

public class OtherContact {

    @SerializedName("fk_contact_type_id")
    private int contactTypeId;

    @SerializedName("contact_type_name")
    private String contactTypeName;

    @SerializedName("contact_full_name")
    private String contactName;

    @SerializedName("phone_number")
    private String phoneNumber;

    public OtherContact(int contactTypeId, String contactTypeName, String contactName, String phoneNumber) {
        this.contactTypeId = contactTypeId;
        this.contactTypeName = contactTypeName;
        this.contactName = contactName;
        this.phoneNumber = phoneNumber;
    }

    public OtherContact() {
    }

    public int getContactTypeId() {
        return contactTypeId;
    }

    public void setContactTypeId(int contactTypeId) {
        this.contactTypeId = contactTypeId;
    }

    public String getContactTypeName() {
        return contactTypeName;
    }

    public void setContactTypeName(String contactTypeName) {
        this.contactTypeName = contactTypeName;
    }

    public String getContactName() {
        return contactName;
    }

    public void setContactName(String contactName) {
        this.contactName = contactName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    @Override
    public String toString() {
        return "OtherContact{" +
                "contactTypeId=" + contactTypeId +
                ", contactTypeName='" + contactTypeName + '\'' +
                ", contactName='" + contactName + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                '}';
    }
}
