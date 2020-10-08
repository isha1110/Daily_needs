package com.skinfotech.dailyneeds.models.requests;

import com.google.gson.annotations.SerializedName;

public class VerifyOtpRequest {

    @SerializedName("mobileNumber")
    private String mMobileNumber = "";
    @SerializedName("otp")
    private String mOtpEntered = "";

    public VerifyOtpRequest(String otpEntered, String mobileNumber) {
        mMobileNumber = mobileNumber;
        mOtpEntered = otpEntered;
    }
}
