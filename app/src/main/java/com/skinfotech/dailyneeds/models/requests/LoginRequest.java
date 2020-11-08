package com.skinfotech.dailyneeds.models.requests;

import com.google.gson.annotations.SerializedName;

public class LoginRequest {

    @SerializedName("email")
    private String mEmailAddress = "";
    @SerializedName("password")
    private String mPassword = "";
    @SerializedName("mode")
    private String mMode = "";

    public void setMode(String mode) { mMode = mode; }

    public void setEmailAddress(String emailAddress) {
        mEmailAddress = emailAddress;
    }

    public void setPassword(String password) {
        mPassword = password;
    }
}
