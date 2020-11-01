package com.skinfotech.dailyneeds.models.responses;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class LocationResponse extends CommonResponse{

    @SerializedName("locationList")
    private List<LocationItem> mLocationList = new ArrayList<>();

    public List<LocationItem> getLocationList() {
        return mLocationList;
    }

    public static class LocationItem {

        @SerializedName("id")
        private String locationId;
        @SerializedName("locationName")
        private String locationName;

        public String getLocationId() {
            return locationId;
        }

        public String getLocationName() {
            return locationName;
        }

    }
}