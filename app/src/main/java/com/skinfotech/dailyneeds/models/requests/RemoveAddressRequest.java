package com.skinfotech.dailyneeds.models.requests;

import com.google.gson.annotations.SerializedName;

public class RemoveAddressRequest extends CommonRequest {

    @SerializedName("addressId")
    private String mAddressId;

    public void setmAddressId(String mAddressId) {
        this.mAddressId = mAddressId;
    }
}
