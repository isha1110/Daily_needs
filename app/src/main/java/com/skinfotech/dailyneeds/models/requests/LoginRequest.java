package com.skinfotech.dailyneeds.models.requests;

import com.google.gson.annotations.SerializedName;

public class LoginRequest {

    @SerializedName("email")
    private String mEmailAddress = "";
    @SerializedName("password")
    private String mPassword = "";

    public void setEmailAddress(String emailAddress) {
        mEmailAddress = emailAddress;
    }

    public void setPassword(String password) {
        mPassword = password;
    }
}
