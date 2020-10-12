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
import com.skinfotech.dailyneeds.models.requests.ProductLabelsRequest;
import com.skinfotech.dailyneeds.models.requests.SubCategoryRequest;
import com.skinfotech.dailyneeds.models.responses.CategoryResponse;
import com.skinfotech.dailyneeds.models.responses.ProductResponse;
import com.skinfotech.dailyneeds.models.responses.ProductsLabels;
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
        setupUI();
        return view;
    }

    @Override
    public void onClick(View view) {
//        YOUR VIEWS ONCLICK
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
                    if(productsLabels != null) {
                        List<ProductsLabels.CategoryLabels> categoryLabels = productsLabels.getCategoryLabels();
                        myProductCategoryPagerAdapter.setCategoryLabelsList(categoryLabels);
                        myProductCategoryPagerAdapter.notifyDataSetChanged();
                    }
                }
                stopProgress();
            }
        }).start();
    }
}
