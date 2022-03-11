package com.loan.recovery.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.gson.Gson;
import com.loan.recovery.LoanApplication;
import com.loan.recovery.R;
import com.loan.recovery.retrofit.ApiClient;
import com.loan.recovery.retrofit.ApiInterface;
import com.loan.recovery.retrofit.model.AirtelPhone;
import com.loan.recovery.retrofit.model.AirtelResponse;
import com.loan.recovery.retrofit.model.Partner;
import com.loan.recovery.retrofit.model.PartnerCallResponse;
import com.loan.recovery.retrofit.model.PaymentType;
import com.loan.recovery.retrofit.model.PaymentTypesResponse;
import com.loan.recovery.retrofit.model.SignInResponse;
import com.loan.recovery.retrofit.model.Status;
import com.loan.recovery.retrofit.model.StatusCallResponse;
import com.loan.recovery.retrofit.model.UserData;
import com.loan.recovery.retrofit.model.UserPhoneResponse;
import com.loan.recovery.util.Utils;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SplashActivity extends AppCompatActivity {

    private LoanApplication application;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP_MR1)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        int permissionSum = ContextCompat.checkSelfPermission(
                this, Manifest.permission.READ_PHONE_STATE)
                + ContextCompat.checkSelfPermission(
                this, Manifest.permission.RECORD_AUDIO)
                + ContextCompat.checkSelfPermission(
                this, Manifest.permission.READ_EXTERNAL_STORAGE)
                + ContextCompat.checkSelfPermission(
                this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                + ContextCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_FINE_LOCATION)
                + ContextCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_COARSE_LOCATION);

//        loadHomeScreen();

        if (!Utils.isGPSEnabled(this)) {
            Toast.makeText(this, "Enable GPS to continue", Toast.LENGTH_LONG).show();
            loadHomeScreen();
        } else {
            if (permissionSum
                    != PackageManager.PERMISSION_GRANTED) {
                loadHomeScreen();
            } else {
                application = LoanApplication.getInstance();
                String email = Utils.getValueFromPreferences(SplashActivity.this, "email");
                String password = Utils.getValueFromPreferences(SplashActivity.this, "password");
                if (email != null && password != null) {
                    if (Utils.isConnected(this)) {
                        ApiInterface apiService =
                                ApiClient.getClient(this, 1).create(ApiInterface.class);

                        Call<SignInResponse> call = apiService.loginUser(email, password);
                        call.enqueue(new SignInCallBack());
                    } else
                        loadHomeScreen();
                } else
                    loadHomeScreen();
            }
        }
    }

    private void callOtherApis(String userId) {
        ApiInterface apiService =
                ApiClient.getClient(this, 1).create(ApiInterface.class);
//        Call<StatusCallResponse> call = apiService.getStatusList("0", "26");
//        call.enqueue(new StatusCallBack());

        Call<StatusCallResponse> call = apiService.getModeStatusList(11);
        call.enqueue(new StatusCallBack());

        Call<StatusCallResponse> call4 = apiService.getModeStatusList(12);
        call4.enqueue(new StatusCallBack2());

        Call<PartnerCallResponse> call2 = apiService.getPartnersList();
        call2.enqueue(new PartnersCallBack());

        Call<PaymentTypesResponse> call3 = apiService.getPaymentTypes();
        call3.enqueue(new PaymentTypesCallBack());

        Call<UserPhoneResponse> call5 = apiService.getPhoneNumber(userId);
        call5.enqueue(new PhoneNumberCallBack());

//        Call<AirtelResponse> call6 = apiService.getPhoneNumbersList();
//        call6.enqueue(new AirtelPhoneNumberCallBack());

//        ApiInterface apiService2 =
//                ApiClient.getClient(this, 4).create(ApiInterface.class);
    }

//    class AirtelPhoneNumberCallBack implements Callback<AirtelResponse> {
//        @Override
//        public void onResponse(Call<AirtelResponse> call, Response<AirtelResponse> response) {
//            System.out.println("<=== PhoneNumberCallBack - Response : ===> " + new Gson().toJson(response.body()));
//            AirtelResponse userPhoneResponse = response.body();
//            assert userPhoneResponse != null;
//            application.setPhoneNumbersList(userPhoneResponse.getAirtelPhones());
//        }
//
//        @Override
//        public void onFailure(Call<AirtelResponse> call, Throwable t) {
//            System.out.println("<=== PhoneNumberCallBack - Response : ===> " + t.getMessage());
//        }
//    }

    class PhoneNumberCallBack implements Callback<UserPhoneResponse> {
        @Override
        public void onResponse(Call<UserPhoneResponse> call, Response<UserPhoneResponse> response) {
            System.out.println("<=== PhoneNumberCallBack - Response : ===> " + new Gson().toJson(response.body()));
            UserPhoneResponse userPhoneResponse = response.body();
            assert userPhoneResponse != null;
            String phone = userPhoneResponse.getUserPhone().get(0).getUserPhone();
            char isGpsTracking = userPhoneResponse.getUserPhone().get(0).getIsTrackGps();
            char isDirectCall = userPhoneResponse.getUserPhone().get(0).getIsDirectCall();
            String callOperator = userPhoneResponse.getUserPhone().get(0).getIspName();
            int virtuoId = userPhoneResponse.getUserPhone().get(0).getVirtuoId();
            AirtelPhone aiqPhone = new AirtelPhone(userPhoneResponse.getUserPhone().get(0).getAiqNumber());
            List<AirtelPhone> aiqPhnList = new ArrayList<AirtelPhone>();
            aiqPhnList.add(aiqPhone);

            AirtelResponse airtelResponse = new AirtelResponse(aiqPhnList);

            application.setAgentPhoneNumber(phone);
            application.setIsTrackingGps(isGpsTracking);
            application.setIsDirectCall(isDirectCall);
            application.setPhoneNumbersList(airtelResponse.getAirtelPhones());
            application.setCallingOperator(callOperator);
            application.setVirtuoId(virtuoId);
        }

        @Override
        public void onFailure(Call<UserPhoneResponse> call, Throwable t) {
            System.out.println("<=== PhoneNumberCallBack - Response : ===> " + t.getMessage());
        }
    }

    class PartnersCallBack implements Callback<PartnerCallResponse> {
        @Override
        public void onResponse(Call<PartnerCallResponse> call, Response<PartnerCallResponse> response) {
            System.out.println("<=== PartnersCallBack - Response : ===> " + new Gson().toJson(response.body()));
            PartnerCallResponse partnerCallResponse = response.body();
            assert partnerCallResponse != null;
            List<Partner> partners = partnerCallResponse.getPartnersList();
            Partner partner = new Partner();
            partner.setPartnerName("Select Partner");
            partner.setPartnerUuid("NA");
            partners.add(0, partner);
            application.setPartners(partners);
        }

        @Override
        public void onFailure(Call<PartnerCallResponse> call, Throwable t) {
            System.out.println("<=== PartnersCallBack - Response : ===> " + t.getMessage());
        }
    }

    class PaymentTypesCallBack implements Callback<PaymentTypesResponse> {
        @Override
        public void onResponse(Call<PaymentTypesResponse> call, Response<PaymentTypesResponse> response) {
            System.out.println("<=== PaymentTypesCallBack - Response : ===> " + new Gson().toJson(response.body()));
            PaymentTypesResponse paymentTypesResponse = response.body();
            List<PaymentType> partners = paymentTypesResponse.getPaymentTypeList();
            PaymentType partner = new PaymentType();
            partner.setPaymentTypeName("Select Payment Type");
            partner.setPaymentTypeId(0000);
            partners.add(0, partner);
            application.setPaymentTypes(partners);
        }

        @Override
        public void onFailure(Call<PaymentTypesResponse> call, Throwable t) {
            System.out.println("<=== PaymentTypesCallBack - Response : ===> " + t.getMessage());
        }
    }

    class StatusCallBack implements Callback<StatusCallResponse> {
        @Override
        public void onResponse(Call<StatusCallResponse> call, Response<StatusCallResponse> response) {
            System.out.println("<=== StatusCallBack - Response : ===> " + new Gson().toJson(response.body()));
            StatusCallResponse statusCallResponse = response.body();
            assert statusCallResponse != null;
            List<Status> statusList = statusCallResponse.getStatusList();
            Status status = new Status();
            status.setStatusName("Select Status");
            status.setStatusCode(0000);

            statusList.add(0, status);
            application.setPhoneStatusList(statusList);
        }

        @Override
        public void onFailure(Call<StatusCallResponse> call, Throwable t) {
            System.out.println("<=== StatusCallBack - Response : ===> " + t.getMessage());
        }
    }

    class StatusCallBack2 implements Callback<StatusCallResponse> {
        @Override
        public void onResponse(Call<StatusCallResponse> call, Response<StatusCallResponse> response) {
            System.out.println("<=== StatusCallBack - Response : ===> " + new Gson().toJson(response.body()));
            StatusCallResponse statusCallResponse = response.body();
            assert statusCallResponse != null;
            List<Status> statusList = statusCallResponse.getStatusList();

            Status status = new Status();
            status.setStatusName("Select Status");
            status.setStatusCode(0000);

            statusList.add(0, status);
            application.setFieldStatusList(statusList);
        }

        @Override
        public void onFailure(Call<StatusCallResponse> call, Throwable t) {
            System.out.println("<=== StatusCallBack - Response : ===> " + t.getMessage());
        }
    }

    class SignInCallBack implements Callback<SignInResponse> {
        @Override
        public void onResponse(Call<SignInResponse> call, Response<SignInResponse> response) {
            StringBuilder result = new StringBuilder();
            result.append("SignIn");
            SignInResponse apiResult = response.body();
            // added - duplicate - updated
            if (apiResult != null && apiResult.getMessage().equals("success")) {
                Utils.storeUserDataInApplication(apiResult.getData());
                callOtherApis(apiResult.getData().getUserId());

                Utils.storeUserDataInApplication(apiResult.getData());
                Utils.storeKeyInPreferences(SplashActivity.this, application.getCurrentUser(),16);
                Intent intent = new Intent(SplashActivity.this, HomeActivity.class);
                startActivity(intent);
                finish();
            } else {
                loadHomeScreen();
                Utils.clearDataOnLogout(SplashActivity.this);
            }
        }

        @Override
        public void onFailure(Call<SignInResponse> call, Throwable t) {
            Utils.clearDataOnLogout(SplashActivity.this);
            loadHomeScreen();
        }
    }

    private void loadHomeScreen() {
        new Handler().postDelayed(() -> {
            Utils.clearDataOnLogout(SplashActivity.this);
            Intent intent = new Intent(getApplicationContext(), SignInActivity.class);
            startActivity(intent);
            finish();
        }, 2000);
    }
}
