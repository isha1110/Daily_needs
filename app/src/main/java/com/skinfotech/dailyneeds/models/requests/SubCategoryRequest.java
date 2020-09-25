package com.skinfotech.dailyneeds.models.requests;

import com.google.gson.annotations.SerializedName;

public class SubCategoryRequest {

    @SerializedName("categoryId")
    private String categoryId;
    @SerializedName("subCategoryId")
    private String subCategoryId;
    @SerializedName("subSubCategoryId")
    private String subSubCategoryId;
    @SerializedName("cardId")
    private String cardId;
    @SerializedName("mode")
    private String mode;

    public void setSubSubCategoryId(String subSubCategoryId) {
        this.subSubCategoryId = subSubCategoryId;
    }

    public void setSubCategoryId(String subCategoryId) {
        this.subCategoryId = subCategoryId;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    public void setCardId(String cardId) {
        this.cardId = cardId;
    }
}
