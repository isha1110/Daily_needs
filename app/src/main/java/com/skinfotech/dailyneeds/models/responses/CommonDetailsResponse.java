package com.skinfotech.dailyneeds.models.responses;

import com.google.gson.annotations.SerializedName;

public class CommonDetailsResponse extends CommonResponse{

    @SerializedName("cartCount")
    private String mCartCount = "";
    @SerializedName("location")
    private String mLocation = "";

    public String getLocation() {
        return mLocation;
    }

    public String getCartCount() {
        return mCartCount;
    }
}
