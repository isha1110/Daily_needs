package com.skinfotech.dailyneeds.models.requests;

import com.google.gson.annotations.SerializedName;

public class TokenRequest {

    @SerializedName("userId")
    private String userId = "";
    @SerializedName("token")
    private String token = "";

    public TokenRequest(String userId, String token) {
        this.userId = userId;
        this.token = token;
    }
}
