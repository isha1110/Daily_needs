package com.skinfotech.dailyneeds.models.requests;

import com.google.gson.annotations.SerializedName;

public class RegistrationRequest {

    @SerializedName("name")
    private String mUserName = "";
    @SerializedName("email")
    private String mEmailAddress = "";
    @SerializedName("password")
    private String mPassword = "";
    @SerializedName("mobile")
    private String mUserMobile = "";

    public void setUserName(String userName) {
        mUserName = userName;
    }

    public void setEmailAddress(String emailAddress) {
        mEmailAddress = emailAddress;
    }

    public void setPassword(String password) {
        mPassword = password;
    }

    public void setUserMobile(String userMobile) {
        mUserMobile = userMobile;
    }
}
