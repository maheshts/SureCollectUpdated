package com.loan.recovery.fragments;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.loan.recovery.LoanApplication;
import com.loan.recovery.R;
import com.loan.recovery.activity.HomeActivity;
import com.loan.recovery.adapter.CasesRecyclerAdapter;
import com.loan.recovery.adapter.CommonDataListAdapter;
import com.loan.recovery.adapter.ContactModeListAdapter;
import com.loan.recovery.adapter.PartnerListAdapter;
import com.loan.recovery.adapter.StatusListAdapter;
import com.loan.recovery.database.Recording;
import com.loan.recovery.database.RepositoryImpl;
import com.loan.recovery.databinding.FragmentHomeBinding;
import com.loan.recovery.databinding.FragmentUpdatePhoneNumberBinding;
import com.loan.recovery.retrofit.ApiClient;
import com.loan.recovery.retrofit.ApiInterface;
import com.loan.recovery.retrofit.model.BaseResponse;
import com.loan.recovery.retrofit.model.Case;
import com.loan.recovery.retrofit.model.CaseData;
import com.loan.recovery.retrofit.model.CasesList;
import com.loan.recovery.retrofit.model.Partner;
import com.loan.recovery.retrofit.model.Roles;
import com.loan.recovery.retrofit.model.Status;
import com.loan.recovery.retrofit.model.UserData;
import com.loan.recovery.retrofit.model.UserPhoneResponse;
import com.loan.recovery.retrofit.util.ApiUtil;
import com.loan.recovery.util.AppConstants;
import com.loan.recovery.util.UriUtils;
import com.loan.recovery.util.Utils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Mallikarjuna on 05/12/2020.
 */

public class UpdatePhoneNumberFragment extends Fragment implements View.OnClickListener {
    private FragmentUpdatePhoneNumberBinding binding;
    private HomeActivity activity;
    private LoanApplication application;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        activity = (HomeActivity) getActivity();
        application = LoanApplication.getInstance();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentUpdatePhoneNumberBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        UserData userData = application.getCurrentUser();
        binding.etPhone.setText(application.getAgentPhoneNumber());
        binding.etPhone.setEnabled(false);
        binding.imgEdit.setOnClickListener(this);
        binding.btCancel.setOnClickListener(this);
        binding.btSave.setOnClickListener(this);
        return view;
    }

    private void updatePhoneNumber(String phoneNumber) {
        activity.showProgressDialog("Please Wait", "Updating Phone Number...");

        ApiInterface apiService =
                ApiClient.getClient(activity, 1).create(ApiInterface.class);
        UserData userData = application.getCurrentUser();
        if (userData != null) {
            Call<BaseResponse> call = apiService.updatePhoneNumber(userData.getUserId(), phoneNumber);
            call.enqueue(new UpdatePhoneNumberCallBack(phoneNumber));
        }
    }

    private void showResponseDialog(String message) {
        AlertDialog dialog = new AlertDialog.Builder(activity).create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);

        View view = LayoutInflater.from(activity).inflate(R.layout.no_case_status_update_dialog, null);
        Button btOk = view.findViewById(R.id.btOk);
        btOk.setOnClickListener(v -> dialog.dismiss());
        if (message != null) {
            AppCompatTextView tvMessage = view.findViewById(R.id.tvMessage);
            tvMessage.setText(message);
        }

        dialog.setView(view);
        dialog.show();
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.btSave) {
            String phone = binding.etPhone.getText().toString().trim();
            if (phone.length() != 10)
                binding.etPhone.setError("Enter valid phone number");
            else {
                Utils.hideSoftKeyboard(activity, binding.etPhone);
                updatePhoneNumber(phone);
            }
        } else if (id == R.id.btCancel) {
            if (binding.imgEdit.getVisibility() == View.INVISIBLE) {
                binding.imgEdit.setVisibility(View.VISIBLE);
                binding.etPhone.setEnabled(false);
            } else
                activity.displaySelectedScreen(R.id.home);
        } else if (id == R.id.imgEdit) {
            binding.imgEdit.setVisibility(View.INVISIBLE);
            binding.etPhone.setEnabled(true);
            binding.etPhone.requestFocus();
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle(R.string.updatePhone);
    }

    class UpdatePhoneNumberCallBack implements Callback<BaseResponse> {
        String strPhone;

        UpdatePhoneNumberCallBack(String strPhone) {
            this.strPhone = strPhone;
        }

        @Override
        public void onResponse(Call<BaseResponse> call, Response<BaseResponse> response) {
            System.out.println("<=== PhoneNumberCallBack - Response : ===> " + new Gson().toJson(response.body()));
            activity.hideProgressDialog();
            BaseResponse baseResponse = response.body();
            assert baseResponse != null;
            if (baseResponse.getStatus().equals("1")) {
                application.setAgentPhoneNumber(strPhone);
                showResponseDialog("Your phone number is updated successfully");
            } else {
                showResponseDialog("Oops! Something went wrong, try again later");
            }
        }

        @Override
        public void onFailure(Call<BaseResponse> call, Throwable t) {
            activity.hideProgressDialog();
            showResponseDialog("Oops! Something went wrong, try again later");
        }
    }
}