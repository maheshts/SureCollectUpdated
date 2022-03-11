package com.loan.recovery.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationManager;
import android.media.AudioManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatTextView;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.loan.recovery.LoanApplication;
import com.loan.recovery.R;
import com.loan.recovery.adapter.CasesRecyclerAdapter;
import com.loan.recovery.fragments.DatePickerFragment;
import com.loan.recovery.retrofit.ApiClient;
import com.loan.recovery.retrofit.ApiInterface;
import com.loan.recovery.retrofit.model.BaseResponse;
import com.loan.recovery.retrofit.model.UserData;
import com.loan.recovery.retrofit.util.ApiUtil;

import org.json.JSONArray;
import org.json.JSONException;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.Context.INPUT_METHOD_SERVICE;

public class Utils {

    public static boolean isGPSEnabled(Context context) {
        boolean isEnabled = false;
        final LocationManager locManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);

        if (locManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            isEnabled = true;
        }
        return isEnabled;
    }
    public static boolean isOSOreo() {
        int sdkInt = Build.VERSION.SDK_INT;
        boolean isOreo = false;
        if((sdkInt == Build.VERSION_CODES.O) || (sdkInt == Build.VERSION_CODES.O_MR1))
        {
            isOreo = true;
        }
        return isOreo;
    }

    public static int getMaxDays(int partnerCode) {
        Calendar calendar = Calendar.getInstance();
        int days = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
        int currentDay = calendar.get(Calendar.DAY_OF_MONTH);
        int diff = days - currentDay;
        int result = diff;

        if (diff >= 4)
            result = 4;

        if (partnerCode == 1003) {
            // 1003 - Partner Id
            if (currentDay <= 20) {
                int ptpDiff = 20 - currentDay;
                if (ptpDiff >= 4)
                    result = 4;
                else
                    result = ptpDiff;
            }
        }
        return result;
    }

    @SuppressLint("SetTextI18n")
    public static void checkStatus(View view, int statusCode,
                                   AppCompatTextView tvDate, AppCompatTextView tvAmount) {
        if (statusCode == 1050 || statusCode == 4020) {
            // 1050 - Promised to Pay - Phone
            // 4020 - Promised to Pay - Field Visit
            view.findViewById(R.id.lout4).setVisibility(View.VISIBLE);
            view.findViewById(R.id.lout5).setVisibility(View.VISIBLE);
            view.findViewById(R.id.lout6).setVisibility(View.GONE);
            view.findViewById(R.id.lout7).setVisibility(View.GONE);
            tvDate.setText("PTP Date");
            tvAmount.setText("Promised Amount");
        } else if (statusCode == 1390 || statusCode == 1290 || statusCode == 4050 || statusCode == 4040) {
            // 1290 - Collected Partial EMI - Phone
            // 1390 - Collected EMI Payment - Phone

            // 4040 - Collected Partial EMI - Field Visit
            // 4050 - Collected EMI Payment - Field Visit
            view.findViewById(R.id.lout4).setVisibility(View.VISIBLE);
            view.findViewById(R.id.lout5).setVisibility(View.VISIBLE);
            view.findViewById(R.id.lout6).setVisibility(View.VISIBLE);
            view.findViewById(R.id.lout7).setVisibility(View.VISIBLE);
        } else {
            view.findViewById(R.id.lout4).setVisibility(View.GONE);
            view.findViewById(R.id.lout5).setVisibility(View.GONE);
            view.findViewById(R.id.lout6).setVisibility(View.GONE);
            view.findViewById(R.id.lout7).setVisibility(View.GONE);
        }
    }

    public static void contactModeChanged(View view) {
        view.findViewById(R.id.lout4).setVisibility(View.GONE);
        view.findViewById(R.id.lout5).setVisibility(View.GONE);
        view.findViewById(R.id.lout6).setVisibility(View.GONE);
        view.findViewById(R.id.lout7).setVisibility(View.GONE);
    }

    public static void showCalendar(AppCompatActivity context, DatePickerFragment.UseDateListener listener, boolean isMaxDate, int day) {
        DatePickerFragment dFragment = DatePickerFragment.newInstance();
        Bundle bundle = new Bundle();
        bundle.putBoolean("maxdate", isMaxDate);
        bundle.putInt("maxDay", day);
        dFragment.setArguments(bundle);
        dFragment.setUseDateListener(listener);
        dFragment.show(context.getSupportFragmentManager(), "DatePicker");
    }

    public static String getTransKey(UserData userData) {



        return userData.getUserId() +
                "--" +
                userData.getRoles().getRoleId();
    }

    public static String getDateTime() {
        Date date = Calendar.getInstance().getTime();
        // Display a date in day, month, year format
        @SuppressLint("SimpleDateFormat") DateFormat formatter = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        String today = formatter.format(date);
        System.out.println("Before formatting: " + today);
        return today;
    }

    public static String getDate() {
        Date date = Calendar.getInstance().getTime();
        // Display a date in day, month, year format
        @SuppressLint("SimpleDateFormat") DateFormat formatter = new SimpleDateFormat("MM-dd-yyyy");
        String today = formatter.format(date);
        System.out.println("Before formatting: " + today);
        return today;
    }

    public static String getDateTimeForPaymentFile() {
        Date date = Calendar.getInstance().getTime();
        // Display a date in day, month, year format
        @SuppressLint("SimpleDateFormat") DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String today = formatter.format(date);
        System.out.println("Before formatting: " + today);
        return today;
    }

    public static boolean isConnected(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null
                && activeNetwork.isConnectedOrConnecting();
    }

    public static void hideSoftKeyboard(Context context, View view) {
        try {
            InputMethodManager imm = (InputMethodManager) context.getSystemService(INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        } catch (Exception e) {
            // TODO: handle exception
        }
    }

    public static void storeUserDataInApplication(UserData userData) {
        LoanApplication application = LoanApplication.getInstance();
        application.setCurrentUser(userData);
    }

    public static void storeCredentialsInPreferences(Context context, String phoneNumber, String pin) {
        SharedPreferences preferences = context.getSharedPreferences(AppConstants.PREFERENCES_FILE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("email", phoneNumber);
        editor.putString("password", pin);
        editor.apply();
    }
    public static void storeKeyInPreferences(Context context, UserData userData,int keyid){
        SharedPreferences preferences = context.getSharedPreferences("RECOVERY_APP", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        String transKey = userData.getUserId()+"--15";
        editor.putString("keyid",transKey);
        editor.apply();
    }
    public static String getKeyInPreferences(Context context){
        SharedPreferences preferences = context.getSharedPreferences("RECOVERY_APP", Context.MODE_PRIVATE);
        //SharedPreferences.Editor editor = preferences.edit();
        return preferences.getString("keyid", "");
    }

public static void storePatnerIDPreferences(Context context, int keyid){
        SharedPreferences preferences = context.getSharedPreferences("RECOVERY_APP", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
       // String transKey = userData.getUserId()+"--15";
        editor.putInt("patnerid",keyid);
        editor.apply();
    }
    public static int getPatnerIDPreferences(Context context){
        SharedPreferences preferences = context.getSharedPreferences("RECOVERY_APP", Context.MODE_PRIVATE);
        //SharedPreferences.Editor editor = preferences.edit();
        return preferences.getInt("patnerid", 0);
    }

    public static void setLoctiontrackDataInPreferences(Context context, String data){
        SharedPreferences preferences = context.getSharedPreferences("RECOVERY_APP", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        // String transKey = userData.getUserId()+"--15";
        editor.putString("com.locationtrackdata",data);
        editor.apply();
    }
    public static JsonArray getLoctiontrackDataInPreferences(Context context){
        SharedPreferences preferences = context.getSharedPreferences("RECOVERY_APP", Context.MODE_PRIVATE);
        //  SharedPreferences.Editor editor = preferences.edit();
        //return preferences.getString("com.locationtrackdata", "[]");

        String temp = preferences.getString("com.locationtrackdata", "[]");

        JsonArray newarray = null;
        if(temp.isEmpty() || temp == null){
            newarray = new JsonArray();
        }else{
            newarray = JsonParser.parseString(temp).getAsJsonArray();
        }
        return  newarray;
    }

    public static JSONArray getUpdateTagData(Context ctx) {
        SharedPreferences preferences = ctx.getSharedPreferences("RECOVERY_APP", Context.MODE_PRIVATE);

        String temp = preferences.getString("UpdateTagData", "[]");

        JSONArray newarray = null;
        if(temp.isEmpty() || temp == null){
            newarray = new JSONArray();
        }else{
            try {
                newarray = new JSONArray(temp);
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }


        return newarray;
    }


    public static void setUpdateTagData(String updateData,Context ctx) {
        SharedPreferences preferences = ctx.getSharedPreferences("RECOVERY_APP", Context.MODE_PRIVATE);


        preferences.edit().putString("UpdateTagData",updateData).apply();
    }


    public static void clearDataOnLogout(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(AppConstants.PREFERENCES_FILE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.remove("email");
        editor.remove("password");
        editor.apply();
        LoanApplication application = LoanApplication.getInstance();
        application.setCurrentUser(null);
    }

    public static String getValueFromPreferences(Context context, String name) {
        SharedPreferences preferences = context.getSharedPreferences(AppConstants.PREFERENCES_FILE_NAME, Context.MODE_PRIVATE);
        return preferences.getString(name, null);
    }

    public static void storeJobStartDate(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(AppConstants.PREFERENCES_FILE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("jobdate", getDate());
        editor.apply();
    }

    public static boolean isJobStartedToday(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(AppConstants.PREFERENCES_FILE_NAME, Context.MODE_PRIVATE);
        String date = preferences.getString("jobdate", null);
        String currentDate = getDate();
        return date != null && date.equals(currentDate);
    }

    public static boolean isCallActive(Context context){
        AudioManager manager = (AudioManager)context.getSystemService(Context.AUDIO_SERVICE);
        return manager.getMode() == AudioManager.MODE_IN_CALL;
    }
}
