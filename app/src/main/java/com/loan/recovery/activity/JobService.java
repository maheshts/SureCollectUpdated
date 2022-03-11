package com.loan.recovery.activity;

import android.app.IntentService;
import android.content.Intent;

import androidx.annotation.Nullable;

public class JobService extends IntentService {

    public JobService() {
        super("");
    }

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */
    public JobService(String name) {
        super(name);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        System.out.println("JobService ===========> onCreate");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        String name = (String) intent.getExtras().get("name");
        System.out.println("JobService ===========> "+name);
        int a = 5;
        int b = 0;
        while(a > b)
        {
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("JobService Loop ===========> "+b);
            ++b;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        System.out.println("JobService Loop ===========> Closed");
    }
}
