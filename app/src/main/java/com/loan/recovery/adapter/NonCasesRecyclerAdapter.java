package com.loan.recovery.adapter;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.loan.recovery.LoanApplication;
import com.loan.recovery.R;
import com.loan.recovery.activity.HomeActivity;
import com.loan.recovery.fragments.DatePickerFragment;
import com.loan.recovery.fragments.HomeFragment;
import com.loan.recovery.retrofit.ApiClient;
import com.loan.recovery.retrofit.ApiInterface;
import com.loan.recovery.retrofit.model.BaseResponse;
import com.loan.recovery.retrofit.model.CaseData;
import com.loan.recovery.retrofit.model.FileUploadResponse;
import com.loan.recovery.retrofit.model.Partner;
import com.loan.recovery.retrofit.model.PaymentType;
import com.loan.recovery.retrofit.model.Status;
import com.loan.recovery.retrofit.model.UserData;
import com.loan.recovery.retrofit.util.ApiUtil;
import com.loan.recovery.util.AppConstants;
import com.loan.recovery.util.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NonCasesRecyclerAdapter extends RecyclerView.Adapter<NonCasesRecyclerAdapter.NonCaseViewHolder>
        implements DatePickerFragment.UseDateListener, StatusListAdapter.ItemClickListener,
        PaymentTypeListAdapter.ItemClickListener, ContactModeListAdapter.ItemClickListener {

    //private final List<CaseData> caseDataList;
    private final List<JSONObject> caseDataList;
    private final HomeActivity context;

    private static int currentPosition = -1;
    private final LoanApplication application;

    private AlertDialog dialog, dialog2, otherConDialog;
    private List<Status> statusList;
    private List<PaymentType> paymentTypes;
    private EditText etDate;
    int mPatnerId;
    private Status statusSelected;
    private PaymentType paymentTypeSelected;
    private AppCompatTextView tvStatus, tvPaymentType, tvDate, tvAmount, tvMode;
    private View view, otherConView;
    private CaseData currentCase = null;
    private String strRemarks;
    private HomeFragment homeFragment;
    private int modePosition = 0;
    private StatusListAdapter listAdapter;

    public NonCasesRecyclerAdapter(List<JSONObject> caseData, HomeActivity context, HomeFragment fragment, int mPatnerId) {
        this.caseDataList = caseData;
        this.context = context;
        this.homeFragment = fragment;
        application = LoanApplication.getInstance();
        paymentTypeSelected = new PaymentType();
        paymentTypeSelected.setPaymentTypeName("Select Type");
        paymentTypeSelected.setPaymentTypeId(0000);
        statusSelected = new Status();
        statusSelected.setStatusName("Select Status");
        statusList = application.getPhoneStatusList();
    }

    @NonNull
    @Override
    public NonCaseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.non_case_item_layout, parent, false);
        return new NonCaseViewHolder(v);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(final NonCaseViewHolder holder, final int position) {
        try {
            JSONObject json = caseDataList.get(position);
            String casedata =json.getString("caseList");
            JSONObject casejson = new JSONObject(casedata);
            if (holder instanceof NonCaseViewHolder) {
                NonCaseViewHolder genericViewHolder = (NonCaseViewHolder) holder;
                genericViewHolder.jsonObject = casejson;
                String strPhone = casejson.getString("phone_number");
                String aiqNumber = application.getPhoneNumbersList().get(0).getUserPhone();
                holder.tvName.setText(": " + casejson.getString("assigned_to"));
                holder.textViewName.setText(casejson.getString("user_name"));
                holder.imgEdit.setTag(casejson.getString("status_code"));
                holder.tvDueAmount.setText(": " + casejson.getString("last_name"));
                holder.tvPrinciple.setText(": " + casejson.getString("email_address"));
                holder.tvPhoneNumber.setText(": " + strPhone);
                if (casejson.has("lu_case_realty_id")) {
                    holder.tvAgreementId.setText(":" + "#" + casejson.getString("lu_case_realty_id"));
                }
                holder.tvScore.setText(": " + casejson.getString("first_name"));
//            holder.tvCity.setText(": " + ((caseData.getOffCityName() != null) ? caseData.getOffCityName() : "NA"));
                holder.tvStatus.setText(": " + casejson.getString("status_code"));
//            holder.tvRemarks.setText(": " + caseData.getRemarks());
//            holder.tvBucket.setText(": " + caseData.getPartnerBucketNumber());
//            holder.tvContactMode.setText(": " + getModeString(caseData.getContactMode()));
//            holder.tvEMI.setText(": \u20B9 " + caseData.getEmiAmount());

//            if (caseData.getShowOtherContacts() == 'Y') {
//                holder.imgOtherContact.setVisibility(View.VISIBLE);
//            }
//
//            if (caseData.getAltPhoneNumber1() != null) {
//                holder.tvOtherContact1.setText(caseData.getAltPhoneNumber1());
//                holder.trOtherContact1.setVisibility(View.VISIBLE);
//
//                if (caseData.getAltPhoneNumber2() != null) {
//                    holder.tvOtherContact2.setText(caseData.getAltPhoneNumber2());
//                    holder.trOtherContact2.setVisibility(View.VISIBLE);
//                }
//            }

//            holder.btnCallOtherContact1.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    Toast.makeText(context, strPhone, Toast.LENGTH_SHORT).show();
//                    try {
//                        String finalAltNum1 = caseData.getAltPhoneNumber1();
//                        currentPosition = position;
//                        if (finalAltNum1.length() > 9) {
//                            createCallSessionOtherContacts(caseData, finalAltNum1, aiqNumber);
//                            saveUpdatedDate(caseData.getRetraCaseId());
//                            currentCase = caseData;
//                            application.setCaseId(currentCase.getCaseUuid());
//                        } else {
//                            showCannotUpdateStatusDialog("Customer phone number is not valid : " + finalAltNum1);
//                        }
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                        System.out.println("<=== Exceptions 1 ===> " + e.toString());
//                        application.setCaseId(null);
//                    }
//                }
//            });

//            holder.btnCallOtherContact2.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    Toast.makeText(context, caseData.getAltPhoneNumber2(), Toast.LENGTH_SHORT).show();
//                    try {
//                        String finalAltNum2 = caseData.getAltPhoneNumber2();
//                        currentPosition = position;
//                        if (finalAltNum2.length() > 9) {
//                            createCallSessionOtherContacts(caseData, finalAltNum2, aiqNumber);
//                            saveUpdatedDate(caseData.getRetraCaseId());
//                            currentCase = caseData;
//                            application.setCaseId(currentCase.getCaseUuid());
//                        } else {
//                            showCannotUpdateStatusDialog("Customer phone number is not valid : " + finalAltNum2);
//                        }
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                        System.out.println("<=== Exceptions 1 ===> " + e.toString());
//                        application.setCaseId(null);
//                    }
//                }
//            });


//            strPhone = aiqNumber;
//
//            holder.linearLayout.setVisibility(View.GONE);
//
//            //if the position is equals to the item position which is to be expanded
//            if (currentPosition == position) {
//                //creating an animation
//                Animation slideDown = AnimationUtils.loadAnimation(context, R.anim.slide_down);
//
//                //toggling visibility
//                holder.linearLayout.setVisibility(View.VISIBLE);
//
//                //adding sliding effect
//                holder.linearLayout.startAnimation(slideDown);
//            }
//
                holder.textViewName.setOnClickListener(v -> {
                    //getting the position of the item to expand it
                    currentPosition = position;

                    //reloading the list
                    notifyDataSetChanged();
                });
                if (strPhone == null || strPhone.isEmpty())
                    holder.imgCall.setVisibility(View.INVISIBLE);
                else
                    holder.imgCall.setVisibility(View.VISIBLE);
                String finalStrPhone = strPhone;
                holder.imgCall.setOnClickListener(v -> {
                    try {
                        currentPosition = position;
                        boolean isCallActive = Utils.isCallActive(context);

                        if (finalStrPhone.length() > 9 && !isCallActive) {
                            if (!Utils.isOSOreo())
                                if (true) {
                                    createCallSession(genericViewHolder.jsonObject, aiqNumber);
                                   // saveUpdatedDate(caseData.getRetraCaseId());
                                   // currentCase = caseData;
                                    application.setCaseId(currentCase.getCaseUuid());
                                    String strTransKey = Utils.getTransKey(application.getCurrentUser());
                                    String builder = currentCase.getRetraCaseId() +
                                            "&&&" +
                                            getPartnerId(currentCase.getPartnerName()) +
                                            "&&&" +
                                            currentCase.getPhoneNumber() +
                                            "&&&" +
                                            strTransKey +
                                            "&&&" +
                                            application.getCurrentUser().getUserId();
                                    application.setMetaData(builder);
                                    application.setMetaData("");
                                }
//                    if (!Utils.isOSOreo())
//                    if(true) {
//                        createCallSession(caseData, aiqNumber);
//                        saveUpdatedDate(caseData.getRetraCaseId());
//                        currentCase = caseData;
//                        application.setCaseId(currentCase.getCaseUuid());
//                        String strTransKey = Utils.getTransKey(application.getCurrentUser());
//                        String builder = currentCase.getRetraCaseId() +
//                                "&&&" +
//                                getPartnerId(currentCase.getPartnerName()) +
//                                "&&&" +
//                                currentCase.getPhoneNumber() +
//                                "&&&" +
//                                strTransKey +
//                                "&&&" +
//                                application.getCurrentUser().getUserId();
//                        application.setMetaData(builder);
//                    application.setMetaData("");
//                    }
                        } else if (isCallActive) {
                            Toast.makeText(context, "Already call running", Toast.LENGTH_SHORT).show();
                        } else {
                            showCannotUpdateStatusDialog("Customer phone number is not valid : " + finalStrPhone);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        System.out.println("<=== Exceptions 1 ===> " + e.toString());
                        application.setCaseId(null);
                    }
                });
            }



            holder.imgEdit.setOnClickListener(v -> {
                currentPosition = position;
                //currentCase = caseData;
               // int statusCode = caseData.getStatusCode();
                try {
                    int statusCode = casejson.getInt("status_code");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                List<Status> statuses = application.getPhoneStatusList();
                // 1290 - Partially collected
                //1390 - Full EMI Collected
                showStatusUpdateDialog(casejson, 'N');
//                if (statusCode == 1290 || statusCode == 1390) {
//                    if (caseData.getMultiPayment().equalsIgnoreCase("N")) {
//                        showCannotUpdateStatusDialog(null);
//                    } else {
//                        showStatusUpdateDialog(caseData, 'N');
//                    }
//                }else {
//                    showStatusUpdateDialog(caseData, 'N');
//                }
            });

//            holder.imgSave.setOnClickListener(v -> {
//                currentCase = caseData;
//                saveLatLng("0000", "1002", null);
//                pickImage();
//            });
        } catch (Exception e) {
            System.out.println("<=== Exceptions 2 ===> " + e.toString());
            e.printStackTrace();
            application.setCaseId(null);
        }
    }

    private void pickImage() {
        Intent chooseFile = new Intent(Intent.ACTION_GET_CONTENT);
        chooseFile.setType("*/*");
        chooseFile = Intent.createChooser(chooseFile, "Choose a file");
        homeFragment.startActivityForResult(chooseFile, AppConstants.PICK_FILE_RESULT_CODE);
    }

    @Override
    public int getItemCount() {
        return caseDataList.size();
    }

    private void showCannotUpdateStatusDialog(String message) {
        dialog2 = new AlertDialog.Builder(context).create();
        dialog2.setCanceledOnTouchOutside(false);
        dialog2.setCancelable(false);

        view = LayoutInflater.from(context).inflate(R.layout.no_case_status_update_dialog, null);
        Button btOk = view.findViewById(R.id.btOk);
        btOk.setOnClickListener(v -> dialog2.dismiss());
        if (message != null) {
            AppCompatTextView tvMessage = view.findViewById(R.id.tvMessage);
            tvMessage.setText(message);
        }

        dialog2.setView(view);
        dialog2.show();
    }

    private void showStatusUpdateDialog(JSONObject data, char isUpdateMendetory) {
        paymentTypes = application.getPaymentTypes();

        dialog = new AlertDialog.Builder(context).create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);

        view = LayoutInflater.from(context).inflate(R.layout.case_status_update_dialog, null);
        RelativeLayout loutStatus = view.findViewById(R.id.loutStatus);
        RelativeLayout loutMode = view.findViewById(R.id.loutMode);
        RelativeLayout loutPaymentType = view.findViewById(R.id.loutPaymentMode);
        RelativeLayout crlayout = view.findViewById(R.id.lout9);
        tvStatus = view.findViewById(R.id.tvSelectStatus);
        tvPaymentType = view.findViewById(R.id.tvSelectPaymentMode);
        tvDate = view.findViewById(R.id.tvDate);
        tvAmount = view.findViewById(R.id.tvAmount);
        tvMode = view.findViewById(R.id.tvSelectMode);
        if(mPatnerId == 1024){
            crlayout.setVisibility(View.GONE);
        }else{
            crlayout.setVisibility(View.VISIBLE);

        }

        loutStatus.setOnClickListener(v -> showCustomSpinnerDialog(1, null));
        loutPaymentType.setOnClickListener(v -> showCustomSpinnerDialog(2, paymentTypes));
        loutMode.setOnClickListener(v -> showCustomSpinnerDialog(3, null));

        final Button btSave = view.findViewById(R.id.btSave);
        final Button btCancel = view.findViewById(R.id.btCancel);
        final AppCompatTextView tvPhone = view.findViewById(R.id.tvPhoneNumber);
        final AppCompatTextView tvName = view.findViewById(R.id.tvName);
        final AppCompatTextView tvCr = view.findViewById(R.id.tvCr);
        final AppCompatTextView tvMinDue = view.findViewById(R.id.tvMinDue);
        final AppCompatTextView tvPOS = view.findViewById(R.id.tvPOS);

        etDate = view.findViewById(R.id.etDate);
        EditText etRemarks = view.findViewById(R.id.etRemarks);
        EditText etAmount = view.findViewById(R.id.etAmount);
        EditText etTransRef = view.findViewById(R.id.etTrRefNum);
        try {
            tvPhone.setText(" : " + data.getString("phone_number"));
            tvName.setText(" : " + data.getString("first_name")+""+data.getString("last_name"));
           // tvCr.setText(" : " + data.getPartnerCaseId());
           // tvMinDue.setText(" : " + data.getDueAmount());
            //tvPOS.setText(" : " + data.getOdPrincipal());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        btCancel.setOnClickListener(v -> dialog.dismiss());

        if (isUpdateMendetory == 'Y') {
            btCancel.setVisibility(View.GONE);
            view.findViewById(R.id.imgClose).setVisibility(View.GONE);
        }

        view.findViewById(R.id.loutCalendar).setOnClickListener(v -> {
            boolean isMaxDate = false;
            int maxDays = 0;
            int partnerCode = 0;
            try {
                partnerCode = getPartnerId(data.getString("partner_name"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            //if (statusSelected.getStatusCode() == 1050 || statusSelected.getStatusCode() == 4020) {
            if (statusSelected.getStatusCode() == 8000 || statusSelected.getStatusCode() == 8100) {
                isMaxDate = true;
                maxDays = Utils.getMaxDays(partnerCode);
            }
            Utils.showCalendar(context, this, isMaxDate, maxDays);
        });

        view.findViewById(R.id.imgClose).setOnClickListener(v -> dialog.dismiss());

        dialog.setOnShowListener(dialog -> {
            InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(etAmount, InputMethodManager.SHOW_IMPLICIT);
        });

        btSave.setOnClickListener(v -> {
            try {
                boolean callSaveService = true;
                if(application.getIsTrackingGps()=='Y') {
                    if (!Utils.isGPSEnabled(context)) {
                        JsonObject jsonEvent = new JsonObject();
                        jsonEvent.addProperty("userId",application.getCurrentUser().getUserId());
                        jsonEvent.addProperty("eventId",19);
                        jsonEvent.addProperty("appVersion",context.getString(R.string.app_version));
                        new SaveUserEventData().execute(jsonEvent);
                        buildAlertMessageNoGps();
                        callSaveService = false;
                    } else {
                        saveUserLatLong(17);
                    }
                }
                if(callSaveService) {
                    Utils.hideSoftKeyboard(context, etAmount);
                    int strPaymentType = paymentTypeSelected.getPaymentTypeId();
                    String strDate = etDate.getText().toString().trim();
                   // String strAmount = etAmount.getText().toString().trim();
                    String strRemarks = etRemarks.getText().toString().trim();
                    String strTransRef = etTransRef.getText().toString().trim();
                    String strTransKey = Utils.getTransKey(application.getCurrentUser());
                    int statusCode = statusSelected.getStatusCode();
                  //  double amount = (strAmount.equals("")) ? 0 : Double.parseDouble(strAmount);
                    if (statusCode == 0000)
                        Toast.makeText(context, "Select Status", Toast.LENGTH_SHORT).show();
                    else if (TextUtils.isEmpty(strRemarks))
                        Toast.makeText(context, "Enter Remarks", Toast.LENGTH_SHORT).show();
                    else if (statusCode == 8000 || statusCode == 8100 ) {
                        if (TextUtils.isEmpty(strDate))
                            Toast.makeText(context, "Select Date", Toast.LENGTH_SHORT).show();


                        } else {
                            if (Utils.isConnected(context)) {
                                dialog.dismiss();
                                saveStatus(data, statusCode, strTransKey, strRemarks,
                                        strDate, isPaymentDone(statusCode),
                                         "", isFollowUpReq(statusCode), 0);
                            } else
                                Toast.makeText(context, "No Internet", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        dialog.dismiss();
                        if (Utils.isConnected(context)) {
//                            saveStatus(data, statusCode, strTransKey, strRemarks,
//                                    strDate, isPaymentDone(statusCode),
//                                     strTransRef, isFollowUpReq(statusCode), strPaymentType);
                        } else
                            Toast.makeText(context, "No Internet", Toast.LENGTH_SHORT).show();
                    }

            } catch (Exception e) {
                System.out.println("<=== Save Status - Exception : ===> " + e.toString());
            }
        });

        dialog.setView(view);
        dialog.show();
    }

    private String getModeString(int mode) {
        String modeName = "Phone";
        if (mode == 12)
            modeName = "Field Visit";
        return modeName;
    }

    private String isPaymentDone(int statusCode) {
        String isPaymentDone = "N";
        if (statusCode == 1390 || statusCode == 1290 || statusCode == 4040 || statusCode == 4050)
            isPaymentDone = "Y";
        return isPaymentDone;
    }

    private String isFollowUpReq(int statusCode) {
        String isPaymentDone = "Y";
        if (statusCode == 1390 || statusCode == 1290 || statusCode == 4040 || statusCode == 4050)
            isPaymentDone = "N";
        return isPaymentDone;
    }

    @SuppressLint("ResourceAsColor")
    private void showCustomSpinnerDialog(int dataType, List<PaymentType> paymentTypeList) {
        View view = context.getLayoutInflater().inflate(R.layout.list_dialog, null);
        final ListView listView = view.findViewById(R.id.listItems);
        listView.setTextFilterEnabled(true);
        if (dataType == 1) {
            listAdapter = new StatusListAdapter(context, R.layout.project_list_item, statusList);
            listAdapter.setClickListener(this);
            listView.setAdapter(listAdapter);
        } else if (dataType == 2) {
            final PaymentTypeListAdapter listAdapter = new PaymentTypeListAdapter(context, R.layout.project_list_item, paymentTypeList);
            listAdapter.setClickListener(this);
            listView.setAdapter(listAdapter);
        } else {
            String[] modes = {"Phone", "Field Visit"};
            final ContactModeListAdapter listAdapter = new ContactModeListAdapter(context, R.layout.project_list_item, modes);
            listAdapter.setClickListener(this);
            listView.setAdapter(listAdapter);
        }

        dialog2 = new AlertDialog.Builder(context).create();
        dialog2.setCanceledOnTouchOutside(false);
        dialog2.setCancelable(false);

        dialog2.setView(view);
        dialog2.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancel", (dialog, which) -> dialog.dismiss());

        dialog2.setOnShowListener(arg0 -> dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(R.color.colorPrimary));
        dialog2.show();
    }

    @Override
    public void useDate(String date) {
        etDate.setText(date);
    }

    @Override
    public void onItemClick(Status status) {
        if (dialog2 != null && dialog2.isShowing())
            dialog2.dismiss();
        statusSelected = status;
        tvStatus.setText(status.toString());
        Utils.checkStatus(view, statusSelected.getStatusCode(), tvDate, tvAmount);
    }

    @Override
    public void onItemClick(PaymentType paymentType) {
        if (dialog2 != null && dialog2.isShowing())
            dialog2.dismiss();
        paymentTypeSelected = paymentType;
        tvPaymentType.setText(paymentTypeSelected.getPaymentTypeName());
    }

    @Override
    public void onItemClick(String modeSelected, int position) {
        if (dialog2 != null && dialog2.isShowing())
            dialog2.dismiss();
        tvMode.setText(modeSelected);

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
            Utils.contactModeChanged(view);
        }
        modePosition = position;
    }

    class CasesStatusCallBack implements Callback<BaseResponse> {
        int statusCode, modeType;

        CasesStatusCallBack(int status, int mode) {
            statusCode = status;
            modeType = mode;
        }

        @Override
        public void onResponse(Call<BaseResponse> call, Response<BaseResponse> response) {
            context.hideProgressDialog();
            System.out.println("<=== CasesCallBack - Response : ===> " + new Gson().toJson(response.body()));
            BaseResponse baseResponse = response.body();
            if (baseResponse == null) {
                Toast.makeText(context, "Status is not updated", Toast.LENGTH_SHORT).show();
            } else {
                if (baseResponse.getStatusCode() == 1000) {
                    if (modeType == 1)
                        saveLatLng(statusCode + "", "1001", null);
//                    CaseData caseData = caseDataList.get(currentPosition);
//                    caseData.setStatusCode(statusSelected.getStatusCode());
//                    caseData.setStatusName(statusSelected.getStatusName());
//                    caseData.setRemarks(strRemarks);
//                    caseDataList.set(currentPosition, caseData);
//                    notifyItemChanged(currentPosition);
                    Toast.makeText(context, "Status is updated successfully", Toast.LENGTH_SHORT).show();
                } else if (baseResponse.getStatusCode() == 2345) {
                    Toast.makeText(context, "Amount is already collected for this case", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(context, "Status is not updated", Toast.LENGTH_SHORT).show();
                }
            }
        }

        @Override
        public void onFailure(Call<BaseResponse> call, Throwable t) {
//            Toast.makeText(context, "Status is not updated", Toast.LENGTH_SHORT).show();
            System.out.println("<=== CasesCallBack - Response : ===> " + t.getMessage());
            context.hideProgressDialog();
        }
    }

    private class SaveUserEventData extends AsyncTask<JsonObject, Void, Void> {

        @Override
        protected Void doInBackground(JsonObject... jsonEvent) {
            ApiInterface apiService =
                    ApiClient.getClient(context, 2).create(ApiInterface.class);

            JsonObject object = null;
            try {
                object = ApiUtil.getSaveEventObject(jsonEvent[0]);
            } catch (Exception e) {
                e.printStackTrace();
            }

            Call<BaseResponse> call = apiService.saveUserEvent(object);
            call.enqueue(new Callback<BaseResponse>() {
                @Override
                public void onResponse(Call<BaseResponse> call, Response<BaseResponse> response) {
                    try {
                        BaseResponse serverResponse = response.body();
                        if (serverResponse != null && serverResponse.getStatusCode() == 1000) {
                            System.out.println("<=== saveJobStartData - Success ===> " + serverResponse.getStatus());
                        } else {
                            System.out.println("<=== saveJobStartData - Failed ===> ");
                        }
                    } catch (Exception e) {
                        System.out.println("<=== saveJobStartData - Crash : ===> " + e.toString());
                    }
                }

                @Override
                public void onFailure(Call<BaseResponse> call, Throwable t) {
                    System.out.println("<=== Exceptions ===> " + t.toString());
                }
            });
            return null;
        }
    }

    public void saveFile(File file) {
        context.showProgressDialog("Please Wait", "Uploading Case file...");

        // Parsing any Media type file
        System.out.println("<=== Upload File - Started ========> ");

        RequestBody requestBody = RequestBody.create(MediaType.parse("*/*"), file);

        MultipartBody.Part fileToUpload = MultipartBody.Part.createFormData("imagesArray", file.getName(), requestBody);
        String filePoints = ApiUtil.getPaymentFileObject(file).toString();

        RequestBody caseId = RequestBody.create(MediaType.parse("text/plain"), currentCase.getRetraCaseId());
        RequestBody filesPoints = RequestBody.create(MediaType.parse("text/plain"), filePoints);

        ApiInterface apiService =
                ApiClient.getClient(context, 1).create(ApiInterface.class);

        Call<FileUploadResponse> call = apiService.uploadFileWithPartMap(fileToUpload, caseId, filesPoints);

        call.enqueue(new Callback<FileUploadResponse>() {
            @Override
            public void onResponse(Call<FileUploadResponse> call, Response<FileUploadResponse> response) {
                try {
                    context.hideProgressDialog();
                    FileUploadResponse serverResponse = response.body();
                    if (serverResponse != null && serverResponse.getMessage().equalsIgnoreCase("success")) {
                        saveLatLng(currentCase.getStatusCode() + "", "1002", serverResponse.getFileRef());
                        showCannotUpdateStatusDialog("File(s) uploaded successfully.");
                    } else {
                        showCannotUpdateStatusDialog("Oops, File(s) not uploaded");
                        System.out.println("<=== Upload Receipt - Failed ===> " + serverResponse.getStatus());
                    }
                } catch (Exception e) {
                    System.out.println("<=== Upload Receipt - Crash : ===> " + e.toString());
                    showCannotUpdateStatusDialog("Oops, File(s) not uploaded.");
                }
            }

            @Override
            public void onFailure(Call<FileUploadResponse> call, Throwable t) {
                System.out.println("<=== Exceptions ===> " + t.toString());
                showCannotUpdateStatusDialog("Oops, File(s) not uploaded.");
                context.hideProgressDialog();
            }
        });
    }

    public void showConfirmationDialog(File file, boolean isFileUpload, JSONObject data, int statusCode, String strTransKey,
                                       String strRemarks, String nextActionDate,
                                       double amount, String strTransRef, int strPaymentType) {
        final AlertDialog dialogConfirm = new AlertDialog.Builder(context).create();
        View view = context.getLayoutInflater().inflate(R.layout.confirm_dialog, null);

        AppCompatTextView tvMessage = view.findViewById(R.id.tvMessage);
        AppCompatTextView tvYes = view.findViewById(R.id.tvYes);
        AppCompatTextView tvNo = view.findViewById(R.id.tvNo);
        if (isFileUpload)
            tvMessage.setText("Are you sure, you want to save this file : " + file.getName());
        else
            tvMessage.setText("The amount entered is different from the due amount. Are you sure to submit?");
        tvYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogConfirm.dismiss();
                if (isFileUpload)
                    saveFile(file);
                else {
                    dialog.dismiss();
                    saveStatus(data, statusCode, strTransKey, strRemarks,
                            nextActionDate, isPaymentDone(statusCode),
                             strTransRef, isFollowUpReq(statusCode), strPaymentType);
                }
            }
        });

        tvNo.setOnClickListener(v -> dialogConfirm.dismiss());

        dialogConfirm.setView(view);
        dialogConfirm.show();
    }

    private void createCallSession(JSONObject caseDatajson, String aiq) {
        UserData currentUser = application.getCurrentUser();
        String agentNumber = application.getAgentPhoneNumber();
        String temp = agentNumber.replace(" ", "");
//        String operatorNumber = application.getCallingOperator().equals("VIRTUO")
//                ?Integer.toString(application.getVirtuoId())
//                :aiq;
        try {
            if (temp != null && caseDatajson.getString("phone_number") != null && aiq != null) {
                ApiInterface apiService =
                        ApiClient.getClient(context, 4).create(ApiInterface.class);
                JsonObject object;
                object = ApiUtil.getCallSessionObject(caseDatajson.getString("lu_case_realty_id"),
                        "", caseDatajson.getString("phone_number"),
                        agentNumber, aiq,
                        Utils.getTransKey(currentUser));

                Call<BaseResponse> call = apiService.createCallSession(object);
                if ( caseDatajson.getString("status_code").equalsIgnoreCase("8200")) {
                    showStatusUpdateDialog(caseDatajson, 'Y');
                }
                call.enqueue(new CallSessionCallBack(aiq, caseDatajson.getString("phone_number")));
            } else
                Toast.makeText(context, "Invalid phone numbers", Toast.LENGTH_SHORT).show();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void createCallSessionOtherContacts(CaseData caseData, String phone, String aiq) {
        UserData currentUser = application.getCurrentUser();
        String agentNumber = application.getAgentPhoneNumber();
        String temp = agentNumber.replace(" ", "");
        if (temp != null && phone != null && aiq != null) {
            ApiInterface apiService =
                    ApiClient.getClient(context, 4).create(ApiInterface.class);
            JsonObject object;
            object = ApiUtil.getCallSessionObject(caseData.getRetraCaseId(),
                    caseData.getPortfolioId(), phone,
                    agentNumber, aiq,
                    Utils.getTransKey(currentUser));
            Call<BaseResponse> call = apiService.createCallSession(object);
            if (caseData.getStatusCode() != 1290 &&
                    caseData.getStatusCode() != 1390 &&
                    caseData.getStatusCode() != 4040 &&
                    caseData.getStatusCode() != 4050) {
                //showStatusUpdateDialog(caseData, 'Y');
            }
            call.enqueue(new CallSessionCallBack(aiq, phone));
        } else
            Toast.makeText(context, "Invalid phone numbers", Toast.LENGTH_SHORT).show();
    }

    private void saveStatus(JSONObject caseData, int status, String transKey,
                            String remarks, String nextActionDate,
                            String isPaymentDone, String transRef, String isFollowupReq, int paymentType) {
        strRemarks = remarks;
        context.showProgressDialog("Please Wait", "Updating Case Status...");

        if (modePosition == 1) {
            saveLatLng(status + "", "1001", null);
        }

        ApiInterface apiService =
                ApiClient.getClient(context, 2).create(ApiInterface.class);
        JsonObject object = null;
        try {
        if (status == 8000 || status == 8100) {
          
                object = ApiUtil.getPTPStatusObject(caseData.getString("lu_case_realty_id"),
                        status, remarks, caseData.getString("phone_number"), transKey,
                        isPaymentDone,  transRef, paymentType,
                        nextActionDate, nextActionDate, isFollowupReq);
            
        } else {
            object = ApiUtil.getStatusObject(caseData.getString("lu_case_realty_id"),
                    status, remarks, caseData.getString("phone_number"), transKey,
                    isPaymentDone, transRef, paymentType,
                    nextActionDate, nextActionDate, isFollowupReq);
        }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Call<BaseResponse> call = apiService.updateCaseStatus(object);
        call.enqueue(new CasesStatusCallBack(status, modePosition));
    }

    private void saveLatLng(String status, String pageId, String snapRefId) {
        // Parsing any Media type file
        System.out.println("<=== Update LatLng - Started ========> ");
        Location location = application.getCurrentLocation();
        double lat = 0.0;
        double longi = 0.0;
        if (location != null) {
            lat = location.getLatitude();
            longi = location.getLongitude();
        }
        ApiInterface apiService =
                ApiClient.getClient(context, 2).create(ApiInterface.class);
        JsonObject object = ApiUtil.getLatLngDataObject(currentCase.getCaseUuid(),
                lat, longi, currentCase.getPostalCode(),
                "", "", "", Utils.getTransKey(application.getCurrentUser()), pageId, status, snapRefId);
        Call<BaseResponse> call = apiService.saveLatLong(object);
        call.enqueue(new Callback<BaseResponse>() {
            @Override
            public void onResponse(Call<BaseResponse> call, Response<BaseResponse> response) {
                try {
                    BaseResponse serverResponse = response.body();
                    if (serverResponse != null && serverResponse.getStatusCode() == 1000) {
                        System.out.println("<=== Update LatLng - Success ===> " + serverResponse.getStatus());
                    } else {
                        System.out.println("<=== Update LatLng - Failed ===> ");
                    }
                } catch (Exception e) {
                    System.out.println("<=== Update LatLng - Crash : ===> " + e.toString());
                }
            }

            @Override
            public void onFailure(Call<BaseResponse> call, Throwable t) {
                System.out.println("<=== Exceptions ===> " + t.toString());
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void saveUpdatedDate(String caseUuid) {
        ApiInterface apiService =
                ApiClient.getClient(context, 2).create(ApiInterface.class);
        JsonObject updateBody = new JsonObject();
        updateBody.addProperty("luCaseId", caseUuid);
        Call<BaseResponse> call = apiService.updateCaseModifiedDate(updateBody);
        call.enqueue(new CasesUpdateDateCallBack());
    }

    class CallSessionCallBack implements Callback<BaseResponse> {
        String phoneNumber, customerPhone;

        CallSessionCallBack(String phone, String customerNumber) {
            phoneNumber = phone;
            customerPhone = customerNumber;
        }
        @Override
        public void onResponse(Call<BaseResponse> call, Response<BaseResponse> response) {
            System.out.println("<=== CallSessionCallBack - Response : ===> " + new Gson().toJson(response.body()));
            BaseResponse baseResponse = response.body();
            assert baseResponse != null;
            String dialingNumber = application.getIsDirectCall()=='Y'?customerPhone:phoneNumber;
            if (baseResponse.getStatusCode() == 1000) {
//                if(application.getCallingOperator().equals("VIRTUO")){
//
//                    OkHttpClient client = new OkHttpClient();
//                    String url = "http://103.35.69.18/c2csip.php?user=niranjan&pwd=Wonderful2021&" +
//                            "agent="+application.getVirtuoId()+
//                            "&customer="+customerPhone+
//                            "&uid="+baseResponse.getAgentCallSessionId();
//
//                    System.out.println("Url is :: "+url);
//
//                    Request request = new Request.Builder().url(url).build();
//                    client.newCall(request).enqueue(new okhttp3.Callback() {
//                        @Override
//                        public void onFailure(okhttp3.Call call, IOException e) {
//                            e.printStackTrace();
//                        }
//
//                        @Override
//                        public void onResponse(okhttp3.Call call, okhttp3.Response response) throws IOException {
//                            if(response.isSuccessful()){
//                                String val = response.body().string();
//                                System.out.println("---->> " +val);
//                            }
//                        }
//                    });
//                }
//                else {
                // Start the dialer app activity to make phone call
                Intent intent = new Intent(Intent.ACTION_CALL);
                // Send phone number to intent as data
                intent.setData(Uri.parse("tel:" + dialingNumber));
                context.startActivity(intent);

//                }
                System.out.println("<=== CallSessionCallBack - Success : ===> " + new Gson().toJson(response.body()));
            } else {
                System.out.println("<=== CallSessionCallBack - Failed : ===> " + new Gson().toJson(response.body()));
            }
        }

        @Override
        public void onFailure(Call<BaseResponse> call, Throwable t) {
            System.out.println("<=== CallSessionCallBack - Failed : ===> " + t.getMessage());
        }
    }

    class CasesUpdateDateCallBack implements Callback<BaseResponse> {
        @Override
        public void onResponse(Call<BaseResponse> call, Response<BaseResponse> response) {
            context.hideProgressDialog();
            System.out.println("<=== CasesUpdateDateCallBack - Response : ===> " + new Gson().toJson(response.body()));
            BaseResponse baseResponse = response.body();
            if (baseResponse == null) {
                Toast.makeText(context, "Status is not updated", Toast.LENGTH_SHORT).show();
                System.out.println("<=== CasesUpdateDateCallBack - Failed : ===> " + new Gson().toJson(response.body()));
            } else {
                if (baseResponse.getStatusCode() == 1000)
                    System.out.println("<=== CasesUpdateDateCallBack - Success : ===> " + new Gson().toJson(response.body()));
                else
                    System.out.println("<=== CasesUpdateDateCallBack - Failed : ===> " + new Gson().toJson(response.body()));
            }
        }

        @Override
        public void onFailure(Call<BaseResponse> call, Throwable t) {
            System.out.println("<=== CasesUpdateDateCallBack - Failure : ===> " + t.getMessage());
            context.hideProgressDialog();
        }
    }

    private int getPartnerId(String partnerName) {
        List<Partner> partners = application.getPartners();
        for (Partner partner : partners) {
            if (partner.getPartnerName().equalsIgnoreCase(partnerName))
                return partner.getPartnerId();
        }

        return 0;
    }

    static class NonCaseViewHolder extends RecyclerView.ViewHolder {
        TextView textViewName, tvAgreementId, tvScore, tvDueAmount, tvPrinciple, tvPhoneNumber,
                tvName, tvStatus, tvCity, tvRemarks, tvBucket, tvContactMode, tvEMI, tvOtherContact1, tvOtherContact2;
        ImageView imgCall, imgEdit, imgSave, imgOtherContact;
        LinearLayout linearLayout;
        RelativeLayout loutParent;
        ImageButton btnCallOtherContact1, btnCallOtherContact2;
        TableRow trOtherContact1, trOtherContact2;
        JSONObject jsonObject;

        NonCaseViewHolder(View itemView) {
            super(itemView);

            textViewName = itemView.findViewById(R.id.textViewName);
            tvAgreementId = itemView.findViewById(R.id.tvAgreementId);
            tvScore = itemView.findViewById(R.id.tvScore);
            tvDueAmount = itemView.findViewById(R.id.tvDueAmount);
            tvPrinciple = itemView.findViewById(R.id.tvPrinciple);
            tvPhoneNumber = itemView.findViewById(R.id.tvPhoneNumber);
            tvName = itemView.findViewById(R.id.tvName);
            tvStatus = itemView.findViewById(R.id.tvStatus);
            tvCity = itemView.findViewById(R.id.tvCity);
            tvRemarks = itemView.findViewById(R.id.tvRemarks);
            tvBucket = itemView.findViewById(R.id.tvBucket);
            tvContactMode = itemView.findViewById(R.id.tvContactMode);
            tvEMI = itemView.findViewById(R.id.tvEmi);
            tvOtherContact1 = itemView.findViewById(R.id.tvOtherContact1);
            tvOtherContact2 = itemView.findViewById(R.id.tvOtherContact2);
            btnCallOtherContact1 = itemView.findViewById(R.id.btnOtherContact1);
            btnCallOtherContact2 = itemView.findViewById(R.id.btnOtherContact2);
            trOtherContact1 = itemView.findViewById(R.id.trOtherContact1);
            trOtherContact2 = itemView.findViewById(R.id.trOtherContact2);


            imgCall = itemView.findViewById(R.id.imgCall);
            imgEdit = itemView.findViewById(R.id.imgEdit);
            imgSave = itemView.findViewById(R.id.imgSave);
            imgOtherContact = itemView.findViewById(R.id.imgOtherConacts);

            linearLayout = itemView.findViewById(R.id.linearLayout);
            loutParent = itemView.findViewById(R.id.loutParent);
            imgSave.setVisibility(View.GONE);
        }
    }

    private void saveUserLatLong(int eventId) {

        // Parsing any Media type file
        System.out.println("<=== Update Metadata - Started ========> ");
        Location location = application.getCurrentLocation();
        ApiInterface apiService =
                ApiClient.getClient(context, 2).create(ApiInterface.class);
        double lat = 0.0;
        double longi = 0.0;
        JsonObject object = null;
        if (location != null) {
            lat = location.getLatitude();
            longi = location.getLongitude();
        }
        try{
            object = ApiUtil.getJobStartDataObject(
                    lat, longi, Utils.getTransKey(application.getCurrentUser()), eventId);
        }catch(Exception e){
            System.out.println(e);
        }

        Call<BaseResponse> call = apiService.startJob(object);
        call.enqueue(new Callback<BaseResponse>() {
            @Override
            public void onResponse(Call<BaseResponse> call, Response<BaseResponse> response) {
                try {
                    BaseResponse serverResponse = response.body();
                    if (serverResponse != null && serverResponse.getStatusCode() == 1000) {
                        System.out.println("<=== saveJobStartData - Success ===> " + serverResponse.getStatus());
                    } else {
                        System.out.println("<=== saveJobStartData - Failed ===> ");
                    }
                } catch (Exception e) {
                    System.out.println("<=== saveJobStartData - Crash : ===> " + e.toString());
                }
            }

            @Override
            public void onFailure(Call<BaseResponse> call, Throwable t) {
                System.out.println("<=== Exceptions ===> " + t.toString());
            }
        });
    }

    private void buildAlertMessageNoGps() {

        final AlertDialog dialogConfirm = new AlertDialog.Builder(context).create();
        View view = LayoutInflater.from(context).inflate(R.layout.confirm_dialog, null);


        AppCompatTextView tvMessage = view.findViewById(R.id.tvMessage);
        AppCompatTextView tvYes = view.findViewById(R.id.tvYes);
        AppCompatTextView tvNo = view.findViewById(R.id.tvNo);
        tvYes.setText("Enable");
        tvNo.setText("Cancel");
        tvMessage.setText("Switch your GPS ON. Your manager is notified that it was switched OFF.");
        tvYes.setOnClickListener(v -> {
            dialogConfirm.dismiss();
            context.startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
        });

        tvNo.setOnClickListener(v -> {
            dialogConfirm.dismiss();
        });
        dialogConfirm.setCancelable(false);
        dialogConfirm.setCanceledOnTouchOutside(false);
        dialogConfirm.setView(view);
        dialogConfirm.show();
    }
}