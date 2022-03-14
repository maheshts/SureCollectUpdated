package com.loan.recovery.retrofit;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.loan.recovery.retrofit.model.AirtelResponse;
import com.loan.recovery.retrofit.model.BaseResponse;
import com.loan.recovery.retrofit.model.CasesList;
import com.loan.recovery.retrofit.model.DayReportList;
import com.loan.recovery.retrofit.model.FileUploadResponse;
import com.loan.recovery.retrofit.model.NonRetraBaseResponse;
import com.loan.recovery.retrofit.model.NonRetraCaseRequest;
import com.loan.recovery.retrofit.model.NonRetraCasesResponse;
import com.loan.recovery.retrofit.model.OtherContactResponse;
import com.loan.recovery.retrofit.model.PartnerCallResponse;
import com.loan.recovery.retrofit.model.PaymentTypesResponse;
import com.loan.recovery.retrofit.model.Result;
import com.loan.recovery.retrofit.model.SignInResponse;
import com.loan.recovery.retrofit.model.StatusCallResponse;
import com.loan.recovery.retrofit.model.UserPhoneResponse;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Map;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.PartMap;
import retrofit2.http.Query;
import retrofit2.http.Url;

/**
 * Created by mjonnakuti on 15/03/2020.
 */

public interface ApiInterface {

    @FormUrlEncoded
    @POST("login")
    @Headers({"Content-Type: application/x-www-form-urlencoded"})
    Call<SignInResponse> loginUser(@Field("email") String email, @Field("password") String password);

    @FormUrlEncoded
    @POST("getcasesearchdata")
    @Headers({"Content-Type: application/x-www-form-urlencoded"})
    Call<CasesList> getCasesList(@Field("loggedin_user_id") String userId, @Field("loggedin_user_role_id") String rollId, @Field("start") String start,
                                 @Field("end") String end, @Field("contact_mode_type") String contactMode);
//https://surecollect.ai:9003/getLuCaseEduData
    @FormUrlEncoded
    @POST("getcasesearchdata")
    @Headers({"Content-Type: application/x-www-form-urlencoded"})
    Call<CasesList> getCasesList(@Field("loggedin_user_id") String userId,
                                 @Field("loggedin_user_role_id") String rollId,
                                 @Field("start") String start,
                                 @Field("end") String end,
                                 @Field("agreement_id") String agreementId,
                                 @Field("score") String score,
                                 @Field("Bucket") String Bucket,
                                 @Field("partner_uuid") String partnerUuid,
                                 @Field("phonenumber") String phonenumber,
                                 @Field("posamount") String posAmount,
                                 @Field("dueamount") String dueAmount,
                                 @Field("contactStatus") int status,
                                 @Field("contact_mode_type") String contactMode);
    //@FormUrlEncoded
    @POST("getLuCaseEduData")
    @Headers({"Content-Type: application/json"})
    Call<Object> getOtherCaseList(@Body JsonObject requestBody);
//    Call<CasesList> getCaseList(@Field("loggedinUserId") String userId,
//                                 @Field("loggedInRoleId") String rollId,
//                                 @Field("startRows") String start,
//                                 @Field("endRows") String end,
//                                 @Field("fkPartnerId") String partnerid,
//                                 @Field("phoneNumber") String phonenumber,
//                                 @Field("partnerCaseId") String partnercaseid,
//                                 @Field("firstName") String firstName,
//                                 @Field("lastName") String lastname,
//                                // @Field("agreement_id") String agreementId,
//                                 @Field("statusCode") String statuscode,
//                                 @Field("forExport") String forexport,
//                                 @Field("sortByColumn") String sortByColum,
//                                @Field("selectedUserId") String selecteduserid,
//                                 @Field("sortByOrder") String sortByorder);





    @FormUrlEncoded
    @POST("getcasesearchdata")
    @Headers({"Content-Type: application/x-www-form-urlencoded"})
    Call<CasesList> getCasesList(@Field("loggedin_user_id") String userId,
                                 @Field("loggedin_user_role_id") String rollId,
                                 @Field("start") String start,
                                 @Field("end") String end,
                                 @Field("agreement_id") String agreementId,
                                 @Field("score") String score,
                                 @Field("Bucket") String Bucket,
                                 @Field("partner_uuid") String partnerUuid,
                                 @Field("phonenumber") String phonenumber,
                                 @Field("posamount") String posAmount,
                                 @Field("dueamount") String dueAmount,
                                 @Field("contact_mode_type") String contactMode);

    @POST("updateCaseModifiedDate")
    @Headers({"Content-Type: application/json"})
    Call<BaseResponse> updateCaseModifiedDate(@Body JsonObject caseId);

    @POST("AddCallMetaData")
    @Headers({"Content-Type: application/x-www-form-urlencoded"})
    Call<BaseResponse> updateCaseMetaData(@Body JsonObject statusData);

    @POST("getparentlist")
    Call<PartnerCallResponse> getPartnersList();

    @FormUrlEncoded
    @POST("getContactModeDispositions")
    @Headers({"Content-Type: application/x-www-form-urlencoded"})
    Call<StatusCallResponse> getModeStatusList(@Field("contact_mode_type_id") int modeId);

    @FormUrlEncoded
    @POST("getOtherContactDetails")
    @Headers({"Content-Type: application/x-www-form-urlencoded"})
    Call<OtherContactResponse> getOtherContacts(@Field("caseId") int caseId);

    @FormUrlEncoded
    @POST("statuslist")
    @Headers({"Content-Type: application/x-www-form-urlencoded"})
    Call<StatusCallResponse> getStatusList(@Field("start") String start, @Field("end") String end);

    @POST("updateCaseStatus")
    @Headers({"Content-Type: application/x-www-form-urlencoded"})
    Call<BaseResponse> updateCaseStatus(@Body JsonObject statusData);

    @Headers({"Content-Type: application/x-www-form-urlencoded"})
    @POST("getPaymentTypes")
    Call<PaymentTypesResponse> getPaymentTypes();

    @Multipart
    @POST("saveRecording")
    Call<BaseResponse> saveRecording(@Part("fromNumber") int description,
                                     @Part MultipartBody.Part file);

    @Multipart
    @POST("saveCaseFiles")
    Call<BaseResponse> saveReceipt(@Part("case_uuid") int caseId, @Part("files_points") String filePoints,
                                   @Part MultipartBody.Part imagesArray);

    @Multipart
    @POST("saveCaseFiles")
    Call<FileUploadResponse> uploadFileWithPartMap(
            @Part MultipartBody.Part file,
            @Part("case_uuid") RequestBody caseId,
            @Part("files_points")   RequestBody filePoints);

    @POST("matchLatLongByPincode")
    @Headers({"Content-Type: application/json"})
    Call<BaseResponse> saveLatLong(@Body JsonObject latLongData);

    @POST("saveUserLocation")
    @Headers({"Content-Type: application/json"})
    Call<BaseResponse> startJob(@Body JsonObject latLongData);

    @POST("updateCallSetting")
    @Headers({"Content-Type: application/json"})
    Call<BaseResponse> updateDirectCall(@Body JsonObject latLongData);

    @POST("saveLocationEvent16")
    @Headers({"Content-Type: application/json"})
        //Call<BaseResponse> locationJob(@Body JSONArray latLongData);
    Call<BaseResponse> locationJob(@Body JsonObject latLongData);

    @POST("createCallSession")
    @Headers({"Content-Type: application/json"})
    Call<BaseResponse> createCallSession(@Body JsonObject latLongData);

    @POST("saveUserEvent")
    @Headers({"Content-Type: application/json"})
    Call<BaseResponse> saveUserEvent(@Body JsonObject latLongData);

    @FormUrlEncoded
    @POST("updateUserPhoneNumber")
    @Headers({"Content-Type: application/x-www-form-urlencoded"})
    Call<BaseResponse> updatePhoneNumber(@Field("userId") String userId, @Field("phoneNumber") String phoneNumber);

    @FormUrlEncoded
    @POST("getUserPhoneNumber")
    @Headers({"Content-Type: application/x-www-form-urlencoded"})
    Call<UserPhoneResponse> getPhoneNumber(@Field("userId") String userId);

    @GET("getAiqNumber")
    Call<AirtelResponse> getPhoneNumbersList();

    @FormUrlEncoded
    @POST("dayprogresscallsummary")
    @Headers({"Content-Type: application/x-www-form-urlencoded"})
    Call<DayReportList> getDayProgressReport(@Field("userId") String userId, @Field("progressDate") String progressDate);

    @GET
    Call<String> c2cCallingRequest(@Url String url);
}