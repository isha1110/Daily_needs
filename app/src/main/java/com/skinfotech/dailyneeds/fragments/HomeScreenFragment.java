package com.skinfotech.dailyneeds.fragments;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import androidx.core.graphics.drawable.RoundedBitmapDrawable;
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory;
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
import com.skinfotech.dailyneeds.models.requests.CommonProductRequest;
import com.skinfotech.dailyneeds.models.requests.CommonRequest;
import com.skinfotech.dailyneeds.models.requests.HomeCouponsRequest;
import com.skinfotech.dailyneeds.models.requests.HomeProductsRequest;
import com.skinfotech.dailyneeds.models.responses.CardResponse;
import com.skinfotech.dailyneeds.models.responses.CategoryResponse;
import com.skinfotech.dailyneeds.models.responses.ProductResponse;
import com.skinfotech.dailyneeds.models.responses.ProductsLabels;
import com.skinfotech.dailyneeds.models.responses.ProfileResponse;
import com.skinfotech.dailyneeds.models.responses.SubCategoryProductItem;
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
    private NewProductListAdapter mNewArrivalAdapter;
    private int mRecyclerViewSelectedPosition = 0;
    private RecyclerView mNewArrivalsRecycler;
    private CouponsListAdapter mCouponsListAdapter;
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
        fetchHomeProductsServerCall();
        fetchCardsServerCall(Constants.ICouponModes.TOP_CARD);
        fetchCardsServerCall(Constants.ICouponModes.BOTTOM_CARD);
        fetchCategoriesServerCall();
        mSwipeRefreshLayout.setOnRefreshListener(() -> {
            fetchHomeProductsServerCall();
            fetchCategoriesServerCall();
            fetchCardsServerCall(Constants.ICouponModes.TOP_CARD);
            fetchCardsServerCall(Constants.ICouponModes.BOTTOM_CARD);
            mSwipeRefreshLayout.setRefreshing(false);
        });
        return mContentView;
    }

    private void setupUI() {
        ToolBarManager.getInstance().hideToolBar(mActivity, false);
        ToolBarManager.getInstance().setHeaderTitle("Raj Nagar Extension");
        mActivity.isToggleButtonEnabled(true);
        mNewArrivalsRecycler = mContentView.findViewById(R.id.productRecycler);
        RecyclerView offersCardRecycler = mContentView.findViewById(R.id.offersCardRecycler);
        RecyclerView categoryItemListRecycler = mContentView.findViewById(R.id.categoryItemRecyclerView);
        RecyclerView bannerCouponItemRecycler = mContentView.findViewById(R.id.bannerCouponItemRecyclerView);
        mNewArrivalAdapter = new NewProductListAdapter();
        mCouponsListAdapter = new CouponsListAdapter();
        SnapHelper snapHelper = new PagerSnapHelper();
        mBannerItemListAdapter = new BannerItemListAdapter();
        snapHelper.attachToRecyclerView(bannerCouponItemRecycler);
        LinearLayoutManager layoutManager = new LinearLayoutManager(mActivity, LinearLayoutManager.HORIZONTAL, false);
        mNewArrivalsRecycler.setLayoutManager(layoutManager);
        mNewArrivalsRecycler.setAdapter(mNewArrivalAdapter);
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
                                categoryAdapter.setCategoryItemList(categoryItemList);
                                categoryAdapter.notifyDataSetChanged();
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

    }

    private void fetchCardsServerCall(String mode) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    HomeCouponsRequest request = new HomeCouponsRequest(mode);
                    request.setUserId(getStringDataFromSharedPref(USER_ID));
                    Call<CardResponse> call = RetrofitApi.getAppServicesObject().fetchCards(request);
                    final Response<CardResponse> response = call.execute();
                    updateOnUiThread(() -> handleResponse(response));
                } catch (Exception e) {
                    stopProgress();
                    showToast(e.getMessage());
                    Log.e(TAG, e.getMessage(), e);
                }
            }

            private void handleResponse(Response<CardResponse> response) {
                if (response.isSuccessful()) {
                    CardResponse cardResponse = response.body();
                    if (cardResponse != null) {
                        if (Constants.SUCCESS.equalsIgnoreCase(cardResponse.getErrorCode())) {
                            List<CardResponse.CardItem> cardItemList = cardResponse.getCardItems();
                            if (!Utility.isEmpty(cardItemList)) {
                                if (mode.equalsIgnoreCase(Constants.ICouponModes.TOP_CARD)) {
                                    mCouponsListAdapter.setCategoryItemList(cardItemList);
                                    mCouponsListAdapter.notifyDataSetChanged();
                                } else {
                                    mBannerItemListAdapter.setCategoryItemList(cardItemList);
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

    private void fetchHomeProductsServerCall() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Call<ProductResponse> call = RetrofitApi.getAppServicesObject().fetchHomeProducts();
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
                                    mNewArrivalAdapter.setProductList(productList);
                                    mNewArrivalAdapter.notifyDataSetChanged();
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
                launchFragment(new ProductCategoryFragment(), true);
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
        mActivity.showSearchIcon();
    }


    private class NewProductListAdapter extends RecyclerView.Adapter<NewProductListAdapter.RecyclerViewHolder> {

        private List<ProductResponse.ProductItem> productList = new ArrayList<>();

        public void setProductList(List<ProductResponse.ProductItem> productList) {
            this.productList = productList;
        }

        @NonNull
        @Override
        public NewProductListAdapter.RecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.best_seller_item_list, parent, false);
            return new RecyclerViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull NewProductListAdapter.RecyclerViewHolder holder, int position) {
            ProductResponse.ProductItem item = productList.get(position);
            setFadeAnimation(holder.bestSellerItemContainer);
            holder.productName.setText(item.getProductName());
            holder.productOriginalPrice.setText(Utility.getAmountInCurrencyFormat(item.getProductPrice()));
            holder.productDiscountPrice.setText(Utility.getAmountInCurrencyFormat(item.getProductSpecialPrice()));
            if (!Utility.isEmpty(item.getProductImage())) {
                Picasso.get().load(item.getProductImage()).placeholder(R.drawable.default_image).into(holder.productImage);
            }
            String measureStr = item.getProductMeasure().concat(" ").concat(item.getProductUnit());
            holder.measureTextView.setText(measureStr);
            holder.mQuantity.setText(String.valueOf(item.getCount()));
            holder.saveAmount.setText(item.getProductDiscount()+"% OFF");
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
                saveAmount = itemView.findViewById(R.id.percentOFFTextView);
                productOriginalPrice = itemView.findViewById(R.id.productMrpPrice);
                productImage = itemView.findViewById(R.id.productImage);
                bestSellerItemContainer = itemView.findViewById(R.id.bestSellerItemContainer);
                TextView addToCart = itemView.findViewById(R.id.addToCart);
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
                    double originalPrice =(counter * Double.parseDouble(item.getProductPrice()));
                    double discountPrice =  (counter * Double.parseDouble(item.getProductSpecialPrice()));
                    double saveAmountPrice = (counter * Double.parseDouble(item.getProductDiscount()));
                    saveAmount.setText(Utility.getAmountInCurrencyFormat(String.valueOf(saveAmountPrice)));
                    productDiscountPrice.setText(Utility.getAmountInCurrencyFormat(String.valueOf(discountPrice)));
                    productOriginalPrice.setText(Utility.getAmountInCurrencyFormat(String.valueOf(originalPrice)));
                    mQuantity.setText(String.valueOf(counter));
                    double difference = originalPrice - discountPrice;
                    saveAmountPrice=(difference/originalPrice)*100;
                    saveAmount.setText(saveAmountPrice+"% OFF");
                    item.setCount(counter);
                });
                addQuantity.setOnClickListener(v -> {
                    ProductResponse.ProductItem item = productList.get(getAdapterPosition());
                    int counter = item.getCount();
                    counter++;
                    double originalPrice = (counter * Double.parseDouble(item.getProductPrice()));
                    double discountPrice =  (counter * Double.parseDouble(item.getProductSpecialPrice()));
                    double saveAmountPrice =  (counter * Double.parseDouble(item.getProductDiscount()));
                    saveAmount.setText(Utility.getAmountInCurrencyFormat(String.valueOf(saveAmountPrice)));
                    productDiscountPrice.setText(Utility.getAmountInCurrencyFormat(String.valueOf(discountPrice)));
                    productOriginalPrice.setText(Utility.getAmountInCurrencyFormat(String.valueOf(originalPrice)));
                    mQuantity.setText(String.valueOf(counter));
                    double difference = originalPrice - discountPrice;
                    saveAmountPrice=(difference/originalPrice)*100;
                    saveAmount.setText(saveAmountPrice+"% OFF");
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

        private List<CardResponse.CardItem> cardItemList = new ArrayList<>();

        public void setCategoryItemList(List<CardResponse.CardItem> cardItemList) {
            this.cardItemList = cardItemList;
        }

        @NonNull
        @Override
        public CouponsListAdapter.RecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.offer_coupon_items, parent, false);
            return new RecyclerViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull CouponsListAdapter.RecyclerViewHolder holder, int position) {
            if (!Utility.isEmpty(cardItemList.get(position).getCardImage())) {
                Picasso.get().load(cardItemList.get(position).getCardImage()).placeholder(R.drawable.default_image).into(holder.couponImageView);
            }
        }

        @Override
        public int getItemCount() {
            return cardItemList.size();
        }

        private class RecyclerViewHolder extends RecyclerView.ViewHolder {

            private ImageView couponImageView;

            RecyclerViewHolder(@NonNull View itemView) {
                super(itemView);
                couponImageView = itemView.findViewById(R.id.couponImage);
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
            if (!Utility.isEmpty(item.getCategoryImage()))
            {
                Picasso.get().load(item.getCategoryImage()).placeholder(R.drawable.grocery_staples).into(holder.iconImage);
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
                    launchFragment(new ProductCategoryFragment(item.getCategoryId(), Constants.CatModes.CATEGORIES), true);
                });
            }
        }
    }

    private class BannerItemListAdapter extends RecyclerView.Adapter<BannerItemListAdapter.RecyclerViewHolder> {

        private List<CardResponse.CardItem> cardItemList = new ArrayList<>();

        public void setCategoryItemList(List<CardResponse.CardItem> cardItemList) {
            this.cardItemList = cardItemList;
        }

        @NonNull
        @Override
        public BannerItemListAdapter.RecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.banner_coupon_item, parent, false);
            return new RecyclerViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull BannerItemListAdapter.RecyclerViewHolder holder, int position) {
            if (!Utility.isEmpty(cardItemList.get(position).getCardImage())) {
                Picasso.get().load(cardItemList.get(position).getCardImage()).placeholder(R.drawable.category_page).into(holder.bannerImageView);
            }
        }

        @Override
        public int getItemCount() {
            return cardItemList.size();
        }

        private class RecyclerViewHolder extends RecyclerView.ViewHolder {

            private ImageView bannerImageView;

            RecyclerViewHolder(@NonNull View itemView) {
                super(itemView);
                bannerImageView = itemView.findViewById(R.id.bannerImage);
                bannerImageView.setOnClickListener(view -> {
                    CardResponse.CardItem item = cardItemList.get(getAdapterPosition());
                    launchFragment(new ProductCategoryFragment(/*item.getCategoryId(), Constants.IModes.CARDS*/), true);
                });
            }
        }
    }

    private class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.RecyclerViewHolder>  {
        private List<CategoryResponse.CategoryItem> categoryItemList = new ArrayList<>();

        public void setCategoryItemList(List<CategoryResponse.CategoryItem> categoryItemList) {
            this.categoryItemList = categoryItemList;
        }

        @NonNull
        @Override
        public CategoryAdapter.RecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.header_subcategory_item, parent, false);
            return new RecyclerViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull CategoryAdapter.RecyclerViewHolder holder, int position) {
            CategoryResponse.CategoryItem item = categoryItemList.get(position);
            if (!Utility.isEmpty(item.getCategoryImage())) {
                Picasso.get().load(item.getCategoryImage()).placeholder(R.drawable.grocery_staples).into(holder.iconImage);
            }
            holder.categoryNameTextView.setText(Utility.toCamelCase(item.getCategoryName()));
            GridLayoutManager layoutManager = new GridLayoutManager(
                    holder.recyclerView.getContext(),
                    3
            );
            SubCategoryAdapter subItemAdapter = new SubCategoryAdapter(item.getmCategoryProductList());
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
            return categoryItemList.size();
        }

        private class RecyclerViewHolder extends RecyclerView.ViewHolder {

            private ConstraintLayout constraintLayout;
            private TextView categoryNameTextView;
            private RecyclerView recyclerView;
            private ConstraintLayout childLayoutContainer;
            private ConstraintLayout constraintLayout12;
            private ImageView iconImage;

            RecyclerViewHolder(@NonNull View itemView) {
                super(itemView);
                iconImage = itemView.findViewById(R.id.productImageView);
                constraintLayout = itemView.findViewById(R.id.constraintContainer);
                constraintLayout12 = itemView.findViewById(R.id.constraintLayout12);
                categoryNameTextView = itemView.findViewById(R.id.categoryNameTextView);
                recyclerView = itemView.findViewById(R.id.childCategoryRecyclerView);
                childLayoutContainer = itemView.findViewById(R.id.childLayoutContainer);

            }
        }
    }

    private class SubCategoryAdapter extends RecyclerView.Adapter<SubCategoryAdapter.RecyclerViewHolder> {
        private List<SubCategoryProductItem> categoryItemList = new ArrayList<>();

        public SubCategoryAdapter(List<SubCategoryProductItem> getmCategoryProductList) {
            this.categoryItemList = getmCategoryProductList;
        }

        public void setCategoryItemList(List<SubCategoryProductItem> categoryItemList) {
            this.categoryItemList = categoryItemList;
        }
        @NonNull
        @Override
        public SubCategoryAdapter.RecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.child_subcategory_item, parent, false);
            return new RecyclerViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull SubCategoryAdapter.RecyclerViewHolder holder, int position) {
            SubCategoryProductItem item = categoryItemList.get(position);
            if (!Utility.isEmpty(item.getSubCategoryImage())) {
                Picasso.get().load(item.getSubCategoryImage()).placeholder(R.drawable.grocery_staples).into(holder.iconImage);
            }
            holder.categoryNameTextView.setText(Utility.toCamelCase(item.getSubCategoryName()));
            holder.constraintLayout.setOnClickListener(v -> {
                launchFragment(new ProductCategoryFragment(item.getSubCategoryId(), Constants.CatModes.SUBCATEGORIES), true);
            });
        }

        @Override
        public int getItemCount() {
            return categoryItemList.size();
        }

        private class RecyclerViewHolder extends RecyclerView.ViewHolder {
            private ConstraintLayout constraintLayout;
            private TextView categoryNameTextView;
            private ImageView iconImage;

            RecyclerViewHolder(@NonNull View itemView) {
                super(itemView);
                iconImage = itemView.findViewById(R.id.subCategoryImageView);
                categoryNameTextView = itemView.findViewById(R.id.subCategoryName);
                constraintLayout = itemView.findViewById(R.id.constraintContainer);
            }
        }
    }



    private void setFadeAnimation(View view) {
        AlphaAnimation anim = new AlphaAnimation(0.0f, 1.0f);
        anim.setDuration(500);
        view.startAnimation(anim);
    }
}
