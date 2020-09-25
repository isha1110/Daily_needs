package com.skinfotech.dailyneeds.models.responses;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class CheckOutResponse extends CommonResponse {

    @SerializedName("productList")
    private List<ProductItem> mProductList = new ArrayList<>();
    @SerializedName("bagTotal")
    private String bagTotal = "";
    @SerializedName("bagItems")
    private String bagItems = "";
    @SerializedName("bagDiscount")
    private String bagDiscount = "";
    @SerializedName("orderTotal")
    private String orderTotal = "";
    @SerializedName("deliveryCharge")
    private String deliveryCharge = "";
    @SerializedName("discountMessage")
    private String discountMessage = "";

    public String getDiscountMessage() {
        return discountMessage;
    }

    public String getBagItems() {
        return bagItems;
    }

    public String getBagTotal() {
        return bagTotal;
    }

    public String getBagDiscount() {
        return bagDiscount;
    }

    public String getOrderTotal() {
        return orderTotal;
    }

    public String getDeliveryCharge() {
        return deliveryCharge;
    }

    public List<ProductItem> getProductList() {
        return mProductList;
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
