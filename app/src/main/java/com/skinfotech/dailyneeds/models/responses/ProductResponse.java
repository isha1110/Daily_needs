package com.skinfotech.dailyneeds.models.responses;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class ProductResponse extends CommonResponse {

    @SerializedName("productList")
    private List<ProductItem> mProductList = new ArrayList<>();

    public List<ProductItem> getProductList() {
        return mProductList;
    }

    public static class ProductItem {

        @SerializedName("productId")
        private String mProductId = "";
        @SerializedName("productName")
        private String mProductName = "";
        @SerializedName("productImage")
        private String mProductImage = "";
        @SerializedName("productMeasureId")
        private String mProductPUId = "";
        @SerializedName("productMRP")
        private String mProductPrice = "";
        @SerializedName("productSellingPrice")
        private String mProductSpecialPrice = "";
        @SerializedName("productMeasure")
        private String mProductMeasure = "";
        @SerializedName("productUnit")
        private String mProductUnit = "";
        @SerializedName("quantity")
        private String mProductQuantity = "";
        @SerializedName("productDiscount")
        private String mProductDiscount = "";

        public String getProductDiscount() {
            return mProductDiscount;
        }

        private transient int mCount = 1;

        public String getProductQuantity() {
            return mProductQuantity;
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
