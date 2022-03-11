package com.loan.recovery.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.loan.recovery.R;

import com.loan.recovery.retrofit.model.ReportDayProgress;

import java.util.ArrayList;

public class DayProgressReportAdaptor extends ArrayAdapter {

    private final LayoutInflater mInFlater;
    private ArrayList<ReportDayProgress> tasks=null;
    private final int mViewResourceId;
    Context parentContext;
    LinearLayout mainLayout;
    TextView tvTimeRange, tvDialedCases, tvAnsweredCases, tvMinActive, tvMinInactive, tvMinRatio;

    public DayProgressReportAdaptor(Context context, int textViewResourceId, ArrayList<ReportDayProgress> tasks){
        super (context, textViewResourceId, tasks);

        this.tasks= tasks;
        parentContext = context;
        mInFlater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mViewResourceId = textViewResourceId;
    }

    public int getCount() {
        return tasks.size();
    }

    public View getView(int position, View converView, final ViewGroup parents){

        converView = mInFlater.inflate(mViewResourceId, null);

        final ReportDayProgress task = tasks.get(position);

        if(task!=null){
            tvTimeRange = (TextView) converView.findViewById(R.id.tv_time_range);
            tvDialedCases = (TextView) converView.findViewById(R.id.tv_dialed_cases);
            tvAnsweredCases = (TextView) converView.findViewById(R.id.tv_ans_cases);
            tvMinActive = (TextView) converView.findViewById(R.id.tv_mins_active);
            tvMinInactive = (TextView) converView.findViewById(R.id.tv_mins_inactive);
            tvMinRatio = (TextView) converView.findViewById(R.id.tv_mins_ratio);


            tvTimeRange.setText(task.getHrRange());
            tvDialedCases.setText(Integer.toString(task.getDialedCases()));
            tvAnsweredCases.setText(Integer.toString(task.getConnectedCases()));
            tvMinActive.setText(Integer.toString(task.getActiveTimeMin()));
            tvMinInactive.setText(Integer.toString(task.getInActiveTimeMin()));
            if(task.getActiveTimePercent()!=null){
                tvMinRatio.setText(task.getActiveTimePercent()+"%");
            }
        }
        return converView;

    }
}
