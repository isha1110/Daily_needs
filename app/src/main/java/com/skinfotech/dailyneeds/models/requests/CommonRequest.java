package com.skinfotech.dailyneeds.models.requests;

import com.google.gson.annotations.SerializedName;

public class CommonRequest {

    @SerializedName("userId")
    private String mUserId = "";

    public void setUserId(String userId) {
        mUserId = userId;
    }

    public CommonRequest() {
    }

    public CommonRequest(String userId) {
        mUserId = userId;
    }
}
