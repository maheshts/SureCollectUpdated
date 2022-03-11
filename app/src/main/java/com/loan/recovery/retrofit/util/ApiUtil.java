package com.loan.recovery.retrofit.util;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.loan.recovery.util.Utils;

import java.io.File;

public class ApiUtil {

    public static JsonObject getCallSessionObject(String caseId, String portfolioId, String customerPhone,
                                                  String agentPhone, String airtelIqPhone, String userTransKey) {

        JsonObject object = new JsonObject();
        object.addProperty("caseId", caseId);
        object.addProperty("portfolioId", portfolioId);
        object.addProperty("customerPhone", customerPhone);
        object.addProperty("agentPhone", agentPhone);
        object.addProperty("airtelIqPhone", airtelIqPhone);
        object.addProperty("userTransKey", userTransKey);

        return object;
    }

    public static JsonArray getPaymentFileObject(File file) {
        JsonArray array = new JsonArray();
        JsonObject object = new JsonObject();
        object.addProperty("uploadDate", Utils.getDateTimeForPaymentFile());
        object.addProperty("image", file.getName());
        object.addProperty("fileType", 11);
        array.add(object);
        return array;
    }

    public static JsonObject getStatusObject(String caseUuid, int statusCode, String remarks,
                                             String phone, String userTransKey,
                                             String isPaymentDone, double amount, String transRef, int paymentType,
                                             String nextActionDate, String nextFollowUpDate, String followUpReq) {
        JsonObject object = new JsonObject();
        object.addProperty("case_uuid", caseUuid);
        object.addProperty("contactMode", 11);
        object.addProperty("actionStatusCode", statusCode);
        object.addProperty("remarks", remarks);
        object.addProperty("contactPerson", phone);
        object.addProperty("userTransKey", userTransKey);
        object.addProperty("isPaymentDone", isPaymentDone);
        object.addProperty("isPTP", "N");

        JsonObject payment = new JsonObject();
        payment.addProperty("paymentAmount", amount + "");
        payment.addProperty("transRef", transRef);
        payment.addProperty("paymentType", paymentType);
        payment.addProperty("paymentFilePath", "image.png");
        object.add("paymentDetails", payment);
        object.addProperty("nextActionDate", nextActionDate);
        object.addProperty("nextFollowupDate", nextFollowUpDate);
        object.addProperty("followupRequired", followUpReq);
        return object;
    }

    public static JsonObject getPTPStatusObject(String caseUuid, int statusCode, String remarks,
                                                String phone, String userTransKey,
                                                String isPaymentDone, double amount, String transRef, int paymentType,
                                                String nextActionDate, String nextFollowUpDate, String followUpReq) {
        JsonObject object = new JsonObject();
        object.addProperty("case_uuid", caseUuid);
        object.addProperty("contactMode", 11);
        object.addProperty("actionStatusCode", statusCode);
        object.addProperty("remarks", remarks);
        object.addProperty("contactPerson", phone);
        object.addProperty("userTransKey", userTransKey);
        object.addProperty("isPaymentDone", isPaymentDone);
        object.addProperty("isPTP", "Y");

        JsonObject payment = new JsonObject();
        payment.addProperty("ptpAmount", amount);
        payment.addProperty("ptpDate", nextActionDate);
        object.add("ptpDetails", payment);
        object.addProperty("nextActionDate", nextActionDate);
        object.addProperty("nextFollowupDate", nextFollowUpDate);
        object.addProperty("followupRequired", followUpReq);
        return object;
    }

    public static JsonObject getCaseMetaDataObject(String caseUuid, String agreementId, int partnerId,
                                                   String callStartTime, String callEndTime, String phone,
                                                   String agentPhone, String userTransKey, String fileName) {
        JsonObject object = new JsonObject();
        object.addProperty("caseId", caseUuid);
        object.addProperty("agreementId", agreementId);
        object.addProperty("partnerId", "" + partnerId);
        object.addProperty("callStartTime", callStartTime);
        object.addProperty("contactPhone", phone);
        object.addProperty("callEndTime", callEndTime);
        object.addProperty("agentPhone", agentPhone); // Change the agent number
        object.addProperty("userTransKey", userTransKey);
        object.addProperty("filePath", fileName);
        return object;
    }

    public static JsonObject getLatLngDataObject(String caseUuid, double latitude, double longitude,
                                                 String pincode, String address1, String sub_locality,
                                                 String locality, String transKey, String pageId,
                                                 String statusCode, String snapRefId) {
        JsonObject object = new JsonObject();
        object.addProperty("caseId", caseUuid);
        object.addProperty("latitude", latitude + "");
        object.addProperty("longitude", longitude + "");
        object.addProperty("pincode", "" + pincode);
        object.addProperty("address1", address1);
        object.addProperty("sub_locality", sub_locality);
        object.addProperty("locality", locality);
        object.addProperty("userTransKey", transKey);
        object.addProperty("pageId", pageId);
        object.addProperty("statusCode", statusCode);
        object.addProperty("snapRefId", snapRefId);
        return object;
    }

    public static JsonObject getJobStartDataObject(double latitude, double longitude, String transKey, int eventId) {
        JsonObject object = new JsonObject();
        object.addProperty("latitude", latitude + "");
        object.addProperty("longitude", longitude + "");
        object.addProperty("userTransKey", transKey);
        object.addProperty("fkEventId",eventId);
        return object;
    }

    public static JsonObject getSaveEventObject(JsonObject objectData){
        JsonObject object = new JsonObject();
        object.addProperty("userId", objectData.has("userId")?objectData.get("userId").getAsString():"0");
        object.addProperty("fkEventId", objectData.has("eventId")?objectData.get("eventId").getAsInt():0);
        object.addProperty("statusMessage", objectData.has("message")?objectData.get("message").getAsString():"");
        object.addProperty("sourceModel", objectData.has("sourceModel")?objectData.get("sourceModel").getAsString():"");
        object.addProperty("appVersion", objectData.has("appVersion")?objectData.get("appVersion").getAsString():"");
        return object;
    }
}
