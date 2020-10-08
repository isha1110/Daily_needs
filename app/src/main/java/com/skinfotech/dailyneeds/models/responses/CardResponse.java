package com.skinfotech.dailyneeds.models.responses;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class CardResponse extends CommonResponse {

    @SerializedName("categoryList")
    private List<CardItem> cardItems = new ArrayList<>();

    public List<CardItem> getCardItems() {
        return cardItems;
    }

    public static class CardItem {
        @SerializedName("cardId")
        private String cardId = "";
        @SerializedName("cardImage")
        private String cardImage = "";

        public String getCardImage() {
            return cardImage;
        }

        public String getCardId() {
            return cardId;
        }

    }
}
