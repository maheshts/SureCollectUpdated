package com.loan.recovery.location;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.BatteryManager;
import android.os.Build;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.loan.recovery.R;
import com.loan.recovery.activity.HomeActivity;
import com.loan.recovery.retrofit.ApiClient;
import com.loan.recovery.retrofit.ApiInterface;
import com.loan.recovery.retrofit.model.BaseResponse;
import com.loan.recovery.util.Utils;

import org.json.JSONArray;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LocationUpdatesService extends Service {
    private static final String TAG = LocationUpdatesService.class.getName();
    private static final String CHANNEL_ID = "SC1221";
    private static final int ONGOING_NOTIFICATION_ID = 1001;
    // this will check the continues below 50m accuracy updates
    private static final int INTERVAL_MILLIS = 15;     //change the time here

    private int mLocationUpdateCounter = 0;
    private static LocationUpdatesService sInstance;
    Calendar rightNow = Calendar.getInstance();
    JSONArray newjsonArray;

    int currentHourIn24Format = rightNow.get(Calendar.HOUR_OF_DAY); // return the hour in 24 hrs format (ranging from 0-23)


    public static boolean isServiceOn() {
        return sInstance != null;
    }

    private CountDownTimer mCountDownTimer = new CountDownTimer(30L * 24 * 60 * 60 * 1000, INTERVAL_MILLIS * 60 * 1000) {
        public void onTick(long millisUntilFinished) {
            Log.v(TAG, "onTick - " + (2592000000L - millisUntilFinished));
            Log.v(TAG, "currentHourIn24Format - " + currentHourIn24Format);
            // Toast.makeText(LocationUpdatesService.this, "currentHourIn24Format : "+currentHourIn24Format, Toast.LENGTH_SHORT).show();
            if (currentHourIn24Format <= 21 && currentHourIn24Format >= 9) {

                if (2592000000L - millisUntilFinished >= 30 * 1000) {
                    Log.v(TAG, "onTick - " + millisUntilFinished);
                    startLocationUpdates();
                }
            }
        }

        public void onFinish() {

        }
    };

    /*Location*/
    private FusedLocationProviderClient fusedLocationClient;
    private LocationCallback locationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            if (locationResult == null) {
                return;
            }
            Location location = locationResult.getLocations().get(0);

            if (location != null) {
                Log.v(TAG, "currentHourIn24Format - " + currentHourIn24Format);

                Log.v(TAG, "Latitude - " + location.getLatitude());
                Log.v(TAG, "Longitude - " + location.getLongitude());
                Log.v(TAG, "Accuracy - " + location.getAccuracy());
                if (location.getAccuracy() <= 1500) {
                    if (mLocationUpdateCounter >= 3) {
                        mLocationUpdateCounter = 0;
                        // do the api call for saving the location
                        stopLocationUpdates();
                        savedata(location);
                        mCountDownTimer.cancel();
                        mCountDownTimer.start();
//                        Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
//                        // Vibrate for 500 milliseconds
//                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//                            v.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE));
//                        } else {
//                            //deprecated in API 26
//                            v.vibrate(500);
//                        }
                        //stopService();
                    }
                    mLocationUpdateCounter++;

                } else {
                    mLocationUpdateCounter = 0;
                }


            }
        }
    };



    private void savedata(Location location) {
        if (currentHourIn24Format <= 21 && currentHourIn24Format >= 9) { //for checking the time period

            ApiInterface apiService =
                    ApiClient.getClient(this, 2).create(ApiInterface.class);
            double lat = 0.0;
            JSONArray jsonArray = null;
            double longi = 0.0;
            Date d = new Date();
            Timestamp tss = new Timestamp(d.getTime());

            if (location != null) {
                lat = location.getLatitude();
                longi = location.getLongitude();
            }
            String transkey = Utils.getKeyInPreferences(getApplicationContext());
            JsonObject object = new JsonObject();
            object.addProperty("latitude", lat);
            object.addProperty("longitude", longi);
            object.addProperty("userTransKey", transkey);
            object.addProperty("fkEventId", 16);
            object.addProperty("created_datetime", tss.toString());
            object.addProperty("isGpsEnable", "Y");
            object.addProperty("isMobileNw", "Y");

            JsonArray newjsonArray = Utils.getLoctiontrackDataInPreferences(sInstance);
            Log.v("nn in reciver", "" + newjsonArray);
            newjsonArray.add(object);
            Utils.setLoctiontrackDataInPreferences(sInstance, newjsonArray.toString());

//            JSONArray dataArray = new JSONArray();
            JsonObject datajson = new JsonObject();


            datajson.add("data",newjsonArray);
            Log.v(TAG, "<=== datajson - : ===> " + datajson);


            Call<BaseResponse> call = apiService.locationJob(datajson);
            call.enqueue(new Callback<BaseResponse>() {
                @Override
                public void onResponse(Call<BaseResponse> call, Response<BaseResponse> response) {
                    try {
                        BaseResponse serverResponse = response.body();
                        if (serverResponse != null && serverResponse.getStatusCode() == 1000) {
                            Log.v(TAG, "<=== saveLocationData - Success ===> " + serverResponse.getStatus());
                            Utils.setLoctiontrackDataInPreferences(sInstance, "");
                        } else {
                            Log.v(TAG, "<=== saveLocationData - Failed ===> ");
                        }
                        //stopService();

                    } catch (Exception e) {
                        //stopService();

                        Log.v(TAG, "<=== saveLocationData - Crash : ===> " + e.toString());
                    }
                }

                @Override
                public void onFailure(Call<BaseResponse> call, Throwable t) {
                    Log.v(TAG, "<=== Exceptions ===> " + t.toString());
                }
            });
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        sInstance = null;
        Log.v(TAG, "onDestroy");
        unregisterReceiver(mLocationProviderChangeBroadcastReceiver);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        sInstance = this;

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        registerReceiver(mLocationProviderChangeBroadcastReceiver, new IntentFilter(LocationManager.PROVIDERS_CHANGED_ACTION));
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.v(TAG, "onStartCommand");
        createNotificationChannel();
        Intent notificationIntent = new Intent(this, HomeActivity.class);
        PendingIntent pendingIntent =
                PendingIntent.getActivity(this, 0, notificationIntent, 0);

        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle(getText(R.string.notification_title))
                //.setContentText(getText(R.string.notification_message))
                .setSmallIcon(R.mipmap.sure_icon)
                .setContentIntent(pendingIntent)
                .setShowWhen(true)
                .setNotificationSilent()
                //.setTicker(getText(R.string.ticker_text))
                .build();

        // Notification ID cannot be 0.
        startForeground(ONGOING_NOTIFICATION_ID, notification);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startLocationUpdates();

            }
        }, 1000);
        return START_NOT_STICKY;

    }

    private void stopService() {
        stopLocationUpdates();
        //stopForeground(true);
        //stopSelf();
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel serviceChannel = new NotificationChannel(
                    CHANNEL_ID,
                    "Foreground Service Channel",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(serviceChannel);
        }
    }

    private LocationRequest createLocationRequest() {
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setInterval(1000);
        locationRequest.setFastestInterval(1000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        return locationRequest;
    }

    private void startLocationUpdates() {


        Timestamp tss = new Timestamp(new Date().getTime());
        boolean nwkey = Utils.isConnected(sInstance);
        int nw_status;
        if (nwkey) {
            nw_status = 1;
        } else {
            nw_status = 2;
        }
        //Log.v("startLocationUpdates", "tss : " + tss.toString());

        LocationManager locationManager = (LocationManager) sInstance.getSystemService(Context.LOCATION_SERVICE);

        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            JsonObject sendjsonObject = new JsonObject();
            //sendjsonObject.addProperty("userId", SharedPrefrenceData.getKey_userid(context));

            sendjsonObject.addProperty("latitude", "0");
            sendjsonObject.addProperty("longitude", "0");
            sendjsonObject.addProperty("userTransKey", Utils.getKeyInPreferences(getApplicationContext()));
            sendjsonObject.addProperty("fkEventId", "16");
            sendjsonObject.addProperty("BateryCharge_status", getBatteryLevel());

            sendjsonObject.addProperty("Datetime", tss.toString());
            sendjsonObject.addProperty("isGpsEnable", "N"); // gps status off
            sendjsonObject.addProperty("isMobileNw", nw_status); // nw // statutus on/off

            JsonArray newjsonArray = Utils.getLoctiontrackDataInPreferences(sInstance);
            Log.v("nn in gps", "" + newjsonArray);
            newjsonArray.add(sendjsonObject);
            if(nw_status == 2) {
                Utils.setLoctiontrackDataInPreferences(sInstance,newjsonArray.toString());
            }else{
                Log.v("yes in net", "No gps" + newjsonArray);

                sendnwdata();  //no gsp only internet
            }
//            Intent i = new Intent(context, SendDataService.class);
//            i.addPropertyExtra("jsondata", newjsonArray.toString());
//            context.startService(i);
        } else {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            fusedLocationClient.requestLocationUpdates(createLocationRequest(),
                    locationCallback,
                    Looper.getMainLooper());
        }
    }

    private void stopLocationUpdates() {
        fusedLocationClient.removeLocationUpdates(locationCallback);
    }

    void  sendnwdata(){


        JsonArray newjsonArray = Utils.getLoctiontrackDataInPreferences(sInstance);
        Log.v("nn in reciver", "yes net" + newjsonArray);
        //newjsonArray.add(sendjsonObject);
        //Utils.setLoctiontrackDataInPreferences(sInstance, newjsonArray.toString());

        JsonObject datajson = new JsonObject();

        datajson.add("data",newjsonArray);
        ApiInterface apiService =
                ApiClient.getClient(this, 2).create(ApiInterface.class);
        Call<BaseResponse> call = apiService.locationJob(datajson);
        call.enqueue(new Callback<BaseResponse>() {
            @Override
            public void onResponse(Call<BaseResponse> call, Response<BaseResponse> response) {
                try {
                    BaseResponse serverResponse = response.body();
                    if (serverResponse != null && serverResponse.getStatusCode() == 1000) {
                        Log.v(TAG, "<=== saveLocationData - Success ===> " + serverResponse.getStatus());
                        Utils.setLoctiontrackDataInPreferences(sInstance, "");
                    } else {
                        Log.v(TAG, "<=== saveLocationData - Failed ===> ");
                    }
                    //stopService();

                } catch (Exception e) {
                    //stopService();

                    Log.v(TAG, "<=== saveLocationData - Crash : ===> " + e.toString());
                }
            }

            @Override
            public void onFailure(Call<BaseResponse> call, Throwable t) {
                Log.v(TAG, "<=== Exceptions ===> " + t.toString());
            }
        });


    }
    private final BroadcastReceiver mLocationProviderChangeBroadcastReceiver = new BroadcastReceiver() {
        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().matches(LocationManager.PROVIDERS_CHANGED_ACTION)) {
                //Do your stuff on GPS status change
                ContentResolver contentResolver = getContentResolver();
                // Find out what the settings say about which providers are enabled
                int mode = Settings.Secure.getInt(
                        contentResolver, Settings.Secure.LOCATION_MODE, Settings.Secure.LOCATION_MODE_OFF);
                String locationMode = "";
                if (mode != Settings.Secure.LOCATION_MODE_OFF) {
                    if (mode == Settings.Secure.LOCATION_MODE_HIGH_ACCURACY) {
                        locationMode = "High accuracy. Uses GPS, Wi-Fi, and mobile networks to determine location";
                    } else if (mode == Settings.Secure.LOCATION_MODE_SENSORS_ONLY) {
                        locationMode = "Device only. Uses GPS to determine location";
                    } else if (mode == Settings.Secure.LOCATION_MODE_BATTERY_SAVING) {
                        locationMode = "Battery saving. Uses Wi-Fi and mobile networks to determine location";
                    }
                }else{
                    locationMode = "Location Off!";
                    Date d = new Date();
                    Timestamp tss = new Timestamp(d.getTime());
                    boolean nwkey = Utils.isConnected(sInstance);
                    int nw_status;
                    if (nwkey) {
                        nw_status = 1;
                    } else {
                        nw_status = 2;
                    }

                    String transkey = Utils.getKeyInPreferences(getApplicationContext());
                    JsonObject object = new JsonObject();
                    object.addProperty("latitude", "0");
                    object.addProperty("longitude", "0");
                    object.addProperty("userTransKey", transkey);
                    object.addProperty("fkEventId", 16);
                    object.addProperty("BateryCharge_status", getBatteryLevel());

                    object.addProperty("created_datetime", tss.toString());
                    object.addProperty("isGpsEnable", "N");
                    object.addProperty("isMobileNw", nw_status);

                    JsonArray newjsonArray = Utils.getLoctiontrackDataInPreferences(sInstance);
                    Log.v("mahesh", "gps off" + newjsonArray);
                    newjsonArray.add(object);

                    if(Utils.isConnected(sInstance)) {
                        sendnwdata();
                    }else{
                        Utils.setLoctiontrackDataInPreferences(sInstance, newjsonArray.toString());

                    }
                }

                //Log.v(TAG, locationMode);
                // send the locationMode to server
            }
        }
    };

    public float getBatteryLevel() {
        Intent batteryIntent = registerReceiver(null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        int level = batteryIntent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
        int scale = batteryIntent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);

        // Error checking that probably isn't needed but I added just in case.
        if(level == -1 || scale == -1) {
            return 50.0f;
        }

        return ((float)level / (float)scale) * 100.0f;
    }

}
