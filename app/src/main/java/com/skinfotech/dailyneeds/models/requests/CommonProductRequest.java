package com.skinfotech.dailyneeds.models.requests;

import com.google.gson.annotations.SerializedName;

public class CommonProductRequest extends CommonRequest {

    @SerializedName("productId")
    private String mProductsId;
    @SerializedName("quantity")
    private String mProductsQuantity;
    @SerializedName("productMeasureId")
    private String mProductsSizeId;

    public void setProductsId(String productsId) {
        mProductsId = productsId;
    }

    public void setProductsQuantity(String productsQuantity) {
        mProductsQuantity = productsQuantity;
    }

    public void setProductsSizeId(String productsSizeId) {
        mProductsSizeId = productsSizeId;
    }
}
