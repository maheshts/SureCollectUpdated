package com.loan.recovery.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.VisibleForTesting;

import java.util.ArrayList;
import java.util.List;

public class RepositoryImpl implements Repository {
    private SQLiteDatabase database;

    public RepositoryImpl(Context context, String dbname) {
        SQLiteOpenHelper helper = new CallRecorderDbHelper(context, dbname);
        this.database = helper.getWritableDatabase();
    }

    @Override
    public Long getHiddenNumberContactId() {
        Cursor cursor = database.query(ContactsContract.Contacts.TABLE_NAME, new String[]{ContactsContract.Contacts._ID},
                ContactsContract.Contacts.COLUMN_NAME_NUMBER + " is " + "NULL", null, null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            long id = cursor.getLong(cursor.getColumnIndex(ContactsContract.Contacts._ID));
            cursor.close();
            return id;
        } else
            return null;
    }

    private Recording populateRecording(Cursor cursor) {
        Recording recording = new Recording();
        recording.setId(cursor.getLong(cursor.getColumnIndex(RecordingsContract.Recordings._ID)));
        String contactId = cursor.getString(cursor.getColumnIndex(RecordingsContract.Recordings.COLUMN_NAME_CONTACT_ID));
        recording.setContactId(contactId);
        recording.setIncoming(cursor.getInt(cursor.getColumnIndex(RecordingsContract.Recordings.COLUMN_NAME_INCOMING)) == 1);
        recording.setPath(cursor.getString(cursor.getColumnIndex(RecordingsContract.Recordings.COLUMN_NAME_PATH)));
        recording.setStartTimestamp(cursor.getString(cursor.getColumnIndex(RecordingsContract.Recordings.COLUMN_NAME_START_TIMESTAMP)));
        recording.setEndTimestamp(cursor.getString(cursor.getColumnIndex(RecordingsContract.Recordings.COLUMN_NAME_END_TIMESTAMP)));
        recording.setFormat(cursor.getString(cursor.getColumnIndex(RecordingsContract.Recordings.COLUMN_NAME_FORMAT)));
        recording.setIsNameSet(cursor.getInt(cursor.getColumnIndex(RecordingsContract.Recordings.COLUMN_NAME_IS_NAME_SET)) == 1);
        recording.setMode(cursor.getString(cursor.getColumnIndex(RecordingsContract.Recordings.COLUMN_NAME_MODE)));
        recording.setSource(cursor.getString(cursor.getColumnIndex(RecordingsContract.Recordings.COLUMN_NAME_SOURCE)));
        recording.setRecording(cursor.getInt(cursor.getColumnIndex(RecordingsContract.Recordings.COLUMN_NAME_CALL_RECORDING_FILE)));
        recording.setMetadata(cursor.getInt(cursor.getColumnIndex(RecordingsContract.Recordings.COLUMN_NAME_CALL_META_DATA)));
        return recording;
    }

    @Override
    public List<Recording> getRecordings() {
        List<Recording> list = new ArrayList<>();
        Cursor cursor = database.query(RecordingsContract.Recordings.TABLE_NAME,
                null, null, null, null, null, RecordingsContract.Recordings.COLUMN_NAME_END_TIMESTAMP + " DESC");

        while (cursor.moveToNext()) {
            list.add(populateRecording(cursor));
        }
        cursor.close();
        return list;
    }

    @VisibleForTesting
    Recording getRecording(Long id) {
        Cursor cursor = database.query(RecordingsContract.Recordings.TABLE_NAME, null, RecordingsContract.Recordings._ID +
                "=" + id, null, null, null, null);

        Recording recording = null;
        if (cursor != null && cursor.moveToFirst()) {
            recording = populateRecording(cursor);
        }
        return recording;
    }

    private ContentValues createRecordingContentValues(Recording recording) {
        ContentValues values = new ContentValues();

        values.put(RecordingsContract.Recordings.COLUMN_NAME_CONTACT_ID, recording.getContactId());
        values.put(RecordingsContract.Recordings.COLUMN_NAME_PATH, recording.getPath());
        values.put(RecordingsContract.Recordings.COLUMN_NAME_INCOMING, recording.isIncoming());
        values.put(RecordingsContract.Recordings.COLUMN_NAME_START_TIMESTAMP, recording.getStartTimestamp());
        values.put(RecordingsContract.Recordings.COLUMN_NAME_END_TIMESTAMP, recording.getEndTimestamp());
        values.put(RecordingsContract.Recordings.COLUMN_NAME_IS_NAME_SET, recording.getIsNameSet());
        values.put(RecordingsContract.Recordings.COLUMN_NAME_FORMAT, recording.getFormat());
        values.put(RecordingsContract.Recordings.COLUMN_NAME_MODE, recording.getMode());
        values.put(RecordingsContract.Recordings.COLUMN_NAME_SOURCE, recording.getSource());
        return values;
    }

    @Override
    public void insertRecording(Recording recording) {
        ContentValues values = createRecordingContentValues(recording);
        long rowId = database.insertOrThrow(RecordingsContract.Recordings.TABLE_NAME, null, values);
        recording.setId(rowId);
    }

    @Override
    public void updateRecording(Recording recording) throws IllegalStateException, SQLException {
        if (recording.getId() == 0)
            throw new IllegalStateException("This contact was not saved in database");

        ContentValues values = createRecordingContentValues(recording);
        int updatedRows = database.update(RecordingsContract.Recordings.TABLE_NAME, values,
                RecordingsContract.Recordings._ID + "=" + recording.getId(), null);
        if (updatedRows != 1)
            throw new SQLException("The return value of updating this recording was " + updatedRows);
    }

    @Override
    public void deleteRecording(Long recordId) throws IllegalStateException, SQLException {
        if (recordId == 0)
            throw new IllegalStateException("This recording was not saved in database");

        Recording recording = getRecording(recordId);
        if (recording.getMetadata() == 1 && recording.getRecording() == 1) {
            int deletedRows = database.delete(RecordingsContract.Recordings.TABLE_NAME,
                    RecordingsContract.Recordings._ID + "=" + recordId, null);
            if (deletedRows != 1)
                throw new SQLException("The return value of deleting this recording was " + deletedRows);
        }
    }

    @Override
    public void updateRecording(Long recordId, boolean isRecording, int value) {
        ContentValues values = new ContentValues();
        if (isRecording)
            values.put(RecordingsContract.Recordings.COLUMN_NAME_CALL_RECORDING_FILE, value);
        else
            values.put(RecordingsContract.Recordings.COLUMN_NAME_CALL_META_DATA, value);

        int updatedRows = database.update(RecordingsContract.Recordings.TABLE_NAME, values,
                RecordingsContract.Recordings._ID + "=" + recordId, null);
//        if (updatedRows != 1)
//            throw new SQLException("The return value of updating this recording was " + updatedRows);
    }

    @VisibleForTesting
    void closeDb() {
        database.close();
    }
}
