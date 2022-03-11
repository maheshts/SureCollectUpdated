package com.loan.recovery.recorder;

import android.app.NotificationManager;
import android.content.Context;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.util.Log;

import com.loan.recovery.R;
import com.loan.recovery.util.AppConstants;

import static android.media.MediaRecorder.AudioSource.VOICE_RECOGNITION;

abstract class RecordingThread {
    static final int SAMPLE_RATE = 44100;
    final int channels;
    final int bufferSize;
    final AudioRecord audioRecord;
    protected final Recorder recorder;
    protected Context context;

    RecordingThread(Context context, String mode, Recorder recorder) throws RecordingException {
        this.context = context;
        channels = (mode.equals(Recorder.MONO) ? 1 : 2);
        this.recorder = recorder;
        bufferSize = AudioRecord.getMinBufferSize(SAMPLE_RATE, channels == 1 ? AudioFormat.CHANNEL_IN_MONO : AudioFormat.CHANNEL_IN_STEREO,
                AudioFormat.ENCODING_PCM_16BIT);
        audioRecord = createAudioRecord();
        audioRecord.startRecording();
    }

    private AudioRecord createAudioRecord() throws RecordingException {
        AudioRecord audioRecord;
//        SharedPreferences settings = Utils.getPreferences(context);
//        int source = Integer.valueOf(settings.getString(AppConstants.SOURCE,
//                String.valueOf(VOICE_RECOGNITION)));
        int source = Integer.parseInt(String.valueOf(VOICE_RECOGNITION));
            try {
                audioRecord = new AudioRecord(source, SAMPLE_RATE,
                        channels == 1 ? AudioFormat.CHANNEL_IN_MONO : AudioFormat.CHANNEL_IN_STEREO,
                        AudioFormat.ENCODING_PCM_16BIT, bufferSize * 10);
            } catch (Exception e) {
                throw new RecordingException(e.getMessage());
            }

        if(audioRecord.getState() == AudioRecord.STATE_INITIALIZED) {
            Log.e(AppConstants.DEBUG, "createAudioRecord(): Audio source chosen: " + source);
            recorder.setSource(audioRecord.getAudioSource());
        }

        if(audioRecord.getState() != AudioRecord.STATE_INITIALIZED)
            throw new RecordingException("Unable to initialize AudioRecord");

        return audioRecord;
    }

    void disposeAudioRecord() {
        audioRecord.stop();
        audioRecord.release();
    }

    static void notifyOnError(Context context) {
        RecorderService service = RecorderService.getService();
        if (service != null) {
            NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            if (nm != null)
                nm.notify(RecorderService.NOTIFICATION_ID,
                        service.buildNotification(RecorderService.RECORD_ERROR,
                                R.string.error_recorder_failed));
        }
    }
}
