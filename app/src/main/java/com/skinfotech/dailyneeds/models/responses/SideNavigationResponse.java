package com.skinfotech.dailyneeds.models.responses;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class SideNavigationResponse extends CommonResponse {

    @SerializedName("navigationList")
    private List<NavigationOuterItem> mNavigationList = new ArrayList<>();

    public List<NavigationOuterItem> getNavigationList() {
        return mNavigationList;
    }

    public static class NavigationOuterItem {

        @SerializedName("id")
        private String mOuterItemId = "";
        @SerializedName("image")
        private String mOuterItemImage = "";
        @SerializedName("name")
        private String mOuterItemName = "";
        @SerializedName("innerNavigationList")
        private List<NavigationInnerItem> mInnerNavigationList = new ArrayList<>();

        public String getOuterItemImage() {
            return mOuterItemImage;
        }

        public List<NavigationInnerItem> getInnerNavigationList() {
            return mInnerNavigationList;
        }

        public String getOuterItemId() {
            return mOuterItemId;
        }

        public String getOuterItemName() {
            return mOuterItemName;
        }
    }

    public static class NavigationInnerItem {

        @SerializedName("id")
        private String mInnerItemId = "";
        @SerializedName("name")
        private String mInnerItemName = "";

        public String getInnerItemId() {
            return mInnerItemId;
        }

        public String getInnerItemName() {
            return mInnerItemName;
        }
    }
}
