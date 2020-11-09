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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.skinfotech.dailyneeds.Constants;
import com.skinfotech.dailyneeds.R;
import com.skinfotech.dailyneeds.Utility;
import com.skinfotech.dailyneeds.constant.ToolBarManager;
import com.skinfotech.dailyneeds.models.responses.CategoryResponse;
import com.skinfotech.dailyneeds.models.responses.SubCategoryProductItem;
import com.skinfotech.dailyneeds.retrofit.RetrofitApi;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;

import static android.content.ContentValues.TAG;

public class CategoryListFrgament extends BaseFragment  {
    private RecyclerView mCategoryRecyclerView;
    private CategoryAdapter categoryAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mContentView = inflater.inflate(R.layout.fragment_categories_list, container, false);
        setupUI();
        return mContentView;
    }

    private void setupUI() {
        ToolBarManager.getInstance().hideToolBar(mActivity, false);
        ToolBarManager.getInstance().setHeaderTitle(getString(R.string.all_categories));
        mActivity.isToggleButtonEnabled(false);
        ToolBarManager.getInstance().onBackPressed(this);
        mCategoryRecyclerView = mContentView.findViewById(R.id.subCategoryRecyclerView);
        categoryAdapter = new CategoryAdapter();
        mCategoryRecyclerView.setLayoutManager(new LinearLayoutManager(mActivity, LinearLayoutManager.VERTICAL, false));
        mCategoryRecyclerView.setAdapter(categoryAdapter);
        fetchCategoriesServerCall();
    }
    private void fetchCategoriesServerCall() {
        showProgress();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Call<CategoryResponse> call = RetrofitApi.getAppServicesObject().fetchCategories();
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
                            List<CategoryResponse.CategoryItem> categoryItemList = categoryResponse.getCategoryList();
                            if (!Utility.isEmpty(categoryItemList)) {
                                categoryAdapter.setCategoryItemList(categoryItemList);
                                categoryAdapter.notifyDataSetChanged();
                            }
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
        mActivity.showBackButton();
        hideKeyboard();
        mActivity.showCartIcon();
        mActivity.hideSearchIcon();
    }

    private class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.RecyclerViewHolder> {
        private List<CategoryResponse.CategoryItem> categoryItemList = new ArrayList<>();

        public void setCategoryItemList(List<CategoryResponse.CategoryItem> categoryItemList) {
            this.categoryItemList = categoryItemList;
        }

        @NonNull
        @Override
        public CategoryAdapter.RecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.header_subcategory_item, parent, false);
            return new CategoryAdapter.RecyclerViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull CategoryAdapter.RecyclerViewHolder holder, int position) {
            CategoryResponse.CategoryItem item = categoryItemList.get(position);
            if (!Utility.isEmpty(item.getCategoryImage()))
            {
                Picasso.get().load(item.getCategoryImage()).placeholder(R.drawable.grocery_staples).into(holder.downImageView);
            }
            holder.categoryNameTextView.setText(Utility.toCamelCase(item.getCategoryName()));
            GridLayoutManager layoutManager = new GridLayoutManager(
                    holder.recyclerView.getContext(),
                    3
            );
            SubCategoryAdapter subItemAdapter = new SubCategoryAdapter(item.getmCategoryProductList());
            holder.recyclerView.setLayoutManager(layoutManager);
            holder.recyclerView.setAdapter(subItemAdapter);
            holder.categoryNameTextView.setOnClickListener(v -> {
                if (holder.childLayoutContainer.getVisibility() == View.VISIBLE) {
                    holder.childLayoutContainer.setVisibility(View.GONE);
                    holder.constraintLayout12.setBackgroundColor(ContextCompat.getColor(mActivity, R.color.colorWhite));
                    holder.categoryNameTextView.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_keyboard_arrow_down, 0);

                } else {
                    holder.constraintLayout12.setBackgroundColor(ContextCompat.getColor(mActivity, R.color.light_pink));
                    holder.constraintLayout.setBackgroundColor(ContextCompat.getColor(mActivity, R.color.light_pink));
                    holder.childLayoutContainer.setVisibility(View.VISIBLE);
                    holder.categoryNameTextView.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_keyboard_arrow_up, 0);
                }
            });
        }

        @Override
        public int getItemCount() {
            return categoryItemList.size();
        }

        private class RecyclerViewHolder extends RecyclerView.ViewHolder {

            private ImageView downImageView;
            private ConstraintLayout constraintLayout;
            private TextView categoryNameTextView;
            private RecyclerView recyclerView;
            private ConstraintLayout childLayoutContainer;
            private ConstraintLayout constraintLayout12;

            RecyclerViewHolder(@NonNull View itemView) {
                super(itemView);
                downImageView = itemView.findViewById(R.id.productImageView);
                constraintLayout = itemView.findViewById(R.id.constraintContainer);
                constraintLayout12 = itemView.findViewById(R.id.constraintLayout12);
                categoryNameTextView = itemView.findViewById(R.id.categoryNameTextView);
                recyclerView = itemView.findViewById(R.id.childCategoryRecyclerView);
                childLayoutContainer = itemView.findViewById(R.id.childLayoutContainer);

            }
        }
    }

    private class SubCategoryAdapter extends RecyclerView.Adapter<SubCategoryAdapter.RecyclerViewHolder> {
        private List<SubCategoryProductItem> categoryItemList;

        public SubCategoryAdapter(List<SubCategoryProductItem> getmCategoryProductList) {
            this.categoryItemList = getmCategoryProductList;
        }

        @NonNull
        @Override
        public SubCategoryAdapter.RecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.child_subcategory_item, parent, false);
            return new SubCategoryAdapter.RecyclerViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull SubCategoryAdapter.RecyclerViewHolder holder, int position) {
            SubCategoryProductItem item = categoryItemList.get(position);
            if (!Utility.isEmpty(item.getSubCategoryImage())) {
                Picasso.get().load(item.getSubCategoryImage()).placeholder(R.drawable.app_logo).into(holder.iconImage);
            }
            holder.categoryNameTextView.setText(Utility.toCamelCase(item.getSubCategoryName()));
            holder.constraintLayout.setOnClickListener(v -> launchFragment(new ProductCategoryFragment(item.getSubCategoryId(), Constants.CatModes.SUBCATEGORIES,item.getSubCategoryName()), true));

        }

        @Override
        public int getItemCount() {
            return categoryItemList.size();
        }

        private class RecyclerViewHolder extends RecyclerView.ViewHolder {
            private ConstraintLayout constraintLayout;
            private TextView categoryNameTextView;
            private ImageView iconImage;

            RecyclerViewHolder(@NonNull View itemView) {
                super(itemView);
                iconImage = itemView.findViewById(R.id.subCategoriesImageView);
                categoryNameTextView = itemView.findViewById(R.id.subCategoryName);
                constraintLayout = itemView.findViewById(R.id.constraintContainer);
                constraintLayout.setOnClickListener(v -> launchFragment(new ProductCategoryFragment(), true));
            }
        }
    }
}
