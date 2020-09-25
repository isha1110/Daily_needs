package com.skinfotech.dailyneeds.models.requests;

import com.google.gson.annotations.SerializedName;

public class HomeCouponsRequest extends CommonRequest{

    @SerializedName("mode")
    private String mCouponsMode;

    public HomeCouponsRequest(String couponsMode) {
        mCouponsMode = couponsMode;
    }
}
