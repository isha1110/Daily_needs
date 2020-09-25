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
import com.skinfotech.dailyneeds.Constants;
import com.skinfotech.dailyneeds.R;
import com.skinfotech.dailyneeds.Utility;
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
    private ProductsAdapter mProductAdapter;
    private SubCategoryItemListAdapter mSubCategoryAdapter;
    private CartItemListAdapter mCartAdapter;
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

    public ProductCategoryFragment(String id, String modes) {
        if (modes.equalsIgnoreCase(Constants.IModes.CARDS)) {
            mCardId = id;
        } else if (modes.equalsIgnoreCase(Constants.IModes.CATEGORIES)) {
            mCategoryId = id;
        } else if (modes.equalsIgnoreCase(Constants.IModes.NEW_ARRIVAL)) {
            mModeStr = Constants.IModes.NEW_ARRIVAL;
        } else if (modes.equalsIgnoreCase(Constants.IModes.BEST_SELLER)) {
            mModeStr = Constants.IModes.BEST_SELLER;
        } else if (modes.equalsIgnoreCase(Constants.IModes.SIDE_NAVIGATION)) {
            mIsFromSideNavigation = true;
            mIsSubCategoryFetched = true;
            String[] idSplit = id.split(Constants.SPLIT);
            mCategoryId = idSplit[0];
            mSubCategoryId = idSplit[1];
        }
    }

    public ProductCategoryFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_product_category, container, false);
        ToolBarManager.getInstance().hideToolBar(mActivity, false);
        ToolBarManager.getInstance().setHeaderTitle("");
        mActivity.isToggleButtonEnabled(true);
        bottomSheetDialog = new BottomSheetDialog(mActivity, R.style.BottomSheetDialogTheme);
        bottomSheetDialog.setContentView(R.layout.fragment_bottom_cart_slide);
        setupUI();
        if (mIsFromSideNavigation) {
            fetchAllProductsServerCall(mSubCategoryId, "");
            fetchSubCategoryServerCall(mSubCategoryId, "");
        } else {
            fetchAllProductsServerCall("", "");
            fetchSubCategoryServerCall("", "");
        }
        fetchCartServerCall();
        return view;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.checkoutButton:
                launchFragment(new CheckOutFragment(), true);
                bottomSheetDialog.dismiss();
                break;
            case R.id.constraintLayout2:
                if (cartCountTextView.getText().toString().equals(getString(R.string.zero_item))) {
                    showToast(getString(R.string.txt_no_product_found));
                    return;
                }
                bottomSheetDialog.show();
                break;
        }
    }

    private void setupUI() {
        cartCountTextView = view.findViewById(R.id.cartCount);
        cartTotalTextView = view.findViewById(R.id.cartTotalPrice);
        RecyclerView recyclerView = view.findViewById(R.id.productCategoryRecycler);
        mProductAdapter = new ProductsAdapter();
        RecyclerView mSubCategoryItemRecycler = view.findViewById(R.id.categoryItemRecyclerView);
        mSubCategoryAdapter = new SubCategoryItemListAdapter();
        RecyclerView cartItemListRecycler = bottomSheetDialog.findViewById(R.id.cartItemListRecycler);
        mCartAdapter = new CartItemListAdapter();
        StaggeredGridLayoutManager gridLayoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.setAdapter(mProductAdapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(mActivity, LinearLayoutManager.HORIZONTAL, false);
        mSubCategoryItemRecycler.setLayoutManager(layoutManager);
        mSubCategoryItemRecycler.setAdapter(mSubCategoryAdapter);
        LinearLayoutManager cartLayoutManager = new LinearLayoutManager(mActivity, LinearLayoutManager.VERTICAL, false);
        if (null != cartItemListRecycler) {
            cartItemListRecycler.setLayoutManager(cartLayoutManager);
            cartItemListRecycler.setAdapter(mCartAdapter);
        }
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
    }

    @Override
    protected void cartAddedSuccessCallBack() {
        fetchCartServerCall();
    }

    @Override
    public void onStart() {
        super.onStart();
        mActivity.hideBackButton();
        mActivity.hideCartIcon();
        mActivity.showFilterIcon();
    }

    private void fetchCartServerCall() {
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
                                String cartCountStr = productList.size() + " items";
                                cartCountTextView.setText(cartCountStr);
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
    }

    @Override
    protected void cartRemovedSuccessCallBack() {
        fetchCartServerCall();
    }

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

    private void fetchAllProductsServerCall(String subCategoryId, String subSubCategoryId) {
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
    }

    private class ProductsAdapter extends RecyclerView.Adapter<ProductsAdapter.RecyclerViewHolder> {

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
                        fetchAllProductsServerCall(item.getCategoryId(), "");
                    } else if (!mIsSubSubCategoryFetched) {
                        mIsSubSubCategoryFetched = true;
                        fetchSubCategoryServerCall("", item.getCategoryId());
                        fetchAllProductsServerCall("", item.getCategoryId());
                    }
                });
            }
        }
    }

    private class CartItemListAdapter extends RecyclerView.Adapter<CartItemListAdapter.RecyclerViewHolder> {

        private List<ProductResponse.ProductItem> productList = new ArrayList<>();

        public void setProductList(List<ProductResponse.ProductItem> productList) {
            this.productList = productList;
        }

        @NonNull
        @Override
        public CartItemListAdapter.RecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cart_item_list, parent, false);
            return new CartItemListAdapter.RecyclerViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull CartItemListAdapter.RecyclerViewHolder holder, int position) {
            ProductResponse.ProductItem item = productList.get(position);
            if (!Utility.isEmpty(item.getProductImage())) {
                Picasso.get().load(item.getProductImage()).placeholder(R.drawable.default_image).into(holder.productImage);
            }
            holder.productName.setText(item.getProductName());
            holder.productOriginalPrice.setText(Utility.getAmountInCurrencyFormat(item.getProductPrice()));
            holder.mProductPrice.setText(Utility.getAmountInCurrencyFormat(item.getProductSpecialPrice()));
            String sizeStr = item.getProductMeasure().concat(" ").concat(item.getProductUnit());
            holder.measureTextView.setText(sizeStr);
            String quantityStr = item.getProductQuantity() + " quantity";
            holder.mQuantity.setText(quantityStr);
            double diff = Double.parseDouble(item.getProductPrice()) - Double.parseDouble(item.getProductSpecialPrice());
            holder.saveAmount.setText(String.valueOf(diff));

        }

        @Override
        public int getItemCount() {
            return productList.size();
        }

        private class RecyclerViewHolder extends RecyclerView.ViewHolder {

            private TextView mQuantity;
            private TextView mProductPrice;
            private TextView productOriginalPrice;
            private TextView saveAmount;
            private TextView measureTextView;
            private ImageView productImage;
            private TextView productName;

            RecyclerViewHolder(@NonNull View itemView) {
                super(itemView);
                measureTextView = itemView.findViewById(R.id.measureTextView);
                saveAmount = itemView.findViewById(R.id.amountSaved);
                productName = itemView.findViewById(R.id.productName);
                productOriginalPrice = itemView.findViewById(R.id.mrpPrice);
                productImage = itemView.findViewById(R.id.productImage);
                itemView.findViewById(R.id.quantityMinus).setVisibility(View.GONE);
                itemView.findViewById(R.id.quantityAdd).setVisibility(View.GONE);
                mQuantity = itemView.findViewById(R.id.productQuantity);
                ImageView cancelItem = itemView.findViewById(R.id.cancleItem);
                mProductPrice = itemView.findViewById(R.id.amountPaid);
                cancelItem.setOnClickListener(v -> {
                    ProductResponse.ProductItem item = productList.get(getAdapterPosition());
                    CommonProductRequest request = new CommonProductRequest();
                    request.setUserId(getStringDataFromSharedPref(USER_ID));
                    request.setProductsId(item.getProductId());
                    request.setProductsSizeId(item.getProductPUId());
                    removeFromCartServerCall(request);
                });
            }
        }
    }
}
