package com.skinfotech.dailyneeds.models.requests;

import com.google.gson.annotations.SerializedName;

public class SearchRequest extends CommonRequest {

    @SerializedName("searchStr")
    private String mSearchStr;

    public void setSearchStr(String searchStr) {
        mSearchStr = searchStr;
    }
}
