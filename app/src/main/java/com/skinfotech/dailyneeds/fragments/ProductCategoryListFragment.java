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
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.skinfotech.dailyneeds.Constants;
import com.skinfotech.dailyneeds.HomeActivity;
import com.skinfotech.dailyneeds.R;
import com.skinfotech.dailyneeds.Utility;
import com.skinfotech.dailyneeds.models.requests.CommonProductRequest;
import com.skinfotech.dailyneeds.models.requests.CommonRequest;
import com.skinfotech.dailyneeds.models.responses.CommonDetailsResponse;
import com.skinfotech.dailyneeds.models.responses.InnerCategoryProduct;
import com.skinfotech.dailyneeds.retrofit.RetrofitApi;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;

import static android.content.ContentValues.TAG;
import static android.content.Context.MODE_PRIVATE;
import static com.skinfotech.dailyneeds.Constants.SHARED_PREF_NAME;
import static com.skinfotech.dailyneeds.Constants.USER_ID;

public class ProductCategoryListFragment extends BaseFragment {
    private InnerProductListAdapter innerProductListAdapter;
    private RecyclerView mProductCategoryRecyclerView;
    private List<InnerCategoryProduct> innerCategoryProducts = new ArrayList<>();

    public ProductCategoryListFragment(List<InnerCategoryProduct> innerCategoryProducts) {
        this.innerCategoryProducts = innerCategoryProducts;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mContentView = inflater.inflate(R.layout.fragment_product_category_list, container, false);
        if (Utility.isEmpty(innerCategoryProducts)) {
            mContentView.findViewById(R.id.noItemFoundContainer).setVisibility(View.VISIBLE);
        } else {
            mContentView.findViewById(R.id.noItemFoundContainer).setVisibility(View.GONE);
        }
        mProductCategoryRecyclerView = mContentView.findViewById(R.id.productCategoryRecycler);
        innerProductListAdapter = new InnerProductListAdapter(mActivity,innerCategoryProducts);
        StaggeredGridLayoutManager gridLayoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        mProductCategoryRecyclerView.setLayoutManager(gridLayoutManager);
        mProductCategoryRecyclerView.setAdapter(innerProductListAdapter);
        fetchCommonDetailsServerCall();
        return mContentView;
    }
    @Override
    protected void cartAddedSuccessCallBack() {updateOnUiThread(this::fetchCommonDetailsServerCall);

    }

    private void fetchCommonDetailsServerCall() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Call<CommonDetailsResponse> call = RetrofitApi.getAppServicesObject().fetchCartCount(new CommonRequest(getStringDataFromSharedPref(USER_ID)));
                    final Response<CommonDetailsResponse> response = call.execute();
                    updateOnUiThread(() -> handleResponse(response));
                } catch (Exception e) {
                    stopProgress();
                    showToast(e.getMessage());
                    Log.e(TAG, e.getMessage(), e);
                }
            }

            private void handleResponse(Response<CommonDetailsResponse> response) {
                if (response.isSuccessful()) {
                    CommonDetailsResponse commonDetailsResponse = response.body();
                    if (commonDetailsResponse != null) {
                        if (Constants.SUCCESS.equalsIgnoreCase(commonDetailsResponse.getErrorCode())) {
                            mActivity.setCartCount(commonDetailsResponse.getCartCount());

                        }
                    }
                }
                stopProgress();
            }
        }).start();
    }

    private class InnerProductListAdapter extends RecyclerView.Adapter<InnerProductListAdapter.InnerProductListViewHolder> {

        private HomeActivity mActivity;
        private List<InnerCategoryProduct> innerCategoryProducts = new ArrayList<>();

        public InnerProductListAdapter(HomeActivity mActivity, List<InnerCategoryProduct> innerCategoryProducts) {
            this.mActivity = mActivity;
            this.innerCategoryProducts = innerCategoryProducts;
        }

        @NonNull
        @Override
        public InnerProductListAdapter.InnerProductListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.product_category_item, parent, false);
            return new InnerProductListViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull InnerProductListAdapter.InnerProductListViewHolder holder, int position) {
            InnerCategoryProduct innerCategoryProduct = innerCategoryProducts.get(position);

            holder.productDiscount.setText(innerCategoryProduct.getmProductDiscount() + getString(R.string.discount_percentage));
            if (innerCategoryProduct.getmProductImage() != null)
                Picasso.get().load(innerCategoryProduct.getmProductImage()).placeholder(R.drawable.app_logo).into(holder.productImage);
            holder.productSellingPrice.setText(Utility.getAmountInCurrencyFormat(innerCategoryProduct.getmProductSpecialPrice()));
            holder.productMRP.setText(Utility.getAmountInCurrencyFormat(innerCategoryProduct.getmProductMRP()));
            holder.productName.setText(innerCategoryProduct.getmProductName());
            holder.productMeasure.setText(innerCategoryProduct.getmProductMeasure());
            holder.productContainer.setOnClickListener(v -> {
                CommonProductRequest request = new CommonProductRequest();
                request.setUserId(mActivity.getSharedPreferences(SHARED_PREF_NAME, MODE_PRIVATE).getString(USER_ID, ""));
                request.setProductsId(innerCategoryProduct.getmProductId());
                request.setProductsSizeId(innerCategoryProduct.getProductMeasureId());
                mActivity.launchFragment(new ProductDetailFragment(request), true);
            });
        }

        @Override
        public int getItemCount() {
            return innerCategoryProducts.size();
        }

        private class InnerProductListViewHolder extends RecyclerView.ViewHolder {

            private TextView productDiscount;
            private ImageView productImage;
            private TextView productSellingPrice;
            private TextView productMRP;
            private TextView productName;
            private TextView productMeasure;
            private ImageView productAddToCart;
            private ConstraintLayout productContainer;

            public InnerProductListViewHolder(@NonNull View itemView) {
                super(itemView);
                productDiscount = itemView.findViewById(R.id.textView9);
                productImage = itemView.findViewById(R.id.productImage);
                productSellingPrice = itemView.findViewById(R.id.productPrice);
                productMRP = itemView.findViewById(R.id.textView40);
                productName = itemView.findViewById(R.id.productName);
                productMeasure = itemView.findViewById(R.id.productQuantity);
                productAddToCart = itemView.findViewById(R.id.addToCart);
                productContainer = itemView.findViewById(R.id.productContainer);
                productAddToCart.setOnClickListener(view -> {
                    InnerCategoryProduct item = innerCategoryProducts.get(getAdapterPosition());
                    CommonProductRequest request = new CommonProductRequest();
                    request.setUserId(getStringDataFromSharedPref(USER_ID));
                    request.setProductsId(item.getmProductId());
                    request.setProductsQuantity(String.valueOf(item.getCount()));
                    request.setProductsSizeId(item.getProductMeasureId());
                    addToCartServerCall(request);
                });
            }
        }
    }
}
