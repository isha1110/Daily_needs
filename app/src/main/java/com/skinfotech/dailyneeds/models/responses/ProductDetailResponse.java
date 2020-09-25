package com.skinfotech.dailyneeds.models.responses;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class ProductDetailResponse extends CommonResponse {

    @SerializedName("id")
    private String mProductId = "";
    @SerializedName("image")
    private String mProductImage = "";
    @SerializedName("product_name")
    private String mProductName = "";
    @SerializedName("puId")
    private String mProductPUId = "";
    @SerializedName("price")
    private String mProductPrice = "";
    @SerializedName("special_price")
    private String mProductSpecialPrice = "";
    @SerializedName("measure")
    private String mProductMeasure = "";
    @SerializedName("unit")
    private String mProductUnit = "";
    @SerializedName("quantity")
    private String mProductQuantity = "";
    @SerializedName("description")
    private String mProductDescription = "";
    @SerializedName("wishlist")
    private boolean mIsWishListDone;
    @SerializedName("sizeList")
    private List<SizeList> mSizeList = new ArrayList<>();

    private transient int mCount = 1;

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

    public String getProductId() {
        return mProductId;
    }

    public String getProductImage() {
        return mProductImage;
    }

    public String getProductName() {
        return mProductName;
    }

    public String getProductPUId() {
        return mProductPUId;
    }

    public String getProductPrice() {
        return mProductPrice;
    }

    public String getProductSpecialPrice() {
        return mProductSpecialPrice;
    }

    public String getProductMeasure() {
        return mProductMeasure;
    }

    public String getProductUnit() {
        return mProductUnit;
    }

    public String getProductQuantity() {
        return mProductQuantity;
    }

    public boolean isWishListDone() {
        return mIsWishListDone;
    }

    public static class SizeList {

        @SerializedName("puId")
        private String mSizeListPUId = "";
        @SerializedName("price")
        private String mSizeListPrice = "";
        @SerializedName("special_price")
        private String mSizeListSpecialPrice = "";
        @SerializedName("measure")
        private String mSizeListMeasure = "";
        @SerializedName("unit")
        private String mSizeListUnit = "";

        public String getSizeListPUId() {
            return mSizeListPUId;
        }

        public String getSizeListPrice() {
            return mSizeListPrice;
        }

        public String getSizeListSpecialPrice() {
            return mSizeListSpecialPrice;
        }

        public String getSizeListMeasure() {
            return mSizeListMeasure;
        }

        public String getSizeListUnit() {
            return mSizeListUnit;
        }
    }
}
