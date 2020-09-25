package com.skinfotech.dailyneeds.models.requests;

import com.google.gson.annotations.SerializedName;

public class ProfileRequest {

    @SerializedName("username")
    private String username = "";
    @SerializedName("userId")
    private String userId = "";

    public ProfileRequest(String username,String userId) {
        this.username = username;
        this.userId=userId;
    }

}
