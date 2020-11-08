package com.skinfotech.dailyneeds.models.responses;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class MyOrderResponse extends CommonResponse {

    @SerializedName("categoryList")
    private List<OrderItem> mOrderList = new ArrayList<>();

    public List<OrderItem> getOrderList() {
        return mOrderList;
    }

    public static class OrderItem {

        @SerializedName("orderId")
        private String mOrderId = "";
        @SerializedName("id")
        private String mId = "";
        @SerializedName("total_amount")
        private String mTotalAmount = "";
        @SerializedName("delivery_charges")
        private String mDeliveryCharges = "";
        @SerializedName("payment_id")
        private String mPaymentId = "";
        @SerializedName("date_time")
        private String mDateTime = "";
        @SerializedName("expected_delivery")
        private String mExpectedDelivery = "";
        @SerializedName("delivered_flag")
        private String mDeliveryFlag = "";
        @SerializedName("isCancel")
        private boolean mIsCancel;

        public String getOrderId() {
            return mOrderId;
        }

        public String getId() {
            return mId;
        }

        public String getTotalAmount() {
            return mTotalAmount;
        }

        public String getPaymentId() {
            return mPaymentId;
        }

        public String getDateTime() {
            return mDateTime;
        }

        public String getExpectedDelivery() {
            return mExpectedDelivery;
        }

        public String getDeliveryFlag() {
            return mDeliveryFlag;
        }

        public boolean isCancel() {
            return mIsCancel;
        }
    }
}
