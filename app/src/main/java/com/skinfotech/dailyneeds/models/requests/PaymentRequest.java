package com.skinfotech.dailyneeds.models.requests;

import com.google.gson.annotations.SerializedName;

public class PaymentRequest extends CommonRequest {

    @SerializedName("paymentId")
    private String mTxnId = "";
    @SerializedName("addressId")
    private String mAddressId = "";

    public void setTxnId(String txnId) {
        mTxnId = txnId;
    }

    public void setAddressId(String addressId) {
        mAddressId = addressId;
    }
}
