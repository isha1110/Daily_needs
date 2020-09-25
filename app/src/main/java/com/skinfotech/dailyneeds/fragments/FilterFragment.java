package com.skinfotech.dailyneeds.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.skinfotech.dailyneeds.Constants;
import com.skinfotech.dailyneeds.R;
import com.skinfotech.dailyneeds.Utility;
import com.skinfotech.dailyneeds.constant.ToolBarManager;
import com.skinfotech.dailyneeds.models.requests.AllProductRequest;
import com.skinfotech.dailyneeds.models.responses.BrandListResponse;
import com.skinfotech.dailyneeds.models.responses.ProductResponse;
import com.skinfotech.dailyneeds.retrofit.RetrofitApi;
import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Response;

public class FilterFragment extends BaseFragment implements RadioGroup.OnCheckedChangeListener {
    private BrandNameListAdapter mBrandNameListAdapter;
    private RecyclerView mBrandNameListRecycler;
    private RadioGroup radioGroup;
    private String mSelectedPriceStr = "";
    private RadioButton lowHighRadioButton;
    private RadioButton highLowRadioButton;
    private TextView brandTextView;
    private TextView priceTextView;
    public TextView clearAllTextView;
    private static final String TAG = "FilterFragment";
    // private String mSelectedBrandId = "";
    private ArrayList<String> mBrandList = new ArrayList<String>();
    private List<ProductResponse.ProductItem> filterProductList = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mContentView = inflater.inflate(R.layout.fragment_filter, container, false);
        ToolBarManager.getInstance().hideToolBar(mActivity, true);
        ToolBarManager.getInstance().setHeaderTitle("");
        mActivity.isToggleButtonEnabled(false);
        clearAllTextView = mContentView.findViewById(R.id.cancelTextView);
        fetchBrandServerCall(sCategoryParentId, sSubCategoryParentId, sSubSubCategoryParentId);
        setupUI();
        return mContentView;
    }

    @Override
    public void onStart() {
        super.onStart();
        mActivity.hideBackButton();
        mActivity.hideCartIcon();
    }

    private void setupUI() {
        mBrandNameListRecycler = mContentView.findViewById(R.id.brandNameListRecyclerView);
        mBrandNameListAdapter = new BrandNameListAdapter();
        LinearLayoutManager layoutManager = new LinearLayoutManager(mActivity);
        mBrandNameListRecycler.setLayoutManager(layoutManager);
        mBrandNameListRecycler.setAdapter(mBrandNameListAdapter);
        radioGroup = mContentView.findViewById(R.id.radioGroup);
        lowHighRadioButton = mContentView.findViewById(R.id.lowHighRadioButton);
        highLowRadioButton = mContentView.findViewById(R.id.highLowRadioButton);
        priceTextView = mContentView.findViewById(R.id.priceTextView);
        brandTextView = mContentView.findViewById(R.id.brandTextView);
        radioGroup.setOnCheckedChangeListener(this);
    }

    private void fetchBrandServerCall(String categoryId, String subCategoryId, String subSubCategoryId) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    AllProductRequest request = new AllProductRequest();
                    request.setUserId(getStringDataFromSharedPref(Constants.USER_ID));
                    request.setCategoryId(categoryId);
                    request.setSubCategoryId(subCategoryId);
                    request.setSubSubCategoryId(subSubCategoryId);
                    Call<BrandListResponse> call = RetrofitApi.getAppServicesObject().fetchBrandList(request);
                    final Response<BrandListResponse> response = call.execute();
                    updateOnUiThread(() -> handleResponse(response));
                } catch (Exception e) {
                    stopProgress();
                    showToast(e.getMessage());
                    Log.e(TAG, e.getMessage(), e);
                }
            }

            private void handleResponse(Response<BrandListResponse> response) {
                if (response.isSuccessful()) {
                    BrandListResponse brandListResponse = response.body();
                    if (brandListResponse != null) {
                        if (Constants.SUCCESS.equalsIgnoreCase(brandListResponse.getErrorCode())) {
                            List<BrandListResponse.BrandItem> brandList = brandListResponse.getBrandList();
                            if (!Utility.isEmpty(brandList)) {
                                mBrandNameListAdapter.setBrandList(brandList);
                                mBrandNameListAdapter.notifyDataSetChanged();
                            }
                        }
                    }
                }
                stopProgress();
            }
        }).start();
    }

    private void saveApplyChangesServerCall(String categoryId, String subCategoryId, String subSubCategoryId, ArrayList<String> brandId, String mode) {
        showProgress();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    AllProductRequest request = new AllProductRequest();
                    request.setUserId(getStringDataFromSharedPref(Constants.USER_ID));
                    request.setCategoryId(categoryId);
                    request.setSubCategoryId(subCategoryId);
                    request.setSubSubCategoryId(subSubCategoryId);
                    request.setBrandId(brandId);
                    request.setMode(mode);
                    Call<ProductResponse> call = RetrofitApi.getAppServicesObject().saveApplyChanges(request);
                    final Response<ProductResponse> response = call.execute();
                    updateOnUiThread(() -> handleResponse(response));
                } catch (Exception e) {
                    stopProgress();
                    Log.e(TAG, e.getMessage(), e);
                }
            }

            private void handleResponse(Response<ProductResponse> response) {
                stopProgress();
                if (response.isSuccessful()) {
                    ProductResponse productResponse = response.body();
                    if (productResponse != null) {
                        if (Constants.SUCCESS.equalsIgnoreCase(productResponse.getErrorCode())) {
                            filterProductList = productResponse.getProductList();
                            if (Utility.isEmpty(filterProductList)) {
                                showToast(getString(R.string.no_item_available));
                                return;
                            }
                            launchFragment(new SearchFragment(filterProductList, true), true);
                        }
                    }
                }
            }
        }).start();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.brandTextView:
                priceTextView.setBackgroundColor(ContextCompat.getColor(mActivity, R.color.off_white));
                brandTextView.setBackgroundColor(ContextCompat.getColor(mActivity, R.color.colorWhite));
                mBrandNameListRecycler.setVisibility(View.VISIBLE);
                radioGroup.setVisibility(View.GONE);
                fetchBrandServerCall(sCategoryParentId, sSubCategoryParentId, sSubSubCategoryParentId);
                break;
            case R.id.priceTextView:
                priceTextView.setBackgroundColor(ContextCompat.getColor(mActivity, R.color.colorWhite));
                brandTextView.setBackgroundColor(ContextCompat.getColor(mActivity, R.color.off_white));
                mBrandNameListRecycler.setVisibility(View.GONE);
                radioGroup.setVisibility(View.VISIBLE);
                break;
            case R.id.doneTextView:
                saveApplyChangesServerCall(sCategoryParentId, sSubCategoryParentId, sSubSubCategoryParentId, mBrandList, mSelectedPriceStr);
                break;
            case R.id.cancelTextView:
                int position=0;
                if (Utility.isNotEmpty(mBrandList)) {
                   mBrandNameListAdapter.notifyItemRemoved(position);
                    mBrandNameListAdapter.notifyDataSetChanged();
              }
                radioGroup.clearCheck();
                break;
        }
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        switch (checkedId) {
            case R.id.lowHighRadioButton:
                //mSelectedPriceStr = lowHighRadioButton.getText().toString();
                mSelectedPriceStr = "lowToHigh";
                break;
            case R.id.highLowRadioButton:
                //mSelectedPriceStr = highLowRadioButton.getText().toString();
                mSelectedPriceStr = "highToLow";
                break;
        }
    }

    private class BrandNameListAdapter extends RecyclerView.Adapter<BrandNameListAdapter.RecyclerViewHolder> {
        private List<BrandListResponse.BrandItem> brandList = new ArrayList<>();

        void setBrandList(List<BrandListResponse.BrandItem> brandList) {
            this.brandList = brandList;
        }

        @NonNull
        @Override
        public BrandNameListAdapter.RecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.brand_item_list, parent, false);
            return new RecyclerViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull BrandNameListAdapter.RecyclerViewHolder holder, int position) {
            final BrandListResponse.BrandItem item = brandList.get(position);
            holder.mSelectBrandCheckBox.setText(item.getBrandName());
            holder.mSelectBrandCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (isChecked) {
                    mBrandList.add(item.getBrandId());
                    holder.mSelectBrandCheckBox.setChecked(true);
                } else {
                    mBrandList.remove(item.getBrandId());
                    holder.mSelectBrandCheckBox.setChecked(false);
                }
            });
        }

        @Override
        public int getItemCount() {
            return brandList.size();
        }

        private class RecyclerViewHolder extends RecyclerView.ViewHolder {

            private CheckBox mSelectBrandCheckBox;

            RecyclerViewHolder(@NonNull View itemView) {
                super(itemView);
                mSelectBrandCheckBox = itemView.findViewById(R.id.selectBrandCheckBox);
            }
        }

    }
}
