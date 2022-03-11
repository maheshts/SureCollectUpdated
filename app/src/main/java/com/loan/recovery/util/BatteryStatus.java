package com.loan.recovery.util;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;

public class BatteryStatus {

	
	public  float batteryChargedRemaining;
	private  int batteryStatus;
	Context ctx;
	
	
	public BatteryStatus(Context context) {
		// TODO Auto-generated constructor stub
		ctx = context;
	}
	/*public float  getBatterypLevel() {
        BroadcastReceiver batteryLevelReceiver = new BroadcastReceiver() {
            public void onReceive(Context context, Intent intent) {
                context.unregisterReceiver(this);
                int rawlevel = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
                int scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
                batteryChargedRemaining = -1;
                if (rawlevel >= 0 && scale > 0) {
                	batteryChargedRemaining = (rawlevel * 100) / scale;
                }
               // batteryChargedRemaining.setText("Battery Level Remaining: " + level + "%");
            }
        };
        IntentFilter batteryLevelFilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        ctx.getApplicationContext().registerReceiver(batteryLevelReceiver, batteryLevelFilter);
		return batteryChargedRemaining;
    }*/
	
	
	public float getBatteryLevel() {
		Intent batteryIntent = ctx.getApplicationContext().registerReceiver(null,new IntentFilter(Intent.ACTION_BATTERY_CHANGED));

		int level = batteryIntent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
		int scale = batteryIntent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
		
		 int status = batteryIntent.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
	     boolean isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING ||
	                            status == BatteryManager.BATTERY_STATUS_FULL;
	        
	        if(isCharging){	        	
	        	batteryStatus = 1;
	        }else{	        	
	        	batteryStatus = 2;
	        }
	        
	        
		if (level == -1 || scale == -1) {
			batteryChargedRemaining = 50.0f;

		}

		batteryChargedRemaining = ((float) level / (float) scale) * 100.0f;

		return batteryChargedRemaining;
	}

}
