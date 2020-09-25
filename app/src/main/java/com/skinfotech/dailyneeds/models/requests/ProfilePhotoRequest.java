package com.skinfotech.dailyneeds.models.requests;

import com.google.gson.annotations.SerializedName;

public class ProfilePhotoRequest extends CommonRequest{

    @SerializedName("profilePhoto")
    private String mProfilePhoto = "";

    public ProfilePhotoRequest(String profilePhoto) {
        mProfilePhoto = profilePhoto;
    }
}
