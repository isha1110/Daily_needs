package com.skinfotech.dailyneeds.fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.skinfotech.dailyneeds.Constants;
import com.skinfotech.dailyneeds.R;
import com.skinfotech.dailyneeds.Utility;
import com.skinfotech.dailyneeds.adapters.MyProductCategoryPagerAdapter;
import com.skinfotech.dailyneeds.constant.ToolBarManager;
import com.skinfotech.dailyneeds.models.requests.AllProductRequest;
import com.skinfotech.dailyneeds.models.requests.CommonProductRequest;
import com.skinfotech.dailyneeds.models.requests.CommonRequest;
import com.skinfotech.dailyneeds.models.requests.SubCategoryRequest;
import com.skinfotech.dailyneeds.models.responses.CategoryResponse;
import com.skinfotech.dailyneeds.models.responses.ProductResponse;
import com.skinfotech.dailyneeds.retrofit.RetrofitApi;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.squareup.picasso.Picasso;
import retrofit2.Call;
import retrofit2.Response;

import java.util.ArrayList;
import java.util.List;

import static com.skinfotech.dailyneeds.Constants.USER_ID;

public class ProductCategoryFragment extends BaseFragment {

    private View view;
    private BottomSheetDialog bottomSheetDialog;
    private SubCategoryItemListAdapter mSubCategoryAdapter;
    private static final String TAG = "ProductCategoryFragment";
    private String mCategoryId = "";
    private String mSubCategoryId = "";
    private String mCardId = "";
    private String mModeStr = "";
    private String mOffTextView = "";
    private List<ProductResponse.ProductItem> mOriginalProductList = new ArrayList<>();
    private boolean mIsSubCategoryFetched = false;
    private boolean mIsFromSideNavigation = false;
    private boolean mIsSubSubCategoryFetched = false;
    private TextView cartCountTextView;
    private TextView cartTotalTextView;
    //This unused variables are declared for the case we have to enable the pagination in the recycler view
    private boolean mIsDataAvailable = false;
    private boolean isScrolling = false;
    private int currentItems;
    private int totalItems;
    private int scrollOutItems;
   // private OrderDetailResponse productItem;
    private ProductResponse categoryResponse;
    private TabLayout categoriesTabs;
    private ViewPager productCategoriesPager;

    public ProductCategoryFragment(String id, String modes) {
        if (modes.equalsIgnoreCase(Constants.IModes.CARDS)) {
            mCardId = id;
        } else if (modes.equalsIgnoreCase(Constants.IModes.CATEGORIES)) {
            mCategoryId = id;
        } else if (modes.equalsIgnoreCase(Constants.IModes.NEW_ARRIVAL)) {
            mModeStr = Constants.IModes.NEW_ARRIVAL;
        } else if (modes.equalsIgnoreCase(Constants.IModes.BEST_SELLER)) {
            mModeStr = Constants.IModes.BEST_SELLER;
        }/* else if (modes.equalsIgnoreCase(Constants.IModes.SIDE_NAVIGATION)) {
            mIsFromSideNavigation = true;
            mIsSubCategoryFetched = true;
            String[] idSplit = id.split(Constants.SPLIT);
            mCategoryId = idSplit[0];
            mSubCategoryId = idSplit[1];
        }*/
    }

    public ProductCategoryFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_product_category, container, false);
        ToolBarManager.getInstance().hideToolBar(mActivity, false);
        ToolBarManager.getInstance().setHeaderTitle("");
        ToolBarManager.getInstance().onBackPressed(this);
        mActivity.isToggleButtonEnabled(false);
        setupUI();
        /*if (mIsFromSideNavigation) {
            fetchAllProductsServerCall(mSubCategoryId, "");
            fetchSubCategoryServerCall(mSubCategoryId, "");
        } else {
            fetchAllProductsServerCall("", "");
            fetchSubCategoryServerCall("", "");
        }*/
        return view;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.checkoutButton:
                launchFragment(new CheckOutFragment(), true);
                bottomSheetDialog.dismiss();
                break;
        }
    }

    private void setupUI() {
        mSubCategoryAdapter = new SubCategoryItemListAdapter();
        /*LinearLayoutManager cartLayoutManager = new LinearLayoutManager(mActivity, LinearLayoutManager.VERTICAL, false);
        if (null != cartItemListRecycler) {
            cartItemListRecycler.setLayoutManager(cartLayoutManager);
            cartItemListRecycler.setAdapter(mCartAdapter);
        }*/
        //This commented code is done in case we have to enable the pagination in the recycler view
        /*recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                    isScrolling = true;
                }
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                currentItems = gridLayoutManager.getChildCount();
                totalItems = gridLayoutManager.getItemCount();
                scrollOutItems = gridLayoutManager.findFirstVisibleItemPosition();
                if (isScrolling && (currentItems + scrollOutItems == totalItems) && mIsDataAvailable) {
                    isScrolling = false;
                    fetchAllProductsServerCall("");
                }
            }
        });*/
        productCategoriesPager = view.findViewById(R.id.productCategoryViewPager);
        categoriesTabs = view.findViewById(R.id.categoryTabLayout);
        productCategoriesPager.setAdapter(new MyProductCategoryPagerAdapter(mActivity.getSupportFragmentManager()));
        categoriesTabs.setupWithViewPager(productCategoriesPager);
    }

    /*@Override
    protected void cartAddedSuccessCallBack() {
        fetchCartServerCall();
    }*/

    @Override
    public void onStart() {
        super.onStart();
        mActivity.showBackButton();
        mActivity.showCartIcon();
        mActivity.hideSearchIcon();
    }

    /*private void fetchCartServerCall() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    CommonRequest request = new CommonRequest();
                    request.setUserId(getStringDataFromSharedPref(USER_ID));
                    Call<ProductResponse> call = RetrofitApi.getAppServicesObject().fetchCart(request);
                    final Response<ProductResponse> response = call.execute();
                    updateOnUiThread(() -> handleResponse(response));
                } catch (Exception e) {
                    stopProgress();
                    showToast(e.getMessage());
                    Log.e(TAG, e.getMessage(), e);
                }
            }

            @SuppressLint("SetTextI18n")
            private void handleResponse(Response<ProductResponse> response) {
                if (response.isSuccessful()) {
                     categoryResponse = response.body();
                    if (categoryResponse != null) {
                        if (Constants.SUCCESS.equalsIgnoreCase(categoryResponse.getErrorCode())) {
                            List<ProductResponse.ProductItem> productList = categoryResponse.getProductList();
                            if (!Utility.isEmpty(productList)) {
                                mCartAdapter.setProductList(productList);
                                mCartAdapter.notifyDataSetChanged();
                            }
                            cartTotalTextView.setText("Cart Total : "+getString(R.string.ruppee_symbol) +Utility.getAmountInCurrencyFormat(categoryResponse.getTotalPrice()) );
                        }
                    }
                }
                stopProgress();
            }
        }).start();
    }*/

    /*@Override
    protected void cartRemovedSuccessCallBack() {
        fetchCartServerCall();
    }*/

    private void fetchSubCategoryServerCall(String subCategoryId, String subSubCategoryId) {
        showProgress();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    SubCategoryRequest request = new SubCategoryRequest();
                    request.setCategoryId(mCategoryId);
                    request.setCardId(mCardId);
                    request.setMode(mModeStr);
                    request.setSubCategoryId(subCategoryId);
                    request.setSubSubCategoryId(subSubCategoryId);
                    Call<CategoryResponse> call = RetrofitApi.getAppServicesObject().fetchSubCategories(request);
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
                            List<CategoryResponse.CategoryItem> productList = categoryResponse.getCategoryList();
                            if (!Utility.isEmpty(productList)) {
                                mSubCategoryAdapter.setProductList(productList);
                            } else {
                                mSubCategoryAdapter.setProductList(new ArrayList<>());
                            }
                            mSubCategoryAdapter.notifyDataSetChanged();
                        }
                    }
                }
                stopProgress();
            }
        }).start();
    }

   /* private void fetchAllProductsServerCall(String subCategoryId, String subSubCategoryId) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    AllProductRequest request = new AllProductRequest();
                    request.setUserId(getStringDataFromSharedPref(USER_ID));
                    request.setCategoryId(mCategoryId);
                    request.setCardId(mCardId);
                    request.setMode(mModeStr);
                    request.setSubCategoryId(subCategoryId);
                    request.setSubSubCategoryId(subSubCategoryId);
                    sCategoryParentId = mCategoryId;
                    sSubCategoryParentId = subCategoryId;
                    sSubSubCategoryParentId = subSubCategoryId;
                    Call<ProductResponse> call = RetrofitApi.getAppServicesObject().fetchAllProducts(request);
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
                            mIsDataAvailable = categoryResponse.isDataAvailable();
                            mOriginalProductList.clear();
                            mOriginalProductList.addAll(categoryResponse.getProductList());
                            if (!Utility.isEmpty(mOriginalProductList)) {
                                mOffTextView = categoryResponse.getErrorMessage();
                                mProductAdapter.setProductList(mOriginalProductList);
                            } else {
                                mProductAdapter.setProductList(new ArrayList<>());
                            }
                            mProductAdapter.notifyDataSetChanged();
                        }
                    }
                }
                stopProgress();
            }
        }).start();
    }*/

    /*private class ProductsAdapter extends RecyclerView.Adapter<ProductsAdapter.RecyclerViewHolder> {

        private List<ProductResponse.ProductItem> productList = new ArrayList<>();

        public void setProductList(List<ProductResponse.ProductItem> productList) {
            this.productList = productList;
        }

        @NonNull
        @Override
        public ProductsAdapter.RecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.product_category_item, parent, false);
            return new RecyclerViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ProductsAdapter.RecyclerViewHolder holder, int position) {
            holder.offTextView.setVisibility(position == 1 ? View.VISIBLE : View.GONE);
            holder.offTextView.setText(position == 1 ? mOffTextView : "");
            ProductResponse.ProductItem item = productList.get(position);
            if (!Utility.isEmpty(item.getProductImage())) {
                Picasso.get().load(item.getProductImage()).placeholder(R.drawable.default_image).into(holder.productImage);
            }
            String measureStr = item.getProductMeasure().concat(" ").concat(item.getProductUnit());
            holder.productMeasure.setText(measureStr);
            holder.productName.setText(item.getProductName());
            holder.productOriginalPrice.setText("₹ ".concat(Utility.getAmountInCurrencyFormat(item.getProductPrice())));
            holder.productSpecialPrice.setText("₹ ".concat(Utility.getAmountInCurrencyFormat(item.getProductSpecialPrice())));
            if (item.isWishListDone()) {
                holder.wishlistImageView.setImageDrawable(ContextCompat.getDrawable(mActivity, R.drawable.red_heart_icon));
            }
        }

        @Override
        public int getItemCount() {
            return productList.size();
        }

        private class RecyclerViewHolder extends RecyclerView.ViewHolder {

            private ImageView wishlistImageView;
            private ImageView productImage;
            private TextView productSpecialPrice;
            private TextView productOriginalPrice;
            private TextView productName;
            private TextView productMeasure;
            private TextView offTextView;

            RecyclerViewHolder(@NonNull View itemView) {
                super(itemView);
                productName = itemView.findViewById(R.id.productName);
                productMeasure = itemView.findViewById(R.id.productQuantity);
                productOriginalPrice = itemView.findViewById(R.id.textView40);
                productSpecialPrice = itemView.findViewById(R.id.productPrice);
                productImage = itemView.findViewById(R.id.productImage);
                wishlistImageView = itemView.findViewById(R.id.wishlistIcon);
                ImageView addToCart = itemView.findViewById(R.id.addToCart);
                offTextView = itemView.findViewById(R.id.offTextView);
                productImage.setOnClickListener(v -> {
                    ProductResponse.ProductItem item = productList.get(getAdapterPosition());
                    CommonProductRequest request = new CommonProductRequest();
                    request.setUserId(getStringDataFromSharedPref(USER_ID));
                    request.setProductsId(item.getProductId());
                    request.setProductsSizeId(item.getProductPUId());
                    launchFragment(new ProductDetailFragment(request), true);
                });
                addToCart.setOnClickListener(v -> {
                    ProductResponse.ProductItem item = productList.get(getAdapterPosition());
                    CommonProductRequest request = new CommonProductRequest();
                    request.setUserId(getStringDataFromSharedPref(USER_ID));
                    request.setProductsId(item.getProductId());
                    request.setProductsSizeId(item.getProductPUId());
                    request.setProductsQuantity(String.valueOf(item.getCount()));
                    addToCartServerCall(request);
                });
                wishlistImageView.setOnClickListener(v -> {
                    ProductResponse.ProductItem item = productList.get(getAdapterPosition());
                    if (item.isWishListDone()) {
                        item.setWishListDone(false);
                        wishlistImageView.setImageDrawable(ContextCompat.getDrawable(mActivity, R.drawable.icon_wishlist));
                    } else {
                        item.setWishListDone(true);
                        wishlistImageView.setImageDrawable(ContextCompat.getDrawable(mActivity, R.drawable.red_heart_icon));
                    }
                    CommonProductRequest request = new CommonProductRequest();
                    request.setUserId(getStringDataFromSharedPref(USER_ID));
                    request.setProductsId(item.getProductId());
                    request.setProductsSizeId(item.getProductPUId());
                    wishListServerCall(request);
                });
            }
        }
    }
*/
    private class SubCategoryItemListAdapter extends RecyclerView.Adapter<SubCategoryItemListAdapter.RecyclerViewHolder> {

        private List<CategoryResponse.CategoryItem> productList = new ArrayList<>();

        public void setProductList(List<CategoryResponse.CategoryItem> productList) {
            this.productList = productList;
        }

        @NonNull
        @Override
        public SubCategoryItemListAdapter.RecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.sub_category_item_list, parent, false);
            return new RecyclerViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull SubCategoryItemListAdapter.RecyclerViewHolder holder, int position) {
            CategoryResponse.CategoryItem item = productList.get(position);
            holder.productQuantity.setText(item.getCategoryName());
        }

        @Override
        public int getItemCount() {
            return productList.size();
        }

        private class RecyclerViewHolder extends RecyclerView.ViewHolder {

            private TextView productQuantity;

            RecyclerViewHolder(@NonNull View itemView) {
                super(itemView);
                productQuantity = itemView.findViewById(R.id.productQuantity);
                productQuantity.setOnClickListener(v -> {
                    CategoryResponse.CategoryItem item = productList.get(getAdapterPosition());
                    if (!mIsSubCategoryFetched) {
                        mIsSubCategoryFetched = true;
                        fetchSubCategoryServerCall(item.getCategoryId(), "");
                       // fetchAllProductsServerCall(item.getCategoryId(), "");
                    } else if (!mIsSubSubCategoryFetched) {
                        mIsSubSubCategoryFetched = true;
                        //fetchSubCategoryServerCall("", item.getCategoryId());
                        //fetchAllProductsServerCall("", item.getCategoryId());
                    }
                });
            }
        }
    }
}
