package com.loan.recovery.recorder;

import android.content.Context;
import android.util.Log;

import com.loan.recovery.LoanApplication;
import com.loan.recovery.util.AppConstants;
import com.loan.recovery.util.Utils;

import org.acra.ACRA;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import static android.media.MediaRecorder.AudioSource.MIC;
import static android.media.MediaRecorder.AudioSource.VOICE_CALL;
import static android.media.MediaRecorder.AudioSource.VOICE_COMMUNICATION;
import static android.media.MediaRecorder.AudioSource.VOICE_RECOGNITION;

public class Recorder {
    private File audioFile;
    private Thread recordingThread;
    private String startingTime;
    private final String format;
    private final String mode;
    public static final String WAV_FORMAT = "wav";
    public static final String AAC_HIGH_FORMAT = "aac_hi";
    public static final String AAC_MEDIUM_FORMAT = "aac_med";
    public static final String AAC_BASIC_FORMAT = "aac_bas";
    static final String MONO = "mono";
    private int source;
    private boolean hasError = false;
    private Context context;

    private static final String ACRA_FORMAT = "format";
    private static final String ACRA_MODE = "mode";
    private static final String ACRA_SAVE_PATH = "save_path";

     Recorder(Context context) {
         this.context = context;
         format = AppConstants.FORMAT;
         mode = AppConstants.MODE;
    }

    String getStartingTime() {
        return startingTime;
    }

    String getAudioFilePath() {
        return audioFile.getAbsolutePath();
    }

    void startRecording(String phoneNumber) throws RecordingException {
        if(phoneNumber == null)
            phoneNumber = "";

        if(isRunning())
            stopRecording();
        String extension = format.equals(WAV_FORMAT) ? ".wav" : ".aac";
        File recordingsDir;

            recordingsDir = context.getFilesDir();
//        else {
//            String filePath = settings.getString(AppConstants.STORAGE_PATH, null);
//            recordingsDir = (filePath == null) ? context.getExternalFilesDir(null) : new File(filePath);
//            if(recordingsDir == null) //recordingsDir poate fi null în cazul în care getExternalFilesDir(null) returnează null, adică nu e montat (disponibil) un astfel de spațiu.
//                recordingsDir = context.getFilesDir();
//            }

//        <CalledPhoneNumber>_<yymmddhhmm>.awv

        phoneNumber = phoneNumber.replaceAll("[()/.,* ;+]", "_");
        System.out.println("========> "+phoneNumber);
        if(phoneNumber == null || phoneNumber.isEmpty())
        {
            String metaData = LoanApplication.getInstance().getMetaData();
            if(metaData != null && !metaData.isEmpty()) {
                phoneNumber = metaData.split("&&&")[2];
            }
        }
        String fileName = phoneNumber+"_" + new SimpleDateFormat("yyyyMMddhhmmss", Locale.US).
                format(new Date(System.currentTimeMillis())) + extension;
        System.out.println("========> "+fileName);
        audioFile = new File(recordingsDir, fileName);
        Log.e(AppConstants.DEBUG, String.format("Recording session started. Format: %s. Mode: %s. Save path: %s",
                format, mode, audioFile.getAbsolutePath()));
        //This data is cleared in RecorderService::onDestroy().
        try {
            ACRA.getErrorReporter().putCustomData(ACRA_FORMAT, format);
            ACRA.getErrorReporter().putCustomData(ACRA_MODE, mode);
            ACRA.getErrorReporter().putCustomData(ACRA_SAVE_PATH, audioFile.getAbsolutePath());
        }
        catch (IllegalStateException exc) {
        }

        if(format.equals(WAV_FORMAT))
            recordingThread = new Thread(new RecordingThreadWav(context, mode, this));
        else
            recordingThread = new Thread(new RecordingThreadAac(context, audioFile, format, mode, this));

        recordingThread.start();
        startingTime = Utils.getDateTime();
    }

    void stopRecording() {
        if(recordingThread != null) {
            Log.e(AppConstants.DEBUG, "Recording session ended.");
                recordingThread.interrupt();
            recordingThread = null;
            if(format.equals(WAV_FORMAT)) {
                Thread copyPcmToWav = new Thread(new RecordingThreadWav.CopyPcmToWav(context, audioFile, mode, this));
                copyPcmToWav.start();
            }
        }
    }

    boolean isRunning() {
        return recordingThread != null && recordingThread.isAlive();
    }

    public String getFormat() {
        return format;
    }

    public String getMode() {
        return mode;
    }

    public String getSource() {
        switch (source) {
            case VOICE_RECOGNITION: return "Voice recognition";
            case VOICE_COMMUNICATION: return "Voice communication";
            case VOICE_CALL: return "Voice call";
            case MIC:return "Microphone";
            default:return "Source unrecognized";
        }
    }

    public void setSource(int source) { this.source = source; }

    public boolean hasError() { return hasError; }

    void setHasError() { this.hasError = true; }
}
