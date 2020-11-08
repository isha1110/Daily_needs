package com.skinfotech.dailyneeds.models.requests;

import com.google.gson.annotations.SerializedName;

public class AddressRequest {
    @SerializedName("userId")
    private String userId = "";
    @SerializedName("addressId")
    private String addressId = "";

    public AddressRequest(String userId,String addressId) {
        this.userId = userId;
        this.addressId=addressId;
    }
}
