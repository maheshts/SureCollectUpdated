package com.loan.recovery.activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.gson.Gson;
import com.loan.recovery.LoanApplication;
import com.loan.recovery.R;
import com.loan.recovery.databinding.ActivitySigninBinding;
import com.loan.recovery.retrofit.ApiClient;
import com.loan.recovery.retrofit.ApiInterface;
import com.loan.recovery.retrofit.model.AirtelPhone;
import com.loan.recovery.retrofit.model.AirtelResponse;
import com.loan.recovery.retrofit.model.Partner;
import com.loan.recovery.retrofit.model.PartnerCallResponse;
import com.loan.recovery.retrofit.model.ProjectConfigData;
import com.loan.recovery.retrofit.model.ConfigResponse;
import com.loan.recovery.retrofit.model.PaymentType;
import com.loan.recovery.retrofit.model.PaymentTypesResponse;
import com.loan.recovery.retrofit.model.ProjectData;
import com.loan.recovery.retrofit.model.ProjectTypesResponse;
import com.loan.recovery.retrofit.model.SignInResponse;
import com.loan.recovery.retrofit.model.Status;
import com.loan.recovery.retrofit.model.StatusCallResponse;
import com.loan.recovery.retrofit.model.UserPhoneResponse;
import com.loan.recovery.util.Utils;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP_MR1)
public class SignInActivity extends BaseActivity {

    private ActivitySigninBinding binding;
    private boolean isPermissionCheckOnSignIn = false;
    private static final int MY_PERMISSIONS_REQUEST_CODE = 1;
    private int permissionSum;
    private boolean rationale;
    private String[] permissions;
    private Context mContext;
    private TextView tvVersion;
    private int apistatuscounter = 0;
    private LoanApplication application;
    private String[] batteryPermission;
    private boolean isBatteryPermissionGrant = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySigninBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        application = LoanApplication.getInstance();
        tvVersion = (TextView) view.findViewById(R.id.tvVersion);
        tvVersion.setText(getResources().getText(R.string.app_version));
        binding.etEmail.setText("agent1.raymond");
        binding.etPassword.setText("Raymond@007");
        permissionSum = ContextCompat.checkSelfPermission(
                this, Manifest.permission.CALL_PHONE)
                + ContextCompat.checkSelfPermission(
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
//                + ContextCompat.checkSelfPermission(
//                this, Manifest.permission.ACCESS_BACKGROUND_LOCATION);

        rationale = ActivityCompat.shouldShowRequestPermissionRationale(
                this, Manifest.permission.CALL_PHONE)
                || ActivityCompat.shouldShowRequestPermissionRationale(
                this, Manifest.permission.READ_PHONE_STATE)
                || ActivityCompat.shouldShowRequestPermissionRationale(
                this, Manifest.permission.RECORD_AUDIO)
                || ActivityCompat.shouldShowRequestPermissionRationale(
                this, Manifest.permission.READ_EXTERNAL_STORAGE)
                || ActivityCompat.shouldShowRequestPermissionRationale(
                this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                || ActivityCompat.shouldShowRequestPermissionRationale(
                this, Manifest.permission.ACCESS_FINE_LOCATION)
                || ActivityCompat.shouldShowRequestPermissionRationale(
                this, Manifest.permission.ACCESS_COARSE_LOCATION);
//                || ActivityCompat.shouldShowRequestPermissionRationale(
//                this, Manifest.permission.ACCESS_BACKGROUND_LOCATION);

        permissions = new String[]{
                Manifest.permission.CALL_PHONE,
                Manifest.permission.READ_PHONE_STATE,
                Manifest.permission.RECORD_AUDIO,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
//                ,
//                Manifest.permission.ACCESS_BACKGROUND_LOCATION
        };

        batteryPermission = new String[]{
                Manifest.permission.ACCESS_BACKGROUND_LOCATION
        };

//        if (!Utils.isGPSEnabled(this)) {
//            buildAlertMessageNoGps();
//        } else
//            checkPermission();
    }

    private void validateUserData() {
        Utils.hideSoftKeyboard(this, binding.etPassword);
        String strEmail = binding.etEmail.getText().toString().trim();
        String strPassword = binding.etPassword.getText().toString().trim();

//        if (TextUtils.isEmpty(strEmail) || !Patterns.EMAIL_ADDRESS.matcher(strEmail).matches()) {
        if (TextUtils.isEmpty(strEmail)) {
            binding.etEmail.setError("User Id can't be empty");
            binding.etEmail.requestFocus();
        } else if (TextUtils.isEmpty(strPassword)) {
            binding.etPassword.setError("Password can't be empty");
            binding.etPassword.requestFocus();
        } else {
            try {
                if (Utils.isConnected(this)) {
                    showProgressDialog("Please Wait", "Validating data...");
                    ApiInterface apiService =
                            ApiClient.getClient(this, 1).create(ApiInterface.class);
                    Log.v("signin",apiService.toString());

//                Call<SignInResponse> call = apiService.loginUser("manoj.gupta@retrafinance.com", "Manoj@007");
                    Call<SignInResponse> call = apiService.loginUser(strEmail, strPassword);
                    call.enqueue(new SignInCallBack());
                } else
                    Toast.makeText(SignInActivity.this, "No Internet", Toast.LENGTH_SHORT).show();
            }catch (Exception e){
                Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    class SignInCallBack implements Callback<SignInResponse> {
        @Override
        public void onResponse(Call<SignInResponse> call, Response<SignInResponse> response) {
            hideProgressDialog();
            SignInResponse apiResult = response.body();
            // added - duplicate - updated
            Log.v("login","Response : "+response.toString());
            Log.v("login","getData : "+apiResult.getData().getPartnerId());
            if (apiResult != null && apiResult.getMessage().equals("success")) {
                Toast.makeText(SignInActivity.this, "Login successful", Toast.LENGTH_SHORT).show();
                String phoneNumber = binding.etEmail.getText().toString().trim();
                String pin = binding.etPassword.getText().toString().trim();

                callOtherApis(apiResult.getData().getUserId(),apiResult.getData().getPartnerId());

                Utils.storeUserDataInApplication(apiResult.getData());
                //Utils.storeCredentialsInPreferences(SignInActivity.this, phoneNumber, pin);
                Utils.storePatnerIDPreferences(SignInActivity.this, Integer.parseInt(apiResult.getData().getPartnerId()));

            } else {
                Toast.makeText(SignInActivity.this, "Login failed", Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        public void onFailure(Call<SignInResponse> call, Throwable t) {
            hideProgressDialog();
            Toast.makeText(SignInActivity.this, "Login Failed", Toast.LENGTH_SHORT).show();
        }
    }

    private void callOtherApis(String userId,String patnerid) {
        ApiInterface apiService =
                ApiClient.getClient(this, 1).create(ApiInterface.class);
//        Call<StatusCallResponse> call = apiService.getStatusList("0", "26");
//        call.enqueue(new StatusCallBack());
        //mContext.showProgressDialog("Please Wait", "Uploading Case file...");
        showProgressDialog("Please Wait", "Retrieving data...");

        apistatuscounter = 0;


        if(patnerid.equalsIgnoreCase("1024")) {
            Call<StatusCallResponse> callreal = apiService.getRealStateStatusList(1024, 15);
            callreal.enqueue(new RealStateStatusCallBack());
        }else{
            Call<StatusCallResponse> call = apiService.getModeStatusList(11);
            call.enqueue(new StatusCallBack());
        }

        Call<StatusCallResponse> call4 = apiService.getModeStatusList(12);
        call4.enqueue(new StatusCallBack2());

        Call<PartnerCallResponse> call2 = apiService.getPartnersList();
        call2.enqueue(new PartnersCallBack());

        Call<PaymentTypesResponse> call3 = apiService.getPaymentTypes();
        call3.enqueue(new PaymentTypesCallBack());


        Call<UserPhoneResponse> call5 = apiService.getPhoneNumber(userId);
        call5.enqueue(new PhoneNumberCallBack());

        if(patnerid.equalsIgnoreCase("1024")) {
            Call<ConfigResponse> call44 = apiService.getConfigData(1024);
            call44.enqueue(new RealConfigTypesCallBack());


            Call<ProjectTypesResponse> call45 = apiService.getProjects(1001);
            call45.enqueue(new ProjectTypesCallBack());
        }

//        Call<AirtelResponse> call6 = apiService.getPhoneNumbersList();
//        call6.enqueue(new AirtelPhoneNumberCallBack());

//        ApiInterface apiService2 =
//                ApiClient.getClient(this, 4).create(ApiInterface.class);
    }

//    class AirtelPhoneNumberCallBack implements Callback<AirtelResponse> {
//        @Override
//        public void onResponse(Call<AirtelResponse> call, Response<AirtelResponse> response) {
//            System.out.println("<=== PhoneNumberCallBack airtel- Response : ===> " + new Gson().toJson(response.body()));
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
            apistatuscounter = apistatuscounter+1;
            Log.v("apistatuscounter","Mahesh"+apistatuscounter);

            UserPhoneResponse userPhoneResponse = response.body();
            System.out.println("<=== PhoneNumberCallBack - Response : ===> Count "+apistatuscounter+ new Gson().toJson(response.body()));

            assert userPhoneResponse != null;
            Log.v("apistatuscounter","Mahesh"+userPhoneResponse);

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
//            if(isDirectCall=='Y'){
//
//            }
            application.setIsDirectCall(isDirectCall);
            application.setPhoneNumbersList(airtelResponse.getAirtelPhones());
//            //  application.setCallingOperator(callOperator);
//            LoanApplication.getInstance().callingOperator = callOperator;
//            // application.setVirtuoId(virtuoId);
//            Log.v("111","virtuoId"+virtuoId);
//            LoanApplication.getInstance().virtuoId  = virtuoId;
//            Log.v("111","virtuoId"+ LoanApplication.getInstance().virtuoId);
            if(apistatuscounter == 7){
                setData();
            }

        }

        @Override
        public void onFailure(Call<UserPhoneResponse> call, Throwable t) {
            System.out.println("<=== PhoneNumberCallBack - Response : ===> " + t.getMessage());
        }
    }

    class PaymentTypesCallBack implements Callback<PaymentTypesResponse> {
        @Override
        public void onResponse(Call<PaymentTypesResponse> call, Response<PaymentTypesResponse> response) {
            apistatuscounter = apistatuscounter+1;
            Log.v("apistatuscounter","Mahesh"+apistatuscounter);

            System.out.println("<=== PaymentTypesCallBack - Response : ===> Count "+apistatuscounter + new Gson().toJson(response.body()));
            PaymentTypesResponse paymentTypesResponse = response.body();
            List<PaymentType> partners = paymentTypesResponse.getPaymentTypeList();
            PaymentType partner = new PaymentType();
            partner.setPaymentTypeName("Select Payment Type");
            partner.setPaymentTypeId(0000);
            partners.add(0, partner);
            application.setPaymentTypes(partners);
            if(apistatuscounter == 7){
                setData();
            }
        }

        @Override
        public void onFailure(Call<PaymentTypesResponse> call, Throwable t) {
            System.out.println("<=== PaymentTypesCallBack - Response : ===> " + t.getMessage());
        }
    }
    class ProjectTypesCallBack implements Callback<ProjectTypesResponse> {
        @Override
        public void onResponse(Call<ProjectTypesResponse> call, Response<ProjectTypesResponse> response) {
            apistatuscounter = apistatuscounter+1;
            Log.v("apistatuscounter","Mahesh"+apistatuscounter);

            System.out.println("<=== PaymentTypesCallBack - Response : ===> Count "+apistatuscounter + new Gson().toJson(response.body()));
            ProjectTypesResponse paymentTypesResponse = response.body();
            List<ProjectData> partners = paymentTypesResponse.getPaymentTypeList();
            ProjectData partner = new ProjectData();
            partner.setProjectName("Select Project Type");
            partner.setProjectId(0000);
            partners.add(0, partner);
            application.setProjectTypes(partners);
//            if(apistatuscounter == 5){
//                setData();
//            }
        }

        @Override
        public void onFailure(Call<ProjectTypesResponse> call, Throwable t) {
            System.out.println("<=== PROJECT DATA - Response : ===> " + t.getMessage());
        }
    }

    class RealConfigTypesCallBack implements Callback<ConfigResponse> {
        @Override
        public void onResponse(Call<ConfigResponse> call, Response<ConfigResponse> response) {
            apistatuscounter = apistatuscounter+1;
            Log.v("apistatuscounter","Mahesh"+apistatuscounter);

            System.out.println("<=== Config - Response111 : ===>"+new Gson().toJson(response.body()));
            ConfigResponse paymentTypesResponse = response.body();
            Log.v("apistatuscounter","Mahesh"+response.body());

            List<ProjectConfigData> partners = paymentTypesResponse.getPaymentTypeList();
            ProjectConfigData partner = new ProjectConfigData();
            partner.setProjectName("Select Config Type");
            partner.setProjectId(0000);
            partners.add(0, partner);
            application.setConfigTypes(partners);

//            if(apistatuscounter == 5){
//                setData();
//            }
        }

        @Override
        public void onFailure(Call<ConfigResponse> call, Throwable t) {
            System.out.println("<=== PARTNER DATA - Response : ===> " + t.getMessage());
        }
    }

    class PartnersCallBack implements Callback<PartnerCallResponse> {
        @Override
        public void onResponse(Call<PartnerCallResponse> call, Response<PartnerCallResponse> response) {
            apistatuscounter = apistatuscounter +1;
            Log.v("apistatuscounter","Mahesh"+apistatuscounter);

            System.out.println("<=== PartnersCallBack - Response : ===> Count "+apistatuscounter + new Gson().toJson(response.body()));

            PartnerCallResponse partnerCallResponse = response.body();
            assert partnerCallResponse != null;
            List<Partner> partners = partnerCallResponse.getPartnersList();
            Partner partner = new Partner();
            partner.setPartnerName("Select Partner");
            partner.setPartnerUuid("NA");
            partners.add(0, partner);
            application.setPartners(partners);
            if(apistatuscounter == 7){
                setData();
            }
        }

        @Override
        public void onFailure(Call<PartnerCallResponse> call, Throwable t) {
            System.out.println("<=== PartnersCallBack - Response : ===> " + t.getMessage());
        }
    }

    class StatusCallBack implements Callback<StatusCallResponse> {
        @Override
        public void onResponse(Call<StatusCallResponse> call, Response<StatusCallResponse> response) {
            apistatuscounter = apistatuscounter + 1;
            Log.v("apistatuscounter","Mahesh"+apistatuscounter);

            System.out.println("<=== StatusCallBack - Response : ===> Count "+apistatuscounter+ new Gson().toJson(response.body()));
            Log.v("login","Mahesh"+new Gson().toJson(response.body()));
            //Toast.makeText(getApplicationContext(), ""+new Gson().toJson(response.body()), Toast.LENGTH_SHORT).show();
            //  System.out.println("<=== StatusCallBack -Login Response : ===> " + new Gson().toJson(response.body()));
            StatusCallResponse statusCallResponse = response.body();
            assert statusCallResponse != null;
            List<Status> statusList = statusCallResponse.getStatusList();

            Status status = new Status();
            status.setStatusName("Select Status");
            status.setStatusCode(0000);

            statusList.add(0, status);
            application.setPhoneStatusList(statusList);
            //startService(new Intent(getApplicationContext(), MyService()))
            // startService(new Intent(getApplicationContext(), MyService.class));
            if(apistatuscounter == 7){
                setData();
            }
        }

        @Override
        public void onFailure(Call<StatusCallResponse> call, Throwable t) {
            System.out.println("<=== StatusCallBack - Response : ===> " + t.getMessage());
        }
    }
    class RealStateStatusCallBack implements Callback<StatusCallResponse> {
        @Override
        public void onResponse(Call<StatusCallResponse> call, Response<StatusCallResponse> response) {
            apistatuscounter = apistatuscounter + 1;
            Log.v("apistatuscounter","Mahesh"+apistatuscounter);

            System.out.println("<=== StatusCallBack - Response : ===> Count "+apistatuscounter+ new Gson().toJson(response.body()));
            Log.v("login","Mahesh"+new Gson().toJson(response.body()));
            //Toast.makeText(getApplicationContext(), ""+new Gson().toJson(response.body()), Toast.LENGTH_SHORT).show();
            //  System.out.println("<=== StatusCallBack -Login Response : ===> " + new Gson().toJson(response.body()));
            StatusCallResponse statusCallResponse = response.body();
            assert statusCallResponse != null;
            List<Status> statusList = statusCallResponse.getStatusList();

            Status status = new Status();
            status.setStatusName("Select Status");
            status.setStatusCode(0000);

            statusList.add(0, status);
            application.setPhoneStatusList(statusList);
            //startService(new Intent(getApplicationContext(), MyService()))
            // startService(new Intent(getApplicationContext(), MyService.class));
            if(apistatuscounter == 7){
                setData();
            }
        }

        @Override
        public void onFailure(Call<StatusCallResponse> call, Throwable t) {
            System.out.println("<=== StatusCallBack - Response : ===> " + t.getMessage());
        }
    }
    void setData(){
        hideProgressDialog();
        Intent intent = new Intent(SignInActivity.this, HomeActivity.class);
        startActivity(intent);
        finish();
    }

    class StatusCallBack2 implements Callback<StatusCallResponse> {
        @Override
        public void onResponse(Call<StatusCallResponse> call, Response<StatusCallResponse> response) {
            apistatuscounter = apistatuscounter +1;
            Log.v("apistatuscounter","Mahesh"+apistatuscounter);

            System.out.println("<=== StatusCallBack - Response : ===>Count "+apistatuscounter + new Gson().toJson(response.body()));
            StatusCallResponse statusCallResponse = response.body();
            assert statusCallResponse != null;
            List<Status> statusList = statusCallResponse.getStatusList();

            Status status = new Status();
            status.setStatusName("Select Status");
            status.setStatusCode(0000);

            statusList.add(0, status);
            application.setFieldStatusList(statusList);
            if(apistatuscounter == 7){
                setData();
            }
        }

        @Override
        public void onFailure(Call<StatusCallResponse> call, Throwable t) {
            System.out.println("<=== StatusCallBack - Response : ===> " + t.getMessage());
        }
    }

    public void onClick(View v) {
        if (v.getId() == R.id.btSignIn) {
            isPermissionCheckOnSignIn = true;
            if (!Utils.isGPSEnabled(this)) {
                buildAlertMessageNoGps();
            }else
//                if(isBatteryPermissionGrant){
//                userBatteryPermissionReq();
//            }else
                checkPermission();

        }
    }

//    private void userBatteryPermissionReq() {
//        int out = ActivityCompat.checkSelfPermission(SignInActivity.this, batteryPermission[0]);
//        if(out!= PackageManager.PERMISSION_GRANTED) {
//            Toast.makeText(SignInActivity.this, "permission not granted", Toast.LENGTH_SHORT).show();
//            ActivityCompat.requestPermissions(SignInActivity.this, batteryPermission, MY_PERMISSIONS_REQUEST_CODE);
//        }else{
//            Toast.makeText(SignInActivity.this, "permission granted", Toast.LENGTH_SHORT).show();
//            isBatteryPermissionGrant = true;
//    }

    protected void checkPermission() {
        if (permissionSum
                != PackageManager.PERMISSION_GRANTED) {
            if (rationale) {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage("Location, Read phone state and Call phone, read and write external storage" +
                        " permissions are required to continue the process.");
                builder.setTitle("Please grant those permissions");
                builder.setPositiveButton("Ok", (dialogInterface, i) -> ActivityCompat.requestPermissions(
                        SignInActivity.this,
                        permissions,
                        MY_PERMISSIONS_REQUEST_CODE
                ));
                builder.setNeutralButton("Cancel", null);
                AlertDialog dialog = builder.create();
                dialog.show();
            } else {
                ActivityCompat.requestPermissions(this, permissions, MY_PERMISSIONS_REQUEST_CODE);
            }
        } else {
            if (isPermissionCheckOnSignIn) {
                isPermissionCheckOnSignIn = false;
                validateUserData();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == MY_PERMISSIONS_REQUEST_CODE) {
            if ((grantResults[0] + grantResults[1]) == PackageManager.PERMISSION_GRANTED) {
                if (isPermissionCheckOnSignIn) {
                    isPermissionCheckOnSignIn = false;
                    validateUserData();
                }
            } else {
                // Permissions are denied
                Toast.makeText(this, "Permissions denied.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void buildAlertMessageNoGps() {

        final android.app.AlertDialog dialogConfirm = new android.app.AlertDialog.Builder(this).create();
        View view = getLayoutInflater().inflate(R.layout.confirm_dialog, null);

        AppCompatTextView tvMessage = view.findViewById(R.id.tvMessage);
        AppCompatTextView tvYes = view.findViewById(R.id.tvYes);
        AppCompatTextView tvNo = view.findViewById(R.id.tvNo);
        tvYes.setText("Enable");
        tvNo.setText("Cancel");
        tvMessage.setText("Your GPS seems to be disabled, please enable it proceed further");
        tvYes.setOnClickListener(v -> {
            dialogConfirm.dismiss();
            startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
        });

        tvNo.setOnClickListener(v -> {
            dialogConfirm.dismiss();
            finish();
        });
        dialogConfirm.setCancelable(false);
        dialogConfirm.setCanceledOnTouchOutside(false);
        dialogConfirm.setView(view);
        dialogConfirm.show();
    }

    @Override
    protected Fragment createFragment() {
        return null;
    }
}
