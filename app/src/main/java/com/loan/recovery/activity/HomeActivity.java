package com.loan.recovery.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.navigation.NavigationView;
import com.google.gson.JsonObject;
import com.loan.recovery.LoanApplication;
import com.loan.recovery.R;
import com.loan.recovery.adapter.CasesRecyclerAdapter;
import com.loan.recovery.fragments.HomeFragment;
import com.loan.recovery.location.LocationUtils;
import com.loan.recovery.retrofit.ApiClient;
import com.loan.recovery.retrofit.ApiInterface;
import com.loan.recovery.retrofit.model.BaseResponse;
import com.loan.recovery.retrofit.model.UserData;
import com.loan.recovery.retrofit.util.ApiUtil;
import com.loan.recovery.util.Utils;

import java.util.Calendar;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeActivity extends BaseActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private NavigationView navigationView;
    //creating fragment object
    private Fragment fragment = null;
    private LoanApplication application;
    private DrawerLayout drawer;
    private Context mContext;
    SaveUserLocationData saveUserLocationData = null;
    private String appVersion = "";
    @Override
    protected Fragment createFragment() {
        return null;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mContext = this;

        // add back arrow to toolbar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        application = LoanApplication.getInstance();
        application.setContext(getApplicationContext());

        appVersion = getString(R.string.app_version);
        saveLoginEvent();
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        initDrawerLayout();

        if(application.getIsTrackingGps()=='Y') {
//            saveUserLocationData = new SaveUserLocationData();
//            saveUserLocationData.execute();
// LocationUtils.startEnqueueUniquePeriodicWork();
            LocationUtils.startService(this);
        }
        displaySelectedScreen(R.id.home);
    }

    @Override
    protected void onResume() {
        super.onResume();
        invalidateOptionsMenu();
        FusedLocationProviderClient fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        LocationRequest locationRequest;
        locationRequest = new LocationRequest();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(100);
        locationRequest.setFastestInterval(100);
        locationRequest.setSmallestDisplacement(1);

        LocationCallback locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                for (Location location: locationResult.getLocations()){
                    if (location != null) {
                        application.setCurrentLocation(location);
                        System.out.println(location.getLatitude()+" <=====Loc Changed====> "+location.getLongitude());
                    }
                }
            }
        };

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
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        // Got last known location. In some rare situations this can be null.
                        if (location != null) {
                            application.setCurrentLocation(location);
                            System.out.println(location.getLatitude()+" <====Last Known=====> "+location.getLongitude());
                        }
                    }
                });

        fusedLocationClient.requestLocationUpdates(
                locationRequest, locationCallback, null);
    }

    @SuppressLint("SetTextI18n")
    private void initDrawerLayout() {
        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        View headerView = navigationView.getHeaderView(0);
        TextView tvName = headerView.findViewById(R.id.tvName);
        UserData userData = application.getCurrentUser();
        if (userData != null) {
            tvName.setText(userData.getFirstName() + " " + userData.getLastName());
        }
        Menu menu = navigationView.getMenu();
        MenuItem menuItem = menu.findItem(R.id.startJob);
        menuItem.setVisible(!Utils.isJobStartedToday(HomeActivity.this));
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            FragmentManager fragmentManager = getSupportFragmentManager();
            int backCount = fragmentManager.getBackStackEntryCount();

            if (backCount <= 1) {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Logout");
                builder.setMessage("Do you want to Logout....?");
                builder.setNegativeButton("No", null);
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        logout();
                    }
                });
                builder.show();
            } else {
                super.onBackPressed();
                updateNavigationView();
            }
        }
    }

    private void updateNavigationView() {
        FragmentManager fm = getSupportFragmentManager();
        String name = fm.getBackStackEntryAt(fm.getBackStackEntryCount() - 1).getName();
        if (!TextUtils.isEmpty(name))
            navigationView.setCheckedItem(Integer.parseInt(name));
    }

    private void saveLoginEvent(){
        JsonObject jsonEvent = new JsonObject();
        jsonEvent.addProperty("userId",application.getCurrentUser().getUserId());
        jsonEvent.addProperty("eventId",12);
        jsonEvent.addProperty("appVersion",appVersion);
        new SaveUserEventData().execute(jsonEvent);
    }

    @SuppressLint("NonConstantResourceId")
    public void displaySelectedScreen(int itemId) {
        navigationView.setCheckedItem(itemId);
        //initializing the fragment object which is selected
        switch (itemId) {
            case R.id.home:
                fragment = new HomeFragment();
                break;

//            case R.id.callLog:
//                fragment = new UnassignedRecordingsFragment();
//                startActivity(new Intent(this, ContactsListActivityMain.class));
//                break;

            case R.id.dayProgress:
                openMyDayProgressActivity();
                break;

            case R.id.callSetting:
                openCallSettingActivity();
                break;

            case R.id.logout:
                logout();
                break;

            case R.id.startJob:
                scheduleJobStartAPICall();
                navigationView.setCheckedItem(R.id.home);
                break;

//            case R.id.updatePhone:
//                fragment = new UpdatePhoneNumberFragment();
//                break;

        }

        //replacing the fragment
        if (fragment != null) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.content_frame, fragment)
                    .addToBackStack("" + itemId)
                    .commit();
        }

        drawer.closeDrawer(GravityCompat.START);
    }

    private void scheduleJobStartAPICall() {
        JsonObject jsonEvent = new JsonObject();
        jsonEvent.addProperty("userId",application.getCurrentUser().getUserId());
        jsonEvent.addProperty("eventId",15);
        jsonEvent.addProperty("appVersion",appVersion);
        new SaveUserEventData().execute(jsonEvent);
        new SaveJobDataTask().execute();
    }

    class SaveJobDataTask extends AsyncTask<Void, Void, Void>{
        @Override
        protected Void doInBackground(Void... voids) {

            int count = 1;
            while (count < 6)
            {
                saveJobStartData(15);
                try {
                    Thread.sleep(60000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if(count == 5)
                {
                    Utils.storeJobStartDate(HomeActivity.this);
                }
                ++count;
            }
            return null;
        }
    }

    public class SaveUserLocationData extends AsyncTask<Void, Void, Void>{

        @Override
        protected Void doInBackground(Void... voids) {

            Thread t1 = new Thread(new Runnable() {
                @Override
                public void run() {
                    Calendar rightNow = Calendar.getInstance();
                    int currentHourIn24Format = rightNow.get(Calendar.HOUR_OF_DAY);
                    while (currentHourIn24Format <= 23) {
                        saveJobStartData(16);
                        try {
                            Thread.sleep(600000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
            t1.start();
            return null;
        }
    }


    private void saveJobStartData(int eventId) {

        // Parsing any Media type file
        System.out.println("<=== Update Metadata - Started ========> ");
        Location location = application.getCurrentLocation();
        ApiInterface apiService =
                ApiClient.getClient(this, 2).create(ApiInterface.class);
        double lat = 0.0;
        double longi = 0.0;
        JsonObject object = null;
        if (location != null) {
            lat = location.getLatitude();
            longi = location.getLongitude();
        }
        try{
            object = ApiUtil.getJobStartDataObject(
                    lat, longi, Utils.getTransKey(application.getCurrentUser()), eventId);
        }catch(Exception e){
            saveUserLocationData.cancel(true);
            e.printStackTrace();
        }


        Call<BaseResponse> call = apiService.startJob(object);
        call.enqueue(new Callback<BaseResponse>() {
            @Override
            public void onResponse(Call<BaseResponse> call, Response<BaseResponse> response) {
                try {
                    BaseResponse serverResponse = response.body();
                    if (serverResponse != null && serverResponse.getStatusCode() == 1000) {
                        System.out.println("<=== saveJobStartData - Success ===> " + serverResponse.getStatus());
                    } else {
                        System.out.println("<=== saveJobStartData - Failed ===> ");
                    }
                } catch (Exception e) {
                    System.out.println("<=== saveJobStartData - Crash : ===> " + e.toString());
                }
            }

            @Override
            public void onFailure(Call<BaseResponse> call, Throwable t) {
                System.out.println("<=== Exceptions ===> " + t.toString());
            }
        });
    }

    public class SaveUserEventData extends AsyncTask<JsonObject, Void, Void> {

        @Override
        protected Void doInBackground(JsonObject... jsonEvent) {
            ApiInterface apiService =
                    ApiClient.getClient(HomeActivity.this, 2).create(ApiInterface.class);

            JsonObject object = null;
            try {
                object = ApiUtil.getSaveEventObject(jsonEvent[0]);
            } catch (Exception e) {
                e.printStackTrace();
            }

            Call<BaseResponse> call = apiService.saveUserEvent(object);
            call.enqueue(new Callback<BaseResponse>() {
                @Override
                public void onResponse(Call<BaseResponse> call, Response<BaseResponse> response) {
                    try {
                        BaseResponse serverResponse = response.body();
                        if (serverResponse != null && serverResponse.getStatusCode() == 1000) {
                            System.out.println("<=== saveJobStartData - Success ===> " + serverResponse.getStatus());
                        } else {
                            System.out.println("<=== saveJobStartData - Failed ===> ");
                        }
                    } catch (Exception e) {
                        System.out.println("<=== saveJobStartData - Crash : ===> " + e.toString());
                    }
                }

                @Override
                public void onFailure(Call<BaseResponse> call, Throwable t) {
                    System.out.println("<=== Exceptions ===> " + t.toString());
                }
            });
            return null;
        }
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        //calling the method displayselectedscreen and passing the id of selected menu
        displaySelectedScreen(item.getItemId());
        //make this method blank
        return true;
    }

    private void openMyDayProgressActivity(){
        Intent intent = new Intent(this, DayProgressActivity.class);
        startActivity(intent);
    }

    private void openCallSettingActivity(){
        Intent intent = new Intent(this, ActivityCallSetting.class);
        startActivity(intent);
    }

    private void logout() {
        JsonObject jsonEvent = new JsonObject();
        jsonEvent.addProperty("userId",application.getCurrentUser().getUserId());
        jsonEvent.addProperty("eventId",14);
        jsonEvent.addProperty("appVersion",appVersion);
        new SaveUserEventData().execute(jsonEvent);
        drawer.closeDrawer(GravityCompat.START);
        Utils.clearDataOnLogout(this);
        Intent logoutIntent = new Intent(HomeActivity.this, SignInActivity.class);
        logoutIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(logoutIntent);
        Toast.makeText(this, "Logged Out", Toast.LENGTH_SHORT).show();
        finish();
    }

    @Override
    public void invalidateOptionsMenu() {
        super.invalidateOptionsMenu();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        if(application.getIsDirectCall()=='N'){
            menu.getItem(0).setVisible(false);
        }
        return super.onCreateOptionsMenu(menu);
    }
}
