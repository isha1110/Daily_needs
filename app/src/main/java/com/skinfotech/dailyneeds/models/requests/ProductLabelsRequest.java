package com.skinfotech.dailyneeds.models.requests;

import com.google.gson.annotations.SerializedName;

public class ProductLabelsRequest {
    @SerializedName("mode")
    private String mode = "";

    @SerializedName("modeId")
    private String modeId = "";

    public ProductLabelsRequest(String mode, String modeId) {
        this.mode = mode;
        this.modeId = modeId;
    }
}
