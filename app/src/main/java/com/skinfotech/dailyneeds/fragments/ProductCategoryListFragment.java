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
import com.skinfotech.dailyneeds.adapters.InnerProductListAdapter;
import com.skinfotech.dailyneeds.constant.ToolBarManager;
import com.skinfotech.dailyneeds.models.requests.AllProductRequest;
import com.skinfotech.dailyneeds.models.requests.CommonProductRequest;
import com.skinfotech.dailyneeds.models.responses.InnerCategoryProduct;
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
        ToolBarManager.getInstance().hideToolBar(mActivity, false);
        ToolBarManager.getInstance().setHeaderTitle("");
        mActivity.isToggleButtonEnabled(false);
        mProductCategoryRecyclerView = mContentView.findViewById(R.id.productCategoryRecycler);
        innerProductListAdapter = new InnerProductListAdapter(mActivity,innerCategoryProducts);
        StaggeredGridLayoutManager gridLayoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        mProductCategoryRecyclerView.setLayoutManager(gridLayoutManager);
        mProductCategoryRecyclerView.setAdapter(innerProductListAdapter);
        return mContentView;
    }

}
