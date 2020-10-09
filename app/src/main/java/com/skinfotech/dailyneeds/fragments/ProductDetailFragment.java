package com.skinfotech.dailyneeds.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.skinfotech.dailyneeds.R;
import com.skinfotech.dailyneeds.Utility;
import com.skinfotech.dailyneeds.constant.ToolBarManager;
import com.skinfotech.dailyneeds.models.requests.CommonProductRequest;
import com.skinfotech.dailyneeds.models.responses.CommonResponse;
import com.skinfotech.dailyneeds.models.responses.ProductDetailResponse;
import com.skinfotech.dailyneeds.retrofit.RetrofitApi;
import com.squareup.picasso.Picasso;
import retrofit2.Call;
import retrofit2.Response;

import java.util.ArrayList;
import java.util.List;

public class ProductDetailFragment extends BaseFragment {

    private TextView mQuantityTextView;
    private TextView mProductPrice;
    private CommonProductRequest mCommonProductRequest = new CommonProductRequest();
    private ProductQuantityListAdapter mProductQuantityListAdapter;
    private int mSelectedSizeListItem;
    private TextView productOriginalAmount;
    private TextView productSpecialAmount;
    private ProductDetailResponse productDetailResponse;
    private static final String TAG = "Product Detail";
    private TextView mSaveAmount;

    public ProductDetailFragment(CommonProductRequest commonProductRequest) {
        mCommonProductRequest = commonProductRequest;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mContentView = inflater.inflate(R.layout.fragment_product_detail, container, false);
        setupUI();
        if (mCommonProductRequest != null) {
            productDetailServerCall(mCommonProductRequest);
        }
        return mContentView;
    }

    private void setupUI() {
        ToolBarManager.getInstance().hideToolBar(mActivity, false);
        ToolBarManager.getInstance().setHeaderTitle(TAG);
        mActivity.isToggleButtonEnabled(false);
        ToolBarManager.getInstance().onBackPressed(this);
        mQuantityTextView = mContentView.findViewById(R.id.quantityValue);
        mProductPrice = mContentView.findViewById(R.id.productAmount);
        RecyclerView recyclerView = mContentView.findViewById(R.id.productQuantityRecyclerView);
        mProductQuantityListAdapter = new ProductQuantityListAdapter();
        LinearLayoutManager layoutManager = new LinearLayoutManager(mActivity, LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(mProductQuantityListAdapter);
    }

    private void productDetailServerCall(CommonProductRequest request) {
        showProgress();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Call<ProductDetailResponse> call = RetrofitApi.getAppServicesObject().productDetail(request);
                    final Response<ProductDetailResponse> response = call.execute();
                    updateOnUiThread(() -> handleResponse(response));
                } catch (Exception e) {
                    stopProgress();
                    showToast(e.getMessage());
                }
            }

            private void handleResponse(Response<ProductDetailResponse> response) {
                if (response.isSuccessful()) {
                    productDetailResponse = response.body();
                    if (productDetailResponse != null) {
                        setupUIFromResponse(productDetailResponse);
                    }
                }
                stopProgress();
            }
        }).start();
    }

    private void setupUIFromResponse(ProductDetailResponse response) {
        ImageView productImage = mContentView.findViewById(R.id.productImage);
        TextView productName = mContentView.findViewById(R.id.productName);
        productOriginalAmount = mContentView.findViewById(R.id.productOriginalAmount);
        productSpecialAmount = mContentView.findViewById(R.id.productAmount);
        mSaveAmount = mContentView.findViewById(R.id.percentOFFTextView);
        TextView productDescription = mContentView.findViewById(R.id.productDescription);
        if (!Utility.isEmpty(response.getProductImage())) {
            Picasso.get().load(response.getProductImage()).placeholder(R.drawable.default_image).into(productImage);
        }
        productDescription.setText(response.getProductDescription());
        productName.setText(response.getProductName());
        productSpecialAmount.setText(Utility.getAmountInCurrencyFormat(response.getProductSpecialPrice()));
        productOriginalAmount.setText(Utility.getAmountInCurrencyFormat(response.getProductPrice()));
        mSaveAmount.setText(Utility.getAmountInCurrencyFormat(response.getProductDiscount())+"% OFF");
        List<ProductDetailResponse.SizeList> sizeList = response.getSizeList();
        if (!Utility.isEmpty(sizeList)) {
            mProductQuantityListAdapter.setSizeList(sizeList);
            mProductQuantityListAdapter.notifyDataSetChanged();
        }
        productImage.setOnClickListener(view -> showImageDialog(response.getProductImage()));
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.addToCart:
                String quantity = mQuantityTextView.getText().toString();
                mCommonProductRequest.setProductsQuantity(quantity);
                addToCartServerCall(mCommonProductRequest);
                break;
            case R.id.addQuantity:
                int counter = productDetailResponse.getCount();
                counter++;
                double originalPrice = counter * Double.parseDouble(productDetailResponse.getProductPrice());
                double discountPrice = counter * Double.parseDouble(productDetailResponse.getProductSpecialPrice());
                productSpecialAmount.setText(Utility.getAmountInCurrencyFormat(String.valueOf(discountPrice)));
                productOriginalAmount.setText(Utility.getAmountInCurrencyFormat(String.valueOf(originalPrice)));
                mQuantityTextView.setText(String.valueOf(counter));
                float difference = (float) (originalPrice - discountPrice);
                mSaveAmount.setText(String.valueOf(difference));
                productDetailResponse.setCount(counter);
                break;
            case R.id.minusQuantity:
                counter = productDetailResponse.getCount();
                if (counter == 1) {
                    return;
                }
                counter--;
                originalPrice = counter * Double.parseDouble(productDetailResponse.getProductPrice());
                discountPrice = counter * Double.parseDouble(productDetailResponse.getProductSpecialPrice());
                productSpecialAmount.setText(Utility.getAmountInCurrencyFormat(String.valueOf(discountPrice)));
                productOriginalAmount.setText(Utility.getAmountInCurrencyFormat(String.valueOf(originalPrice)));
                mQuantityTextView.setText(String.valueOf(counter));
                double difference1 = originalPrice - discountPrice;
                mSaveAmount.setText(String.valueOf(difference1));
                productDetailResponse.setCount(counter);
                break;
        }
    }


    @Override
    public void onStart() {
        super.onStart();
        mActivity.showBackButton();
        mActivity.showCartIcon();
        mActivity.hideSearchIcon();
    }

    private class ProductQuantityListAdapter extends RecyclerView.Adapter<ProductQuantityListAdapter.RecyclerViewHolder> implements View.OnClickListener {

        private List<ProductDetailResponse.SizeList> mSizeList = new ArrayList<>();

        public void setSizeList(List<ProductDetailResponse.SizeList> sizeList) {
            mSizeList = sizeList;
        }

        @NonNull
        @Override
        public ProductQuantityListAdapter.RecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.quantity_selector_list, parent, false);
            return new RecyclerViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ProductQuantityListAdapter.RecyclerViewHolder holder, int position) {
            ProductDetailResponse.SizeList sizeItem = mSizeList.get(position);
            String measureStr = sizeItem.getSizeListMeasure().concat(" ").concat(sizeItem.getSizeListUnit());
            holder.mProductQuantity.setText(measureStr);
            holder.mProductQuantity.setBackgroundResource(mSelectedSizeListItem == position ? R.drawable.quantity_border : R.drawable.quantity_border_grey);
        }

        @Override
        public int getItemCount() {
            return mSizeList.size();
        }

        @Override
        public void onClick(View v) {
        }

        private class RecyclerViewHolder extends RecyclerView.ViewHolder {

            private TextView mProductQuantity;

            RecyclerViewHolder(@NonNull View itemView) {
                super(itemView);
                mProductQuantity = itemView.findViewById(R.id.productQuantity);
                mProductQuantity.setOnClickListener(v -> {
                    mSelectedSizeListItem = getAdapterPosition();
                    ProductDetailResponse.SizeList item = mSizeList.get(mSelectedSizeListItem);
                    mCommonProductRequest.setProductsSizeId(item.getSizeListPUId());
                    productDetailByUnitIdServerCall(mCommonProductRequest);
                });
            }
        }
    }

    private void productDetailByUnitIdServerCall(CommonProductRequest request) {
        showProgress();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Call<ProductDetailResponse> call = RetrofitApi.getAppServicesObject().productDetailByUnitId(request);
                    final Response<ProductDetailResponse> response = call.execute();
                    updateOnUiThread(() -> handleResponse(response));
                } catch (Exception e) {
                    stopProgress();
                    showToast(e.getMessage());
                }
            }

            private void handleResponse(Response<ProductDetailResponse> response) {
                if (response.isSuccessful()) {
                    ProductDetailResponse productDetailResponse = response.body();
                    if (productDetailResponse != null) {
                        setupUIFromResponse(productDetailResponse);
                    }
                }
                stopProgress();
            }
        }).start();
    }
}
