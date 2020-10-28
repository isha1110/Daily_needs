package com.skinfotech.dailyneeds.models.responses;

import com.google.gson.annotations.SerializedName;

public class CommonDetailsResponse extends CommonResponse{

    @SerializedName("cartCount")
    private String mCartCount = "";
    @SerializedName("location")
    private String mLocation = "";
    @SerializedName("username")
    private String nameStr;
    @SerializedName("mobileNumber")
    private String mobileNumber;

    public String getMobileNumber() {
        return mobileNumber;
    }

    public String getNameStr() {
        return nameStr;
    }

    public String getLocation() {
        return mLocation;
    }

    public String getCartCount() {
        return mCartCount;
    }
}
