package com.loan.recovery.setup;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.loan.recovery.R;
import com.loan.recovery.activity.BaseActivity;
import com.loan.recovery.contactslist.ContactsListActivityMain;

public class SetupActivity extends BaseActivity {
    private int checkResult;
    public static final String EXIT_APP = "exit_app";

    @Override
    protected Fragment createFragment() {
        if((checkResult & ContactsListActivityMain.PERMS_NOT_GRANTED) != 0)
            return new PermissionsFragment();
        else
            return null;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
//        setTheme();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setup_activity);
        checkResult = getIntent().getIntExtra(ContactsListActivityMain.SETUP_ARGUMENT,
                ContactsListActivityMain.PERMS_NOT_GRANTED);
        insertFragment(R.id.setup_fragment_container);
    }

    public void cancelSetup() {
        Intent intent = new Intent();
        intent.putExtra(SetupActivity.EXIT_APP, true);
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    public void onBackPressed() {
        cancelSetup();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}
