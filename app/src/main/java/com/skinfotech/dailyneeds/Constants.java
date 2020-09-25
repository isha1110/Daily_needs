package com.skinfotech.dailyneeds;

public class Constants {

    public static final String SHARED_PREF_NAME = "SHARED_PREF_DOCTOR_USER";
    public static final int SPLASH_TIME_INTERVAL = 2500;
    public static final String SUCCESS = "0000";
    public static final String USER_LOGIN_DONE = "USER_LOGIN_DONE";
    public static final String YES = "YES";
    public static final String NO = "NO";
    public static final String USER_ID = "USER_ID";
    public static final String SPLIT = "###";
    public static final int PERMISSION_REQUEST_CODE = 1001;

    public interface IImagePickConstants {

        int CAMERA = 1001;
        int GALLERY = 1002;
    }

    public interface IProductModes {

        String BEST_SELLER = "bestsellers";
        String NEW = "new";
        String LOW="low";
        String HIGH="high";
    }

    public interface ICouponModes {

        String BEST_SELLER_COUPONS = "bestsellers_coupons";
        String NEW_COUPONS = "new_coupons";
        String BANNER_COUPONS = "banner_coupons";
    }

    public interface IModes {

        String CATEGORIES = "CATEGORIES";
        String CARDS = "CARDS";
        String NEW_ARRIVAL = "NEW_ARRIVAL";
        String BEST_SELLER = "BEST_SELLER";
        String SIDE_NAVIGATION = "SIDE_NAVIGATION";
    }

    public interface IExpectedDeliveryModes {

        String DELIVERED = "1";
        String IN_TRANSIT = "In transit";
    }
}
