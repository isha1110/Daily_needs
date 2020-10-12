package com.skinfotech.dailyneeds.models.requests;

import com.google.gson.annotations.SerializedName;

public class SaveAddressRequest {

    @SerializedName("userId")
    private String mUserId;
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

    public void setmUserId(String mUserId) {
        this.mUserId = mUserId;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public void setmAddress(String mAddress) {
        this.mAddress = mAddress;
    }

    public void setmAddress1(String mAddress1) {
        this.mAddress1 = mAddress1;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public void setState(String state) {
        this.state = state;
    }

    public void setPincode(String pincode) {
        this.pincode = pincode;
    }

    public void setmLocation(String mLocation) {
        this.mLocation = mLocation;
    }
}
