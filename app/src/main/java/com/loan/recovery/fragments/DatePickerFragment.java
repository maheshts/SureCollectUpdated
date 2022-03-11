package com.loan.recovery.fragments;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.widget.DatePicker;

import androidx.fragment.app.DialogFragment;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Mallikarjuna on 05/03/2020.
 */

public class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {
    private UseDateListener listener;

    public DatePickerFragment() {
        // Required empty public constructor
    }

    public static DatePickerFragment newInstance() {
        DatePickerFragment fragment = new DatePickerFragment();
        return fragment;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Bundle bundle = getArguments();
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int maxDay = bundle.getInt("maxDay", 0);
        boolean isMaxDate = bundle.getBoolean("maxdate");
        DatePickerDialog dpd = new DatePickerDialog(getActivity(), this,
                year, month, day);

        long time = System.currentTimeMillis();
        long newTime = time;
        if(maxDay != 0)
        {
            newTime = time + (maxDay * 86400000);
        }

        if(isMaxDate) {
            dpd.getDatePicker().setMaxDate(newTime);
            dpd.getDatePicker().setMinDate(time);
        }
        else
            dpd.getDatePicker().setMaxDate(time);
        return dpd;
    }


    public void onDateSet(DatePicker view, int year, int month, int day) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(0);
        cal.set(year, month, day, 0, 0, 0);
        Date chosenDate = cal.getTime();
        SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd");
        String date = format.format(chosenDate);
        listener.useDate(date);
    }

    public interface UseDateListener {
        void useDate(String date);
    }

    public void setUseDateListener(UseDateListener listener) {
        this.listener = listener;
    }
}
