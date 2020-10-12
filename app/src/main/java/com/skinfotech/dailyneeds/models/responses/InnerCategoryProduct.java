package com.skinfotech.dailyneeds.models.responses;

import com.google.gson.annotations.SerializedName;

public class InnerCategoryProduct {
    @SerializedName("productId")
    private String mProductId = "";
    @SerializedName("productName")
    private String mProductName = "";
    @SerializedName("productImage")
    private String mProductImage = "";
    @SerializedName("productMRP")
    private String mProductMRP = "";
    @SerializedName("productMeasureId")
    private String mproductMeasureId = "";
    @SerializedName("productSellingPrice")
    private String mProductSpecialPrice = "";
    @SerializedName("productMeasure")
    private String mProductMeasure = "";
    @SerializedName("productDiscount")
    private String mProductDiscount = "";
    private transient int mCount = 1;

    public int getCount() {
        return mCount;
    }

    public String getmProductId() {
        return mProductId;
    }

    public String getmProductName() {
        return mProductName;
    }

    public String getmProductImage() {
        return mProductImage;
    }

    public String getmProductMRP() {
        return mProductMRP;
    }

    public String getProductMeasureId() {
        return mproductMeasureId;
    }

    public String getmProductSpecialPrice() {
        return mProductSpecialPrice;
    }

    public String getmProductMeasure() {
        return mProductMeasure;
    }

    public String getmProductDiscount() {
        return mProductDiscount;
    }
}
