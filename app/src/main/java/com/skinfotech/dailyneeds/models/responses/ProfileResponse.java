package com.skinfotech.dailyneeds.models.responses;

import com.google.gson.annotations.SerializedName;

public class ProfileResponse extends CommonResponse{

    @SerializedName("username")
    private String mUserName = "";
    @SerializedName("mail")
    private String mUserEmail = "";
    @SerializedName("mobileNumber")
    private String mMobileNumber = "";
    @SerializedName("userImage")
    private String mUserImage = "";
    @SerializedName("primaryAddress")
    private String mPrimaryAddress = "";
    @SerializedName("cartCount")
    private String mCartCount = "";

    public String getCartCount() {
        return mCartCount;
    }

    public String getUserName() {
        return mUserName;
    }

    public String getUserEmail() {
        return mUserEmail;
    }

    public String getMobileNumber() {
        return mMobileNumber;
    }

    public String getUserImage() {
        return mUserImage;
    }

    public String getPrimaryAddress() {
        return mPrimaryAddress;
    }
}
