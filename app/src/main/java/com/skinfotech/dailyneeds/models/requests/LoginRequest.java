package com.skinfotech.dailyneeds.models.requests;

import com.google.gson.annotations.SerializedName;

public class LoginRequest {

    @SerializedName("mobileNumber")
    private String mMobileNumber = "";

    public LoginRequest(String mMobileNumber) {
        this.mMobileNumber = mMobileNumber;
    }
}
