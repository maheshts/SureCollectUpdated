package com.loan.recovery.retrofit.model;

import com.google.gson.annotations.SerializedName;

public class FileUploadResponse extends BaseResponse {
    @SerializedName("fileUuid")
    private String fileRef;

    public String getFileRef() {
        return fileRef;
    }

    public void setFileRef(String fileRef) {
        this.fileRef = fileRef;
    }
}
