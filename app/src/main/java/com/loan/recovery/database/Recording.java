package com.loan.recovery.database;

import android.content.Context;
import android.content.res.Resources;
import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.loan.recovery.R;
import com.loan.recovery.contactslist.MoveAsyncTask;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Recording implements Parcelable {
    private Long id = 0L;
    private String contactId;
    private String path;
    private Boolean incoming;
    private String startTimestamp, endTimestamp;
    private Boolean isNameSet;
    private String format;
    private String mode;
    private String source;
    private String fromPhone;

    private int recording;
    private int metadata;

    public Recording() {}

    public Recording(Long id, String contactId, String path, Boolean incoming, String startTimestamp, String endTimestamp,
                     String format, Boolean isNameSet, String mode, String source, String fromPhone, int recording, int metadata) {
        if(id != null) this.id = id;
        if(contactId != null) this.contactId = contactId;
        if(path != null) this.path = path;
        if(incoming != null) this.incoming = incoming;
        if(startTimestamp != null) this.startTimestamp = startTimestamp;
        if(endTimestamp != null) this.endTimestamp = endTimestamp;
        if(isNameSet != null) this.isNameSet = isNameSet;
        if(format != null) this.format = format;
        if(mode != null) this.mode = mode;
        if(source != null) this.source = source;
        if(fromPhone != null) this.fromPhone = fromPhone;
        this.recording = recording;
        this.metadata = metadata;
    }

    public boolean exists() {
        return new File(path).isFile();
    }

    public void save(Repository repository) {
        repository.insertRecording(this);
    }

    public String getName() {
        if(!isNameSet)
            return startTimestamp;
        String fileName = new File(path).getName();
        return fileName.substring(0, fileName.length() - 4);
    }

    public static boolean hasIllegalChar(CharSequence fileName) {
        Pattern pattern = Pattern.compile("[^a-zA-Z0-9.\\- ]");
        Matcher matcher = pattern.matcher(fileName);
        return matcher.find();
    }

    public long getSize() {
        return new File(path).length();
    }


    public String getDate() {
        return startTimestamp;
    }

    public String getTime() {
        return new SimpleDateFormat("h:mm a", Locale.US).format(new Date(startTimestamp)); //3:45 PM
    }

    public void delete(Repository repository) throws SecurityException {
        repository.deleteRecording(id);
        new File(path).delete();
    }

    public void move(Repository repository, String folderPath, @Nullable MoveAsyncTask asyncTask, long totalSize)
            throws IOException {
        String fileName = new File(path).getName();
        InputStream in = new FileInputStream(path);
        OutputStream out = new FileOutputStream(new File(folderPath, fileName));

        byte[] buffer = new byte[1048576]; //dacă folosesc 1024 merge foarte încet
        int read;
        while ((read = in.read(buffer)) != -1) {
            out.write(buffer, 0, read);
            if(asyncTask != null) {
                asyncTask.alreadyCopied += read;
                asyncTask.callPublishProgress(Math.round(100 * asyncTask.alreadyCopied / totalSize));
                if (asyncTask.isCancelled())
                    break;
            }
        }
        in.close();
        out.flush();
        new File(path).delete();
        path = new File(folderPath, fileName).getAbsolutePath();
        repository.updateRecording(this);
    }

    public String getHumanReadingFormat(Context context) {
        final int wavBitrate = 705, aacHighBitrate = 128, aacMedBitrate = 64, aacBasBitrate = 32;
        Resources res = context.getResources();
        switch (format) {
            case "wav":
                return res.getString(R.string.lossless_quality) + " (WAV), 44khz 16bit WAV " + (mode.equals("mono") ? wavBitrate : wavBitrate * 2)
                        + "kbps " + mode.substring(0, 1).toUpperCase() + mode.substring(1);
            case "aac_hi":
                return res.getString(R.string.hi_quality) + " (AAC), 44khz 16bit AAC128 " + (mode.equals("mono") ? aacHighBitrate : aacHighBitrate * 2)
                        + "kbps " + mode.substring(0, 1).toUpperCase() + mode.substring(1);
            case "aac_med":
                return res.getString(R.string.med_quality) + " (AAC), 44khz 16bit AAC64 " + (mode.equals("mono") ? aacMedBitrate : aacMedBitrate * 2)
                        + "kbps " + mode.substring(0, 1).toUpperCase() + mode.substring(1);
            case "aac_bas":
                return res.getString(R.string.bas_quality) + " (AAC), 44khz 16bit AAC32 " + (mode.equals("mono") ? aacBasBitrate : aacBasBitrate * 2)
                        + "kbps " + mode.substring(0, 1).toUpperCase() + mode.substring(1);
        }
        return null;
    }

    public Long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public Boolean isIncoming() {
        return incoming;
    }

    public void setIncoming(boolean incoming) {
        this.incoming = incoming;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public String getFormat() {
        return format;
    }

    public Boolean getIsNameSet() { return isNameSet; }

    public void setIsNameSet(Boolean isNameSet) { this.isNameSet = isNameSet; }

    public String getContactId() { return contactId; }

    public void setContactId(String contactId) { this.contactId = contactId; }

    public String getSource() { return source; }

    public void setSource(String source) { this.source = source; }

    public String getStartTimestamp() {
        return startTimestamp;
    }

    public String getEndTimestamp() {
        return endTimestamp;
    }

    public String getMode() {
        return mode;
    }

    public void setStartTimestamp(String startTimestamp) {
        this.startTimestamp = startTimestamp;
    }

    public void setEndTimestamp(String endTimestamp) {
        this.endTimestamp = endTimestamp;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public String getFromPhone() {
        return fromPhone;
    }

    public void setFromPhone(String fromPhone) {
        this.fromPhone = fromPhone;
    }

    public int getRecording() {
        return recording;
    }

    public void setRecording(int recording) {
        this.recording = recording;
    }

    public int getMetadata() {
        return metadata;
    }

    public void setMetadata(int metadata) {
        this.metadata = metadata;
    }

    /** Necesară pentru comparațiile din teste. */
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Recording recording = (Recording) o;
        return id.equals(recording.id) &&
                Objects.equals(contactId, recording.contactId) &&
                Objects.equals(path, recording.path) &&
                Objects.equals(incoming, recording.incoming) &&
                Objects.equals(startTimestamp, recording.startTimestamp) &&
                Objects.equals(endTimestamp, recording.endTimestamp) &&
                Objects.equals(isNameSet, recording.isNameSet) &&
                Objects.equals(format, recording.format) &&
                Objects.equals(mode, recording.mode) &&
                Objects.equals(source, recording.source)&&
                Objects.equals(fromPhone, recording.fromPhone);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public int hashCode() {
        return Objects.hash(id, path);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(this.id);
        dest.writeValue(this.contactId);
        dest.writeString(this.path);
        dest.writeValue(this.incoming);
        dest.writeValue(this.startTimestamp);
        dest.writeValue(this.endTimestamp);
        dest.writeValue(this.isNameSet);
        dest.writeString(this.format);
        dest.writeString(this.mode);
        dest.writeString(this.source);
        dest.writeString(this.fromPhone);
    }

    protected Recording(Parcel in) {
        this.id = (Long) in.readValue(Long.class.getClassLoader());
        this.contactId = in.readString();
        this.path = in.readString();
//        this.incoming = (Boolean) in.readValue(Boolean.class.getClassLoader());
        this.startTimestamp = in.readString();
        this.endTimestamp = in.readString();
        this.isNameSet = (Boolean) in.readValue(Boolean.class.getClassLoader());
        this.format = in.readString();
        this.mode = in.readString();
        this.source = in.readString();
        this.fromPhone = in.readString();
    }

    public static final Creator<Recording> CREATOR = new Creator<Recording>() {
        @Override
        public Recording createFromParcel(Parcel source) {
            return new Recording(source);
        }

        @Override
        public Recording[] newArray(int size) {
            return new Recording[size];
        }
    };
}
