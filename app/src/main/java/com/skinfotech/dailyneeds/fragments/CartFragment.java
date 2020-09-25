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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.skinfotech.dailyneeds.Constants;
import com.skinfotech.dailyneeds.R;
import com.skinfotech.dailyneeds.Utility;
import com.skinfotech.dailyneeds.constant.ToolBarManager;
import com.skinfotech.dailyneeds.models.requests.CommonProductRequest;
import com.skinfotech.dailyneeds.models.requests.CommonRequest;
import com.skinfotech.dailyneeds.models.responses.ProductResponse;
import com.skinfotech.dailyneeds.retrofit.RetrofitApi;
import com.squareup.picasso.Picasso;
import retrofit2.Call;
import retrofit2.Response;

import java.util.ArrayList;
import java.util.List;

import static com.skinfotech.dailyneeds.Constants.USER_ID;

public class CartFragment extends BaseFragment {

    private CartItemListAdapter mCartItemListAdapter;
    private static final String TAG = "CartFragment";
    private View noItemFoundContainer;
    private View cartGroup;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mContentView = inflater.inflate(R.layout.fragment_cart, container, false);
        setupUI();
        return mContentView;
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.checkoutButton) {
            launchFragment(new CheckOutFragment(), true);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        mActivity.hideBackButton();
        mActivity.hideCartIcon();
        mActivity.hideFilterIcon();
    }

    private void setupUI() {
        ToolBarManager.getInstance().hideToolBar(mActivity, false);
        ToolBarManager.getInstance().setHeaderTitle(mActivity.getString(R.string.cart));
        mActivity.isToggleButtonEnabled(true);
        RecyclerView recyclerView = mContentView.findViewById(R.id.cartItemRecycler);
        cartGroup = mContentView.findViewById(R.id.cartGroup);
        noItemFoundContainer = mContentView.findViewById(R.id.noItemFoundContainer);
        mCartItemListAdapter = new CartItemListAdapter();
        recyclerView.setLayoutManager(new LinearLayoutManager(mActivity));
        recyclerView.setAdapter(mCartItemListAdapter);
        fetchCartServerCall();
    }

    private void fetchCartServerCall() {
        showProgress();
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

            private void handleResponse(Response<ProductResponse> response) {
                if (response.isSuccessful()) {
                    ProductResponse categoryResponse = response.body();
                    if (categoryResponse != null) {
                        if (Constants.SUCCESS.equalsIgnoreCase(categoryResponse.getErrorCode())) {
                            List<ProductResponse.ProductItem> productList = categoryResponse.getProductList();
                            if (!Utility.isEmpty(productList)) {
                                noItemFoundContainer.setVisibility(View.GONE);
                                cartGroup.setVisibility(View.VISIBLE);
                                mCartItemListAdapter.setProductList(productList);
                                mCartItemListAdapter.notifyDataSetChanged();
                            } else {
                                noItemFoundContainer.setVisibility(View.VISIBLE);
                                cartGroup.setVisibility(View.GONE);
                                mCartItemListAdapter.setProductList(new ArrayList<>());
                            }
                            mCartItemListAdapter.notifyDataSetChanged();
                        }
                    }
                }
                stopProgress();
            }
        }).start();
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
            return new RecyclerViewHolder(view);
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
            holder.mQuantity.setText(item.getProductQuantity() + " quantity");
            double diff = Double.parseDouble(item.getProductPrice()) - Double.parseDouble(item.getProductSpecialPrice());
            holder.saveAmount.setText(String.valueOf(diff));
        }

        @Override
        public int getItemCount() {
            return productList.size();
        }

        class RecyclerViewHolder extends RecyclerView.ViewHolder {

            private ImageView mMinusQuantity;
            private ImageView mAddQuantity;
            private TextView mQuantity;
            private int counter = 1;
            private int counter1 = 250;
            private ImageView mCancelItem;
            private TextView mProductPrice;
            private TextView productOriginalPrice;
            private TextView saveAmount;
            private TextView measureTextView;
            private ImageView productImage;
            private TextView productName;

            @SuppressLint("SetTextI18n")
            RecyclerViewHolder(@NonNull View itemView) {
                super(itemView);
                measureTextView = itemView.findViewById(R.id.measureTextView);
                saveAmount = itemView.findViewById(R.id.amountSaved);
                productName = itemView.findViewById(R.id.productName);
                productOriginalPrice = itemView.findViewById(R.id.mrpPrice);
                productImage = itemView.findViewById(R.id.productImage);
                mMinusQuantity = itemView.findViewById(R.id.quantityMinus);
                mAddQuantity = itemView.findViewById(R.id.quantityAdd);
                mAddQuantity.setVisibility(View.GONE);
                mMinusQuantity.setVisibility(View.GONE);
                mQuantity = itemView.findViewById(R.id.productQuantity);
                mCancelItem = itemView.findViewById(R.id.cancleItem);
                mProductPrice = itemView.findViewById(R.id.amountPaid);
                mMinusQuantity.setOnClickListener(v -> {
                    counter--;
                    counter1 = counter1 - 250;
                    if (counter < 1) {
                        counter = 1;
                    }
                    if (counter1 < 250) {
                        counter1 = 250;
                    }
                    mProductPrice.setText(counter1 + "");
                    mQuantity.setText(counter + "");
                });
                mAddQuantity.setOnClickListener(v -> {
                    counter++;
                    counter1 = counter1 + 250;
                    mProductPrice.setText(counter1 + "");
                    mQuantity.setText(counter + "");
                });
                productImage.setOnClickListener(v -> {
                    ProductResponse.ProductItem item = productList.get(getAdapterPosition());
                    CommonProductRequest request = new CommonProductRequest();
                    request.setUserId(getStringDataFromSharedPref(USER_ID));
                    request.setProductsId(item.getProductId());
                    request.setProductsSizeId(item.getProductPUId());
                    launchFragment(new ProductDetailFragment(request), true);
                });
                mCancelItem.setOnClickListener(v -> {
                    ProductResponse.ProductItem item = productList.get(getAdapterPosition());
                    CommonProductRequest request = new CommonProductRequest();
                    request.setUserId(getStringDataFromSharedPref(USER_ID));
                    request.setProductsId(item.getProductId());
                    request.setProductsQuantity(String.valueOf(item.getCount()));
                    request.setProductsSizeId(item.getProductPUId());
                    removeFromCartServerCall(request);
                });
            }
        }
    }

    @Override
    protected void cartRemovedSuccessCallBack() {
        fetchCartServerCall();
    }
}
