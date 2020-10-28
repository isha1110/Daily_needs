package com.skinfotech.dailyneeds.models.responses;

import com.google.gson.annotations.SerializedName;

public class CommonResponse {

    @SerializedName("errorCode")
    private String mErrorCode = "";
    @SerializedName("errorMessage")
    private String mErrorMessage = "";
    @SerializedName("userId")
    private String mUserId = "";
    @SerializedName("userEmail")
    private String userEmail = "";
    @SerializedName("userMobile")
    private String userMobile = "";

    public String getUserEmail() {
        return userEmail;
    }

    public String getUserMobile() {
        return userMobile;
    }

    public String getUserId() {
        return mUserId;
    }

    public String getErrorCode() {
        return mErrorCode;
    }

    public String getErrorMessage() {
        return mErrorMessage;
    }
}
