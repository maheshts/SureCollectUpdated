package com.loan.recovery.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.loan.recovery.R;
import com.loan.recovery.retrofit.model.OtherContact;
import com.loan.recovery.retrofit.model.ReportDayProgress;

import java.util.ArrayList;
import java.util.List;

public class OtherContactListAdaptor extends ArrayAdapter {

    private final LayoutInflater mInFlater;
    private List<OtherContact> tasks=null;
    private final int mViewResourceId;
    Context parentContext;
    TextView tvName, tvNumber, tvRelation;
    ImageView btnCall;

    public OtherContactListAdaptor(Context context, int textViewResourceId, List<OtherContact> tasks){
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

        final OtherContact task = tasks.get(position);

        if(task!=null){
            tvName = (TextView) converView.findViewById(R.id.tv_con_name);
            tvNumber = (TextView) converView.findViewById(R.id.tv_con_number);
            tvRelation = (TextView) converView.findViewById(R.id.tv_con_relation);
            btnCall = (ImageView) converView.findViewById(R.id.btn_call_con);

            tvName.setText(task.getContactName());
            tvNumber.setText(task.getPhoneNumber());
            tvRelation.setText(task.getContactTypeName());
        }
        return converView;
    }
}
