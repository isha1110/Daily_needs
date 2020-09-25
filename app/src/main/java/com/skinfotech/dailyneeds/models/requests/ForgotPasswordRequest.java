package com.skinfotech.dailyneeds.models.requests;

import com.google.gson.annotations.SerializedName;

public class ForgotPasswordRequest {

    @SerializedName("email")
    private String mEmailAddress;
    @SerializedName("newPassword")
    private String mNewPassword;

    public void setEmailAddress(String emailAddress) {
        mEmailAddress = emailAddress;
    }

    public void setNewPassword(String newPassword) {
        mNewPassword = newPassword;
    }
}
