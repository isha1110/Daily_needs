package com.skinfotech.dailyneeds.models.requests;

import com.skinfotech.dailyneeds.models.responses.CommonResponse;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class AddressResponse extends CommonResponse implements Serializable {

    @SerializedName("addressList")
    private List<AddressItem> mAddressList = new ArrayList<>();

    public List<AddressResponse.AddressItem> getAddressList() {
        return mAddressList;
    }

    public static class AddressItem {

        @SerializedName("addressId")
        private String addressId;
        @SerializedName("address")
        private String addressStr;
        @SerializedName("username")
        private String nameStr;
        @SerializedName("mobile")
        private String mobileStr;
        @SerializedName("email")
        private String emailStr;
        @SerializedName("location")
        private String locationStr;
        @SerializedName("isDefaultAddress")
        private boolean isDefaultAddress;

        public void setDefaultAddress(boolean defaultAddress) {
            isDefaultAddress = defaultAddress;
        }

        public String getNameStr() {
            return nameStr;
        }

        public String getMobileStr() {
            return mobileStr;
        }

        public String getEmailStr() {
            return emailStr;
        }

        public String getLocationStr() {
            return locationStr;
        }

        public String getAddressId() {
            return addressId;
        }

        public String getAddressStr() {
            return addressStr;
        }

        public boolean isDefaultAddress() {
            return isDefaultAddress;
        }
    }
}
