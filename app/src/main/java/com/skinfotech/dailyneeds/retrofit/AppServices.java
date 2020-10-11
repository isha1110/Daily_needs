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
import com.skinfotech.dailyneeds.models.requests.ProductLabelsRequest;
import com.skinfotech.dailyneeds.models.requests.ProfilePhotoRequest;
import com.skinfotech.dailyneeds.models.requests.ProfileRequest;
import com.skinfotech.dailyneeds.models.requests.RegistrationRequest;
import com.skinfotech.dailyneeds.models.requests.SaveAddressRequest;
import com.skinfotech.dailyneeds.models.requests.SearchRequest;
import com.skinfotech.dailyneeds.models.requests.SubCategoryRequest;
import com.skinfotech.dailyneeds.models.requests.VerifyEmailRequest;
import com.skinfotech.dailyneeds.models.requests.VerifyOtpRequest;
import com.skinfotech.dailyneeds.models.responses.BrandListResponse;
import com.skinfotech.dailyneeds.models.responses.CardResponse;
import com.skinfotech.dailyneeds.models.responses.CategoryResponse;
import com.skinfotech.dailyneeds.models.responses.CheckOutResponse;
import com.skinfotech.dailyneeds.models.responses.CommonResponse;
import com.skinfotech.dailyneeds.models.responses.MyOrderResponse;
import com.skinfotech.dailyneeds.models.responses.OrderDetailResponse;
import com.skinfotech.dailyneeds.models.responses.PaymentResponse;
import com.skinfotech.dailyneeds.models.responses.ProductDetailResponse;
import com.skinfotech.dailyneeds.models.responses.ProductResponse;
import com.skinfotech.dailyneeds.models.responses.ProductsLabels;
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

    @POST("verify_otp.php")
    Call<CommonResponse> getVerifyOtpResponse(@Body VerifyOtpRequest request);

    @POST("fetch_cards.php")
    Call<CardResponse> fetchCards(@Body HomeCouponsRequest request);

    @POST("fetch_category.php")
    Call<CategoryResponse> fetchCategories();

    @POST("fetch_new_products.php")
    Call<ProductResponse> fetchHomeProducts();

    @POST("fetch_sub_categories.php")
    Call<CategoryResponse> fetchSubCategories(@Body SubCategoryRequest request);

    @POST("search_products.php")
    Call<ProductResponse> fetchSearchProducts(@Body SearchRequest request);

    @POST("add_to_cart.php")
    Call<CommonResponse> addToCart(@Body CommonProductRequest request);

    @POST("remove_from_cart.php")
    Call<CommonResponse> removeFromCart(@Body CommonProductRequest request);

    @POST("fetch_cart.php")
    Call<ProductResponse> fetchCart(@Body CommonRequest request);

    @POST("fetch_my_orders.php")
    Call<MyOrderResponse> fetchMyOrders(@Body CommonRequest request);

    @POST("cancel_order.php")
    Call<MyOrderResponse> cancelOrders(@Body OrderDetailRequest request);

    @POST("repeat_order.php")
    Call<PaymentResponse> repeatOrders(@Body OrderDetailRequest request);

    @POST("fetch_order_details.php")
    Call<OrderDetailResponse> fetchOrderDetails(@Body OrderDetailRequest request);

    @POST("fetch_products.php")
    Call<ProductResponse> fetchAllProducts(@Body AllProductRequest request);


    @POST("fetch_checkout_details.php")
    Call<CheckOutResponse> fetchCheckout(@Body CommonRequest request);

    @POST("save_payment.php")
    Call<PaymentResponse> savePayment(@Body PaymentRequest request);

    @POST("fetch_address.php")
    Call<AddressResponse> fetchAddress(@Body CommonRequest request);

    @POST("product_detail.php")
    Call<ProductDetailResponse> productDetail(@Body CommonProductRequest request);

    @POST("fetch_price_by_unit_id.php")
    Call<ProductDetailResponse> productDetailByUnitId(@Body CommonProductRequest request);

    @POST("save_address.php")
    Call<CommonResponse> saveAddress(@Body SaveAddressRequest request);

    @POST("make_default_address.php")
    Call<AddressResponse> makeDefaultAddress(@Body DefaultAddressRequest request);

    @POST("fetch_all_products.php")
    Call<ProductsLabels> getLabelsNproducts(@Body ProductLabelsRequest request);
}
