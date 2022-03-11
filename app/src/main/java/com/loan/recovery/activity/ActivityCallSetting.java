package com.loan.recovery.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.loan.recovery.LoanApplication;
import com.loan.recovery.R;
import com.loan.recovery.retrofit.ApiClient;
import com.loan.recovery.retrofit.ApiInterface;
import com.loan.recovery.retrofit.model.BaseResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ActivityCallSetting extends BaseActivity {

    private ImageView imgBack;
    private Switch isCellOptEnable;
    private ActivityCallSetting activity;
    char isOptChecked = 'N';
    private LoanApplication application;

    @Override
    protected Fragment createFragment() {
        return null;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_call_setting);

        activity = ActivityCallSetting.this;
        application = LoanApplication.getInstance();
        application.setContext(getApplicationContext());

        imgBack =(ImageView) findViewById(R.id.arrow_back);
        isCellOptEnable =(Switch) findViewById(R.id.switch_opt_enable);

        if(application.getIsDirectCall()=='Y'){
            isOptChecked = 'Y';
            isCellOptEnable.setChecked(true);
        }else{
            isOptChecked = 'N';
            isCellOptEnable.setChecked(false);
        }

        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        isCellOptEnable.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (android.os.Build.VERSION.SDK_INT <= Build.VERSION_CODES.O_MR1) {
                    if(isChecked){
                        isOptChecked='Y';
                    }else{
                        isOptChecked='N';
                    }
                    updateCallSetting();
                }else {
                    Toast.makeText(getApplicationContext(), "Sorry, Your phone does not support direct call option.", Toast.LENGTH_SHORT).show();
                    isCellOptEnable.setChecked(false);
                }
            }
        });
    }

    private void updateCallSetting(){
        activity.showProgressDialog("Please Wait", "Getting Cases List...");
        ApiInterface apiService =
                ApiClient.getClient(this, 2).create(ApiInterface.class);
        JsonObject settingObj = new JsonObject();
        settingObj.addProperty("userId",application.getCurrentUser().getUserId().toString());
        settingObj.addProperty("isDirectCall",isOptChecked);

        Call<BaseResponse> call = apiService.updateDirectCall(settingObj);
        call.enqueue(new Callback<BaseResponse>() {
            @Override
            public void onResponse(Call<BaseResponse> call, Response<BaseResponse> response) {
                try {
                    activity.hideProgressDialog();
                    BaseResponse serverResponse = response.body();
                    if (serverResponse != null && serverResponse.getStatusCode() == 1000) {
                        System.out.println("<=== saveJobStartData - Success ===> " + serverResponse.getStatus());
                        application.setIsDirectCall(isOptChecked);
                        if(isOptChecked=='Y') {
                            Toast.makeText(activity, "Direct call enabled successfully.", Toast.LENGTH_SHORT).show();
                        }else{
                            Toast.makeText(activity, "Direct call disabled successfully.", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        System.out.println("<=== saveJobStartData - Failed ===> ");
                        Toast.makeText(activity, "Sorry, We are unable to update.", Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    System.out.println("<=== saveJobStartData - Crash : ===> " + e.toString());
                    Toast.makeText(activity, "Sorry, We are unable to update.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<BaseResponse> call, Throwable t) {
                System.out.println("<=== Exceptions ===> " + t.toString());
            }
        });
    }
}