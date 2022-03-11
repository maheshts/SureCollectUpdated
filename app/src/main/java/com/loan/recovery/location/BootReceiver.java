package com.loan.recovery.location;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class BootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
            // start Enqueue Unique Periodic Work for location updates
            LocationUtils.startEnqueueUniquePeriodicWork();
        }
    }
}