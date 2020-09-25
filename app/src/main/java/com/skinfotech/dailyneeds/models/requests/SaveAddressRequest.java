package com.skinfotech.dailyneeds.models.requests;

import com.google.gson.annotations.SerializedName;

public class SaveAddressRequest {

    @SerializedName("userId")
    private String mUserId;
    @SerializedName("address")
    private String mAddress;
    @SerializedName("location")
    private String mLocation;

    public void setUserId(String userId) {
        mUserId = userId;
    }

    public void setAddress(String address) {
        mAddress = address;
    }

    public void setLocation(String location) {
        mLocation = location;
    }
}
