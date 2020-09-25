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
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.skinfotech.dailyneeds.Constants;
import com.skinfotech.dailyneeds.R;
import com.skinfotech.dailyneeds.Utility;
import com.skinfotech.dailyneeds.constant.ToolBarManager;
import com.skinfotech.dailyneeds.models.requests.CommonProductRequest;
import com.skinfotech.dailyneeds.models.requests.CommonRequest;
import com.skinfotech.dailyneeds.models.responses.CommonResponse;
import com.skinfotech.dailyneeds.models.responses.ProductResponse;
import com.skinfotech.dailyneeds.retrofit.RetrofitApi;
import com.squareup.picasso.Picasso;
import retrofit2.Call;
import retrofit2.Response;

import java.util.ArrayList;
import java.util.List;

import static com.skinfotech.dailyneeds.Constants.USER_ID;

public class MyWishListFragment extends BaseFragment {

    private static final String TAG = "MyWishListFragment";
    private WishListItemAdapter wishListAdapter;
    private View noItemFoundContainer;
    private RecyclerView WishlistRecyclerView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mContentView = inflater.inflate(R.layout.fragment_wishlist, container, false);
        setupUI();
        showProgress();
        return mContentView;
    }

    private void setupUI() {
        ToolBarManager.getInstance().hideToolBar(mActivity, false);
        ToolBarManager.getInstance().setHeaderTitle("My WishList");
        mActivity.isToggleButtonEnabled(true);
        WishlistRecyclerView = mContentView.findViewById(R.id.WishlistRecyclerView);
        noItemFoundContainer = mContentView.findViewById(R.id.noItemFoundContainer);
        wishListAdapter = new WishListItemAdapter();
        GridLayoutManager layoutManager = new GridLayoutManager(mActivity, 2);
        WishlistRecyclerView.setLayoutManager(layoutManager);
        WishlistRecyclerView.setAdapter(wishListAdapter);
        fetchWishListServerCall();
    }

    @Override
    protected void onWishListSuccessServerCall(CommonResponse commonResponse) {
        fetchWishListServerCall();
    }

    private void fetchWishListServerCall() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Call<ProductResponse> call = RetrofitApi.getAppServicesObject().fetchWishList(new CommonRequest(getStringDataFromSharedPref(Constants.USER_ID)));
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
                                WishlistRecyclerView.setVisibility(View.VISIBLE);
                                noItemFoundContainer.setVisibility(View.GONE);
                                wishListAdapter.setProductList(productList);
                            } else {
                                wishListAdapter.setProductList(new ArrayList<>());
                                noItemFoundContainer.setVisibility(View.VISIBLE);
                                WishlistRecyclerView.setVisibility(View.GONE);
                            }
                            wishListAdapter.notifyDataSetChanged();
                        }
                    }
                }
                stopProgress();
            }
        }).start();
    }

    @Override
    public void onStart() {
        super.onStart();
        mActivity.hideBackButton();
        mActivity.hideCartIcon();
        hideKeyboard();
        mActivity.hideFilterIcon();
    }

    class WishListItemAdapter extends RecyclerView.Adapter<WishListItemAdapter.RecyclerViewHolder> {

        private List<ProductResponse.ProductItem> productList = new ArrayList<>();

        public void setProductList(List<ProductResponse.ProductItem> productList) {
            this.productList = productList;
        }

        @NonNull
        @Override
        public WishListItemAdapter.RecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.wishlist_item, parent, false);
            return new RecyclerViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull WishListItemAdapter.RecyclerViewHolder holder, int position) {
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
            private ImageView mAddToCart;
            private ImageView productImage;
            private TextView productSpecialPrice;
            private TextView productOriginalPrice;
            private TextView productName;
            private TextView productMeasure;
            private ConstraintLayout mProductContainer;

            RecyclerViewHolder(@NonNull View itemView) {
                super(itemView);
                productName = itemView.findViewById(R.id.productName);
                productMeasure = itemView.findViewById(R.id.productQuantity);
                productOriginalPrice = itemView.findViewById(R.id.textView40);
                productSpecialPrice = itemView.findViewById(R.id.productPrice);
                productImage = itemView.findViewById(R.id.productImage);
                wishlistImageView = itemView.findViewById(R.id.wishlistIcon);
                mAddToCart = itemView.findViewById(R.id.addToCart);
                mProductContainer = itemView.findViewById(R.id.productContainer);
                mProductContainer.setOnClickListener(v -> {
                    ProductResponse.ProductItem item = productList.get(getAdapterPosition());
                    CommonProductRequest request = new CommonProductRequest();
                    request.setUserId(getStringDataFromSharedPref(USER_ID));
                    request.setProductsId(item.getProductId());
                    request.setProductsSizeId(item.getProductPUId());
                    launchFragment(new ProductDetailFragment(request), true);
                });
                wishlistImageView.setOnClickListener(v -> {
                    ProductResponse.ProductItem item = productList.get(getAdapterPosition());
                    CommonProductRequest request = new CommonProductRequest();
                    request.setUserId(getStringDataFromSharedPref(Constants.USER_ID));
                    request.setProductsSizeId(item.getProductPUId());
                    request.setProductsQuantity(String.valueOf(item.getCount()));
                    request.setProductsId(item.getProductId());
                    wishListServerCall(request);
                });
                mAddToCart.setOnClickListener(v -> {
                    ProductResponse.ProductItem item = productList.get(getAdapterPosition());
                    CommonProductRequest request = new CommonProductRequest();
                    request.setUserId(getStringDataFromSharedPref(Constants.USER_ID));
                    request.setProductsSizeId(item.getProductPUId());
                    request.setProductsQuantity(String.valueOf(item.getCount()));
                    request.setProductsId(item.getProductId());
                    addToCartServerCall(request);
                });
            }
        }
    }
}
