package com.loan.recovery.location;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.work.ListenableWorker;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

public class LocationWorkManager extends Worker {

    private final String TAG = LocationWorkManager.class.getSimpleName();
    private Context mContext;

    public LocationWorkManager(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        mContext = context;
    }


    @NonNull
    @Override
    public Result doWork() {
        Log.v(TAG, "onReceive");
        Intent in = new Intent(mContext, LocationUpdatesService.class);
        ContextCompat.startForegroundService(mContext, in);

        return Result.success();
    }
}
