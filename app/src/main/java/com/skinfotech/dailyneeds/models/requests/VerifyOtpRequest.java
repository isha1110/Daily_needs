package com.skinfotech.dailyneeds.models.requests;

import com.google.gson.annotations.SerializedName;

public class VerifyOtpRequest {

    @SerializedName("email")
    private String mEmailAddress = "";
    @SerializedName("otp")
    private String mOtpEntered = "";

    public VerifyOtpRequest(String otpEntered, String emailAddress) {
        mEmailAddress = emailAddress;
        mOtpEntered = otpEntered;
    }
}
