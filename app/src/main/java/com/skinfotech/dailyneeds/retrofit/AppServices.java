package com.skinfotech.dailyneeds.retrofit;

import com.skinfotech.dailyneeds.models.requests.AddressResponse;
import com.skinfotech.dailyneeds.models.requests.AllProductRequest;
import com.skinfotech.dailyneeds.models.requests.ChangePasswordRequest;
import com.skinfotech.dailyneeds.models.requests.CommonProductRequest;
import com.skinfotech.dailyneeds.models.requests.CommonRequest;
import com.skinfotech.dailyneeds.models.requests.DefaultAddressRequest;
import com.skinfotech.dailyneeds.models.requests.ForgotPasswordRequest;
import com.skinfotech.dailyneeds.models.requests.HomeCouponsRequest;
import com.skinfotech.dailyneeds.models.requests.HomeProductsRequest;
import com.skinfotech.dailyneeds.models.requests.LoginRequest;
import com.skinfotech.dailyneeds.models.requests.OrderDetailRequest;
import com.skinfotech.dailyneeds.models.requests.PaymentRequest;
import com.skinfotech.dailyneeds.models.requests.ProfilePhotoRequest;
import com.skinfotech.dailyneeds.models.requests.ProfileRequest;
import com.skinfotech.dailyneeds.models.requests.RegistrationRequest;
import com.skinfotech.dailyneeds.models.requests.SaveAddressRequest;
import com.skinfotech.dailyneeds.models.requests.SearchRequest;
import com.skinfotech.dailyneeds.models.requests.SubCategoryRequest;
import com.skinfotech.dailyneeds.models.requests.VerifyEmailRequest;
import com.skinfotech.dailyneeds.models.requests.VerifyOtpRequest;
import com.skinfotech.dailyneeds.models.responses.BrandListResponse;
import com.skinfotech.dailyneeds.models.responses.CategoryResponse;
import com.skinfotech.dailyneeds.models.responses.CheckOutResponse;
import com.skinfotech.dailyneeds.models.responses.CommonResponse;
import com.skinfotech.dailyneeds.models.responses.MyOrderResponse;
import com.skinfotech.dailyneeds.models.responses.OrderDetailResponse;
import com.skinfotech.dailyneeds.models.responses.PaymentResponse;
import com.skinfotech.dailyneeds.models.responses.ProductDetailResponse;
import com.skinfotech.dailyneeds.models.responses.ProductResponse;
import com.skinfotech.dailyneeds.models.responses.ProfileResponse;
import com.skinfotech.dailyneeds.models.responses.SideNavigationResponse;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface AppServices {

    @POST("login.php")
    Call<CommonResponse> login(@Body LoginRequest request);

    @POST("registration.php")
    Call<CommonResponse> registration(@Body RegistrationRequest request);

    @POST("verify_email.php")
    Call<CommonResponse> verifyEmail(@Body VerifyEmailRequest request);

    @POST("fetch_profile.php")
    Call<ProfileResponse> fetchProfile(@Body CommonRequest request);

    @POST("fetch_categories.php")
    Call<CategoryResponse> fetchCategories();

    @POST("fetch_cards.php")
    Call<CategoryResponse> fetchCards(@Body HomeCouponsRequest request);

    @POST("fetch_home_products.php")
    Call<ProductResponse> fetchHomeProducts(@Body HomeProductsRequest request);

    @POST("fetch_sub_categories.php")
    Call<CategoryResponse> fetchSubCategories(@Body SubCategoryRequest request);

    @POST("fetch_products.php")
    Call<ProductResponse> fetchAllProducts(@Body AllProductRequest request);

    @POST("search_products.php")
    Call<ProductResponse> fetchSearchProducts(@Body SearchRequest request);

    @POST("fetch_cart.php")
    Call<ProductResponse> fetchCart(@Body CommonRequest request);

    @POST("fetch_checkout_details.php")
    Call<CheckOutResponse> fetchCheckout(@Body CommonRequest request);

    @POST("save_payment.php")
    Call<PaymentResponse> savePayment(@Body PaymentRequest request);

    @POST("add_to_cart.php")
    Call<CommonResponse> addToCart(@Body CommonProductRequest request);

    @POST("remove_from_cart.php")
    Call<CommonResponse> removeFromCart(@Body CommonProductRequest request);

    @POST("fetch_my_orders.php")
    Call<MyOrderResponse> fetchMyOrders(@Body CommonRequest request);

    @POST("cancel_order.php")
    Call<MyOrderResponse> cancelOrders(@Body OrderDetailRequest request);

    @POST("upload_profile_photo.php")
    Call<ProfileResponse> uploadProfilePhoto(@Body ProfilePhotoRequest request);

    @POST("repeat_order.php")
    Call<PaymentResponse> repeatOrders(@Body OrderDetailRequest request);

    @POST("fetch_side_navigation.php")
    Call<SideNavigationResponse> fetchSideNavigation();

    @POST("fetch_order_details.php")
    Call<OrderDetailResponse> fetchOrderDetails(@Body OrderDetailRequest request);

    @POST("add_remove_wishlist.php")
    Call<CommonResponse> doWishList(@Body CommonProductRequest request);

    @POST("fetch_wishlist.php")
    Call<ProductResponse> fetchWishList(@Body CommonRequest request);

    @POST("fetch_address.php")
    Call<AddressResponse> fetchAddress(@Body CommonRequest request);

    @POST("product_detail.php")
    Call<ProductDetailResponse> productDetail(@Body CommonProductRequest request);

    @POST("fetch_price_by_unit_id.php")
    Call<ProductDetailResponse> productDetailByUnitId(@Body CommonProductRequest request);

    @POST("change_password.php")
    Call<CommonResponse> changePassword(@Body ChangePasswordRequest request);

    @POST("update_password.php")
    Call<CommonResponse> updatePassword(@Body ForgotPasswordRequest request);

    @POST("save_address.php")
    Call<CommonResponse> saveAddress(@Body SaveAddressRequest request);

    @POST("make_default_address.php")
    Call<AddressResponse> makeDefaultAddress(@Body DefaultAddressRequest request);

    @POST("verify_otp.php")
    Call<CommonResponse> getVerifyOtpResponse(@Body VerifyOtpRequest request);

    @Multipart
    @POST("upload_profile_photo.php")
    Call<ProfileResponse> uploadAudioFile(@Part MultipartBody.Part file, @Part("userId") RequestBody userId);

    @POST("fetch_brand_list.php")
    Call<BrandListResponse> fetchBrandList(@Body AllProductRequest request);

    @POST("save_apply_changes.php")
    Call<ProductResponse> saveApplyChanges(@Body AllProductRequest request);

    @POST("update_profile.php")
    Call<CommonResponse> saveProfileResponse(@Body ProfileRequest request);

}
