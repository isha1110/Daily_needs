package com.skinfotech.dailyneeds.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.skinfotech.dailyneeds.MyApplication;
import com.skinfotech.dailyneeds.R;
import com.skinfotech.dailyneeds.adapters.MyProductCategoryPagerAdapter;
import com.skinfotech.dailyneeds.constant.ToolBarManager;
import com.skinfotech.dailyneeds.models.requests.ProductLabelsRequest;
import com.skinfotech.dailyneeds.models.responses.ProductsLabels;
import com.skinfotech.dailyneeds.retrofit.RetrofitApi;

import java.util.List;

import retrofit2.Call;
import retrofit2.Response;

public class ProductCategoryFragment extends BaseFragment {

    private View view;
    private static final String TAG = "ProductCategoryFragment";

    private TabLayout categoriesTabs;
    private ViewPager productCategoriesPager;


    //Integration of Dynamic Categories
    private String modeID;
    private String mode;
    private MyProductCategoryPagerAdapter myProductCategoryPagerAdapter;

    public ProductCategoryFragment(String catId, String mode) {
        this.modeID = catId;
        this.mode = mode;
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
        return view;
    }



    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupUI();
    }

    private void setupUI() {
        productCategoriesPager = view.findViewById(R.id.productCategoryViewPager);
        categoriesTabs = view.findViewById(R.id.categoryTabLayout);
        myProductCategoryPagerAdapter = new MyProductCategoryPagerAdapter(mActivity.getSupportFragmentManager());
        productCategoriesPager.setAdapter(myProductCategoryPagerAdapter);
        categoriesTabs.setupWithViewPager(productCategoriesPager);
        fetchLabelData();
    }

    @Override
    public void onStart() {
        super.onStart();
        mActivity.showBackButton();
        mActivity.showCartIcon();
        mActivity.hideSearchIcon();
    }

    private void fetchLabelData() {
        showProgress();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    ProductLabelsRequest productLabelsRequest = new ProductLabelsRequest(
                            mode,
                            modeID
                    );
                    Call<ProductsLabels> call = RetrofitApi.getAppServicesObject().getLabelsNproducts(productLabelsRequest);
                    final Response<ProductsLabels> response = call.execute();
                    updateOnUiThread(() -> handleResponse(response));
                } catch (Exception e) {
                    stopProgress();
                    showToast(e.getMessage());
                    Log.e(TAG, e.getMessage(), e);
                }
            }

            private void handleResponse(Response<ProductsLabels> response) {
                if (response.isSuccessful()) {
                    ProductsLabels productsLabels = response.body();
                    if (productsLabels != null) {
                        List<ProductsLabels.CategoryLabels> categoryLabels = productsLabels.getCategoryLabels();
                        setupUIWithData(categoryLabels);
                    }
                }
                stopProgress();
            }
        }).start();
    }



    private void setupUIWithData(List<ProductsLabels.CategoryLabels> categoryLabels) {
        productCategoriesPager = view.findViewById(R.id.productCategoryViewPager);
        categoriesTabs = view.findViewById(R.id.categoryTabLayout);
        myProductCategoryPagerAdapter = new MyProductCategoryPagerAdapter(mActivity.getSupportFragmentManager());
        myProductCategoryPagerAdapter.setCategoryLabelsList(categoryLabels);
        productCategoriesPager.setAdapter(myProductCategoryPagerAdapter);
        categoriesTabs.setupWithViewPager(productCategoriesPager);
        myProductCategoryPagerAdapter.notifyDataSetChanged();
        productCategoriesPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                MyApplication.selectedPosition = position;
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        productCategoriesPager.setCurrentItem(MyApplication.selectedPosition);
    }
}
