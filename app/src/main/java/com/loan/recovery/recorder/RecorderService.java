package com.loan.recovery.recorder;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.database.SQLException;
import android.graphics.Color;
import android.media.AudioManager;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import com.loan.recovery.LoanApplication;
import com.loan.recovery.R;
import com.loan.recovery.contactslist.ContactsListActivityMain;
import com.loan.recovery.database.Recording;
import com.loan.recovery.database.RepositoryImpl;
import com.loan.recovery.util.AppConstants;
import com.loan.recovery.util.Utils;

import org.acra.ACRA;

public class RecorderService extends Service {
    private String receivedNumPhone = null;
    private Boolean incoming = null;
    private Recorder recorder;
    private Thread speakerOnThread;
    private AudioManager audioManager;
    private NotificationManager nm;
    private static RecorderService self;
    private boolean speakerOn = false;
    RepositoryImpl repository;

    public static final int NOTIFICATION_ID = 1;
    private static final String CHANNEL_ID = "call_recorder_channel";
    public static final int RECORD_AUTOMMATICALLY = 1;
    public static final int RECORD_ERROR = 4;
    public static final int RECORD_SUCCESS = 5;
    static final String ACTION_STOP_SPEAKER = "STOP_SPEAKER";
    static final String ACTION_START_SPEAKER = "START_SPEAKER";

    static final String ACRA_PHONE_NUMBER = "phone_number";
    static final String ACRA_INCOMING = "incoming";
    private LoanApplication application;

    @Override
    public IBinder onBind(Intent i) {
        return null;
    }

    public void onCreate() {
        super.onCreate();
        recorder = new Recorder(getApplicationContext());
        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        nm = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
        application = LoanApplication.getInstance();
        self = this;
        repository = new RepositoryImpl(this, AppConstants.DATABASE_NAME);
    }

    public static RecorderService getService() {
        return self;
    }

    public Recorder getRecorder() {
        return recorder;
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private void createChannel() {
        CharSequence name = "Record My Call";
        String description = "Record My Call - Android App";
        int importance = NotificationManager.IMPORTANCE_LOW;
        NotificationChannel mChannel = new NotificationChannel(CHANNEL_ID, name, importance);
        mChannel.setDescription(description);
        mChannel.setShowBadge(false);
        mChannel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
        nm.createNotificationChannel(mChannel);
    }

    public Notification buildNotification(int typeOfNotification, int message) {
        Intent goToActivity = new Intent(getApplicationContext(), ContactsListActivityMain.class);
        PendingIntent tapNotificationPi = PendingIntent.getActivity(getApplicationContext(), 0, goToActivity, 0);
        Intent sendBroadcast = new Intent(getApplicationContext(), ControlRecordingReceiver.class);
        Resources res = getApplicationContext().getResources();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            createChannel();
        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), CHANNEL_ID)
                .setSmallIcon(R.mipmap.sure_icon)
                .setContentIntent(tapNotificationPi);

        switch (typeOfNotification) {
            case RECORD_AUTOMMATICALLY:
                if (audioManager.isSpeakerphoneOn() || speakerOn) {
                    sendBroadcast.setAction(ACTION_STOP_SPEAKER);
                    PendingIntent stopSpeakerPi = PendingIntent.getBroadcast(application.getContext(), 0, sendBroadcast, 0);
                    builder.addAction(new NotificationCompat.Action.Builder(R.mipmap.sure_icon,
                            res.getString(R.string.stop_speaker), stopSpeakerPi).build())
                            .setContentText(res.getString(R.string.recording_speaker_on));
                } else {
                    sendBroadcast.setAction(ACTION_START_SPEAKER);
                    PendingIntent startSpeakerPi = PendingIntent.getBroadcast(getApplicationContext(), 0, sendBroadcast, 0);
                    builder.addAction(new NotificationCompat.Action.Builder(R.mipmap.sure_icon,
                            res.getString(R.string.start_speaker), startSpeakerPi).build());
                }
                break;

            case RECORD_ERROR:
                builder.setColor(Color.RED)
                        .setColorized(true)
                        .setSmallIcon(R.drawable.notification_icon_error)
                        .setAutoCancel(true);
                break;

            case RECORD_SUCCESS:
                builder.setSmallIcon(R.mipmap.sure_icon)
                        .setAutoCancel(true);
        }

        return builder.build();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);

        if (intent.hasExtra(CallReceiver.ARG_NUM_PHONE))
            receivedNumPhone = intent.getStringExtra(CallReceiver.ARG_NUM_PHONE);
        incoming = intent.getBooleanExtra(CallReceiver.ARG_INCOMING, false);
        Log.e(AppConstants.DEBUG, String.format("RecorderService is started. Phone number: %s. Incoming: %s", receivedNumPhone, incoming));
        try {
            ACRA.getErrorReporter().putCustomData(ACRA_PHONE_NUMBER, receivedNumPhone);
            ACRA.getErrorReporter().putCustomData(ACRA_INCOMING, incoming.toString());
        } catch (IllegalStateException ignored) {
        }

        try {
            Log.e(AppConstants.DEBUG, "RecorderService is started in onStartCommand()");
            recorder.startRecording(receivedNumPhone);
//            if (settings.getBoolean(AppConstants.SPEAKER_USE, false))
//                putSpeakerOn();
            startForeground(NOTIFICATION_ID, buildNotification(RECORD_AUTOMMATICALLY, 0));
        } catch (RecordingException e) {
            Log.e(AppConstants.ERROR, "onStartCommand: unable to start recorder: " + e.getMessage() + " Stoping the service...");
            startForeground(NOTIFICATION_ID, buildNotification(RECORD_ERROR, R.string.error_recorder_cannot_start));
        }

        return START_NOT_STICKY;
    }

    private void resetState() {
        self = null;
    }

    void putSpeakerOn() {
        speakerOnThread = new Thread() {
            @Override
            public void run() {
                Log.e(AppConstants.DEBUG, "Speaker has been turned on");
                try {
                    while (!Thread.interrupted()) {
                        audioManager.setMode(AudioManager.MODE_IN_CALL);
                        if (!audioManager.isSpeakerphoneOn())
                            audioManager.setSpeakerphoneOn(true);
                        sleep(500);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };
        speakerOnThread.start();
        speakerOn = true;
    }

    void putSpeakerOff() {
        if (speakerOnThread != null) {
            speakerOnThread.interrupt();
            Log.e(AppConstants.DEBUG, "Speaker has been turned off");
        }
        speakerOnThread = null;
        if (audioManager != null && audioManager.isSpeakerphoneOn()) {
            audioManager.setMode(AudioManager.MODE_NORMAL);
            audioManager.setSpeakerphoneOn(false);
        }
        speakerOn = false;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.e(AppConstants.DEBUG, "RecorderService is stoping now...");

        putSpeakerOff();
        if (!recorder.isRunning() || recorder.hasError()) {
            onDestroyCleanUp();
            return;
        }

        recorder.stopRecording();
        String contactId = application.getCaseId();
        String metaData = application.getMetaData();
        application.setCaseId(null);
        application.setMetaData(null);
        Recording recording = new Recording(null, contactId, recorder.getAudioFilePath(), incoming,
                recorder.getStartingTime(), Utils.getDateTime(), recorder.getFormat(), false, metaData,
                recorder.getSource(), "application.getCurrentUser().get", 0, 0);
        try {
            if (!(contactId == null || contactId.equals(""))) {
                recording.save(repository);
            }
        } catch (SQLException exc) {
            Log.e(AppConstants.ERROR, "SQL exception: " + exc.getMessage());
            onDestroyCleanUp();
            return;
        }

        nm.notify(NOTIFICATION_ID, buildNotification(RECORD_SUCCESS, 0));
        onDestroyCleanUp();
    }

    private void onDestroyCleanUp() {
        resetState();
        try {
            ACRA.getErrorReporter().clearCustomData();
        } catch (IllegalStateException ignored) {
        }
    }
}
