package com.skinfotech.dailyneeds.models.responses;

import com.google.gson.annotations.SerializedName;

public class SubCategoryProductItem {
    @SerializedName("subCategoryId")
    private String mSubCategoryId = "";
    @SerializedName("subCategoryName")
    private String mSubCategoryName = "";
    @SerializedName("subCategoryImage")
    private String mSubCategoryImage = "";

    public String getSubCategoryId() {
        return mSubCategoryId;
    }

    public String getSubCategoryName() {
        return mSubCategoryName;
    }

    public String getSubCategoryImage() {
        return mSubCategoryImage;
    }

}
