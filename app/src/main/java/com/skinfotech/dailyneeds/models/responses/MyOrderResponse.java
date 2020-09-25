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
        @SerializedName("cancel_flag")
        private String mCancelFlag = "";
        @SerializedName("cancel_date")
        private String mCancelDate = "";
        @SerializedName("cancel_by")
        private String mCancelBy = "";
        @SerializedName("delivered_flag")
        private String mDeliveryFlag = "";
        @SerializedName("isCancel")
        private boolean mIsCancel;
        @SerializedName("isRepeatOrder")
        private boolean mIsRepeatOrder;

        public String getOrderId() {
            return mOrderId;
        }

        public String getId() {
            return mId;
        }

        public String getTotalAmount() {
            return mTotalAmount;
        }

        public String getDeliveryCharges() {
            return mDeliveryCharges;
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

        public String getCancelFlag() {
            return mCancelFlag;
        }

        public String getCancelDate() {
            return mCancelDate;
        }

        public String getCancelBy() {
            return mCancelBy;
        }

        public String getDeliveryFlag() {
            return mDeliveryFlag;
        }

        public boolean isCancel() {
            return mIsCancel;
        }

        public boolean isRepeatOrder() {
            return mIsRepeatOrder;
        }
    }
}
