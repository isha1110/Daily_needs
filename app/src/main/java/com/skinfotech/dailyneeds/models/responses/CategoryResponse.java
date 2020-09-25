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

        @SerializedName("id")
        private String mCategoryId = "";
        @SerializedName("name")
        private String mCategoryName = "";
        @SerializedName("image")
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
