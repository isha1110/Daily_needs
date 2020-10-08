package com.skinfotech.dailyneeds.models.responses;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class CategoryResponse extends CommonResponse {

    @SerializedName("categoryList")
    private List<CategoryItem> mCategoryList = new ArrayList<>();

    public List<CategoryItem> getCategoryList() {
        return mCategoryList;
    }

    public static class CategoryItem {
        @SerializedName("subCategoryList")
        private List<CategoryProductItem> mCategoryProductList = new ArrayList<>();
        @SerializedName("categoryId")
        private String mCategoryId = "";
        @SerializedName("categoryName")
        private String mCategoryName = "";
        @SerializedName("categoryImage")
        private String mCategoryImage = "";

        public String getCategoryImage() {
            return mCategoryImage;
        }

        public String getCategoryId() {
            return mCategoryId;
        }

        public String getCategoryName() {
            return mCategoryName;
        }
    }
}
