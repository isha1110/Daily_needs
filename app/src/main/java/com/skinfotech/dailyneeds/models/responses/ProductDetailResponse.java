package com.skinfotech.dailyneeds.models.responses;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class ProductDetailResponse extends CommonResponse {

    @SerializedName("image")
    private String mProductImage = "";
    @SerializedName("product_name")
    private String mProductName = "";
    @SerializedName("price")
    private String mProductPrice = "";
    @SerializedName("special_price")
    private String mProductSpecialPrice = "";
    @SerializedName("quantity")
    private String mProductQuantity = "";
    @SerializedName("description")
    private String mProductDescription = "";
    @SerializedName("productDiscount")
    private String mProductDiscount = "";
    @SerializedName("sizeList")
    private List<SizeList> mSizeList = new ArrayList<>();

    private transient int mCount = 1;

    public String getProductDiscount() {
        return mProductDiscount;
    }

    public int getCount() {
        return mCount;
    }

    public void setCount(int count) {
        mCount = count;
    }

    public String getProductDescription() {
        return mProductDescription;
    }

    public List<SizeList> getSizeList() {
        return mSizeList;
    }

    public String getProductImage() {
        return mProductImage;
    }

    public String getProductName() {
        return mProductName;
    }

    public String getProductPrice() {
        return mProductPrice;
    }

    public String getProductSpecialPrice() {
        return mProductSpecialPrice;
    }

    public String getProductQuantity() {
        return mProductQuantity;
    }

    public static class SizeList {

        @SerializedName("puId")
        private String mSizeListPUId = "";
        @SerializedName("measure")
        private String mSizeListMeasure = "";
        @SerializedName("unit")
        private String mSizeListUnit = "";

        public String getSizeListPUId() {
            return mSizeListPUId;
        }

        public String getSizeListMeasure() {
            return mSizeListMeasure;
        }

        public String getSizeListUnit() {
            return mSizeListUnit;
        }
    }
}
