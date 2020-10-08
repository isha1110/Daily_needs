package com.skinfotech.dailyneeds.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.skinfotech.dailyneeds.Constants;
import com.skinfotech.dailyneeds.R;
import com.skinfotech.dailyneeds.Utility;
import com.skinfotech.dailyneeds.constant.ToolBarManager;
import com.skinfotech.dailyneeds.models.requests.AllProductRequest;
import com.skinfotech.dailyneeds.models.requests.CommonProductRequest;
import com.skinfotech.dailyneeds.models.responses.ProductResponse;
import com.skinfotech.dailyneeds.retrofit.RetrofitApi;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;

import static android.content.ContentValues.TAG;
import static com.skinfotech.dailyneeds.Constants.USER_ID;

public class ProductCategoryListFragment extends BaseFragment {
    private ProductsAdapter mProductAdapter;
    private String mCategoryId = "";
    private String mSubCategoryId = "";
    private String mCardId = "";
    private String mModeStr = "";
    private String mOffTextView = "";
    private boolean mIsDataAvailable = false;
    private RecyclerView mProductCategoryRecyclerView;
    private List<ProductResponse.ProductItem> mOriginalProductList = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mContentView = inflater.inflate(R.layout.fragment_product_category_list, container, false);
        ToolBarManager.getInstance().hideToolBar(mActivity, false);
        ToolBarManager.getInstance().setHeaderTitle("");
        mActivity.isToggleButtonEnabled(false);
        mProductCategoryRecyclerView = mContentView.findViewById(R.id.productCategoryRecycler);
        mProductAdapter = new ProductsAdapter();
        StaggeredGridLayoutManager gridLayoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        mProductCategoryRecyclerView.setLayoutManager(gridLayoutManager);
        mProductCategoryRecyclerView.setAdapter(mProductAdapter);
        //fetchAllProductsServerCall("", "");
        return mContentView;
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

    private class ProductsAdapter extends RecyclerView.Adapter<ProductsAdapter.RecyclerViewHolder> {

       // private List<ProductResponse.ProductItem> productList = new ArrayList<>();

        /*public void setProductList(List<ProductResponse.ProductItem> productList) {
            this.productList = productList;
        }*/

        @NonNull
        @Override
        public ProductsAdapter.RecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.product_category_item, parent, false);
            return new ProductsAdapter.RecyclerViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ProductsAdapter.RecyclerViewHolder holder, int position) {
           /* holder.offTextView.setVisibility(position == 1 ? View.VISIBLE : View.GONE);
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
            }*/
        }

        @Override
        public int getItemCount() {
            return /*productList.size()*/10;
        }

        private class RecyclerViewHolder extends RecyclerView.ViewHolder {

            private ImageView productImage;
            private TextView productSpecialPrice;
            private TextView productOriginalPrice;
            private TextView productName;
            private TextView productMeasure;
            private TextView offTextView;
            private ConstraintLayout mProductContainer;

            RecyclerViewHolder(@NonNull View itemView) {
                super(itemView);
                productName = itemView.findViewById(R.id.productName);
                mProductContainer = itemView.findViewById(R.id.productContainer);
                productMeasure = itemView.findViewById(R.id.productQuantity);
                productOriginalPrice = itemView.findViewById(R.id.textView40);
                productSpecialPrice = itemView.findViewById(R.id.productPrice);
                productImage = itemView.findViewById(R.id.productImage);
                ImageView addToCart = itemView.findViewById(R.id.addToCart);
                offTextView = itemView.findViewById(R.id.offTextView);
                /*mProductContainer.setOnClickListener(v -> {
                    CommonProductRequest request = new CommonProductRequest();
                    launchFragment(new ProductDetailFragment(request),true);
                });*/
          /*      productImage.setOnClickListener(v -> {
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
                });*/
            }
        }
    }

}
