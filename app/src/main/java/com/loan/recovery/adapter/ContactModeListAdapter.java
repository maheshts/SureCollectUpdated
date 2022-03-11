package com.loan.recovery.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.loan.recovery.R;

public class ContactModeListAdapter extends ArrayAdapter<String> {
    private String[] mData;
    private LayoutInflater mInflater;
    private int layout;
    private ItemClickListener mClickListener;

    public ContactModeListAdapter(@NonNull Context context, int resource, String[] data) {
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
        String data = mData[position];
        rowView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mClickListener != null) mClickListener.onItemClick(data, position);
            }
        });
        TextView moviesName = (TextView) rowView.findViewById(R.id.tvName);

        moviesName.setText(data);
        return rowView;
    }

    @Override
    public int getCount() {
        int size = 0;
        if (mData != null)
            size = mData.length;
        return size;
    }

    // allows clicks events to be caught
    public void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    // parent activity will implement this method to respond to click events
    public interface ItemClickListener {
        void onItemClick(String itemClicked, int position);
    }
}
