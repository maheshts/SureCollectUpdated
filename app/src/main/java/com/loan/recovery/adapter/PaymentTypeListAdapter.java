package com.loan.recovery.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;

import com.loan.recovery.R;
import com.loan.recovery.retrofit.model.PaymentType;

import java.util.List;

public class PaymentTypeListAdapter extends ArrayAdapter<PaymentType> {
    private List<PaymentType> mData;
    private LayoutInflater mInflater;
    private int layout;
    private ItemClickListener mClickListener;

    public PaymentTypeListAdapter(@NonNull Context context, int resource, List<PaymentType> data) {
        super(context, resource, data);
        mData = data;
        this.mInflater = LayoutInflater.from(context);
        layout = resource;
    }

    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View rowView = convertView;
        if (rowView == null) {
            rowView = mInflater.inflate(layout, null);
        }
        PaymentType partner = mData.get(position);
        rowView.setOnClickListener(v -> {
            if (mClickListener != null) mClickListener.onItemClick(partner);
        });
        AppCompatTextView tvName = rowView.findViewById(R.id.tvName);
        tvName.setText(partner.getPaymentTypeName());

        return rowView;
    }

    @Override
    public int getCount() {
        int size = 0;
        if (mData != null)
            size = mData.size();
        return size;
    }

    // allows clicks events to be caught
    public void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    // parent activity will implement this method to respond to click events
    public interface ItemClickListener {
        void onItemClick(PaymentType paymentType);
    }
}
