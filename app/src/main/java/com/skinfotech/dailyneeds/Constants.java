package com.skinfotech.dailyneeds;

public class Constants {

    public static final String SHARED_PREF_NAME = "SHARED_PREF_USER";
    public static final int SPLASH_TIME_INTERVAL = 2500;
    public static final String SUCCESS = "0000";
    public static final String USER_LOGIN_DONE = "USER_LOGIN_DONE";
    public static final String USER_EMAIL = "USER_EMAIL";
    public static final String USER_MOBILE = "USER_MOBILE";
    public static final String USER_MODE = "USER_MODE";
    public static final String YES = "YES";
    public static final String NO = "NO";
    public static final String USER_ID = "USER_ID";

    public interface ICouponModes {
        String TOP_CARD = "top";
        String BOTTOM_CARD = "bottom";
    }
    public interface AddressModes {
        String NEW_ADDRESS = "new";
        String UPDATE_ADDRESS = "update";
    }

    public interface CatModes {
        String CARD = "card";
        String CATEGORIES = "category";
        String SUBCATEGORIES = "sub_category";
    }

    public interface IExpectedDeliveryModes {

        String DELIVERED = "1";
    }

    public interface AuthModes {
        String FACEBOOK_AUTH = "facebook";
        String GOOGLE_AUTH = "google";
        String EMAIL_AUTH = "email";
    }
}
