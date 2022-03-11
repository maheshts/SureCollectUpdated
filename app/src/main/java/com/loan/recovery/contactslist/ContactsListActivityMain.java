package com.loan.recovery.contactslist;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.preference.PreferenceManager;

import com.loan.recovery.R;
import com.loan.recovery.activity.BaseActivity;
import com.loan.recovery.setup.SetupActivity;

public class ContactsListActivityMain extends BaseActivity {
    private static final int SETUP_ACTIVITY = 3;
    public static final int PERMS_NOT_GRANTED = 2;
    public static final String SETUP_ARGUMENT = "setup_arg";

    @Override
    protected Fragment createFragment() {
        return new UnassignedRecordingsFragment();
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkIfThemeChanged();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
//        setTheme();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.contacts_list_activity_onepane);

        Toolbar toolbar = findViewById(R.id.toolbar_main);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null)
            actionBar.setDisplayShowTitleEnabled(false);
        TextView title = findViewById(R.id.actionbar_title);
        title.setText(getString(R.string.app_name));
        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);

        int permsNotGranted = 0;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            permsNotGranted = checkPermissions() ? 0 : PERMS_NOT_GRANTED;
        }
        int checkResult = permsNotGranted;

        if (checkResult != 0) {
            Intent setupIntent = new Intent(this, SetupActivity.class);
            setupIntent.putExtra(SETUP_ARGUMENT, checkResult);
            startActivityForResult(setupIntent, SETUP_ACTIVITY);
        }
        FragmentManager fm = getSupportFragmentManager();
        Fragment unassignedToInsert = new UnassignedRecordingsFragment();
        fm.beginTransaction().replace(R.id.contacts_list_fragment_container, unassignedToInsert)
                .commit();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data); //necesar pentru că altfel nu apelează onActivityResult din fragmente:
        if (resultCode == RESULT_OK && requestCode == SETUP_ACTIVITY) {
            if (data.getBooleanExtra(SetupActivity.EXIT_APP, true))
                finish();
        }
    }

//    @Override
//    public void onBackPressed() {
//        new MaterialDialog.Builder(this)
//                .title(R.string.exit_app_title)
//                .icon(getResources().getDrawable(R.drawable.question_mark))
//                .content(R.string.exit_app_message)
//                .positiveText(android.R.string.ok)
//                .negativeText(android.R.string.cancel)
//                .onPositive((@NonNull MaterialDialog dialog, @NonNull DialogAction which) ->
//                        ContactsListActivityMain.super.onBackPressed())
//                .show();
//    }

    private boolean checkPermissions() {
        boolean callState = ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE)
                == PackageManager.PERMISSION_GRANTED;
        boolean phoneState = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE)
                == PackageManager.PERMISSION_GRANTED;
        boolean recordAudio = ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
                == PackageManager.PERMISSION_GRANTED;
        boolean readStorage = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED;
        boolean writeStorage = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED;
        return callState && phoneState && recordAudio && readStorage && writeStorage;
    }
}