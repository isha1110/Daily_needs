package com.skinfotech.dailyneeds.models.responses;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class ProductResponse extends CommonResponse {

    @SerializedName("categoryList")
    private List<ProductItem> mProductList = new ArrayList<>();
    @SerializedName("isDataAvailable")
    private boolean mIsDataAvailable = false;

    public boolean isDataAvailable() {
        return mIsDataAvailable;
    }

    public List<ProductItem> getProductList() {
        return mProductList;
    }

    @SerializedName("cartTotal")
    private String cartTotal = "";

    public String getTotalPrice() {
        return cartTotal;
    }

    public static class ProductItem {

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
        @SerializedName("wishlist")
        private boolean mIsWishListDone;

        public void setWishListDone(boolean wishListDone) {
            mIsWishListDone = wishListDone;
        }

        private transient int mCount = 1;

        public String getProductQuantity() {
            return mProductQuantity;
        }

        public boolean isWishListDone() {
            return mIsWishListDone;
        }

        public int getCount() {
            return mCount;
        }

        public void setCount(int count) {
            mCount = count;
        }

        public String getProductImage() {
            return mProductImage;
        }

        public String getProductId() {
            return mProductId;
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
    }
}
