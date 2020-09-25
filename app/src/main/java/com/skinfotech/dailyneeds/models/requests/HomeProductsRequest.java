package com.skinfotech.dailyneeds.models.requests;

import com.google.gson.annotations.SerializedName;

public class HomeProductsRequest extends CommonRequest{

    @SerializedName("mode")
    private String mProductsMode;

    public HomeProductsRequest(String productsMode) {
        mProductsMode = productsMode;
    }
}
