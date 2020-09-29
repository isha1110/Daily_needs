package com.skinfotech.dailyneeds.fragments;

import android.os.Bundle;
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

import com.skinfotech.dailyneeds.R;
import com.skinfotech.dailyneeds.constant.ToolBarManager;
import com.skinfotech.dailyneeds.models.Item;

import java.util.ArrayList;

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
        ToolBarManager.getInstance().setHeaderTitle("");
        mActivity.isToggleButtonEnabled(false);
        ToolBarManager.getInstance().onBackPressed(this);
        mCategoryRecyclerView = mContentView.findViewById(R.id.subCategoryRecyclerView);
        categoryAdapter = new CategoryAdapter();
        mCategoryRecyclerView.setLayoutManager(new LinearLayoutManager(mActivity, LinearLayoutManager.VERTICAL, false));
        mCategoryRecyclerView.setAdapter(categoryAdapter);
    }
    @Override
    public void onStart() {
        super.onStart();
        mActivity.showBackButton();
        hideKeyboard();
        mActivity.hideCartIcon();
        mActivity.hideFilterIcon();
    }
    private class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.RecyclerViewHolder>  {


        @NonNull
        @Override
        public CategoryAdapter.RecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.header_subcategory_item, parent, false);
            return new CategoryAdapter.RecyclerViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull CategoryAdapter.RecyclerViewHolder holder, int position) {
            GridLayoutManager layoutManager = new GridLayoutManager(
                    holder.recyclerView.getContext(),
                    3
            );
           SubCategoryAdapter subItemAdapter = new SubCategoryAdapter();
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
            return 5;
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
                constraintLayout = itemView.findViewById(R.id.constraintContainer);
                constraintLayout12 = itemView.findViewById(R.id.constraintLayout12);
                categoryNameTextView = itemView.findViewById(R.id.categoryNameTextView);
                recyclerView = itemView.findViewById(R.id.childCategoryRecyclerView);
                childLayoutContainer = itemView.findViewById(R.id.childLayoutContainer);

            }
        }
    }

    private class SubCategoryAdapter extends RecyclerView.Adapter<SubCategoryAdapter.RecyclerViewHolder> {


        @NonNull
        @Override
        public SubCategoryAdapter.RecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.child_subcategory_item, parent, false);
            return new SubCategoryAdapter.RecyclerViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull SubCategoryAdapter.RecyclerViewHolder holder, int position) {

        }

        @Override
        public int getItemCount() {
            return 5;
        }

        private class RecyclerViewHolder extends RecyclerView.ViewHolder {

            RecyclerViewHolder(@NonNull View itemView) {
                super(itemView);
            }
        }
    }


}
