package com.skinfotech.dailyneeds.models.requests;

import com.google.gson.annotations.SerializedName;

public class DefaultAddressRequest {

    @SerializedName("userId")
    private String mUserId;
    @SerializedName("addressId")
    private String mAddressId;

    public void setUserId(String userId) {
        mUserId = userId;
    }

    public void setAddressId(String addressId) {
        mAddressId = addressId;
    }

}
