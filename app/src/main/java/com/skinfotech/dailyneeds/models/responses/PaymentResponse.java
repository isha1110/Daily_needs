package com.skinfotech.dailyneeds.models.responses;

import com.google.gson.annotations.SerializedName;

public class PaymentResponse extends CommonResponse {

    @SerializedName("orderId")
    private String mOrderId = "";
    @SerializedName("expectedDelivery")
    private String mExpectedDelivery = "";

    public String getOrderId() {
        return mOrderId;
    }

    public String getExpectedDelivery() {
        return mExpectedDelivery;
    }
}
