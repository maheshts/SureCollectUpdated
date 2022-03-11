package com.loan.recovery.database;

import android.content.ContentValues;
import android.database.SQLException;

import java.util.List;

public interface Repository {
    Long getHiddenNumberContactId();

    List<Recording> getRecordings();

    void insertRecording(Recording recording);

    void updateRecording(Recording recording);

    void deleteRecording(Long recordingId);

    void updateRecording(Long recordId, boolean isRecording, int value);
}
