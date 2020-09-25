package com.skinfotech.dailyneeds.models.requests;

import com.google.gson.annotations.SerializedName;

public class VerifyEmailRequest {

    @SerializedName("email")
    private String mEmailAddress;

    public VerifyEmailRequest(String emailAddress) {
        mEmailAddress = emailAddress;
    }
}
