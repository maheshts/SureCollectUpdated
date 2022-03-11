package com.loan.recovery.contactslist;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;

import androidx.annotation.NonNull;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.loan.recovery.R;
import com.loan.recovery.database.Recording;
import com.loan.recovery.database.Repository;
import com.loan.recovery.util.AppConstants;

import java.lang.ref.WeakReference;
import java.util.logging.Logger;

public class MoveAsyncTask extends AsyncTask<Recording, Integer, Boolean> {
    public long alreadyCopied = 0;
    private String path;
    private long totalSize;
    private MaterialDialog dialog;
    private Repository repository;
    private WeakReference<Activity> activityRef;

    MoveAsyncTask(Repository repository, String folderPath, long totalSize, Activity activity) {
        this.path = folderPath;
        this.totalSize = totalSize;
        this.repository = repository;
        activityRef = new WeakReference<>(activity);
    }

    public void callPublishProgress(int progress) {
        publishProgress(progress);
    }

    @Override
    protected void onPreExecute() {
        dialog = new MaterialDialog.Builder(activityRef.get())
                .title(R.string.progress_title)
                .content(R.string.progress_text)
                .progress(false, 100, true)
                .negativeText("Cancel")
                .onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        cancel(true);
                    }
                })
                .build();
        dialog.setCancelable(false);
        dialog.show();
    }

    @Override
    protected void onProgressUpdate(Integer...integers) {
        dialog.setProgress(integers[0]);
    }

    @Override
    protected void onCancelled() {
        new MaterialDialog.Builder(activityRef.get())
                .title(R.string.warning_title)
                .content(R.string.canceled_move)
                .positiveText("OK")
                .icon(activityRef.get().getResources().getDrawable(R.drawable.warning))
                .show();
    }

    @Override
    protected void onPostExecute(Boolean result) {
        dialog.dismiss();
        if(result) {
            new MaterialDialog.Builder(activityRef.get())
                    .title(R.string.success_move_title)
                    .content(R.string.success_move_text)
                    .positiveText("OK")
                    .icon(activityRef.get().getResources().getDrawable(R.drawable.success))
                    .show();
        }
        else {
            new MaterialDialog.Builder(activityRef.get())
                    .title(R.string.error_title)
                    .content(R.string.error_move)
                    .positiveText("OK")
                    .icon(activityRef.get().getResources().getDrawable(R.drawable.error))
                    .show();
        }
    }

    @Override
    protected Boolean doInBackground(Recording...recordings) {
        for(Recording recording : recordings) {
            try {
                recording.move(repository, path, this, totalSize);
                if(isCancelled())
                    break;
            }
            catch (Exception exc) {
                Log.e(AppConstants.ERROR, "Error moving the recording(s): " + exc.getMessage());
                return false;
            }
        }
        return true;
    }
}