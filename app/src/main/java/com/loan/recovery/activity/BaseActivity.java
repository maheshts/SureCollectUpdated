package com.loan.recovery.activity;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.loan.recovery.R;
import com.loan.recovery.util.AppConstants;

/**
 * Created by Mallikarjuna on 08/JUN/2020.
 */

public abstract class BaseActivity extends AppCompatActivity {
    private static final String TAG = "BaseActivity";
    ProgressDialog progressDialog;
    private String settedTheme;
    public static final String LIGHT_THEME = "light_theme";
    public static final String DARK_THEME = "dark_theme";

    abstract protected Fragment createFragment();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        progressDialog = new ProgressDialog(BaseActivity.this);
    }

    public void showProgressDialog(String title, String message) {
        progressDialog.setTitle(title);
        progressDialog.setMessage(message);
        progressDialog.show();
        progressDialog.setCancelable(false);
    }

    public void hideProgressDialog() {
        if (progressDialog != null && progressDialog.isShowing())
            progressDialog.dismiss();
    }

    public String getSettedTheme() {
        return settedTheme;
    }

    protected void setTheme() {
        final SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
        if (settings.getString(AppConstants.APP_THEME, LIGHT_THEME).equals(LIGHT_THEME)) {
            settedTheme = LIGHT_THEME;
            setTheme(R.style.AppThemeLight);
        } else {
            settedTheme = DARK_THEME;
            setTheme(R.style.AppThemeDark);
        }
    }

    protected void insertFragment(int fragmentId) {
        FragmentManager fm = getSupportFragmentManager();
        Fragment fragment = fm.findFragmentById(fragmentId);
        if (fragment == null) {
            fragment = createFragment();
            fm.beginTransaction().
                    add(fragmentId, fragment).
                    commit();
        }
    }

    protected void checkIfThemeChanged() {
        final SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
        if (!settings.getString(AppConstants.APP_THEME, LIGHT_THEME).equals(settedTheme)) {
            setTheme();
            recreate();
        }
    }
}
