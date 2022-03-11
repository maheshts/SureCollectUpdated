package com.loan.recovery;

import android.app.Application;
import android.content.Context;
import android.location.Location;

import com.loan.recovery.retrofit.model.AirtelPhone;
import com.loan.recovery.retrofit.model.Partner;
import com.loan.recovery.retrofit.model.PaymentType;
import com.loan.recovery.retrofit.model.Status;
import com.loan.recovery.retrofit.model.UserData;
import com.loan.recovery.retrofit.model.UserPhone;

import org.acra.ACRA;

import java.util.List;

public class LoanApplication extends Application {

    private UserData currentUser;
    private static LoanApplication application;
    private List<Partner> partners;
    private List<PaymentType> paymentTypes;
//    private List<Status> statusList;
    private List<Status> phoneStatusList;
    private String agentPhoneNumber;
    private List<Status> fieldStatusList;
    private List<AirtelPhone> phoneNumbersList;
    private Context context;
    private Location currentLocation = null;
    private char isTrackingGps;
    private String callingOperator;
    private int virtuoId;
    private String caseId;
    private String metaData;
    private char isDirectCall='N';

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        if(!BuildConfig.DEBUG)
            ACRA.init(this);
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    public static LoanApplication getInstance() {
        if (application == null)
            application = new LoanApplication();
        return application;
    }

    public UserData getCurrentUser() {
        return currentUser;
    }

    public void setCurrentUser(UserData currentUser) {
        this.currentUser = currentUser;
    }

    public List<Partner> getPartners() {
        return partners;
    }

    public void setPartners(List<Partner> partners) {
        this.partners = partners;
    }

//    public List<Status> getStatusList() {
//        return statusList;
//    }
//
//    public void setStatusList(List<Status> statusList) {
//        this.statusList = statusList;
//    }

    public List<PaymentType> getPaymentTypes() {
        return paymentTypes;
    }

    public void setPaymentTypes(List<PaymentType> paymentTypes) {
        this.paymentTypes = paymentTypes;
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public String getCaseId() {
        return caseId;
    }

    public void setCaseId(String caseId) {
        this.caseId = caseId;
    }

    public String getMetaData() {
        return metaData;
    }

    public void setMetaData(String metaData) {
        this.metaData = metaData;
    }

    public Location getCurrentLocation() {
        return currentLocation;
    }

    public void setCurrentLocation(Location currentLocation) {
        this.currentLocation = currentLocation;
    }

    public List<Status> getPhoneStatusList() {
        return phoneStatusList;
    }

    public void setPhoneStatusList(List<Status> phoneStatusList) {
        this.phoneStatusList = phoneStatusList;
    }

    public List<Status> getFieldStatusList() {
        return fieldStatusList;
    }

    public void setFieldStatusList(List<Status> fieldStatusList) {
        this.fieldStatusList = fieldStatusList;
    }

    public List<AirtelPhone> getPhoneNumbersList() {
        return phoneNumbersList;
    }

    public void setPhoneNumbersList(List<AirtelPhone> phoneNumbersList) {
        this.phoneNumbersList = phoneNumbersList;
    }

    public String getAgentPhoneNumber() {
        return agentPhoneNumber;
    }

    public void setAgentPhoneNumber(String agentPhoneNumber) {
        this.agentPhoneNumber = agentPhoneNumber;
    }
    public char getIsTrackingGps() {
        return isTrackingGps;
    }

    public void setIsTrackingGps(char isTrackingGps) {
        this.isTrackingGps = isTrackingGps;
    }

    public String getCallingOperator() {
        return callingOperator;
    }

    public void setCallingOperator(String callingOperator) {
        this.callingOperator = callingOperator;
    }

    public int getVirtuoId() {
        return virtuoId;
    }

    public void setVirtuoId(int virtuoId) {
        this.virtuoId = virtuoId;
    }

    public char getIsDirectCall() {
        return isDirectCall;
    }

    public void setIsDirectCall(char isDirectCall) {
        this.isDirectCall = isDirectCall;
    }

    @Override
    public String toString() {
        return "LoanApplication{" +
                "currentUser=" + currentUser +
                ", partners=" + partners +
                ", paymentTypes=" + paymentTypes +
                ", phoneStatusList=" + phoneStatusList +
                ", agentPhoneNumber='" + agentPhoneNumber + '\'' +
                ", fieldStatusList=" + fieldStatusList +
                ", phoneNumbersList=" + phoneNumbersList +
                ", context=" + context +
                ", currentLocation=" + currentLocation +
                ", isTrackingGps=" + isTrackingGps +
                ", callingOperator='" + callingOperator + '\'' +
                ", virtuoId=" + virtuoId +
                ", caseId='" + caseId + '\'' +
                ", metaData='" + metaData + '\'' +
                ", isDirectCall=" + isDirectCall +
                '}';
    }
}