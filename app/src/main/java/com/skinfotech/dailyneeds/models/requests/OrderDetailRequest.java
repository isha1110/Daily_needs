package com.skinfotech.dailyneeds.models.requests;

import com.google.gson.annotations.SerializedName;

public class OrderDetailRequest extends CommonRequest {

    @SerializedName("orderId")
    private String mOrderId = "";
    @SerializedName("txnId")
    private String mTxnId = "";
    @SerializedName("addressId")
    private String mAddressId = "";

    public void setOrderId(String orderId) {
        mOrderId = orderId;
    }

    public void setTxnId(String txnId) {
        mTxnId = txnId;
    }

    public void setAddressId(String addressId) {
        mAddressId = addressId;
    }
}
