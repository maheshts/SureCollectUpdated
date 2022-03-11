package com.loan.recovery.location;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.core.content.ContextCompat;

public class LocationStarterAlarmReceiver extends BroadcastReceiver {
    public static final int REQUEST_CODE = 2001;
    public static final String ACTION = "com.loan.recovery.LOCATION_STARTER_ALARM_RECEIVER";
    private static final String TAG = LocationStarterAlarmReceiver.class.getName();

    // Triggered by the Alarm periodically (starts the service to run task)
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.v(TAG, "onReceive");
        Intent in = new Intent(context, LocationUpdatesService.class);
        if (LocationUpdatesService.isServiceOn()) {
            context.stopService(in);
        }
        ContextCompat.startForegroundService(context, in);
    }
}