package com.skinfotech.dailyneeds.fragments;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import com.skinfotech.dailyneeds.Constants;
import com.skinfotech.dailyneeds.R;
import com.skinfotech.dailyneeds.Utility;
import com.skinfotech.dailyneeds.constant.ToolBarManager;
import com.skinfotech.dailyneeds.models.Item;
import com.skinfotech.dailyneeds.models.requests.CommonProductRequest;
import com.skinfotech.dailyneeds.models.requests.CommonRequest;
import com.skinfotech.dailyneeds.models.requests.HomeCouponsRequest;
import com.skinfotech.dailyneeds.models.requests.HomeProductsRequest;
import com.skinfotech.dailyneeds.models.responses.CategoryResponse;
import com.skinfotech.dailyneeds.models.responses.ProductResponse;
import com.skinfotech.dailyneeds.models.responses.ProfileResponse;
import com.skinfotech.dailyneeds.retrofit.RetrofitApi;
import com.google.android.material.snackbar.Snackbar;
import com.squareup.picasso.Picasso;
import retrofit2.Call;
import retrofit2.Response;
import java.util.ArrayList;
import java.util.List;

import static com.skinfotech.dailyneeds.Constants.USER_ID;

public class HomeScreenFragment extends BaseFragment {

    private boolean mIsDoubleBackPress = false;
    private BestSellerAdapter mBestSellerAdapter;
    private BestSellerAdapter mNewArrivalAdapter;
    private RecyclerView bestSellerRecyclerView;
    private int mRecyclerViewSelectedPosition = 0;
    private RecyclerView mNewArrivalsRecycler;
    private CouponsListAdapter mCouponsListAdapter;
    private CouponsListAdapter mCouponsList2Adapter;
    private CategoryItemListAdapter mCategoryItemListAdapter;
    private static final String TAG = "HomeScreenFragment";
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private BannerItemListAdapter mBannerItemListAdapter;
    private RecyclerView mCategoryRecyclerView;
    private CategoryAdapter categoryAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mContentView = inflater.inflate(R.layout.fragment_home_screen, container, false);
        setupUI();
        mSwipeRefreshLayout = mContentView.findViewById(R.id.swipeRefreshLayout);
        fetchHomeProductsServerCall(Constants.IProductModes.BEST_SELLER);
        fetchHomeProductsServerCall(Constants.IProductModes.NEW);
        fetchProfileServerCall();
        fetchCardsServerCall(Constants.ICouponModes.BEST_SELLER_COUPONS);
        fetchCardsServerCall(Constants.ICouponModes.NEW_COUPONS);
        fetchCardsServerCall(Constants.ICouponModes.BANNER_COUPONS);
        fetchCategoriesServerCall();
        mSwipeRefreshLayout.setOnRefreshListener(() -> {
            fetchProfileServerCall();
            fetchHomeProductsServerCall(Constants.IProductModes.BEST_SELLER);
            fetchHomeProductsServerCall(Constants.IProductModes.NEW);
            fetchCategoriesServerCall();
            fetchCardsServerCall(Constants.ICouponModes.BEST_SELLER_COUPONS);
            fetchCardsServerCall(Constants.ICouponModes.NEW_COUPONS);
            fetchCardsServerCall(Constants.ICouponModes.BANNER_COUPONS);
            mSwipeRefreshLayout.setRefreshing(false);
        });
        return mContentView;
    }

    private void setupUI() {
        ToolBarManager.getInstance().hideToolBar(mActivity, false);
        ToolBarManager.getInstance().setHeaderTitle("");
        mActivity.isToggleButtonEnabled(true);
        bestSellerRecyclerView = mContentView.findViewById(R.id.productRecycler);
        RecyclerView offersCardRecycler = mContentView.findViewById(R.id.offersCardRecycler);
        RecyclerView categoryItemListRecycler = mContentView.findViewById(R.id.categoryItemRecyclerView);
        RecyclerView bannerCouponItemRecycler = mContentView.findViewById(R.id.bannerCouponItemRecyclerView);
        mBestSellerAdapter = new BestSellerAdapter();
        mNewArrivalAdapter = new BestSellerAdapter();
        mCouponsListAdapter = new CouponsListAdapter();
        mCouponsList2Adapter = new CouponsListAdapter();
        SnapHelper snapHelper = new PagerSnapHelper();
        mBannerItemListAdapter = new BannerItemListAdapter();
        snapHelper.attachToRecyclerView(bannerCouponItemRecycler);
        LinearLayoutManager layoutManager = new LinearLayoutManager(mActivity, LinearLayoutManager.HORIZONTAL, false);
        bestSellerRecyclerView.setLayoutManager(layoutManager);
        bestSellerRecyclerView.setAdapter(mBestSellerAdapter);
        offersCardRecycler.setLayoutManager(new LinearLayoutManager(mActivity, LinearLayoutManager.HORIZONTAL, false));
        offersCardRecycler.setAdapter(mCouponsListAdapter);
        mCategoryItemListAdapter = new CategoryItemListAdapter();
        GridLayoutManager gridLayoutManager = new GridLayoutManager(mActivity, 3);
        categoryItemListRecycler.setLayoutManager(gridLayoutManager);
        categoryItemListRecycler.setAdapter(mCategoryItemListAdapter);
        bannerCouponItemRecycler.setLayoutManager(new LinearLayoutManager(mActivity, LinearLayoutManager.HORIZONTAL, false));
        bannerCouponItemRecycler.setAdapter(mBannerItemListAdapter);
        mCategoryRecyclerView = mContentView.findViewById(R.id.parentCategoryRecyclerView);
        categoryAdapter = new CategoryAdapter();
        mCategoryRecyclerView.setLayoutManager(new LinearLayoutManager(mActivity, LinearLayoutManager.VERTICAL, false));
        mCategoryRecyclerView.setAdapter(categoryAdapter);
    }

    private void fetchCategoriesServerCall() {
        showProgress();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Call<CategoryResponse> call = RetrofitApi.getAppServicesObject().fetchCategories();
                    final Response<CategoryResponse> response = call.execute();
                    updateOnUiThread(() -> handleResponse(response));
                } catch (Exception e) {
                    stopProgress();
                    showToast(e.getMessage());
                    Log.e(TAG, e.getMessage(), e);
                }
            }

            private void handleResponse(Response<CategoryResponse> response) {
                if (response.isSuccessful()) {
                    CategoryResponse categoryResponse = response.body();
                    if (categoryResponse != null) {
                        if (Constants.SUCCESS.equalsIgnoreCase(categoryResponse.getErrorCode())) {
                            List<CategoryResponse.CategoryItem> categoryItemList = categoryResponse.getCategoryList();
                            if (!Utility.isEmpty(categoryItemList)) {
                                mCategoryItemListAdapter.setCategoryItemList(categoryItemList);
                                mCategoryItemListAdapter.notifyDataSetChanged();
                            }
                        }
                    }
                }
                stopProgress();
            }
        }).start();
    }

    @Override
    protected void cartAddedSuccessCallBack() {
        updateOnUiThread(this::fetchProfileServerCall);
    }

    private void fetchProfileServerCall() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Call<ProfileResponse> call = RetrofitApi.getAppServicesObject().fetchProfile(new CommonRequest(getStringDataFromSharedPref(USER_ID)));
                    final Response<ProfileResponse> response = call.execute();
                    updateOnUiThread(() -> handleResponse(response));
                } catch (Exception e) {
                    stopProgress();
                    showToast(e.getMessage());
                    Log.e(TAG, e.getMessage(), e);
                }
            }

            private void handleResponse(Response<ProfileResponse> response) {
                if (response.isSuccessful()) {
                    ProfileResponse profileResponse = response.body();
                    if (profileResponse != null) {
                        if (Constants.SUCCESS.equalsIgnoreCase(profileResponse.getErrorCode())) {
                            mActivity.setCartCount(profileResponse.getCartCount());
                        }
                    }
                }
                stopProgress();
            }
        }).start();
    }

    private void fetchCardsServerCall(String mode) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    HomeCouponsRequest request = new HomeCouponsRequest(mode);
                    request.setUserId(getStringDataFromSharedPref(USER_ID));
                    Call<CategoryResponse> call = RetrofitApi.getAppServicesObject().fetchCards(request);
                    final Response<CategoryResponse> response = call.execute();
                    updateOnUiThread(() -> handleResponse(response));
                } catch (Exception e) {
                    stopProgress();
                    showToast(e.getMessage());
                    Log.e(TAG, e.getMessage(), e);
                }
            }

            private void handleResponse(Response<CategoryResponse> response) {
                if (response.isSuccessful()) {
                    CategoryResponse categoryResponse = response.body();
                    if (categoryResponse != null) {
                        if (Constants.SUCCESS.equalsIgnoreCase(categoryResponse.getErrorCode())) {
                            List<CategoryResponse.CategoryItem> categoryItemList = categoryResponse.getCategoryList();
                            if (!Utility.isEmpty(categoryItemList)) {
                                if (mode.equalsIgnoreCase(Constants.ICouponModes.BEST_SELLER_COUPONS)) {
                                    mCouponsListAdapter.setCategoryItemList(categoryItemList);
                                    mCouponsListAdapter.notifyDataSetChanged();
                                } else if (mode.equalsIgnoreCase(Constants.ICouponModes.NEW_COUPONS)) {
                                    mCouponsList2Adapter.setCategoryItemList(categoryItemList);
                                    mCouponsList2Adapter.notifyDataSetChanged();
                                } else {
                                    mBannerItemListAdapter.setCategoryItemList(categoryItemList);
                                    mBannerItemListAdapter.notifyDataSetChanged();
                                }
                            }
                        }
                    }
                }
                stopProgress();
            }
        }).start();
    }

    private void fetchHomeProductsServerCall(String mode) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    HomeProductsRequest request = new HomeProductsRequest(mode);
                    request.setUserId(getStringDataFromSharedPref(USER_ID));
                    Call<ProductResponse> call = RetrofitApi.getAppServicesObject().fetchHomeProducts(request);
                    final Response<ProductResponse> response = call.execute();
                    updateOnUiThread(() -> handleResponse(response));
                } catch (Exception e) {
                    stopProgress();
                    showToast(e.getMessage());
                    Log.e(TAG, e.getMessage(), e);
                }
            }

            private void handleResponse(Response<ProductResponse> response) {
                if (response.isSuccessful()) {
                    ProductResponse categoryResponse = response.body();
                    if (categoryResponse != null) {
                        if (Constants.SUCCESS.equalsIgnoreCase(categoryResponse.getErrorCode())) {
                            List<ProductResponse.ProductItem> productList = categoryResponse.getProductList();
                            if (!Utility.isEmpty(productList)) {
                                if (mode.equalsIgnoreCase(Constants.IProductModes.BEST_SELLER)) {
                                    mBestSellerAdapter.setProductList(productList);
                                    mBestSellerAdapter.notifyDataSetChanged();
                                } else {
                                    mNewArrivalAdapter.setProductList(productList);
                                    mNewArrivalAdapter.notifyDataSetChanged();
                                }
                            }
                        }
                    }
                }
                stopProgress();
            }
        }).start();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.openAllBestsellers:
                launchFragment(new ProductCategoryFragment("", Constants.IModes.BEST_SELLER), true);
                break;
            /*case R.id.openNewArrivals:
                launchFragment(new ProductCategoryFragment("", Constants.IModes.NEW_ARRIVAL), true);
                break;*/

        }
    }

    @Override
    public boolean onBackPressed() {
        if (mIsDoubleBackPress) {
            super.onBackPressedToExit(this);
            return true;
        }
        Snackbar.make(mContentView, getString(R.string.back_press_msg), Snackbar.LENGTH_SHORT).show();
        mIsDoubleBackPress = true;
        new Handler().postDelayed(() -> mIsDoubleBackPress = false, 1500);
        return true;
    }

    @Override
    public void onStart() {
        super.onStart();
        mActivity.hideBackButton();
        hideKeyboard();
        mActivity.showCartIcon();
        mActivity.hideFilterIcon();
    }


    private class BestSellerAdapter extends RecyclerView.Adapter<BestSellerAdapter.RecyclerViewHolder> {

        private List<ProductResponse.ProductItem> productList = new ArrayList<>();

        public void setProductList(List<ProductResponse.ProductItem> productList) {
            this.productList = productList;
        }

        @NonNull
        @Override
        public BestSellerAdapter.RecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.best_seller_item_list, parent, false);
            return new RecyclerViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull BestSellerAdapter.RecyclerViewHolder holder, int position) {
            ProductResponse.ProductItem item = productList.get(position);
            setFadeAnimation(holder.bestSellerItemContainer);
            holder.productName.setText(item.getProductName());
            holder.productOriginalPrice.setText(Utility.getAmountInCurrencyFormat(item.getProductPrice()));
            holder.productDiscountPrice.setText(Utility.getAmountInCurrencyFormat(item.getProductSpecialPrice()));
            if (!Utility.isEmpty(item.getProductImage())) {
                Picasso.get().load(item.getProductImage()).placeholder(R.drawable.default_image).into(holder.productImage);
            }
            double difference = Double.parseDouble(item.getProductPrice()) - Double.parseDouble(item.getProductSpecialPrice());
            holder.saveAmount.setText(Utility.getAmountInCurrencyFormat(String.valueOf(difference)));
            String measureStr = item.getProductMeasure().concat(" ").concat(item.getProductUnit());
            holder.measureTextView.setText(measureStr);
            holder.mQuantity.setText(String.valueOf(item.getCount()));
        }

        @Override
        public int getItemCount() {
            return productList.size();
        }

        private class RecyclerViewHolder extends RecyclerView.ViewHolder {

            private LinearLayout mCategoryContainer;
            private TextView mQuantity;
            private ImageView productImage;
            private TextView productName;
            private View bestSellerItemContainer;
            private TextView productDiscountPrice;
            private TextView productOriginalPrice;
            private TextView saveAmount;
            private TextView measureTextView;

            RecyclerViewHolder(@NonNull View itemView) {
                super(itemView);
                measureTextView = itemView.findViewById(R.id.measureTextView);
                saveAmount = itemView.findViewById(R.id.saveAmount);
                productOriginalPrice = itemView.findViewById(R.id.productMrpPrice);
                productImage = itemView.findViewById(R.id.productImage);
                bestSellerItemContainer = itemView.findViewById(R.id.bestSellerItemContainer);
                ImageView addToCart = itemView.findViewById(R.id.addToCart);
                mCategoryContainer = itemView.findViewById(R.id.categoryContainer);
                ImageView minusQuantity = itemView.findViewById(R.id.minusQuantity);
                ImageView addQuantity = itemView.findViewById(R.id.addQuantity);
                productName = itemView.findViewById(R.id.productName);
                mQuantity = itemView.findViewById(R.id.quantityValue);
                productDiscountPrice = itemView.findViewById(R.id.productPrice);
                minusQuantity.setOnClickListener(v -> {
                    ProductResponse.ProductItem item = productList.get(getAdapterPosition());
                    int counter = item.getCount();
                    if (counter == 1) {
                        return;
                    }
                    counter--;
                    double originalPrice = counter * Double.parseDouble(item.getProductPrice());
                    double discountPrice = counter * Double.parseDouble(item.getProductSpecialPrice());
                    productDiscountPrice.setText(Utility.getAmountInCurrencyFormat(String.valueOf(discountPrice)));
                    productOriginalPrice.setText(Utility.getAmountInCurrencyFormat(String.valueOf(originalPrice)));
                    mQuantity.setText(String.valueOf(counter));
                    double difference = originalPrice - discountPrice;
                    saveAmount.setText(String.valueOf(difference));
                    item.setCount(counter);
                });
                addQuantity.setOnClickListener(v -> {
                    ProductResponse.ProductItem item = productList.get(getAdapterPosition());
                    int counter = item.getCount();
                    counter++;
                    double originalPrice = counter * Double.parseDouble(item.getProductPrice());
                    double discountPrice = counter * Double.parseDouble(item.getProductSpecialPrice());
                    productDiscountPrice.setText(Utility.getAmountInCurrencyFormat(String.valueOf(discountPrice)));
                    productOriginalPrice.setText(Utility.getAmountInCurrencyFormat(String.valueOf(originalPrice)));
                    mQuantity.setText(String.valueOf(counter));
                    double difference = originalPrice - discountPrice;
                    saveAmount.setText(String.valueOf(difference));
                    item.setCount(counter);
                });
                addToCart.setOnClickListener(view -> {
                    ProductResponse.ProductItem item = productList.get(getAdapterPosition());
                    CommonProductRequest request = new CommonProductRequest();
                    request.setUserId(getStringDataFromSharedPref(USER_ID));
                    request.setProductsId(item.getProductId());
                    request.setProductsQuantity(String.valueOf(item.getCount()));
                    request.setProductsSizeId(item.getProductPUId());
                    addToCartServerCall(request);
                });
                mCategoryContainer.setOnClickListener(view -> {
                    ProductResponse.ProductItem item = productList.get(getAdapterPosition());
                    CommonProductRequest request = new CommonProductRequest();
                    request.setUserId(getStringDataFromSharedPref(USER_ID));
                    request.setProductsId(item.getProductId());
                    request.setProductsSizeId(item.getProductPUId());
                    launchFragment(new ProductDetailFragment(request), true);
                });
            }
        }
    }

    private class CouponsListAdapter extends RecyclerView.Adapter<CouponsListAdapter.RecyclerViewHolder> {

        private List<CategoryResponse.CategoryItem> categoryItemList = new ArrayList<>();

        public void setCategoryItemList(List<CategoryResponse.CategoryItem> categoryItemList) {
            this.categoryItemList = categoryItemList;
        }

        @NonNull
        @Override
        public CouponsListAdapter.RecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.offer_coupon_items, parent, false);
            return new RecyclerViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull CouponsListAdapter.RecyclerViewHolder holder, int position) {
            if (!Utility.isEmpty(categoryItemList.get(position).getCategoryImage())) {
                Picasso.get().load(categoryItemList.get(position).getCategoryImage()).placeholder(R.drawable.default_image).into(holder.couponImageView);
            }
        }

        @Override
        public int getItemCount() {
            return categoryItemList.size();
        }

        private class RecyclerViewHolder extends RecyclerView.ViewHolder {

            private ImageView couponImageView;

            RecyclerViewHolder(@NonNull View itemView) {
                super(itemView);
                couponImageView = itemView.findViewById(R.id.couponImage);
                couponImageView.setOnClickListener(view -> {
                    CategoryResponse.CategoryItem item = categoryItemList.get(getAdapterPosition());
                    launchFragment(new ProductCategoryFragment(item.getCategoryId(), Constants.IModes.CARDS), true);
                });
            }
        }
    }

    private class CategoryItemListAdapter extends RecyclerView.Adapter<CategoryItemListAdapter.RecyclerViewHolder> {

        private List<CategoryResponse.CategoryItem> categoryItemList = new ArrayList<>();

        public void setCategoryItemList(List<CategoryResponse.CategoryItem> categoryItemList) {
            this.categoryItemList = categoryItemList;
        }

        @NonNull
        @Override
        public CategoryItemListAdapter.RecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.category_items, parent, false);
            return new RecyclerViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull CategoryItemListAdapter.RecyclerViewHolder holder, int position) {
            CategoryResponse.CategoryItem item = categoryItemList.get(position);
            if (!Utility.isEmpty(item.getCategoryImage())) {
                Picasso.get().load(item.getCategoryImage()).placeholder(R.drawable.icon_snack).into(holder.iconImage);
            }
            holder.categoryName.setText(Utility.toCamelCase(item.getCategoryName()));
        }

        @Override
        public int getItemCount() {
            return categoryItemList.size();
        }

        private class RecyclerViewHolder extends RecyclerView.ViewHolder {

            private LinearLayout constraintLayout;
            private ImageView iconImage;
            private TextView categoryName;

            RecyclerViewHolder(@NonNull View itemView) {
                super(itemView);
                categoryName = itemView.findViewById(R.id.categoryName);
                iconImage = itemView.findViewById(R.id.subCategoryImageView);
                constraintLayout = itemView.findViewById(R.id.categoryContainer);
                constraintLayout.setOnClickListener(v -> {
                    CategoryResponse.CategoryItem item = categoryItemList.get(getAdapterPosition());
                    launchFragment(new ProductCategoryFragment(item.getCategoryId(), Constants.IModes.CATEGORIES), true);
                });
            }
        }
    }

    private class BannerItemListAdapter extends RecyclerView.Adapter<BannerItemListAdapter.RecyclerViewHolder> {

        private List<CategoryResponse.CategoryItem> categoryItemList = new ArrayList<>();

        public void setCategoryItemList(List<CategoryResponse.CategoryItem> categoryItemList) {
            this.categoryItemList = categoryItemList;
        }

        @NonNull
        @Override
        public BannerItemListAdapter.RecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.banner_coupon_item, parent, false);
            return new RecyclerViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull BannerItemListAdapter.RecyclerViewHolder holder, int position) {
            if (!Utility.isEmpty(categoryItemList.get(position).getCategoryImage())) {
                Picasso.get().load(categoryItemList.get(position).getCategoryImage()).placeholder(R.drawable.category_page).into(holder.bannerImageView);
            }
        }

        @Override
        public int getItemCount() {
            return categoryItemList.size();
        }

        private class RecyclerViewHolder extends RecyclerView.ViewHolder {

            private ImageView bannerImageView;

            RecyclerViewHolder(@NonNull View itemView) {
                super(itemView);
                bannerImageView = itemView.findViewById(R.id.bannerImage);
                bannerImageView.setOnClickListener(view -> {
                    CategoryResponse.CategoryItem item = categoryItemList.get(getAdapterPosition());
                    launchFragment(new ProductCategoryFragment(item.getCategoryId(), Constants.IModes.CARDS), true);
                });
            }
        }
    }

    private class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.RecyclerViewHolder>  {


        @NonNull
        @Override
        public CategoryAdapter.RecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.header_subcategory_item, parent, false);
            return new RecyclerViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull CategoryAdapter.RecyclerViewHolder holder, int position) {
            GridLayoutManager layoutManager = new GridLayoutManager(
                    holder.recyclerView.getContext(),
                    3
            );
            SubCategoryAdapter subItemAdapter = new SubCategoryAdapter();
            holder.recyclerView.setLayoutManager(layoutManager);
            holder.recyclerView.setAdapter(subItemAdapter);
            holder.categoryNameTextView.setOnClickListener(v -> {
                if (holder.childLayoutContainer.getVisibility() == View.VISIBLE) {
                    holder.childLayoutContainer.setVisibility(View.GONE);
                    holder.constraintLayout12.setBackgroundColor(ContextCompat.getColor(mActivity, R.color.colorWhite));
                    holder.categoryNameTextView.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_keyboard_arrow_down, 0);

                } else {
                    holder.constraintLayout12.setBackgroundColor(ContextCompat.getColor(mActivity, R.color.light_pink));
                    holder.constraintLayout.setBackgroundColor(ContextCompat.getColor(mActivity, R.color.light_pink));
                    holder.childLayoutContainer.setVisibility(View.VISIBLE);
                    holder.categoryNameTextView.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_keyboard_arrow_up, 0);
                }
            });
        }

        @Override
        public int getItemCount() {
            return 5;
        }

        private class RecyclerViewHolder extends RecyclerView.ViewHolder {

            private ConstraintLayout constraintLayout;
            private TextView categoryNameTextView;
            private RecyclerView recyclerView;
            private ConstraintLayout childLayoutContainer;
            private ConstraintLayout constraintLayout12;

            RecyclerViewHolder(@NonNull View itemView) {
                super(itemView);
                constraintLayout = itemView.findViewById(R.id.constraintContainer);
                constraintLayout12 = itemView.findViewById(R.id.constraintLayout12);
                categoryNameTextView = itemView.findViewById(R.id.categoryNameTextView);
                recyclerView = itemView.findViewById(R.id.childCategoryRecyclerView);
                childLayoutContainer = itemView.findViewById(R.id.childLayoutContainer);

            }
        }
    }

    private class SubCategoryAdapter extends RecyclerView.Adapter<SubCategoryAdapter.RecyclerViewHolder> {


        @NonNull
        @Override
        public SubCategoryAdapter.RecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.child_subcategory_item, parent, false);
            return new RecyclerViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull SubCategoryAdapter.RecyclerViewHolder holder, int position) {

        }

        @Override
        public int getItemCount() {
            return 5;
        }

        private class RecyclerViewHolder extends RecyclerView.ViewHolder {

            RecyclerViewHolder(@NonNull View itemView) {
                super(itemView);
            }
        }
    }



    private void setFadeAnimation(View view) {
        AlphaAnimation anim = new AlphaAnimation(0.0f, 1.0f);
        anim.setDuration(500);
        view.startAnimation(anim);
    }
}
