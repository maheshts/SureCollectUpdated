package com.loan.recovery.location;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import androidx.core.content.ContextCompat;
import androidx.work.Constraints;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.NetworkType;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import java.util.concurrent.TimeUnit;

public class LocationUtils {
    private static final int INTERVAL_MILLIS = 15;
    private static final int FLEX_MILLIS = 10;
    private static final Constraints MY_CONSTRAINTS = new Constraints.Builder()
            .setRequiresCharging(false)
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .setRequiresBatteryNotLow(false)
            .setRequiresStorageNotLow(false)
            .build();
    public static PeriodicWorkRequest PERIODIC_WORK_REQUEST =
            new PeriodicWorkRequest.Builder(LocationWorkManager.class, INTERVAL_MILLIS, TimeUnit.MINUTES, FLEX_MILLIS, TimeUnit.MINUTES)
                    .setConstraints(MY_CONSTRAINTS)
                    .build();

    public static void startEnqueueUniquePeriodicWork() {

        WorkManager.getInstance().enqueueUniquePeriodicWork("secure_collect_work_manager", ExistingPeriodicWorkPolicy.KEEP, PERIODIC_WORK_REQUEST);
    }

    // Setup a recurring alarm every 5 min
    public static void scheduleAlarm(Context context) {
        // Construct an intent that will execute the AlarmReceiver
        Intent intent = new Intent(context, LocationStarterAlarmReceiver.class);
        // Create a PendingIntent to be triggered when the alarm goes off
        final PendingIntent pIntent = PendingIntent.getBroadcast(context, LocationStarterAlarmReceiver.REQUEST_CODE,
                intent, PendingIntent.FLAG_UPDATE_CURRENT);
        // Setup periodic alarm every every half hour from this point onwards
        long firstMillis = System.currentTimeMillis(); // alarm is set right away
        long interval = 2 * 60 * 1000L; // interval 5 min
        AlarmManager alarm = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        if (pIntent != null && alarm != null) {
            alarm.cancel(pIntent);
        }

        // First parameter is the type: ELAPSED_REALTIME, ELAPSED_REALTIME_WAKEUP, RTC_WAKEUP
        // Interval can be INTERVAL_FIFTEEN_MINUTES, INTERVAL_HALF_HOUR, INTERVAL_HOUR, INTERVAL_DAY
        alarm.setRepeating(AlarmManager.RTC_WAKEUP, firstMillis, interval
                /*AlarmManager.INTERVAL_FIFTEEN_MINUTES*/, pIntent);
    }

    public static void cancelAlarm(Context context) {
        Intent intent = new Intent(context, LocationStarterAlarmReceiver.class);
        final PendingIntent pIntent = PendingIntent.getBroadcast(context, LocationStarterAlarmReceiver.REQUEST_CODE,
                intent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarm = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarm.cancel(pIntent);
    }

    public static void startService(Context context) {
        Intent in = new Intent(context, LocationUpdatesService.class);
        if (LocationUpdatesService.isServiceOn()) {
            context.stopService(in);
        }

        ContextCompat.startForegroundService(context, in);
    }

}
