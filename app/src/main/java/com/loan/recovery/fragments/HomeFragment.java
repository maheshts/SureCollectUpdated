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
import android.util.Log;
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
import com.loan.recovery.adapter.NonCasesRecyclerAdapter;
import com.loan.recovery.adapter.PartnerListAdapter;
import com.loan.recovery.adapter.RealestateConfigListAdapter;
import com.loan.recovery.adapter.RealestateProjectListAdapter;
import com.loan.recovery.adapter.StatusListAdapter;
import com.loan.recovery.database.Recording;
import com.loan.recovery.database.RepositoryImpl;
import com.loan.recovery.databinding.FragmentHomeBinding;
import com.loan.recovery.retrofit.ApiClient;
import com.loan.recovery.retrofit.ApiInterface;
import com.loan.recovery.retrofit.model.BaseResponse;
import com.loan.recovery.retrofit.model.Case;
import com.loan.recovery.retrofit.model.CaseData;
import com.loan.recovery.retrofit.model.CasesList;
import com.loan.recovery.retrofit.model.Partner;
import com.loan.recovery.retrofit.model.ProjectConfigData;
import com.loan.recovery.retrofit.model.ProjectData;
import com.loan.recovery.retrofit.model.Roles;
import com.loan.recovery.retrofit.model.Status;
import com.loan.recovery.retrofit.model.UserData;
import com.loan.recovery.retrofit.util.ApiUtil;
import com.loan.recovery.util.AppConstants;
import com.loan.recovery.util.UriUtils;
import com.loan.recovery.util.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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

public class HomeFragment extends Fragment implements View.OnClickListener, RealestateConfigListAdapter.ItemClickListener,
        StatusListAdapter.ItemClickListener, PartnerListAdapter.ItemClickListener,RealestateProjectListAdapter.ItemClickListener,
        CommonDataListAdapter.ItemClickListener, ContactModeListAdapter.ItemClickListener {
    private final String[] scoreOptions = {"Select Score", "0", "1",
            "2", "3", "4", "5", "6", "7", "8", "9", "10"};
    private final String[] bucketOptions = {"Select Bucket", "0", "1",
            "2", "3", "4", "5", "6", "7", "8", "9", "10"};
    private final String[] modeOptions = {"Phone", "Field Visit"};
    private FragmentHomeBinding binding;
    private HomeActivity activity;
    private LoanApplication application;
    private int currentPage = 1, lastPage = -1, casesCount = -1, modePosition = 0, mPatnerId;
    private AlertDialog dialog;
    private Partner partnerSelected;
    private ProjectConfigData configData;
    private ProjectData projectData;
    private Status statusSelected;
    private String scoreSelected, bucketSelected, strAgId, strPhone, strPOSAmount, strDueAmount, strContactMode;
    private AppCompatTextView tvStatus, tvPartner, tvScore, tvContactMode, tvBucket,tvPartners,tvConfigs;
    private boolean isFilter = false, isFilterApplied = false;
    private CasesRecyclerAdapter adapter;
    private NonCasesRecyclerAdapter noncaseadapter;
    private MenuItem menuFilter;
    private List<Status> statusList;
    private StatusListAdapter listAdapter;
    List<ProjectConfigData> configDataList;
    List<ProjectData> projectLists;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle(R.string.home);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        activity = (HomeActivity) getActivity();
        application = LoanApplication.getInstance();
        clearFilters();
        configDataList = application.getConfigTypes();
        projectLists = application.getProjectTypes();
        //Toast.makeText(getContext(), "size "+configDataList.size(), Toast.LENGTH_SHORT).show();
    }

    private void clearFilters() {
        partnerSelected = new Partner();
        partnerSelected.setPartnerName("Select Partner");

        configData = new ProjectConfigData();
        configData.setProjectName("Select Partner");

        projectData = new ProjectData();
        projectData.setProjectName("Select Project");

        statusSelected = new Status();
        statusSelected.setStatusName("Select Status");

        scoreSelected = "Select Score";
        bucketSelected = "Select Bucket";
        strAgId = "";
        strPhone = "";
        strPOSAmount = "";
        strDueAmount = "";
        strContactMode = "Phone";
        modePosition = 0;
        statusList = application.getPhoneStatusList();
        configDataList = application.getConfigTypes();
        projectLists = application.getProjectTypes();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        initUI();
        return view;
    }

    private void initUI() {
        binding.recyclerView.setHasFixedSize(true);
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(activity));
        LoanApplication application = LoanApplication.getInstance();
        UserData userData = application.getCurrentUser();
        if (userData != null)
            binding.tvName.setText(userData.getFirstName() + " " + userData.getLastName());
        else
            binding.tvName.setVisibility(View.GONE);

        binding.tvPrevious.setOnClickListener(this);
        binding.tvNext.setOnClickListener(this);
        mPatnerId = Utils.getPatnerIDPreferences(getActivity().getApplicationContext());
        Log.v("mPatnerId", "home : " + mPatnerId);
        callAPI(0, 10);

    }

    private void callAPI(int start, int end) {
        activity.showProgressDialog("Please Wait", "Getting Cases List...");

        ApiInterface apiService =
                ApiClient.getClient(activity, 1).create(ApiInterface.class);
        UserData userData = application.getCurrentUser();
        if (userData != null) {
            Roles roles = userData.getRoles();
            if (mPatnerId == AppConstants.NONRETRA_TYPE) { ///for non retra user
                //Toast.makeText(getContext(), "NON Re", Toast.LENGTH_SHORT).show();
                getNonRetraCasesList("0", "10", mPatnerId, userData.getPartnerUuid(), strPhone, "", "", "", "N");
            }else if (mPatnerId == AppConstants.REALESTATE_TYPE) { ///for non retra user
                //Toast.makeText(getContext(), "NON Re", Toast.LENGTH_SHORT).show();
                getRealStateCasesList("0", "10", mPatnerId, userData.getPartnerUuid(), strPhone, "",  "", "N");
            } else {
                Call<CasesList> call = apiService.getCasesList(userData.getUserId(), roles.getRoleId(), "" + start, "" + end, "11");
                call.enqueue(new CasesCallBack());
            }
        }
    }

    private void getRealStateCasesList(String start, String end, int partnerid,String fname,
                                       String phoneNumber, String status, String selectpartnerid,
                                      String projectid) {
        activity.showProgressDialog("Please Wait", "Getting Data...");
        isFilter = true;
        JsonObject json = new JsonObject();
        UserData userData = application.getCurrentUser();
        ApiInterface apiService =
                ApiClient.getClient(activity, 5).create(ApiInterface.class);
        if (userData != null) {
            Roles roles = userData.getRoles();

            json.addProperty("endRows", "10");
            json.addProperty("firstName", "");
            json.addProperty("email", "");
            json.addProperty("fkPartnerId", "1024");
            json.addProperty("forExport", "N");
            json.addProperty("lastName", "");
            json.addProperty("loggedInRoleId", "25");
            json.addProperty("loggedinUserId", "10097");
            json.addProperty("partnerCaseId", selectpartnerid);
            json.addProperty("projectId", projectid);
            json.addProperty("phoneNumber", phoneNumber);
            json.addProperty("selectedUserId", "");
            json.addProperty("sortByColumn", "");
            json.addProperty("selectedUserId", "10097");
            json.addProperty("startRows", "0");
            json.addProperty("statusCode", status);
            json.addProperty("sortByOrder", "");

            Log.v("2222", "json" + json);
            // Toast.makeText(getContext(), "json"+json, Toast.LENGTH_SHORT).show();
//            call = apiService.getCaseList("1172", "25",
//                    "0", "10", "1002", "", "",
//                    "","", "","N","", "1172","");
//            call.enqueue(new CasesCallBack());

            Call<Object> call = apiService.getRealEstateCaseList(json);
            Log.v("1111", "jsonstarted");

            //Call<BaseResponse> call
            //Log.v("ccc",call.)
            //call.enqueue(new NonRetraCasesCallBack());
            //call.enqueue(new NonRetraCasesCallBack<NonRetraBaseResponse>(){
            call.enqueue(new Callback() {

                @Override
                public void onResponse(Call call, Response response) {
                    Log.e("TAG", "response 33: " + new Gson().toJson(response.body()));
                    activity.hideProgressDialog();
                    // NonRetraCasesResponse casesList = (NonRetraCasesResponse) response.body();
                    try {
                        JSONObject json1 = new JSONObject(new Gson().toJson(response.body()));
                        Log.v("1111", "json1" + json1);
                        binding.recyclerView.setVisibility(View.VISIBLE);
                        binding.tvNoCases.setVisibility(View.GONE);
                        binding.loutPages.setVisibility(View.VISIBLE);
                        List<JSONObject> itemList = new ArrayList<>();
                        JSONArray dataJsonArray = json1.getJSONObject("result").getJSONArray("data");
                        for (int i = 0; i < dataJsonArray.length(); i++) {
                            JSONObject json = dataJsonArray.getJSONObject(i);
                            itemList.add(json);
                        }


                        noncaseadapter = new NonCasesRecyclerAdapter(itemList, activity, HomeFragment.this,mPatnerId);
                        binding.recyclerView.setAdapter(noncaseadapter);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    //  JsonObject post = new JsonObject().get(response.body().toString()).getAsJsonObject();
                    // Log.v("1111J","json"+ post);

//        if (casesList != null) {
//            List<NonRetraCasesData> caseData = casesList.getCaseListData();
//            if (caseData != null && caseData.size() > 0) {
//                //populateCases(caseData);
//                binding.recyclerView.setVisibility(View.VISIBLE);
//                binding.tvNoCases.setVisibility(View.GONE);
//                binding.loutPages.setVisibility(View.VISIBLE);
//            }else{
//                noCases();
//            }
//        }
                }

                @Override
                public void onFailure(Call call, Throwable t) {
                    Log.e("TAG", "onFailure: " + t.toString());
                    // Log error here since request failed
                }
            });

        }
    }
private void getNonRetraCasesList(String start, String end, int partnerid,String fname,String lname,
                                       String phoneNumber, String status, String email,
                                      String caseid) {
        activity.showProgressDialog("Please Wait", "Getting Cases List...");
        isFilter = true;
        JsonObject json = new JsonObject();
        UserData userData = application.getCurrentUser();
        ApiInterface apiService =
                ApiClient.getClient(activity, 5).create(ApiInterface.class);
        if (userData != null) {
            Roles roles = userData.getRoles();

            json.addProperty("endRows", "10");
            json.addProperty("firstName", fname);
            json.addProperty("email", email);
            json.addProperty("fkPartnerId", "1002");
            json.addProperty("forExport", "N");
            json.addProperty("lastName", lname);
            json.addProperty("loggedInRoleId", "25");
            json.addProperty("loggedinUserId", "1172");
            json.addProperty("partnerCaseId", "");
            json.addProperty("phoneNumber", phoneNumber);
            json.addProperty("selectedUserId", "");
            json.addProperty("sortByColumn", "");
            json.addProperty("selectedUserId", "1172");
            json.addProperty("startRows", "0");
            json.addProperty("statusCode", status);

            Log.v("1111", "json" + json);
            // Toast.makeText(getContext(), "json"+json, Toast.LENGTH_SHORT).show();
//            call = apiService.getCaseList("1172", "25",
//                    "0", "10", "1002", "", "",
//                    "","", "","N","", "1172","");
//            call.enqueue(new CasesCallBack());

            Call<Object> call = apiService.getOtherCaseList(json);
            Log.v("1111", "jsonstarted");

            //Call<BaseResponse> call
            //Log.v("ccc",call.)
            //call.enqueue(new NonRetraCasesCallBack());
            //call.enqueue(new NonRetraCasesCallBack<NonRetraBaseResponse>(){
            call.enqueue(new Callback() {

                @Override
                public void onResponse(Call call, Response response) {
                    Log.e("TAG", "response 33: " + new Gson().toJson(response.body()));
                    activity.hideProgressDialog();
                    // NonRetraCasesResponse casesList = (NonRetraCasesResponse) response.body();
                    try {
                        JSONObject json1 = new JSONObject(new Gson().toJson(response.body()));
                        Log.v("1111", "json1" + json1);
                        binding.recyclerView.setVisibility(View.VISIBLE);
                        binding.tvNoCases.setVisibility(View.GONE);
                        binding.loutPages.setVisibility(View.VISIBLE);
                        List<JSONObject> itemList = new ArrayList<>();
                        JSONArray dataJsonArray = json1.getJSONObject("result").getJSONArray("data");
                        for (int i = 0; i < dataJsonArray.length(); i++) {
                            JSONObject json = dataJsonArray.getJSONObject(i);
                            itemList.add(json);
                        }


                        noncaseadapter = new NonCasesRecyclerAdapter(itemList, activity, HomeFragment.this, mPatnerId);
                        binding.recyclerView.setAdapter(noncaseadapter);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    //  JsonObject post = new JsonObject().get(response.body().toString()).getAsJsonObject();
                    // Log.v("1111J","json"+ post);

//        if (casesList != null) {
//            List<NonRetraCasesData> caseData = casesList.getCaseListData();
//            if (caseData != null && caseData.size() > 0) {
//                //populateCases(caseData);
//                binding.recyclerView.setVisibility(View.VISIBLE);
//                binding.tvNoCases.setVisibility(View.GONE);
//                binding.loutPages.setVisibility(View.VISIBLE);
//            }else{
//                noCases();
//            }
//        }
                }

                @Override
                public void onFailure(Call call, Throwable t) {
                    Log.e("TAG", "onFailure: " + t.toString());
                    // Log error here since request failed
                }
            });

        }
    }

    private void getCasesList(int start, int end, String agreementId, String score, String bucket,
                              String partnerUuid, String phoneNumber, int status, String posAmount,
                              String dueAmount, String contactMode) {
        activity.showProgressDialog("Please Wait", "Getting Cases List...");
        isFilter = true;
        ApiInterface apiService =
                ApiClient.getClient(activity, 1).create(ApiInterface.class);
        UserData userData = application.getCurrentUser();
        if (userData != null) {
            Roles roles = userData.getRoles();
            Call<CasesList> call;
            if (status == 0000) {
                call = apiService.getCasesList(userData.getUserId(), roles.getRoleId(),
                        "" + start, "" + end, agreementId, score, bucket, partnerUuid, phoneNumber,
                        posAmount, dueAmount, contactMode);
            } else {
                call = apiService.getCasesList(userData.getUserId(), roles.getRoleId(),
                        "" + start, "" + end, agreementId, score, bucket, partnerUuid, phoneNumber,
                        posAmount, dueAmount, status, contactMode);
            }
            call.enqueue(new CasesCallBack());
        }
    }

    private void showNonRetraFilterDialog() {
        statusList = application.getPhoneStatusList();
        if (modePosition == 1)
            statusList = application.getFieldStatusList();
        List<Partner> partnerList = application.getPartners();

        AlertDialog dialog = new AlertDialog.Builder(activity).create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        View view = LayoutInflater.from(activity).inflate(R.layout.filter_dialog_nonretra, null);
        RelativeLayout loutStatus = view.findViewById(R.id.loutStatus);
        RelativeLayout loutPartner = view.findViewById(R.id.loutPartner);


        //tvScore = view.findViewById(R.id.tvSelectScore);
        tvStatus = view.findViewById(R.id.tvSelectStatus);
        tvPartner = view.findViewById(R.id.tvSelectPartner);
       TextView tvConfig = view.findViewById(R.id.tvSelectConfig);
       // tvContactMode = view.findViewById(R.id.tvSelectMode);
       // tvBucket = view.findViewById(R.id.tvSelectBucket);


        final Button btApply = view.findViewById(R.id.btApply);
        final Button btClear = view.findViewById(R.id.btClear);
        final EditText etAgId = view.findViewById(R.id.etAgId);
        final EditText etPhone = view.findViewById(R.id.etPhone);
        final EditText etFname = view.findViewById(R.id.etFname);
        final EditText etLname = view.findViewById(R.id.etLname);
        final EditText etemail = view.findViewById(R.id.etEmail);

        tvStatus.setText(statusSelected.getStatusName());
        tvPartner.setText(partnerSelected.getPartnerName());
     //   tvConfig.setText(partnerSelected.get());
      //  tvContactMode.setText(strContactMode);
       // tvBucket.setText(bucketSelected);


        etAgId.setText(strAgId);
        etPhone.setText(strPhone);



        loutStatus.setOnClickListener(v -> showCustomSpinnerDialog(2, null));
        loutPartner.setOnClickListener(v -> showCustomSpinnerDialog(3, partnerList));

        view.findViewById(R.id.imgClose).setOnClickListener(v -> dialog.dismiss());
        btClear.setOnClickListener(v -> {
            currentPage = 1;
            clearFilters();
            isFilterApplied = false;
            etAgId.setText("");
            etPhone.setText("");
            etFname.setText("");
            etLname.setText("");
            etemail.setText("");
            //etPOSAmount.setText("");
            dialog.dismiss();
            callAPI(0, 10);
            menuFilter.setIcon(R.mipmap.filter);
        });

        btApply.setOnClickListener(v -> {
            dialog.dismiss();
            currentPage = 1;
            isFilterApplied = true;
            menuFilter.setIcon(R.mipmap.filter_applied);
            strAgId = etAgId.getText().toString().trim();
            strPhone = etPhone.getText().toString().trim();
           String email = etemail.getText().toString().trim();
           String fname = etFname.getText().toString().trim();
           String lname = etLname.getText().toString().trim();
           String phone = etPhone.getText().toString().trim();


            String selectedPartner = "";
            if (!tvPartner.getText().equals("Select Partner"))
                selectedPartner = partnerSelected.getPartnerId() + "";
          //  String selectedScore = "";
            int selectedStatus = 0000;
            if (!tvStatus.getText().equals("Select Status"))
                selectedStatus = statusSelected.getStatusCode();

            // 11 is for Phone
            // 12 is for field visit
//            String selectedMode = "";
//            if (modePosition == 0)
//                selectedMode = "11";
//            else
//                selectedMode = "12";

            if (Utils.isConnected(activity)) {
               // getCasesList(0, 10, strAgId,selectedScore, selectedBucket, selectedPartner, strPhone, selectedStatus, strPOSAmount, strDueAmount, selectedMode);
                getNonRetraCasesList("","",mPatnerId,fname,lname,phone,selectedStatus+"",email,"");
            } else
                Toast.makeText(activity, "No Internet", Toast.LENGTH_SHORT).show();
        });
        dialog.setView(view);
        dialog.show();
    }
    private void showRealestateFilterDialog() {
        statusList = application.getPhoneStatusList();
        configDataList = application.getConfigTypes();
        //configData = application.getPartnerTypes();
        projectLists = application.getProjectTypes();
        if (modePosition == 1)
            statusList = application.getFieldStatusList();
       // List<Partner> partnerList = application.getPartners();
//        List<PartnerData> partnerLists = application.getPartnerTypes();
//        List<ProjectData> projectLists = application.getProjectTypes();

        AlertDialog dialog = new AlertDialog.Builder(activity).create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        View view = LayoutInflater.from(activity).inflate(R.layout.filter_dialog_realestate, null);
        RelativeLayout loutStatus = view.findViewById(R.id.loutStatus);
        RelativeLayout loutPartners = view.findViewById(R.id.loutPartner);
        RelativeLayout loutConfig = view.findViewById(R.id.loutConf);


        //tvScore = view.findViewById(R.id.tvSelectScore);
        tvStatus = view.findViewById(R.id.tvSelectStatus);
       // tvPartner = view.findViewById(R.id.tvSelectPartner);
        tvPartners = view.findViewById(R.id.tvSelectPartners);
        tvConfigs = view.findViewById(R.id.tvSelectConfig);
       // tvContactMode = view.findViewById(R.id.tvSelectMode);
       // tvBucket = view.findViewById(R.id.tvSelectBucket);


        final Button btApply = view.findViewById(R.id.btApply);
        final Button btClear = view.findViewById(R.id.btClear);

        final EditText etPhone = view.findViewById(R.id.etPhone);
        final EditText etFname = view.findViewById(R.id.etFname);


        //tvStatus.setText(statusSelected.getStatusName());
      //  tvPartner.setText(partnerSelected.getPartnerName());
        //tvPartners.setText(configData.getProjectName());
        //tvProjects.setText(projectData.getProjectName());
      //  tvContactMode.setText(strContactMode);
       // tvBucket.setText(bucketSelected);



        etPhone.setText(strPhone);



        loutStatus.setOnClickListener(v -> showCustomSpinnerDialog(2, null));
        loutConfig.setOnClickListener(v -> showCustomSpinnerDialog(6, null));
        loutPartners.setOnClickListener(v -> showCustomSpinnerDialog(7, null));

        view.findViewById(R.id.imgClose).setOnClickListener(v -> dialog.dismiss());
        btClear.setOnClickListener(v -> {
            currentPage = 1;
            clearFilters();
            isFilterApplied = false;

            etPhone.setText("");
            etFname.setText("");

            //etPOSAmount.setText("");
            dialog.dismiss();
            callAPI(0, 10);
            menuFilter.setIcon(R.mipmap.filter);
        });

        btApply.setOnClickListener(v -> {
            dialog.dismiss();
            currentPage = 1;
            isFilterApplied = true;
            menuFilter.setIcon(R.mipmap.filter_applied);
            //strAgId = etAgId.getText().toString().trim();
            strPhone = etPhone.getText().toString().trim();
           //String email = etemail.getText().toString().trim();
           String fname = etFname.getText().toString().trim();
           //String lname = etLname.getText().toString().trim();
           String phone = etPhone.getText().toString().trim();


            String selectedPartner = "";
            if (!tvPartners.getText().equals("Select Partner"))
                selectedPartner = configData.getProjectId() + "";

            String selectedProject = "";
            if (!tvPartners.getText().equals("Select Project"))
                selectedProject = projectData.getProjectId() + "";
          //  String selectedScore = "";
            int selectedStatus = 0000;
            if (!tvStatus.getText().equals("Select Status"))
                selectedStatus = statusSelected.getStatusCode();

            // 11 is for Phone
            // 12 is for field visit
//            String selectedMode = "";
//            if (modePosition == 0)
//                selectedMode = "11";
//            else
//                selectedMode = "12";

            if (Utils.isConnected(activity)) {
               // getCasesList(0, 10, strAgId,selectedScore, selectedBucket, selectedPartner, strPhone, selectedStatus, strPOSAmount, strDueAmount, selectedMode);
                getRealStateCasesList("","",mPatnerId,fname,phone,selectedStatus+"",selectedPartner,selectedProject);
            } else
                Toast.makeText(activity, "No Internet", Toast.LENGTH_SHORT).show();
        });
        dialog.setView(view);
        dialog.show();
    }
    private void showFilterDialog() {
        statusList = application.getPhoneStatusList();
        if (modePosition == 1)
            statusList = application.getFieldStatusList();
        List<Partner> partnerList = application.getPartners();

        AlertDialog dialog = new AlertDialog.Builder(activity).create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        View view = LayoutInflater.from(activity).inflate(R.layout.filter_dialog_new, null);
        RelativeLayout loutScore = view.findViewById(R.id.loutScore);
        RelativeLayout loutStatus = view.findViewById(R.id.loutStatus);
        RelativeLayout loutPartner = view.findViewById(R.id.loutPartner);
        RelativeLayout loutContactMode = view.findViewById(R.id.loutContactMode);
        RelativeLayout loutBucket = view.findViewById(R.id.loutBucket);
        RelativeLayout posLayout = view.findViewById(R.id.lout6);
        RelativeLayout dueLayout = view.findViewById(R.id.lout7);
        RelativeLayout agreementLayout = view.findViewById(R.id.lout4);




        tvScore = view.findViewById(R.id.tvSelectScore);
        tvStatus = view.findViewById(R.id.tvSelectStatus);
        tvPartner = view.findViewById(R.id.tvSelectPartner);
        tvContactMode = view.findViewById(R.id.tvSelectMode);
        tvBucket = view.findViewById(R.id.tvSelectBucket);


        final Button btApply = view.findViewById(R.id.btApply);
        final Button btClear = view.findViewById(R.id.btClear);
        final EditText etAgId = view.findViewById(R.id.etAgId);
        final EditText etPhone = view.findViewById(R.id.etPhone);
        final EditText etPOSAmount = view.findViewById(R.id.etPOSAmount);
        final EditText etDueAmount = view.findViewById(R.id.etDueAmount);

        tvScore.setText(scoreSelected);
        tvStatus.setText(statusSelected.getStatusName());
        tvPartner.setText(partnerSelected.getPartnerName());
        tvContactMode.setText(strContactMode);
        tvBucket.setText(bucketSelected);


        etAgId.setText(strAgId);
        etPhone.setText(strPhone);
        etPOSAmount.setText(strPOSAmount);
        etDueAmount.setText(strDueAmount);


        loutScore.setOnClickListener(v -> showCustomSpinnerDialog(1, null));
        loutBucket.setOnClickListener(v -> showCustomSpinnerDialog(5, null));
        loutStatus.setOnClickListener(v -> showCustomSpinnerDialog(2, null));
        loutPartner.setOnClickListener(v -> showCustomSpinnerDialog(3, partnerList));
        loutContactMode.setOnClickListener(v -> showCustomSpinnerDialog(4, null));

        view.findViewById(R.id.imgClose).setOnClickListener(v -> dialog.dismiss());
        btClear.setOnClickListener(v -> {
            currentPage = 1;
            clearFilters();
            isFilterApplied = false;
            etAgId.setText("");
            etPhone.setText("");
            etPOSAmount.setText("");
            dialog.dismiss();
            callAPI(0, 10);
            menuFilter.setIcon(R.mipmap.filter);
        });

        btApply.setOnClickListener(v -> {
            dialog.dismiss();
            currentPage = 1;
            isFilterApplied = true;
            menuFilter.setIcon(R.mipmap.filter_applied);
            strAgId = etAgId.getText().toString().trim();
            strPhone = etPhone.getText().toString().trim();
            strPOSAmount = etPOSAmount.getText().toString().trim();
            strDueAmount = etDueAmount.getText().toString().trim();

            String selectedPartner = "";
            if (!tvPartner.getText().equals("Select Partner"))
                selectedPartner = partnerSelected.getPartnerId() + "";
            String selectedScore = "";
            if (!tvScore.getText().equals("Select Score"))
                selectedScore = scoreSelected;
            String selectedBucket = "";
            if (!tvBucket.getText().equals("Select Bucket"))
                selectedBucket = bucketSelected;
            int selectedStatus = 0000;
            if (!tvStatus.getText().equals("Select Status"))
                selectedStatus = statusSelected.getStatusCode();

            // 11 is for Phone
            // 12 is for field visit
            String selectedMode = "";
            if (modePosition == 0)
                selectedMode = "11";
            else
                selectedMode = "12";

            if (Utils.isConnected(activity)) {
                getCasesList(0, 10, strAgId,
                        selectedScore, selectedBucket, selectedPartner, strPhone, selectedStatus, strPOSAmount, strDueAmount, selectedMode);
            } else
                Toast.makeText(activity, "No Internet", Toast.LENGTH_SHORT).show();
        });
        dialog.setView(view);
        dialog.show();
    }

    @SuppressLint("ResourceAsColor")
    private void showCustomSpinnerDialog(int dataType, List<Partner> partnerList) {
        View view = getLayoutInflater().inflate(R.layout.list_dialog, null);
        final ListView listView = view.findViewById(R.id.listItems);
        listView.setTextFilterEnabled(true);
        if (dataType == 1) {
            CommonDataListAdapter listAdapter = new CommonDataListAdapter(activity, R.layout.project_list_item, scoreOptions, 'S');
            listAdapter.setClickListener(this);
            listView.setAdapter(listAdapter);

        } else if (dataType == 2) {
            listAdapter = new StatusListAdapter(activity, R.layout.project_list_item, statusList);
            listAdapter.setClickListener(this);
            listView.setAdapter(listAdapter);
        } else if (dataType == 3) {
            final PartnerListAdapter listAdapter = new PartnerListAdapter(activity, R.layout.project_list_item, partnerList);
            listAdapter.setClickListener(this);
            listView.setAdapter(listAdapter);
        } else if (dataType == 5) {
            CommonDataListAdapter listAdapter = new CommonDataListAdapter(activity, R.layout.project_list_item, bucketOptions, 'B');
            listAdapter.setClickListener(this);
            listView.setAdapter(listAdapter);
        }else if (dataType == 6) {
            final RealestateConfigListAdapter listAdapter = new RealestateConfigListAdapter(activity,  R.layout.project_list_item, configDataList);
            listAdapter.setClickListener(this);
            listView.setAdapter(listAdapter);
        }else if (dataType == 7) {
            final RealestateProjectListAdapter listAdapter = new RealestateProjectListAdapter(activity,  R.layout.project_list_item, projectLists);
            listAdapter.setClickListener(this);
            listView.setAdapter(listAdapter);
        } else {
            final ContactModeListAdapter listAdapter = new ContactModeListAdapter(activity, R.layout.project_list_item, modeOptions);
            listAdapter.setClickListener(this);
            listView.setAdapter(listAdapter);
        }

        dialog = new AlertDialog.Builder(activity).

                create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);

        dialog.setView(view);
        dialog.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancel", (dialog, which) -> dialog.dismiss());

        dialog.setOnShowListener(arg0 -> dialog.getButton(AlertDialog.BUTTON_NEGATIVE).

                setTextColor(R.color.colorPrimary));
        dialog.show();
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.tvNext) {
            currentPage = currentPage + 1;
        } else if (id == R.id.tvPrevious) {
            currentPage = currentPage - 1;
        } else {
            currentPage = id;
        }

        int start = (currentPage - 1) * 10;
        int end = (currentPage * 10);
        if (Utils.isConnected(activity)) {
//            callAPI(start, end);
            if (isFilterApplied) {
                String selectedPartner = "";
                if (!tvPartner.getText().equals("Select Partner"))
                    selectedPartner = partnerSelected.getPartnerId() + "";
                String selectedScore = "";
                if (!tvScore.getText().equals("Select Score"))
                    selectedScore = scoreSelected;
                String selectedBucket = "";
                if (!tvScore.getText().equals("Select Score"))
                    selectedBucket = bucketSelected;
                int selectedStatus = 0000;
                if (!tvStatus.getText().equals("Select Status"))
                    selectedStatus = statusSelected.getStatusCode();
                // 11 is for Phone
                // 12 is for field visit
                String selectedMode = "";
                if (modePosition == 0)
                    selectedMode = "11";
                else
                    selectedMode = "12";

                if (Utils.isConnected(activity)) {
                    getCasesList(start, 10, strAgId,
                            selectedScore, selectedBucket, selectedPartner, strPhone, selectedStatus, strPOSAmount, strDueAmount, selectedMode);
                }
            } else {
                callAPI(start, 10);
            }
        } else
            Toast.makeText(activity, "No Internet", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onItemClick(String itemClicked, char layoutType) {
        if (dialog != null && dialog.isShowing())
            dialog.dismiss();
        if (layoutType == 'S') {
            scoreSelected = itemClicked;
            tvScore.setText(itemClicked);
        } else {
            bucketSelected = itemClicked;
            tvBucket.setText(itemClicked);
        }
    }


    @Override
    public void onItemClick(Partner partner) {
        if (dialog != null && dialog.isShowing())
            dialog.dismiss();
        partnerSelected = partner;
        tvPartner.setText(partner.toString());
    }

    @Override
    public void onItemClick(Status status) {
        if (dialog != null && dialog.isShowing())
            dialog.dismiss();
        statusSelected = status;
        tvStatus.setText(status.toString());
    }

    @Override
    public void onItemClick(String modeSelected, int position) {
        if (dialog != null && dialog.isShowing())
            dialog.dismiss();
        strContactMode = modeSelected;
        tvContactMode.setText(modeSelected);
        if (position == 1)
            statusList = application.getFieldStatusList();
        else
            statusList = application.getPhoneStatusList();

        if (listAdapter != null)
            listAdapter.notifyDataSetChanged();

        if (modePosition != position) {
            statusSelected = new Status();
            statusSelected.setStatusName("Select Status");
            tvStatus.setText("Select Status");
        }
        modePosition = position;
    }

    private void drawPages() {
        Resources resources = activity.getResources();
        int pages = casesCount / 10;
        int remainder = casesCount % 10;
        if (remainder != 0)
            pages = pages + 1;
        lastPage = pages;
        binding.loutPageNumbers.removeAllViews();
        for (int page = 1; page <= pages; ++page) {
            TextView textView = new TextView(activity);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.setMargins(5, 0, 5, 0);
            textView.setLayoutParams(params);
            textView.setGravity(Gravity.CENTER);
            textView.setId((page));
            textView.setOnClickListener(this);
//            textView.setFocusableInTouchMode(true);
            textView.setBackground(resources.getDrawable(R.drawable.round_white));
            textView.setTextColor(resources.getColor(R.color.colorSolidBlue));
            textView.setText("" + (page));
            binding.loutPageNumbers.addView(textView);
        }
        TextView textView = (TextView) binding.loutPageNumbers.getChildAt(currentPage - 1);
        textView.setBackground(resources.getDrawable(R.drawable.round_blue));
        textView.setTextColor(resources.getColor(R.color.colorWhite));
    }
//    class NonRetraCasesCallBack<N extends BaseResponse> implements Callback<NonRetraBaseResponse> {
////        @Override
////        public void onResponse(Call<JSONObject> call, Response<NonRetraBaseResponse> response) {
////            activity.hideProgressDialog();
////            Log.v("1111","json"+ new Gson().toJson(response.body()));
////
////            System.out.println("<=== CasesCallBack -LIST Response : ===> " + new Gson().toJson(response.body()));
//////            CasesList casesList = response.body();
////
////            if (casesList != null) {
////                List<Case> caseData = casesList.getCaseList();
////                if (caseData != null && caseData.size() > 0) {
////                    populateCases(caseData);
////                    binding.recyclerView.setVisibility(View.VISIBLE);
////                    binding.tvNoCases.setVisibility(View.GONE);
////                    binding.loutPages.setVisibility(View.VISIBLE);
////                    casesCount = caseData.get(0).getApplicationCount();
////                    drawPages();
////                    binding.tvCount.setText("Total Cases : " + casesCount);
////                    if (currentPage == 1) {
////                        binding.tvPrevious.setVisibility(View.GONE);
////                    } else {
////                        binding.tvPrevious.setVisibility(View.VISIBLE);
////                    }
////
////                    if (currentPage == lastPage) {
////                        binding.tvNext.setVisibility(View.GONE);
////                    } else {
////                        binding.tvNext.setVisibility(View.VISIBLE);
////                    }
////                } else {
////                    noCases();
////                }
////            } else {
////                noCases();
////            }
//        //}
//
//
//        @Override
//        public void onResponse(Call<NonRetraBaseResponse> call, Response<NonRetraBaseResponse> response) {
//            activity.hideProgressDialog();
//            Log.v("1111","response"+ new Gson().toJson(response.body()));
//
//            System.out.println("<=== NONCasesCallBack -LIST Response : ===> " + new Gson().toJson(response.body()));
//
//        }
//
//        @Override
//        public void onFailure(Call<NonRetraBaseResponse> call, Throwable t) {
//            System.out.println("<=== CasesCallBack - Response : ===> " + t.getMessage());
//            activity.hideProgressDialog();
//            noCases();
//        }
//    }

    private void noCases() {
        binding.recyclerView.setVisibility(View.GONE);
        binding.tvNoCases.setVisibility(View.VISIBLE);
        binding.loutPages.setVisibility(View.GONE);
        casesCount = 0;
        binding.tvCount.setText("Total Cases : " + casesCount);
        if (isFilter) {
            isFilter = false;
            binding.tvNoCases.setText("There are no cases matching with the given search criteria!");
        } else
            binding.tvNoCases.setText("You were not assigned any casess today!");
    }

    private void populateCases(List<Case> cases) {
        try {
            List<CaseData> result = new ArrayList<>();
            for (int i = 0; i < cases.size(); i++) {
                CaseData caseData = cases.get(i).getCaseData();
                result.add(caseData);
            }
//        result.get(0).setPhoneNumber("9492414612");
            adapter = new CasesRecyclerAdapter(result, activity, this);
            binding.recyclerView.setAdapter(adapter);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        switch (requestCode) {
            case AppConstants.PICK_FILE_RESULT_CODE:
                if (resultCode == Activity.RESULT_OK) {
                    Uri uri = data.getData();
                    String path = UriUtils.getPathFromUri(activity, uri);
                    File file = new File(path);
                    adapter.showConfirmationDialog(file, true, null, 0, "",
                            "", "", 0, "", 0);
                }
                break;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case R.id.filter:
                if (mPatnerId == 1023) {
                    showNonRetraFilterDialog();
                }else if (mPatnerId == 1024) {
                    showRealestateFilterDialog();
                }else{
                    showFilterDialog();
                }
                if (menuFilter == null)
                    menuFilter = item;
                return true;
            case R.id.direct_call_indicator:
                Toast.makeText(getContext(), "Direct call option is enable.", Toast.LENGTH_SHORT).show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onResume() {
        System.out.println("<=== Home Fragment - In onResume method ===> ");
        super.onResume();
        try {
            RepositoryImpl repository = new RepositoryImpl(getActivity(), AppConstants.DATABASE_NAME);
            List<Recording> recordings = repository.getRecordings();
            for (Recording recording : recordings) {
                String[] data = recording.getMode().split("&&&");
                String fileName = new File(recording.getPath()).getName();
                System.out.println("recording.getRecording :: " + recording.getRecording());
                System.out.println("recording.getMetadata :: " + recording.getMetadata());
                if (recording.getRecording() == 0)
                    uploadFile(recording, data[4]);
                if (recording.getMetadata() == 0)
                    updateMetadata(recording.getId(), data, recording.getStartTimestamp() + "", recording.getEndTimestamp() + "", data[4] + "/" + fileName);
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("<=== Home Fragment - onResume Exception ===> " + e.toString());
        }
    }

    private void uploadFile(Recording recording, String userId) {
        // Parsing any Media type file
        System.out.println("<=== Upload Recording - Started ========> ");
        File file = new File(recording.getPath());
        RequestBody requestBody = RequestBody.create(MediaType.parse("*/*"), file);
        MultipartBody.Part fileToUpload = MultipartBody.Part.createFormData("file", file.getName(), requestBody);
        ApiInterface apiService =
                ApiClient.getClient(activity, 3).create(ApiInterface.class);

        Call<BaseResponse> call = apiService.saveRecording(Integer.parseInt(userId), fileToUpload);
        call.enqueue(new Callback<BaseResponse>() {
            @Override
            public void onResponse(Call<BaseResponse> call, Response<BaseResponse> response) {
                RepositoryImpl repository = new RepositoryImpl(getActivity(), AppConstants.DATABASE_NAME);
                try {
                    BaseResponse serverResponse = response.body();
                    if (serverResponse != null && serverResponse.getStatusCode() == 1000) {
                        repository.updateRecording(recording.getId(), true, 1);
                        repository.deleteRecording(recording.getId());
                        application.setCaseId(null);
                        System.out.println("<=== Upload Recording - Success ===> " + serverResponse.getStatus());
                    } else {
                        System.out.println("<=== Upload Recording - Failed ===> " + serverResponse.getStatus());
                        repository.updateRecording(recording.getId(), true, 2);
                    }
                } catch (Exception e) {
                    System.out.println("<=== Upload Recording - Crash : ===> " + e.toString());
                    repository.updateRecording(recording.getId(), true, 2);
                }
            }

            @Override
            public void onFailure(Call<BaseResponse> call, Throwable t) {
                RepositoryImpl repository = new RepositoryImpl(getActivity(), AppConstants.DATABASE_NAME);
                repository.updateRecording(recording.getId(), true, 2);
                System.out.println("<=== Exceptions ===> " + t.toString());
            }
        });
    }

    private void updateMetadata(long recordId, String[] data, String startTime, String endTime, String fileName) {
        // Parsing any Media type file
        System.out.println("<=== Update Metadata - Started ========> ");
        if (data != null && data.length > 0) {
            ApiInterface apiService =
                    ApiClient.getClient(activity, 2).create(ApiInterface.class);
            JsonObject object = ApiUtil.getCaseMetaDataObject(data[0],
                    "", Integer.parseInt(data[1]), startTime,
                    endTime, data[2], data[2], data[3], fileName);
            Call<BaseResponse> call = apiService.updateCaseMetaData(object);
            call.enqueue(new Callback<BaseResponse>() {
                @Override
                public void onResponse(Call<BaseResponse> call, Response<BaseResponse> response) {
                    RepositoryImpl repository = new RepositoryImpl(getActivity(), AppConstants.DATABASE_NAME);
                    try {
                        BaseResponse serverResponse = response.body();
                        if (serverResponse != null && serverResponse.getStatusCode() == 1000) {
                            repository.updateRecording(recordId, false, 1);
                            repository.deleteRecording(recordId);
                            application.setCaseId(null);
                            System.out.println("<=== Update Metadata - Success ===> " + serverResponse.getStatus());
                        } else {
                            System.out.println("<=== Update Metadata - Failed ===> " + serverResponse.getStatus());
                            repository.updateRecording(recordId, false, 2);
                        }
                    } catch (Exception e) {
                        System.out.println("<=== Update Metadata - Crash : ===> " + e.toString());
                        repository.updateRecording(recordId, false, 2);
                    }
                }

                @Override
                public void onFailure(Call<BaseResponse> call, Throwable t) {
                    RepositoryImpl repository = new RepositoryImpl(getActivity(), AppConstants.DATABASE_NAME);
                    repository.updateRecording(recordId, false, 2);
                    System.out.println("<=== Exceptions ===> " + t.toString());
                }
            });
        }
    }

    @Override
    public void onItemClick(ProjectData partner) {
        if (dialog != null && dialog.isShowing())
            dialog.dismiss();
        projectData = partner;
        tvPartners.setText(projectData.getProjectName());
    }

    @Override
    public void onItemClick(ProjectConfigData partner) {
        if (dialog != null && dialog.isShowing())
            dialog.dismiss();
        configData = partner;
        tvConfigs.setText(configData.getProjectName());
    }

    class CasesCallBack implements Callback<CasesList> {
        @Override
        public void onResponse(Call<CasesList> call, Response<CasesList> response) {
            activity.hideProgressDialog();
            Log.v("1111", "json" + new Gson().toJson(response.body()));

            System.out.println("<=== CasesCallBack -LIST Response : ===> " + new Gson().toJson(response.body()));
            CasesList casesList = response.body();

            if (casesList != null) {
                List<Case> caseData = casesList.getCaseList();
                if (caseData != null && caseData.size() > 0) {
                    populateCases(caseData);
                    binding.recyclerView.setVisibility(View.VISIBLE);
                    binding.tvNoCases.setVisibility(View.GONE);
                    binding.loutPages.setVisibility(View.VISIBLE);
                    casesCount = caseData.get(0).getApplicationCount();
                    drawPages();
                    binding.tvCount.setText("Total Cases : " + casesCount);
                    if (currentPage == 1) {
                        binding.tvPrevious.setVisibility(View.GONE);
                    } else {
                        binding.tvPrevious.setVisibility(View.VISIBLE);
                    }

                    if (currentPage == lastPage) {
                        binding.tvNext.setVisibility(View.GONE);
                    } else {
                        binding.tvNext.setVisibility(View.VISIBLE);
                    }
                } else {
                    noCases();
                }
            } else {
                noCases();
            }
        }

        @Override
        public void onFailure(Call<CasesList> call, Throwable t) {
            System.out.println("<=== CasesCallBack - Response : ===> " + t.getMessage());
            activity.hideProgressDialog();
            noCases();
        }
    }
}