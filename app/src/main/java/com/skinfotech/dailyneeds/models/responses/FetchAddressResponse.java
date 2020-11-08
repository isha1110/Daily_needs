package com.skinfotech.dailyneeds.models.responses;

import com.google.gson.annotations.SerializedName;

public class FetchAddressResponse extends CommonResponse {

    @SerializedName("name")
    private String name;
    @SerializedName("phoneNumber")
    private String phoneNumber;
    @SerializedName("addressLine1")
    private String mAddress;
    @SerializedName("addressLine2")
    private String mAddress1;
    @SerializedName("city")
    private String city;
    @SerializedName("state")
    private String state;
    @SerializedName("pincode")
    private String pincode;
    @SerializedName("location")
    private String mLocation;

    public String getName() {
        return name;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getmAddress() {
        return mAddress;
    }

    public String getmAddress1() {
        return mAddress1;
    }

    public String getCity() {
        return city;
    }

    public String getState() {
        return state;
    }

    public String getPincode() {
        return pincode;
    }

    public String getmLocation() {
        return mLocation;
    }
}
