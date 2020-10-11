package com.skinfotech.dailyneeds.models.responses;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class ProductsLabels extends CommonResponse {

    @SerializedName("categoryLabelsList")
    private List<CategoryLabels> categoryLabels = new ArrayList<>();

    public List<CategoryLabels> getCategoryLabels() {
        return categoryLabels;
    }

    public static class CategoryLabels {
        @SerializedName("innerProductsList")
        private List<InnerCategoryProduct> innerCategoryProducts = new ArrayList<>();

        @SerializedName("labelId")
        private String labelId = "";

        @SerializedName("labelName")
        private String labelName = "";

        public List<InnerCategoryProduct> getInnerCategoryProducts() {
            return innerCategoryProducts;
        }
        public String getLabelId() { return labelId; }
        public String getLabelName() {
            return labelName;
        }
    }


}
