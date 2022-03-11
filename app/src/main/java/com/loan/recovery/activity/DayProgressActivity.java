package com.loan.recovery.activity;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.google.gson.Gson;
import com.loan.recovery.LoanApplication;
import com.loan.recovery.R;
import com.loan.recovery.adapter.DayProgressReportAdaptor;
import com.loan.recovery.retrofit.ApiClient;
import com.loan.recovery.retrofit.ApiInterface;
import com.loan.recovery.retrofit.model.DayReportList;
import com.loan.recovery.retrofit.model.ReportDayProgress;
import com.loan.recovery.retrofit.model.Roles;
import com.loan.recovery.retrofit.model.UserData;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import pl.pawelkleczkowski.customgauge.CustomGauge;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DayProgressActivity extends BaseActivity {

    ImageButton pickDate;
    EditText edDate;
    Button btnSubmit;
    ListView listView;
    LinearLayout linearLayout3;
    Button btnShowMore;
    TextView tvNoDataFound;
    String pattern = "yyyy-MM-dd";
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    String strSelectedDate = sdf.format(new Date());
//    String strSelectedDate = "2021-08-11";
    private DayProgressActivity activity;
    private LoanApplication application;
    private ArrayAdapter<String> listAdaptor;
    int isExpanded=0;
    LinearLayout layoutHeader;

    @Override
    protected Fragment createFragment() {
        return null;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_day_progress);

        application = LoanApplication.getInstance();
        activity = this;

        pickDate =(ImageButton) findViewById(R.id.pick_date_button);
        edDate =(EditText) findViewById(R.id.ed_date);
        btnSubmit =(Button) findViewById(R.id.submit);
        listView =(ListView) findViewById(R.id.list_items);
        tvNoDataFound = (TextView) findViewById(R.id.tvNoData);
        layoutHeader = (LinearLayout) findViewById(R.id.layout_header);

        linearLayout3 = (LinearLayout) findViewById(R.id.linearLayout3);
        btnShowMore = (Button) findViewById(R.id.btnExpand);

        btnShowMore.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View v) {

                isExpanded = btnShowMore.getText().equals(getResources().getString(R.string.viewLess))?0:1;
                if(isExpanded==1) {
                    Drawable img = getDrawable(R.drawable.ic_expand_less);
                    btnShowMore.setCompoundDrawablesWithIntrinsicBounds(img, null, null, null);
                    btnShowMore.setText(getText(R.string.viewLess));
                    Animation slideDown = AnimationUtils.loadAnimation(DayProgressActivity.this, R.anim.slide_down);
                    linearLayout3.setVisibility(View.VISIBLE);
                    linearLayout3.setAnimation(slideDown);
                }else{
                    Drawable img = getDrawable(R.drawable.ic_expand_more);
                    btnShowMore.setCompoundDrawablesWithIntrinsicBounds(img, null, null, null);
                    btnShowMore.setText(getText(R.string.viewMore));
                    Animation slideup = AnimationUtils.loadAnimation(DayProgressActivity.this, R.anim.slide_up);
                    linearLayout3.setVisibility(View.GONE);
                    linearLayout3.setAnimation(slideup);
                }
            }
        });
        edDate.setText(strSelectedDate);
        callAPI();

        MaterialDatePicker.Builder materialDateBuilder = MaterialDatePicker.Builder.datePicker();

        materialDateBuilder.setTitleText("SELECT A DATE");
        final MaterialDatePicker materialStartDatePicker = materialDateBuilder.build();
        pickDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                materialStartDatePicker.show(getSupportFragmentManager(), materialStartDatePicker.toString());
            }
        });


        materialStartDatePicker.addOnPositiveButtonClickListener(new MaterialPickerOnPositiveButtonClickListener() {
            @Override
            public void onPositiveButtonClick(Object selection) {
                try {
                    SimpleDateFormat sdf = new SimpleDateFormat(pattern);
                    strSelectedDate = sdf.format(new Date(materialStartDatePicker.getHeaderText()));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                edDate.setText(strSelectedDate);
            }
        });

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callAPI();
            }
        });
    }
        private void callAPI() {
            activity.showProgressDialog("Please Wait", "Getting Cases List...");

            ApiInterface apiService =
                    ApiClient.getClient(activity, 1).create(ApiInterface.class);
            UserData userData = application.getCurrentUser();
            if (userData != null) {
                Roles roles = userData.getRoles();
                if(!strSelectedDate.equals("") || strSelectedDate!=null){
                    try {
                        Call<DayReportList> call = apiService.getDayProgressReport(userData.getUserId(), strSelectedDate);
                        call.enqueue(new CasesCallBack());
                    }catch(Exception e){
                        e.printStackTrace();
                    }
                }else{
                    edDate.setError("Please Select Date");
                }
        }
    }

    class CasesCallBack implements Callback<DayReportList> {
        @Override
        public void onResponse(Call<DayReportList> call, Response<DayReportList> response) {
            activity.hideProgressDialog();
            System.out.println("<=== CasesCallBack - Response : ===> " + new Gson().toJson(response.body()));
            DayReportList dayReportList = response.body();

            if (dayReportList != null) {
                List<ReportDayProgress> reportDayProgress = dayReportList.getReportData();
                if (reportDayProgress != null && reportDayProgress.size() > 0) {
                    setHeader(reportDayProgress);
                    btnShowMore.setVisibility(View.VISIBLE);
                    listAdaptor = new DayProgressReportAdaptor(getApplicationContext(), R.layout.day_report_list_adator, (ArrayList<ReportDayProgress>) reportDayProgress);
                    listView.setAdapter(listAdaptor);
                    tvNoDataFound.setVisibility(View.GONE);
                } else {
                    tvNoDataFound.setVisibility(View.VISIBLE);
                    btnShowMore.setVisibility(View.GONE);
                    layoutHeader.setVisibility(View.GONE);
                }
            } else {
                tvNoDataFound.setVisibility(View.VISIBLE);
                btnShowMore.setVisibility(View.GONE);
                layoutHeader.setVisibility(View.GONE);
                Toast.makeText(getApplicationContext(), "Error while fetching data", Toast.LENGTH_SHORT).show();
            }
        }
        @Override
        public void onFailure(Call<DayReportList> call, Throwable t) {

        }
    }
    public void setHeader(List<ReportDayProgress> reportArray){
        float totalCallingPer= 0.0f;
        int totalMinInactive= 0, totalMinActive= 0,workingHrs = 0, totalDialedCalls = 0, totalAnsCalls=0;


        for(ReportDayProgress reportData:reportArray){
            totalMinActive += reportData.getActiveTimeMin();
            totalMinInactive += reportData.getInActiveTimeMin();

            workingHrs += reportData.getOfficialTimeMin();
            totalDialedCalls += reportData.getDialedCases();
            totalAnsCalls += reportData.getConnectedCases();
        }

        String strActiveTime = Integer.toString(totalMinActive/60)+":"+Integer.toString(totalMinActive%60);
        String strInactiveTime = Integer.toString(totalMinInactive/60)+":"+Integer.toString(totalMinInactive%60);
//        totalCallingPer =  (float)(totalMinActive*100)/workingHrs;

        totalCallingPer = (float)(totalMinActive/(double) reportArray.get(0).getMinutesPerDay())*100;
        double roundedValue = Math.round(totalCallingPer*100.0)/100.0;

        String strWorkingHrs= Double.toString(roundedValue)+"%";

        TextView headerDialedCases = (TextView) findViewById(R.id.tv_total_dialed_cases);
        TextView headerAnsCases = (TextView) findViewById(R.id.tv_total_ans_cases);
        TextView headerMinAct = (TextView) findViewById(R.id.tv_total_mins_active);
        TextView headerMinInact = (TextView) findViewById(R.id.tv_total_mins_inactive);
        TextView headerGaugePercentage = (TextView) findViewById(R.id.txtGaugeValue);
        CustomGauge halfGauge = (CustomGauge) findViewById(R.id.half_gauge);

        TextView tvLRDialedCases = (TextView) findViewById(R.id.tv_dialed_cases);
        TextView tvLRAnsCases = (TextView) findViewById(R.id.tv_ans_cases);
        TextView tvLRMinAct = (TextView) findViewById(R.id.tv_mins_active);
        TextView tvLRMinInact = (TextView) findViewById(R.id.tv_mins_inactive);
        TextView tvLRRatio = (TextView) findViewById(R.id.tv_mins_ratio);
        TextView tvLRTimeRange = (TextView) findViewById(R.id.tv_time_range);

        headerDialedCases.setText(Integer.toString(totalDialedCalls));
        headerAnsCases.setText(Integer.toString(totalAnsCalls));
        headerMinAct.setText(strActiveTime);
        headerMinInact.setText(strInactiveTime);
        headerGaugePercentage.setText(strWorkingHrs);

        ReportDayProgress currentTimeRangeItem  = reportArray.get(reportArray.size()-1);

        tvLRDialedCases.setText(Integer.toString(currentTimeRangeItem.getDialedCases()));
        tvLRAnsCases.setText(Integer.toString(currentTimeRangeItem.getConnectedCases()));
        tvLRMinAct.setText(Integer.toString(currentTimeRangeItem.getActiveTimeMin()));
        tvLRMinInact.setText(Integer.toString(currentTimeRangeItem.getInActiveTimeMin()));
        tvLRRatio.setText(currentTimeRangeItem.getActiveTimePercent()+"%");
        tvLRTimeRange.setText(currentTimeRangeItem.getHrRange());

        halfGauge.setValue((int) roundedValue);
        layoutHeader.setVisibility(View.VISIBLE);
    }
}