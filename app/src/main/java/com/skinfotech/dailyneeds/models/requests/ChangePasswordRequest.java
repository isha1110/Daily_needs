package com.skinfotech.dailyneeds.models.requests;

import com.google.gson.annotations.SerializedName;

public class ChangePasswordRequest {

    @SerializedName("userId")
    private String mUserId;
    @SerializedName("oldPassword")
    private String mOldPassword;
    @SerializedName("newPassword")
    private String mNewPassword;

    public void setUserId(String userId) {
        mUserId = userId;
    }

    public void setOldPassword(String oldPassword) {
        mOldPassword = oldPassword;
    }

    public void setNewPassword(String newPassword) {
        mNewPassword = newPassword;
    }
}
