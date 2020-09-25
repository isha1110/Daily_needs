package com.skinfotech.dailyneeds.models.requests;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class AllProductRequest extends CommonRequest {

    @SerializedName("categoryId")
    private String categoryId;
    @SerializedName("cardId")
    private String cardId;
    @SerializedName("subCategoryId")
    private String subCategoryId;
    @SerializedName("subSubCategoryId")
    private String subSubCategoryId;
    @SerializedName("mode")
    private String mode;
    @SerializedName("brandId")
    private List<String> brandId;
    @SerializedName("index")
    private int index = 0;

    public void setBrandId(ArrayList<String> brandId) {
        this.brandId = brandId;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public void setCardId(String cardId) {
        this.cardId = cardId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    public void setSubCategoryId(String subCategoryId) {
        this.subCategoryId = subCategoryId;
    }

    public void setSubSubCategoryId(String subSubCategoryId) {
        this.subSubCategoryId = subSubCategoryId;
    }
}
