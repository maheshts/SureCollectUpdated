package com.loan.recovery.recorder;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.loan.recovery.LoanApplication;
import com.loan.recovery.util.AppConstants;

public class CallReceiver extends BroadcastReceiver {
    private static final String TAG = "CallRecorder";
    public static final String ARG_NUM_PHONE = "arg_num_phone";
    public static final String ARG_INCOMING = "arg_incoming";
    private static boolean serviceStarted = false;
    private static ComponentName serviceName = null;
    private LoanApplication application;

    public CallReceiver() {
        super();
        application = LoanApplication.getInstance();
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle bundle;
        String state;
        String incomingNumber;
        String action = intent.getAction();
        Log.e(AppConstants.DEBUG, String.format("RecorderService - 2", "CallReceiver", ""));
        if(action != null && action.equals(TelephonyManager.ACTION_PHONE_STATE_CHANGED) ) {

            if((bundle = intent.getExtras()) != null) {
                state = bundle.getString(TelephonyManager.EXTRA_STATE);
                Log.d(TAG, intent.getAction() + " " + state);

//                if(state != null && state.equals(TelephonyManager.EXTRA_STATE_RINGING)) {
//                    incomingNumber = bundle.getString(TelephonyManager.EXTRA_INCOMING_NUMBER);
//                    boolean isEnabled = settings.getBoolean(AppConstants.ENABLED, true);
//                    Log.d(TAG, "Incoming number: " + incomingNumber);
//                    if(!serviceStarted && isEnabled) {
//                        Intent intentService = new Intent(context, RecorderService.class);
//                        serviceName = intentService.getComponent();
//                        intentService.putExtra(ARG_NUM_PHONE, incomingNumber);
//                        intentService.putExtra(ARG_INCOMING, true);
//                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
//                            context.startForegroundService(intentService);
//                        else
//                            context.startService(intentService);
//                        serviceStarted = true;
//                    }
//                }
//
//                else
                if(state != null && state.equals(TelephonyManager.EXTRA_STATE_OFFHOOK)) {
//                    boolean isEnabled = settings.getBoolean(AppConstants.ENABLED, true);
                    boolean isEnabled = true;
                    if(!serviceStarted && isEnabled && application.getIsDirectCall()=='Y') { //outgoing
                        Intent intentService = new Intent(context, RecorderService.class);
                        serviceName = intentService.getComponent();
                        intentService.putExtra(ARG_INCOMING, false);
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                            context.startForegroundService(intentService);
                        else
                            context.startService(intentService);
                        serviceStarted = true;
                    }
                }

                else if(state != null && state.equals(TelephonyManager.EXTRA_STATE_IDLE)) {
                    if(serviceStarted) {
                        Intent stopIntent = new Intent(context, RecorderService.class);
                        stopIntent.setComponent(serviceName);
                        context.stopService(stopIntent);
                        serviceStarted = false;
                    }
                    serviceName = null;
                }
            }
        }
    }
}
