package com.skinfotech.dailyneeds.models.responses;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class BrandListResponse extends CommonResponse {
    @SerializedName("categoryList")
    private List<BrandItem> mBrandList = new ArrayList<>();

    public List<BrandListResponse.BrandItem> getBrandList() {
        return mBrandList;
    }

    public static class BrandItem {

        @SerializedName("brandId")
        private String mBrandId = "";
        @SerializedName("brandName")
        private String mBrandName = "";

        public String getBrandId() {
            return mBrandId;
        }

        public String getBrandName() {
            return mBrandName;
        }
    }
}
