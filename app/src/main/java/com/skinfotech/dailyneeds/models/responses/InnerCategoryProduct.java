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
    private double mProductMRP = 0.00;
    @SerializedName("productMeasureId")
    private String mproductMeasureId = "";
    @SerializedName("productSellingPrice")
    private double mProductSpecialPrice = 0.00;
    @SerializedName("productMeasure")
    private String mProductMeasure = "";
    @SerializedName("productDiscount")
    private String mProductDiscount = "";

    public String getmProductId() {
        return mProductId;
    }

    public String getmProductName() {
        return mProductName;
    }

    public String getmProductImage() {
        return mProductImage;
    }

    public double getmProductMRP() {
        return mProductMRP;
    }

    public String getProductMeasureId() {
        return mproductMeasureId;
    }

    public double getmProductSpecialPrice() {
        return mProductSpecialPrice;
    }

    public String getmProductMeasure() {
        return mProductMeasure;
    }

    public String getmProductDiscount() {
        return mProductDiscount;
    }
}
